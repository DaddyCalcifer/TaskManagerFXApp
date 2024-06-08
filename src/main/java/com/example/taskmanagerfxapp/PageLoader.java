package com.example.taskmanagerfxapp;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.http.client.fluent.Request;
import org.apache.hc.core5.http.HttpStatus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.tika.Tika;

public class PageLoader {
    private static final Tika tika = new Tika();
    private static final String PAGES_DIR = "pages";
    private static final Map<String, String> PAGES_URLS = new HashMap<>();

    private static final Map<String, String> downloadedUrls = new HashMap<>();

    static {
        PAGES_URLS.put("page1.html", "https://sites.google.com/view/course-of-study1-c/главная/источники");
        PAGES_URLS.put("page2.html", "https://sites.google.com/view/course-of-study1-c/главная/справочник-по-языку-си");
        PAGES_URLS.put("page3.html", "https://sites.google.com/view/course-of-study1-c/главная/инструменты-разработки");
        PAGES_URLS.put("работа-21-записи.html", "https://sites.google.com/view/course-of-study1-c/практика/работа-21-записи");
        for (int i=1; i < 25; i ++)
        {
            if(i!=21)
                PAGES_URLS.put("работа-"+ i +".html", "https://sites.google.com/view/course-of-study1-c/практика/работа-" + i);
        }
    }

    public static void loadPages() {
        File dir = new File(PAGES_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }

        for (Map.Entry<String, String> entry : PAGES_URLS.entrySet()) {
            String fileName = entry.getKey();
            String url = entry.getValue();
            File file = new File(dir, fileName);

            try {
                if (!file.exists()) {
                    downloadPage(url,file);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void downloadPage(String url, File file) {
        try {
            Document doc = Jsoup.connect(url).get();
            removeRedirection(doc);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(doc.outerHtml());
            }
            downloadResources(doc, url, PAGES_DIR, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveToFile(String content, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        }
    }

    private static void downloadResources(Document doc, String pageUrl, String saveDir, File file) throws IOException {
        Set<String> resources = new HashSet<>();
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");
        Elements cssImports = doc.select("style, [style]");

        // Создание директории ресурсов
        File resourceDir = new File(saveDir, "resources");
        if (!resourceDir.exists()) {
            resourceDir.mkdirs();
        }

        int resourceCounter = 0;
        for (Element src : media) {
            String absUrl = src.attr("abs:src");
            if (!absUrl.isEmpty() && !resources.contains(absUrl)) {
                String localPath = downloadResource(absUrl, resourceDir, resourceCounter++);
                if (localPath != null) {
                    src.attr("src", localPath);
                    resources.add(absUrl);
                }
            }
        }

        for (Element link : imports) {
            String absUrl = link.attr("abs:href");
            if (!absUrl.isEmpty() && !resources.contains(absUrl)) {
                String localPath = downloadResource(absUrl, resourceDir, resourceCounter++);
                if (localPath != null) {
                    link.attr("href", localPath);
                    resources.add(absUrl);
                }
            }
        }

        // Обработка встраиваемых стилей для поиска изображений
        int bonusc = 0;
        for (Element styleTag : cssImports) {
            String cssContent = styleTag.html();
            for (String url : extractUrlsFromCss(cssContent, pageUrl)) {
                if (!resources.contains(url)) {

                    String localPath = downloadResource(url, resourceDir, ((resourceCounter++) + (bonusc++)*100));
                    if (localPath != null) {
                        System.out.println(localPath);
                        while(new File(localPath).exists())
                        {
                            localPath = downloadResource(url, resourceDir, resourceCounter++);
                        }
                        cssContent = cssContent.replace(url, localPath);
                        resources.add(url);
                    }
                }
            }
            styleTag.html(cssContent);
        }

        // Сохранение обработанных страниц на компьютер
        saveToFile(doc.html(), file.getPath());
    }

    private static Set<String> extractUrlsFromCss(String cssContent, String pageUrl) {
        Set<String> urls = new HashSet<>();
        String urlPattern = "url\\((.*?)\\)";
        Pattern pattern = Pattern.compile(urlPattern);
        Matcher matcher = pattern.matcher(cssContent);
        while (matcher.find()) {
            String url = matcher.group(1).replaceAll("\"", "").replaceAll("'", "");
            if (!url.startsWith("http") && !url.startsWith("data:")) {
                try {
                    URL baseUrl = new URL(pageUrl);
                    URL absUrl = new URL(baseUrl, url);
                    url = absUrl.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            urls.add(url);
        }
        return urls;
    }

    private static String getFileExtension(String contentType, String url) {
        if (contentType != null) {
            if (contentType.contains("text/css")) {
                return "css";
            } else if (contentType.contains("image/jpeg")) {
                return "jpg";
            } else if (contentType.contains("image/png")) {
                return "png";
            } else if (contentType.contains("image/gif")) {
                return "gif";
            } else if (contentType.contains("image/svg+xml")) {
                return "svg";
            }
        }

        if (url.endsWith(".css")) {
            return "css";
        } else if (url.endsWith(".jpg") || url.endsWith(".jpeg")) {
            return "jpg";
        } else if (url.endsWith(".png")) {
            return "png";
        } else if (url.endsWith(".gif")) {
            return "gif";
        } else if (url.endsWith(".svg")) {
            return "svg";
        }

        return "";
    }
    private static String downloadResource(String url, File saveDir, int counter) {
        //Оптимизация загрузок, для избежания скачивания уже существующих файлов
        var existed = downloadedUrls.get(url);
        if(existed!=null)
        {
            System.out.println("Найден дубликат " + existed);
            return "resources/" + existed;
        }
        try (InputStream in = new URL(url).openStream()) {
            String detectedType = tika.detect(in);
            String fileExtension = getFileExtension(detectedType, url);

            if (fileExtension.isEmpty()) {
                fileExtension = "css";
            }

            String fileName = "resource" + counter + "." + fileExtension;
            File outputFile = new File(saveDir, fileName);
            while(outputFile.exists())
            {
                counter++;
                fileName = "resource" + counter + "." + fileExtension;
                outputFile = new File(saveDir,fileName);
            }

            try (InputStream inAgain = new URL(url).openStream(); OutputStream out = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inAgain.read(buffer, 0, 1024)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                downloadedUrls.put(url,fileName);
                return "resources/" + fileName;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static void changeTextColor(Document doc) {
        Elements elements = doc.select("*");
        for (Element element : elements) {
            String color = element.cssSelector();
            if ("#ffffff".equalsIgnoreCase(element.attr("color")) || "white".equalsIgnoreCase(element.attr("color"))) {
                element.attr("color", "rgba (255,255,255,1)");
            }
        }
        Element style = doc.head().appendElement("style");
        style.attr("type", "text/css");
        style.appendText("body, body * { color: #000 !important; }");
    }

    private static void removeRedirection(Document doc) {
        Elements scripts = doc.select("script");
        for (Element script : scripts) {
            String scriptContent = script.html();
            if (scriptContent.contains("_at_config")) {
                script.remove();
            }
        }
    }
    private static void removeSvgs(Document doc) {
        Elements svgs = doc.select("svg");
        for (Element svg : svgs) {
            svg.remove();
        }
    }
}