package edu.ucdenver.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ClientApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parameters params = getParameters();
        List<String> list = params.getRaw();
        String port = "20001";
        if (list.size() == 1){
            port = list.get(0);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource("client.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 720 , 500);
        Controller c = fxmlLoader.getController();
        c.setIncomingPort(port);
        stage.setTitle("Messaging Client");
        stage.setScene(scene);
        stage.show();



    }

    public static void main(String[] args) {
        launch(args);
    }
}