module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;

    exports edu.ucdenver.client;
    opens edu.ucdenver.client to javafx.fxml;
}