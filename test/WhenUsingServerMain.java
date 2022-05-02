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

    @BeforeClass
    public static void setupClass(){
        System.setProperty("java.util.logging.config.file", ClassLoader.getSystemResource("logging.properties").getPath());
    }

    @Before
    public void setup() throws IOException {
        System.out.println("Creating server listen on port 7777");
        server.start(7777);
    }

    @Test
    public void successiveClientRequest() throws IOException {

        System.out.println("Creating client");
        ClientMain client = new ClientMain();
        client.startConnection("127.0.0.1", 7777);
        System.out.println("Connected to server");
        String response = client.sendMessage("hello server");
        System.out.println("Sent message");
        assertEquals("hello client", response);

        response = client.sendMessage("hello server");
        System.out.println("Sent message");
        assertEquals("hello client", response);

        client.stopConnection();
    }

    @Test
    public void multipleClients() throws IOException {

        System.out.println("Creating client 1");
        ClientMain client = new ClientMain();
        client.startConnection("127.0.0.1", 7777);
        System.out.println("Connected to server");

        System.out.println("Creating client 2");
        ClientMain client2 = new ClientMain();
        client2.startConnection("127.0.0.1", 7777);
        System.out.println("Connected to server");

        String response = client.sendMessage("hello server");
        System.out.println("Sent message");
        assertEquals("hello client", response);

        response = client2.sendMessage("hello server");
        System.out.println("Sent message");
        assertEquals("hello client", response);

        client.stopConnection();
        client2.stopConnection();
    }

    @After
    public void stop() throws IOException {
        System.out.println("Shutting down server");
        server.stop();
    }
}
