package com.example.taskmanagerfxapp;

import com.example.taskmanagerfxapp.dao.TaskDAO;
import com.example.taskmanagerfxapp.model.Task;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.beans.property.SimpleBooleanProperty;
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


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Информационно-образовательный ресурс");
        View view = new View();

        Scene scene = new Scene(view.getTabPane());
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}