package com.miniredis.command;

import com.miniredis.persistence.AofLogger;
import com.miniredis.storage.HashMapStorage;
import java.util.List;

public class DecrCommand implements Command<String, String> {
    @Override
    public String execute(List<String> args, HashMapStorage<String, String> storage, AofLogger aofLogger){
        if(args.size() < 2) return "-ERR wrong number of arguments\r\n";
        String key = args.get(1);
        String valStr = storage.get(key);
        long val = 0;
        try{
            if(valStr != null) val = Long.parseLong(valStr);
            val--;
            String result = String.valueOf(val);
            storage.put(key, result);
            aofLogger.log(List.of("SET", key, result)); // logging as a SET for easier replay
            return ":" + val + "\r\n";
        }
        catch (NumberFormatException e){
            return "-ERR value is not an integer\r\n";
        }
    }
}
