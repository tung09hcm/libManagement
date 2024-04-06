module com.example.hospitalmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;

    requires java.sql;
    requires java.desktop;

    opens com.example.librarymanagement to javafx.fxml;
    exports com.example.librarymanagement;
}