package edu.ucdenver.app;

import edu.ucdenver.logic.Server;

public class ServerApplication {
    public static void main(String[] args) {
        Server server = new Server();
        server.runServer();
    }
}
