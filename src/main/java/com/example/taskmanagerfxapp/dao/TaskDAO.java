package com.example.taskmanagerfxapp.dao;

import com.example.taskmanagerfxapp.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    private String url;

    public TaskDAO(String url) {
        this.url = url;
    }

    public List<Task> getTasksFromSite() {
        List<Task> tasks = new ArrayList<>();
        for (int i = 1; i < 25; i++) {
            String taskUrl = url + (i != 21 ? i : i + "-записи");
            tasks.add(new Task(getTopicFromUrl(taskUrl), true, false, false, false, taskUrl));
        }
        return tasks;
    }

    public List<Task> getTasksFromDisk(String directoryPath) {
        List<Task> tasks = new ArrayList<>();
        File dir = new File(directoryPath);
        File[] files = dir.listFiles((dir1, name) -> name.endsWith(".html"));

        if (files != null) {
            for (File file : files) {
                tasks.add(new Task(getTopicFromFile(file), true, false, false, false, file.getAbsolutePath()));
            }
        }
        return tasks;
    }

    private String getTopicFromUrl(String taskUrl) {
        String topic = "";
        try {
            Document doc = Jsoup.connect(taskUrl).get();
            Element header = doc.selectFirst("h1");
            if (header != null) {
                topic = header.text();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return topic;
    }

    private String getTopicFromFile(File file) {
        String topic = "";
        try {
            Document doc = Jsoup.parse(file, "UTF-8");
            Element header = doc.selectFirst("h1");
            if (header != null) {
                topic = header.text();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return topic;
    }
}