import client.ClientMain;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import server.ServerMain;

import java.io.IOException;

public class WhenUsingServerMain {

    ServerMain server=new ServerMain();
    ClientMain client, client2;

    @BeforeClass
    public static void setupClass(){
        System.setProperty("java.util.logging.config.file", ClassLoader.getSystemResource("logging.properties").getPath());
    }

    @Before
    public void setup() throws IOException {
        System.out.println("Creating server listen on port 7777");
        server.start(7777);

        client = new ClientMain();
        client2 = new ClientMain();
    }

    @Test
    public void successiveClientRequest() throws IOException {

        System.out.println("Creating client");
        client.startConnection("127.0.0.1", 7777);
        System.out.println("Connected to server");
        String response = client.sendMessage("hello server\n");
        System.out.println("Sent message");
        assertEquals("hello client", response);

        response = client.sendMessage("hello server\n");
        System.out.println("Sent message");
        assertEquals("hello client", response);

    }

    @Test
    public void multipleClients() throws IOException {

        System.out.println("Creating client 1");
        client.startConnection("127.0.0.1", 7777);
        System.out.println("Connected to server");

        System.out.println("Creating client 2");
        client2.startConnection("127.0.0.1", 7777);
        System.out.println("Connected to server");

        client.sendNonTerminatedMessage("hello ");
        System.out.println("Sent non-terminated message on client 1");

        String response = client2.sendMessage("hello server");
        System.out.println("Sent message on client 2");
        assertEquals("hello client", response);

        response = client.sendMessage("server");
        System.out.println("Sent message on client 1");
        assertEquals("hello client", response);

    }

    @After
    public void stop() throws IOException {
        System.out.println("Shutting down server");
        server.stop();

        client.stopConnection();
        client2.stopConnection();
    }
}
