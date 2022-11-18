package edu.ucdenver.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IncomeMsg implements Runnable{
    private final int port;
    private final int backlog;

    private SSLServerSocket serverSocket;
    private SSLSocket clientConnection;

    private boolean keepRunning;
    private boolean keepRunningClient;

    private PrintWriter output;
    private BufferedReader input ;

    private Controller controller;

    public IncomeMsg(int port, int backlog, Controller aController){
        this.port = port;
        this.backlog = backlog;
        this.keepRunning = true;
        this.keepRunningClient = true;
        this.controller = aController;
    }

    private SSLSocket waitForClientConnection() throws IOException {
        System.out.println("Waiting for a connection....");
        SSLSocket clientConnection = (SSLSocket) this.serverSocket.accept();
        System.out.printf("Connection Accepted from %s%n", clientConnection.getInetAddress().getHostName());
        return clientConnection;
    }

    private void getOutputStream(SSLSocket clientConnection) throws IOException {
        this.output = new PrintWriter(clientConnection.getOutputStream(), true);
    }

    private void getInputStream(SSLSocket clientConnection) throws IOException {
        this.input = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
    }

    private void processClientRequest() throws IOException {
        String clientMessage = this.input.readLine();       //rcv from client
        displayMessage("CLIENT SAID>>>" + clientMessage);
        String[] arguments = clientMessage.split("\\|");  // this splits the string using | as the delimiter

        try {
            switch (arguments[0]) { //arguments[0] must be the command
                case "M" -> {  //M|From|the message
                    if (arguments.length==3) {
                        String msg = String.format("%s Says: %s",arguments[1], arguments[2]);
                        this.controller.addIncomingMessage(msg);
                    }
                    else{
                        System.out.println("[ERR] MSG:"+clientMessage);
                    }
                }
                case "T" -> { //T|
                    this.keepRunningClient = false;
                    this.keepRunning = false;
                }
            }
        }
        catch(IllegalArgumentException iae){
        }

    }

    private void closeClientConnection(){
        //Try to close all input, output and socket.
        try { this.input.close();} catch (IOException|NullPointerException e) {  e.printStackTrace();      }
        try { this.output.close();} catch (NullPointerException e) {  e.printStackTrace();      }
        try { this.clientConnection.close();} catch (IOException|NullPointerException e) {  e.printStackTrace();      }
    }

    private void displayMessage(String message) {  // We can improve this method to be log-type one
        System.out.printf("CLIENT>> %s%n", message);
    }


    @Override
    public void run(){
        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
//            this.serverSocket = new ServerSocket(this.port, this.backlog);
            SSLServerSocketFactory sslSrvFact = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            this.serverSocket = (SSLServerSocket)sslSrvFact.createServerSocket(this.port, this.backlog);

            while (this.keepRunning) {
                try {
                    this.clientConnection = this.waitForClientConnection();
                    try {
                        displayMessage("Getting Data Streams");
                        getOutputStream(clientConnection);
                        getInputStream(clientConnection);

                        while (this.keepRunningClient)
                            processClientRequest();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        closeClientConnection();
                        stopServer();
                    }

                }
                catch (IOException ioe){
                    System.out.println("\n------------------\nedu.ucdenver.msg.Server Terminated");
                    ioe.printStackTrace();
                    this.serverSocket.close();
                }
            }
        }
        catch (IOException ioe){
            System.out.println("\n++++++ Cannot open the server ++++++\n");
            executorService.shutdown();
            ioe.printStackTrace();
        }
    }

    public void stopServer() {
        keepRunning = false;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
