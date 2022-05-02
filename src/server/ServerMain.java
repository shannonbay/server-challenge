package server;

import java.net.*;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;
import java.util.logging.Logger;

public class ServerMain {
    private ServerSocket serverSocket;

    private List<Connection> connections = new LinkedList();

    final ExecutorService executor = Executors.newFixedThreadPool(2); // Q1: Explain why 1 thread won't work
    public void start(final int port) throws IOException {

        serverSocket = new ServerSocket(port);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Connection c = new Connection(serverSocket.accept());
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

    private class Connection implements Runnable {
        private PrintWriter out;
        private BufferedReader in;
        private Socket clientSocket;

        public Connection(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }

        @Override
        public void run() {
            try {
                String greeting;
                logger.info("Waiting for incoming message");
                while ((greeting = in.readLine()) != null) {
                    logger.info("Incoming message: '" + greeting + "'");
                    if ("hello server".equals(greeting)) {
                        out.println("hello client");
                    } else {
                        out.println("unrecognised greeting");
                    }

                    logger.info("Waiting for next incoming message");
                }
            } catch (IOException e) {
                logger.info("Client closed");
                try {
                    stop();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        public void stop() throws IOException {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("location: " + System.getProperty("java.util.logging.config.file"));
        ServerMain server=new ServerMain();
        server.start(7777);
    }

    private static final Logger logger = Logger.getLogger("Server");
}
