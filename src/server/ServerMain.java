package server;

import java.net.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class ServerMain {
    private ServerSocket serverSocket;

    // Boolean flag indicates if connection is currently being processed by a thread
    private ConcurrentMap<Connection, Boolean> connections = new ConcurrentHashMap<Connection, Boolean>();

    private final ExecutorService executor = Executors.newFixedThreadPool(4); // Q1: Explain why 1 thread won't work

    public void start(final int port) throws IOException {

        serverSocket = new ServerSocket(port);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Connection c = new Connection(ServerMain.this, serverSocket.accept(), logger);
                        connections.put(c, false);
                   }
                } catch (IOException e) {
                    logger.severe("No longer accepting connections");
                }
            }
        });

        executor.execute(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    for(final Connection c: connections.keySet()) {
                        if(connections.replace(c, false, true)) {
                            logger.finest("Processing connection " + c);
                            executor.submit(new Runnable() {
                                @Override
                                public void run() {
                                    if(c.call()) {
                                        connections.remove(c);
                                        logger.info("Removed ended connection");
                                    } else {
                                        connections.replace(c, true, false);
                                    }
                                }
                            });

                            logger.finest("Finished processing connection " + c);
                        }
                    }
                }
            }
        });

        logger.info("Finished booting server");
    }

    public void stop() throws IOException {
        for(Connection c: connections.keySet()) {
            c.stop();
        }
        executor.shutdownNow();

        serverSocket.close();
        connections.clear();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("location: " + System.getProperty("java.util.logging.config.file"));
        ServerMain server=new ServerMain();
        server.start(7777);
    }

    private static final Logger logger = Logger.getLogger("Server");
}
