package com.miniredis.persistence;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class AofLogger {
    private final String filePath;

    public AofLogger(String filePath){
        this.filePath = filePath;
    }

    // Command is stored exactly as it would be parsed(as a List)
    // but it is written back in a format it can be re-read
    public synchronized void log(List<String> commandParts){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))){
            // Simple format: command arg1, arg2 .... (one per line)
            String line = String.join(" ", commandParts);
            writer.write(line);
            writer.newLine();
            writer.flush();
        }
        catch(IOException e){
            System.err.println("Failed to write to AOF: " + e.getMessage());
        }
    }
}
