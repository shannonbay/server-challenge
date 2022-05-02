package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

class Connection implements Runnable {
    private ServerMain serverMain;
    private PrintWriter out;
    private BufferedReader in;
    private Socket clientSocket;
    private Logger logger;

    public Connection(ServerMain serverMain, Socket clientSocket, Logger logger) throws IOException {
        this.serverMain = serverMain;
        this.clientSocket = clientSocket;
        this.logger = logger;
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
    }
}
