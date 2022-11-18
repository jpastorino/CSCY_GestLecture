package edu.ucdenver.logic;

import com.sun.jdi.request.InvalidRequestStateException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class User {
    private String name;
    private int serverPort;
    private Socket clientServer;

    public User(String name, int serverPort){
        this.name = name;
        this.serverPort = serverPort;
        this.clientServer = null;
    }

    public User(){
        this.name = "";
        this.serverPort = 0;
        this.clientServer = null;
    }

    public String getName() {        return name;    }
    public void setName(String name) {this.name = name;  }

    public int getServerPort() { return serverPort;   }
    public void setServerPort(int serverPort) { this.serverPort = serverPort;    }


    public boolean isConnected(){
        return this.clientServer != null;
    }

    public void connect() throws InvalidRequestStateException {
        if (this.serverPort == 0)
            throw new InvalidRequestStateException("Port is not set");
        else{
            try {
                this.clientServer = new Socket("localhost", this.serverPort);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void disconnect(){
        if (this.isConnected()) {
            try {
                this.clientServer.close();
            } catch (IOException e) {
                this.clientServer=null;
            }
        }

    }

    public void sendMessage(String msg){
        if (this.isConnected()){
            try {
                PrintWriter output = new PrintWriter(this.clientServer.getOutputStream(), true);
                output.println(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
