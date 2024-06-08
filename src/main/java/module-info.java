module com.example.taskmanagerfxapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jsoup;
    requires javafx.web;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires org.apache.httpcomponents.core5.httpcore5.h2;
    requires org.apache.httpcomponents.httpclient.fluent;
    requires org.apache.tika.core;


    opens com.example.taskmanagerfxapp to javafx.fxml;
    exports com.example.taskmanagerfxapp;
}