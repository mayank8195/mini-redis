package com.miniredis.command;

import com.miniredis.storage.HashMapStorage;
import com.miniredis.persistence.AofLogger;
import java.util.List;

public class ExistsCommand implements Command<String, String> {
    @Override
    public String execute(List<String> args, HashMapStorage<String, String> storage, AofLogger aofLogger){
        if(args.size() < 2) return "-ERR wrong number of arguments\r\n";
        return storage.get(args.get(1)) != null ? ":1\r\n" : ":0\r\n";
    }
}
