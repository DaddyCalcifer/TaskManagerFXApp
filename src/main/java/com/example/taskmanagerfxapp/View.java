package com.example.taskmanagerfxapp;

import com.example.taskmanagerfxapp.dao.TaskDAO;
import com.example.taskmanagerfxapp.model.Task;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class View {


    private final TabPane tabPane = new TabPane();
    private final TableView<Task> table = new TableView<>();
    private final WebView webView = new WebView();
    private final WebEngine webEngine = webView.getEngine();
    private final ObservableList<Task> data = FXCollections.observableArrayList();
    private final VBox tableVBox = new VBox();
    private final VBox infoBox ;

    public View() {
        String url = "https://sites.google.com/view/course-of-study1-c/практика/работа-";
        String link1 = "https://sites.google.com/view/course-of-study1-c/главная/источники";
        String link2 = "https://sites.google.com/view/course-of-study1-c/главная/справочник-по-языку-си";
        String link3 = "https://sites.google.com/view/course-of-study1-c/главная/инструменты-разработки";
        TaskDAO taskDAO = new TaskDAO("");
        List<Task> tasks = new ArrayList<Task>();
        File pageDir = new File("pages");
        if (!pageDir.exists()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Образовательный портал");
            alert.setHeaderText("Подгрузка данных");
            alert.setContentText("Для работы портала требуется загрузка данных. Для подтверждения загрузки нажмите ОК, портал откроется после окончания подгрузки данных.");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("к");
                }
            });
            System.out.println("Страницы не загружены - приложение работает с сайтом, паралельно подгружает страницы.");
            PageLoader.loadPages();
        }
        tasks = taskDAO.getTasksFromDisk("pages");

        data.addAll(tasks);

        TableColumn<Task, String> topicCol = new TableColumn<>("Тема");
        topicCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTopic()));
        topicCol.setCellFactory(column -> {
            TableCell<Task, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(item);
                        setOnMouseClicked(event -> {
                            if (event.getClickCount() == 1) {
                                Task task = getTableView().getItems().get(getIndex());
                                openWebpage(task.getUrl());
                            }
                        });
                    } else {
                        setText(null);
                        setOnMouseClicked(null);
                    }
                }
            };
            return cell;
        });

        TableColumn<Task, Boolean> task1Col = new TableColumn<>("Задание 1");
        task1Col.setCellValueFactory(cellData -> cellData.getValue().task1Property());
        task1Col.setCellFactory(CheckBoxTableCell.forTableColumn(task1Col));
        task1Col.setEditable(true);

        TableColumn<Task, Boolean> task2Col = new TableColumn<>("Задание 2");
        task2Col.setCellValueFactory(cellData -> cellData.getValue().task2Property());
        task2Col.setCellFactory(CheckBoxTableCell.forTableColumn(task2Col));

        TableColumn<Task, Boolean> task3Col = new TableColumn<>("Задание 3");
        task3Col.setCellValueFactory(cellData -> cellData.getValue().task3Property());
        task3Col.setCellFactory(CheckBoxTableCell.forTableColumn(task3Col));

        TableColumn<Task, Boolean> homeworkCol = new TableColumn<>("Домашнее задание");
        homeworkCol.setCellValueFactory(cellData -> cellData.getValue().homeworkProperty());
        homeworkCol.setCellFactory(CheckBoxTableCell.forTableColumn(homeworkCol));

        table.setItems(data);
        table.getColumns().addAll(topicCol, task1Col, task2Col, task3Col, homeworkCol);

        tableVBox.getChildren().add(table);
        tableVBox.getChildren().add(webView);

        //   Создание главной страницы
        TextArea infoLabel = new TextArea();
        infoLabel.setWrapText(true);

        Document doc = null;
        String text="_";
        try {
            doc = Jsoup.connect("https://sites.google.com/view/course-of-study1-c/главная").get();
            Elements lines = doc.select("div > p");
            StringBuilder firstThreeLines = new StringBuilder();
            for (int i = 0;  i < lines.size(); i++) {
                firstThreeLines.append(lines.get(i).ownText()).append("\n");
            }
            text=firstThreeLines.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        infoLabel.setText(text);
        infoLabel.setEditable(false);

        // Гиперссылки
        Button blink1 = new Button("Источники");
        Button blink2 = new Button("Справочник по языку Си");
        Button blink3 = new Button("Инструменты разработки");

        // WebView для отображения страниц
        WebView webViewlink = new WebView();
        WebEngine webEnginelink = webViewlink.getEngine();

        // Обработчики событий для кнопок (загрузка страниц в webView)
        blink1.setOnAction(event -> webEnginelink.load(new File("pages/page1.html").toURI().toString()));
        blink2.setOnAction(event -> webEnginelink.load(new File("pages/page2.html").toURI().toString()));
        blink3.setOnAction(event -> webEnginelink.load(new File("pages/page3.html").toURI().toString()));

        // Горизонтальная панель для кнопок
        HBox linkBox = new HBox(10, blink1, blink2, blink3);
        linkBox.setAlignment(Pos.CENTER);

        // Вертикальная панель для информационного поля и кнопок
        infoBox = new VBox(10, infoLabel, linkBox,webViewlink);
        infoBox.setPadding(new Insets(10));
        infoBox.setAlignment(Pos.CENTER);

        // Создание TabPane и добавление вкладок
        Tab tab0 = new Tab("Информация", infoBox);
        Tab tab1 = new Tab("Задания", tableVBox);
        Tab tab2 = new Tab("Обзор", webView);

        tabPane.getTabs().addAll(tab0, tab1, tab2);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    private void openWebpage(String url) {
        // Переключение на вкладку с веб-просмотром и загрузка URL
        if(url.contains("http")) {
            TabPane tabPane = (TabPane) table.getScene().getRoot();
            tabPane.getSelectionModel().select(1);
            webEngine.load(url);
        }
        else {
            System.out.println(url);
            TabPane tabPane = (TabPane) table.getScene().getRoot();
            tabPane.getSelectionModel().select(1);
            webEngine.load(new File(url).toURI().toString());
        }
    }

    public String getTopic(String url) {
        String topic = "_";
        try {
            Document doc;
            doc = Jsoup.connect(url).get();

            Element textElement = doc.selectFirst("p"); // Например, получаем первый тег `<p>`

            topic = textElement.text();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return topic;
    }
}
