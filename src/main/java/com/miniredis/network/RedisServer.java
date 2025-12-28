package com.miniredis.network;

import com.miniredis.storage.Storage;
import com.miniredis.storage.HashMapStorage;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.net.ServerSocket;
import java.net.Socket;

public class RedisServer {
    private final int port;
    private final Storage<String, String> storage;

    public RedisServer(int port){
        this.port = port;
        this.storage = new HashMapStorage<>(2);
    }

    public void start(){
        try(ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("Mini-Redis is running on port " + port);

            while(true){
                Socket clienSocket = serverSocket.accept();
                System.out.println("New client connected!");

                handleClient(clienSocket);
            }
        }
        catch (IOException e){
            System.err.println("could not start server: " + e.getMessage());
        }
    }

    private void handleClient(Socket clienSocket){
        try(
            BufferedReader in = new BufferedReader(new InputStreamReader(clienSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clienSocket.getOutputStream(), true)
        ){
            while(true) {
                // 1. Basic parsing: Split the input by spaces
                // String[] parts = inputLine.trim().split("\\s");

                List<String> parts = RespParser.parseArray(in);
                if(parts == null) break;
                if(parts.isEmpty()) continue;

                String command = parts.get(0).toUpperCase();

                // 2. Routing commands to storage engine
                switch (command) {
                    case "COMMAND":
                        out.print("+OK\r\n");
                        break;
                    case "SET":
                        if(parts.size() < 3){
                            out.println("-ERR wrong number of arguments for 'SET' command");
                        }
                        else{
                            storage.put(parts.get(1), parts.get(2));
                            out.println("OK\r\n");
                        }
                        break;

                    case "GET":
                        if(parts.size() < 2){
                            out.println("-ERR wrong number of arguments for 'GET' command");
                        }
                        else{
                            String value = storage.get(parts.get(1));
                            if(value == null) out.print("$-1\r\n");
                            else out.println("$" + value.length() + "\r\n" + value + "\r\n");
                        }
                        break;
                    
                    case "PING":
                        out.println("+PONG\r\n");
                        break;
                    
                    default:
                        // System.out.println("LOG: Received unknown command: " + command);
                        out.println("-ERR unknown command '" + command + "'");
                        break;
                }
                out.flush();
            }
        }
        catch (IOException e){
            System.out.println("Client disconnected.");
        }
    }

    public static void main(String[] args){
        RedisServer server = new RedisServer(6379);
        server.start();
    }
}
