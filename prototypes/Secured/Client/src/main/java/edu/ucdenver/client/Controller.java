package edu.ucdenver.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Controller {


    @FXML
    private TabPane tabpane;
    @FXML
    private Button btnExit;
    @FXML
    private Button btnSendMsg;
    @FXML
    private Button btnConnect;
    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtMsgTo;
    @FXML
    private TextField txtMsgContent;
    @FXML
    private TextField txtServerIp;
    @FXML
    private TextField txtServerPort;
    @FXML
    private TextField txtIncomingPort;
    @FXML
    private ListView lstMessages;


    private Client client;
    private IncomeMsg incomingRelay;
    ExecutorService executorService;

//    private ArrayList<String> messages;
    private ObservableList<String> messages;
    public Controller(){
        client = null;
        incomingRelay = null;
        messages= FXCollections.observableArrayList();
        executorService = Executors.newCachedThreadPool();
    }

    public void setIncomingPort(String value) {
        this.txtIncomingPort.setText(value);
    }

    public void initialize(){
        this.lstMessages.setItems(messages);
    }



    @FXML
    public void connect(ActionEvent actionEvent) {
        client = new Client(this.txtServerIp.getText(), Integer.parseInt(this.txtServerPort.getText()));
        client.connect();

        incomingRelay = new IncomeMsg(Integer.parseInt(this.txtIncomingPort.getText()),5,this);
        this.executorService.execute(this.incomingRelay);

        try {
            String response = client.sendRequest(String.format("C|%s|%s",this.txtUsername.getText(),
                                                                         this.txtIncomingPort.getText()));
            if (!response.equals("OK|")){
                throw new RuntimeException("Unexpected Response");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.btnConnect.setDisable(true);
        SingleSelectionModel<Tab> selectionModel = this.tabpane.getSelectionModel();
        selectionModel.select(1);
    }

    @FXML
    public void sendMessage(ActionEvent actionEvent) {
        try {
            this.client.sendRequest(String.format("R|%s|%s", this.txtMsgTo.getText(), this.txtMsgContent.getText()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void exitApplication(ActionEvent actionEvent) {
        Stage stage = (Stage) this.btnExit.getScene().getWindow();
        stage.close();

        if (client != null && client.isConnected())
            client.disconnect();
        client = null;

        executorService.shutdown();
        try {
            executorService.awaitTermination(5 , TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.exit(0);

    }

    public void addIncomingMessage(String aMsg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                messages.add(aMsg);
            }
        });
    }
}