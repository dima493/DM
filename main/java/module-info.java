module com.example {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.media;

    opens com.example to javafx.graphics, javafx.fxml;
    opens com.example.screens to javafx.graphics;
    opens com.example.logic to javafx.graphics;

    exports com.example;
}