package com.miniredis.command;

import com.miniredis.storage.HashMapStorage;
import com.miniredis.persistence.AofLogger;
import java.util.List;

public class SetCommand implements Command<String, String> {
    @Override
    public String execute(List<String> args, HashMapStorage<String, String> storage, AofLogger aofLogger){
        if(args.size() < 3) return "-ERR wrong number of arguments\r\n";
        storage.put(args.get(1), args.get(2));
        aofLogger.log(args); // persist write
        return "+OK\r\n";
    }
}
