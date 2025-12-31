package com.miniredis.command;

import com.miniredis.storage.HashMapStorage;
import com.miniredis.persistence.AofLogger;
import java.util.List;

public class DelCommand implements Command<String, String> {
    @Override
    public String execute(List<String> args, HashMapStorage<String, String> storage, AofLogger aofLogger){
        if(args.size() < 2) return "-ERR wrong number of arguments for 'del' command\r\n";
        String removedValue = storage.remove(args.get(1));
        if(removedValue != null){
            aofLogger.log(args);
            return ":1\r\n";
        }
        return ":0\r\n";
    }
}
