package com.example.taskmanagerfxapp.dao;

import com.example.taskmanagerfxapp.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    private static String URL = "https://sites.google.com/view/course-of-study1-c/практика/работа-";
    public TaskDAO(String url)
    {
        URL = url;
    }
    public List<Task> getTasksFromSite(){
        String url = "https://sites.google.com/view/course-of-study1-c/практика/работа-";
        List<Task> tasks = new ArrayList<>();
        TaskDAO taskDAO = new TaskDAO(url);
        for(int i = 1; i < 25; i++)
        {
            if(i != 21)
                tasks.add(new Task(taskDAO.getTopic(i),true,false,false,false, url + i ));
        }
        return tasks;
    }
    public String getTopic(int id) {
        String topic = "";
        try {
            Document doc;
            if(id != 21)
                doc = Jsoup.connect(URL + id).get();
            else
                doc = Jsoup.connect(URL + id + "-записи").get();
            // Предполагаем, что название темы находится в заголовке <h1>
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
