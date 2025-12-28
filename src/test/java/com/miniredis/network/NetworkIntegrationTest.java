package com.miniredis.network;

import org.junit.jupiter.api.Test;
import java.io.*;
import java.net.Socket;
import static org.junit.jupiter.api.Assertions.*;

public class NetworkIntegrationTest {
    
    // @Test
    // void testBasicPingPong() throws IOException{
    //     // Start server in a background thread so that it does not black the test
    //     int port = 9001;
    //     new Thread(() -> {
    //         RedisServer server = new RedisServer(port);
    //         server.start();
    //     }).start();

    //     // giving the server time for starting
    //     try {Thread.sleep(500); } catch (InterruptedException e){}

    //     // connect as a client
    //     try(Socket socket = new Socket("localhost", port);
    //         PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
    //         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
    //         out.println("PING");
    //         String response = in.readLine();
    //         assertEquals("+PONG", response);
    //     }
    // }

    @Test
    void testGarbage() throws IOException {
        // ... setup server on port 9002 (Use a NEW port to avoid ghosts) ...
        int port = 9002;
        new Thread(() -> {
            RedisServer server = new RedisServer(port);
            server.start();
        }).start();

        // Give it a moment to start
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        try (Socket socket = new Socket("localhost", port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            // Send absolute nonsense
            out.println("GARBAGE_DATA");
            
            // If the server is using RespParser, it should throw an error and close the connection.
            // So 'response' should be null.
            String response = in.readLine();
            
            System.out.println("Server responded with: " + response);
            
            // If this PASSES (response is null), your Parser is working.
            // If this FAILS (response is -ERR unknown command), your Parser is NOT connected.
            assertNull(response, "Server should have closed connection on bad protocol");
        }
    }
}
