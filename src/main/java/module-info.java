module com.example.taskmanagerfxapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jsoup;
    requires javafx.web;


    opens com.example.taskmanagerfxapp to javafx.fxml;
    exports com.example.taskmanagerfxapp;
}