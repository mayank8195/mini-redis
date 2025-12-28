package com.miniredis.network;

import org.junit.jupiter.api.Test;
import java.io.*;
import java.net.Socket;
import static org.junit.jupiter.api.Assertions.*;

public class NetworkIntegrationTest {
    
    @Test
    void testBasicPingPong() throws IOException{
        // Start server in a background thread so that it does not black the test
        int port = 9001;
        new Thread(() -> {
            RedisServer server = new RedisServer(port);
            server.start();
        }).start();

        // giving the server time for starting
        try {Thread.sleep(500); } catch (InterruptedException e){}

        // connect as a client
        try(Socket socket = new Socket("localhost", port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){

            out.println("*1\r\n$4\r\nPING\r\n");
            out.flush();
            
            String response = in.readLine();
            assertEquals("+PONG", response);
        }
    }
}
