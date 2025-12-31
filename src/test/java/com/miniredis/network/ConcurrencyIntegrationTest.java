package com.miniredis.network;

import org.junit.jupiter.api.Test;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConcurrencyIntegrationTest {

    @Test
    void testConcurrentWrites() throws InterruptedException {
        int port = 9010;
        int numThreads = 10;
        int requestsPerThread = 50;

        // Start server
        new Thread (() -> new RedisServer(port).start()).start();
        Thread.sleep(1000); // waiting for startup

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        AtomicInteger successfulRequests = new AtomicInteger(0);

        for(int i = 0; i < numThreads * requestsPerThread; i++){
            final int keyId = i;
            executor.submit(() -> {
                try (Socket socket = new Socket("localhost", port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    // Send SET keyId value
                    out.print("*3\r\n$3\r\nSET\r\n$4\r\nkey" + keyId + "\r\n$1\r\nv\r\n");
                    out.flush();

                    if("+OK".equals(in.readLine())){
                        successfulRequests.incrementAndGet();
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // verifying all requests were handled
        assertEquals(numThreads * requestsPerThread, successfulRequests.get(),
        "Server should handlle all concurrent requests without crashing");
    }
}
