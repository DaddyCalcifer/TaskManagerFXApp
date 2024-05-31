package com.example.taskmanagerfxapp.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class Task {
    private final SimpleStringProperty topic;
    private final BooleanProperty task1;
    private final BooleanProperty task2;
    private final BooleanProperty task3;
    private final BooleanProperty homework;
    private final String url;

    public Task(String topic, boolean task1, boolean task2, boolean task3, boolean homework, String url) {
        this.topic = new SimpleStringProperty(topic);
        this.task1 = new SimpleBooleanProperty(task1);
        this.task2 = new SimpleBooleanProperty(task2);
        this.task3 = new SimpleBooleanProperty(task3);
        this.homework = new SimpleBooleanProperty(homework);
        this.url = url;

        // Слушатели для свойств BooleanProperty (не работают как надо)
        this.task1.addListener((observable, oldValue, newValue) -> handleTaskChange("Задание 1", topic, newValue));
        this.task2.addListener((observable, oldValue, newValue) -> handleTaskChange("Задание 2", topic, newValue));
        this.task3.addListener((observable, oldValue, newValue) -> handleTaskChange("Задание 3", topic, newValue));
        this.homework.addListener((observable, oldValue, newValue) -> handleTaskChange("Домашнее задание", topic, newValue));
    }

    public String getTopic() {
        return topic.get();
    }

    public BooleanProperty task1Property() {
        return task1;
    }

    public BooleanProperty task2Property() {
        return task2;
    }

    public BooleanProperty task3Property() {
        return task3;
    }

    public BooleanProperty homeworkProperty() {
        return homework;
    }

    public String getUrl() {
        return url;
    }

    private void handleTaskChange(String taskName, String topic, boolean isCompleted) {
        System.out.println(taskName + " для " + topic + " изменено на " + (isCompleted ? "выполнено" : "не выполнено"));
    }
}
