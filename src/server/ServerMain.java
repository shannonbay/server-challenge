package server;

import java.net.*;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class ServerMain {
    private ServerSocket serverSocket;

    private List<Connection> connections = new LinkedList();

    final ExecutorService executor = Executors.newFixedThreadPool(3); // Q1: Explain why 1 thread won't work
    public void start(final int port) throws IOException {

        serverSocket = new ServerSocket(port);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Connection c = new Connection(ServerMain.this, serverSocket.accept(), logger);
                        connections.add(c);
                        logger.info("New incoming connection");
                        executor.execute(c);
                    }
                } catch (IOException e) {
                    logger.severe("No longer accepting connections");
                }
            }
        });

    }

    public void stop() throws IOException {
        for(Connection c: connections) {
            c.stop();
        }
        executor.shutdownNow();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("location: " + System.getProperty("java.util.logging.config.file"));
        ServerMain server=new ServerMain();
        server.start(7777);
    }

    private static final Logger logger = Logger.getLogger("Server");
}
