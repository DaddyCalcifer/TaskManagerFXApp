module com.example.taskmanagerfxapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.taskmanagerfxapp to javafx.fxml;
    exports com.example.taskmanagerfxapp;
}