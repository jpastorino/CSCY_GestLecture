package edu.ucdenver.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.net.ssl.SSLSocket;


public class ClientBroker implements Runnable {
    private final SSLSocket clientConnection;
    private final Server server;
    private final User user;
    private final int id;

    private PrintWriter output;
    private BufferedReader input ;
    private boolean keepRunningClient;


    public ClientBroker(SSLSocket connection, Server server, User user, int id){
        this.clientConnection = connection;
        this.server = server;
        this.user = user;
        this.id = id;
        this.keepRunningClient = true;
    }


    private void getOutputStream(SSLSocket clientConnection) throws IOException {
        this.output = new PrintWriter(clientConnection.getOutputStream(), true);
    }

    private void getInputStream(SSLSocket clientConnection) throws IOException {
        this.input = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
    }

    private void sendMessage(String message){
        displayMessage("SERVER >> "+ message);
        this.output.println(message);
    }

    private void processClientRequest() throws IOException {
        String clientMessage = this.input.readLine();       //rcv from client
        if (clientMessage == null)
            throw new IOException("Socket Closed");

        displayMessage("CLIENT SAID>>>" + clientMessage);
        String[] arguments = clientMessage.split("\\|");  // this splits the string using | as the delimiter
        String response;                                        //This will be the response to the server.

        try {
            switch (arguments[0]) { //arguments[0] must be the command
                case "R" -> { //RELAY MESSAGE
                    if (arguments.length==3) {
                        String userTo = arguments[1];
                        String msg = arguments[2];
                        this.server.relayMessage(userTo, msg, this.user);
                        response = "OK|";
                    }
                    else{
                        response = "ERR|Wrong Format.";
                    }
                }
                case "C" -> {
                    if (arguments.length==3) {
                        this.user.setName(arguments[1]);
                        this.user.setServerPort(Integer.parseInt(arguments[2]));
                        this.user.connect();
                        response = "OK|";
                    }
                    else{
                        response = "ERR|Wrong Format.";
                    }
                }
                case "T" -> {
                    this.keepRunningClient = false;
                    this.server.removeUser(this.user);
                    response = "OK|";
                }
                default -> response = "ERR|Unknown Command.";
            }
        }
        catch(IllegalArgumentException iae){
            response = "ERR|" + iae.getMessage();
        }

        this.sendMessage(response);
    }

    private void closeClientConnection(){
        //Try to close all input, output and socket.
        try { this.input.close();} catch (IOException|NullPointerException e) {  e.printStackTrace();      }
        try { this.output.close();} catch (NullPointerException e) {  e.printStackTrace();      }
        try { this.clientConnection.close();} catch (IOException|NullPointerException e) {  e.printStackTrace();      }
    }

    private void displayMessage(String message) {  // We can improve this method to be log-type one
        System.out.printf("CLIENT[%d] >> %s%n", this.id, message);
    }


    @Override
    public void run() {
        try {
            displayMessage("Getting Data Streams");
            getOutputStream(clientConnection);
            getInputStream(clientConnection);

            sendMessage("Connected to Messaging Server");

            while (this.keepRunningClient)
                processClientRequest();
        }
        catch (IOException e) {
            //e.printStackTrace();
        }
        finally {
            closeClientConnection();
        }
    }
}


/*
::: PROTOCOL :::
"R|user|msg" -> relay message
"R|john|This is my message to John"
"C|username|listenPort" -> to set who is connected and the port to relay messages
"C|Alice|10005
"T|" -> terminate the client conneciton.
*/
