package com.miniredis.network;

import com.miniredis.storage.Storage;
import com.miniredis.storage.HashMapStorage;
import com.miniredis.persistence.AofLogger;

import java.io.*;
import java.util.List;
// import java.util.ArrayList;
import java.net.ServerSocket;
import java.net.Socket;

public class RedisServer {
    private final int port;
    private final Storage<String, String> storage;
    private AofLogger aofLogger = new AofLogger("appendonly.aof");

    public RedisServer(int port){
        this.port = port;
        this.storage = new HashMapStorage<>(2);
    }

    private void loadFromAof(){
        File aofFile = new File("appendonly.aof");
        if(!aofFile.exists()) return;

        System.out.println("Loading data from AOF...");
        try(BufferedReader reader = new BufferedReader(new FileReader(aofFile))){
            String line;
            while((line = reader.readLine()) != null){
                String[] parts = line.split(" ");
                if(parts.length == 0) continue;

                String command = parts[0].toUpperCase();
                if(command.equals("SET") && parts.length >= 3){
                    storage.put(parts[1], parts[2]);
                }
                // will add other write commands as implement
            }
            System.out.println("AOF recovery complete.");
        }
        catch(IOException e){
            System.err.println("Could not read AOF file: " + e.getMessage());
        }
    }
    
    public void start(){
        loadFromAof(); // 1. Old data is recovered first

        try(ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("Mini-Redis is running on port " + port);

            while(true){
                // 1. Main thread blocking until someone connects 
                Socket clienSocket = serverSocket.accept();
                System.out.println("New client connected: " + clienSocket.getInetAddress());

                // 2. making a new thread: instead of calling handkeClient directly, we wrap it in a thread
                new Thread(() -> {
                    handleClient(clienSocket);
                }).start();
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
                            aofLogger.log(parts); // Logging the successful SET
                            out.println("+OK\r\n");
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
