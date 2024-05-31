package com.example.taskmanagerfxapp;

import com.example.taskmanagerfxapp.dao.TaskDAO;
import com.example.taskmanagerfxapp.model.Task;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class HelloApplication extends Application {
    private final TableView<Task> table = new TableView<>();
    private final WebView webView = new WebView();
    private final WebEngine webEngine = webView.getEngine();
    private final ObservableList<Task> data = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Task Manager");
        String url = "https://sites.google.com/view/course-of-study1-c/практика/работа-";
        TaskDAO taskDAO = new TaskDAO(url);
        var tasks = taskDAO.getTasksFromSite();
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

        VBox tableVBox = new VBox(table);

        // Создание TabPane и добавление вкладок
        TabPane tabPane = new TabPane();
        Tab tab1 = new Tab("Задания", tableVBox);
        Tab tab2 = new Tab("Веб-просмотр", webView);

        tabPane.getTabs().addAll(tab1, tab2);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Scene scene = new Scene(tabPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openWebpage(String url) {
        // Переключение на вкладку с веб-просмотром и загрузка URL
        TabPane tabPane = (TabPane) table.getScene().getRoot();
        tabPane.getSelectionModel().select(1);
        webEngine.load(url);
    }

    public static void main(String[] args) {
        launch(args);
    }
}