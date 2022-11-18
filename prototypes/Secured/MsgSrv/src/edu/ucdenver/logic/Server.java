package edu.ucdenver.logic;

import java.io.IOException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final int backlog;
    private int connectionCounter; // keep track of how many clients are connected.
    private SSLServerSocket serverSocket;
    private boolean keepRunning;

    private final ArrayList<User> users;

    public Server(int port, int backlog){
        this.port = port;
        this.backlog = backlog;
        this.connectionCounter = 0;
        keepRunning = true;
        users = new ArrayList<>(10);
    }

    public Server(){
        this(10000,10);
    }

    private SSLSocket waitForClientConnection() throws IOException {
        System.out.println("Waiting for a connection....");
        SSLSocket clientConnection = (SSLSocket) this.serverSocket.accept();
        this.connectionCounter++ ;
        System.out.printf("Connection #%d accepted from %s %n",this.connectionCounter,
                clientConnection.getInetAddress().getHostName());


        return clientConnection;
    }

    public void runServer(){
        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
//            this.serverSocket = new ServerSocket(this.port, this.backlog);
            SSLServerSocketFactory sslSrvFact = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
            this.serverSocket = (SSLServerSocket)sslSrvFact.createServerSocket(port, backlog);

            while (keepRunning) {
                try {
                    SSLSocket clientConnection = this.waitForClientConnection();
                    User newUser = new User();
                    this.users.add(newUser);
                    ClientBroker cb = new ClientBroker(clientConnection, this,
                                                       newUser, this.connectionCounter);
                    executorService.execute(cb);

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

    public synchronized void removeUser(User aUser){
        aUser.disconnect();
        users.remove(aUser);
    }

    public void relayMessage(String userTo, String theMsg, User userFrom){
        //Protocol: M|userFrom|msg  E.g., M|Alice|Hello World
        String msg = String.format("M|%s|%s",userFrom.getName(),theMsg);
        for (User aUser: users){
            if (aUser.getName().equalsIgnoreCase(userTo)){
                aUser.sendMessage(msg);
            }
        }
    }
}
