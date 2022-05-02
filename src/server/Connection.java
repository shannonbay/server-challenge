package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

class Connection implements Runnable {
    private ServerMain serverMain;
    private PrintWriter out;
    private InputStream in;
    private Socket clientSocket;
    private Logger logger;

    public Connection(ServerMain serverMain, Socket clientSocket, Logger logger) throws IOException {
        this.serverMain = serverMain;
        this.clientSocket = clientSocket;
        this.logger = logger;
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = clientSocket.getInputStream();
    }

    private StringBuffer s = new StringBuffer();

    @Override
    public void run() {
        try {
            logger.finest("Waiting for incoming message");

            int available = in.available();
            if(available <= 0) return;

            byte[] buf = new byte[available];
            in.read(buf);

            s.append(new String(buf, StandardCharsets.UTF_8));

            if(buf[available - 1] == '\n') { //check for complete line
                logger.info("Incoming message: '" + s + "'");
                if ("hello server\n".equals(s.toString())) {
                    out.println("hello client");
                } else {
                    out.println("unrecognised greeting: "+ s);
                }
                s.setLength(0);
            } else {
                logger.info("Received incomplete message: " + s);
            }

            logger.info("Waiting for next incoming message");
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
