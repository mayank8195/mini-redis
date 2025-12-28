package com.miniredis.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RespParser {
    public static List<String> parseArray(BufferedReader in) throws IOException{
        String line = in.readLine();

        // Debug log 1
        System.out.println("DEBUG: Parser received line: " + line);
        if(line == null || !line.startsWith("*")) return null;
        
        int numElements = Integer.parseInt(line.substring(1));
        List<String> commands = new ArrayList<>();

        for(int i = 0; i < numElements; i++){
            String typeLine = in.readLine();

            if(typeLine != null && typeLine.startsWith("$")){
                String actualData = in.readLine();
                //Debug log 2
                System.out.println("DEBUG: Length line: " + typeLine);
                System.out.println("DEBUG: Payload data: " + actualData);
                commands.add(actualData);
            }
        }
        return commands;
    }
}
