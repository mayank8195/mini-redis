package com.miniredis.command;

import com.miniredis.persistence.AofLogger;
import com.miniredis.storage.HashMapStorage;
import java.util.List;

public class GetCommand implements Command<String, String> {
    @Override
    public String execute(List<String> args, HashMapStorage<String, String> storage, AofLogger aofLogger){
        if(args.size() < 2) return "-ERR wrong number of arguments\r\n";
        String val = storage.get(args.get(1));
        return (val == null) ? "$-1\r\n" : "$" + val.length() + "\r\n" + val + "\r\n";
    }
}
