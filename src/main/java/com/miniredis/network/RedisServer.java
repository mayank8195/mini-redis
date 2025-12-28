package com.miniredis.network;

import com.miniredis.storage.Storage;
import com.miniredis.storage.HashMapStorage;

import java.io.*;
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
            String inputLine;
            while((inputLine = in.readLine()) != null) {
                // 1. Basic parsing: Split the input by spaces
                String[] parts = inputLine.trim().split("\\s");
                if(parts.length == 0 || parts[0].isEmpty()) continue;

                String command = parts[0].toUpperCase();

                // 2. Routing commands to storage engine
                switch (command) {
                    case "SET":
                        if(parts.length < 3){
                            out.println("-ERR wrong number of arguments for 'SET' command");
                        }
                        else{
                            storage.put(parts[1], parts[2]);
                            out.println("OK");
                        }
                        break;

                    case "GET":
                        if(parts.length < 2){
                            out.println("-ERR wrong number of arguments for 'GET' command");
                        }
                        else{
                            String value = storage.get(parts[1]);
                            out.println(value == null ? "$-1" : "+" + value);
                        }
                        break;
                    
                    case "PING":
                        out.println("+PONG");
                        break;
                    
                    default:
                        out.println("-ERR unknown command '" + command + "'");
                }
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
