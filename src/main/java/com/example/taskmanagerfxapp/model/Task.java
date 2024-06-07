package com.example.taskmanagerfxapp.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class Task {
    private SimpleStringProperty topic;
    private BooleanProperty task1;
    private BooleanProperty task2;
    private BooleanProperty task3;
    private BooleanProperty homework;
    private final String url;



    public Task(String topic, boolean task1, boolean task2, boolean task3, boolean homework, String url) {
        this.topic = new SimpleStringProperty(topic);
        this.task1 = new SimpleBooleanProperty(task1);
        this.task2 = new SimpleBooleanProperty(task2);
        this.task3 = new SimpleBooleanProperty(task3);
        this.homework = new SimpleBooleanProperty(homework);
        this.url = url;

        // Слушатели для свойств BooleanProperty (не работают как надо)
      //  this.task1.addListener((observable, oldValue, newValue) -> {this.task1.});
      //  this.task2.addListener((observable, oldValue, newValue) -> handleTaskChange("Задание 2", topic, newValue));
      //  this.task3.addListener((observable, oldValue, newValue) -> handleTaskChange("Задание 3", topic, newValue));
      //  this.homework.addListener((observable, oldValue, newValue) -> handleTaskChange("Домашнее задание", topic, newValue));
    }

    public String getTopic() {
        return topic.get();
    }

    public SimpleStringProperty topicProperty() {
        return topic;
    }

    public String getUrl() {
        return url;
    }

    public boolean isHomework() {
        return homework.get();
    }

    public BooleanProperty homeworkProperty() {
        return homework;
    }

    public boolean isTask3() {
        return task3.get();
    }

    public BooleanProperty task3Property() {
        return task3;
    }

    public boolean isTask2() {
        return task2.get();
    }

    public BooleanProperty task2Property() {
        return task2;
    }

    public boolean isTask1() {
        return task1.get();
    }

    public BooleanProperty task1Property() {
        return task1;
    }

    public void setTask1(boolean task1) {
        this.task1.set(task1);
    }

    public void setHomework(boolean homework) {
        this.homework.set(homework);
    }

    public void setTask3(boolean task3) {
        this.task3.set(task3);
    }

    public void setTask2(boolean task2) {
        this.task2.set(task2);
    }
}
