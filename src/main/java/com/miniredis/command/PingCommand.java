package com.miniredis.command;

import com.miniredis.persistence.AofLogger;
import com.miniredis.storage.HashMapStorage;
import java.util.List;

public class PingCommand implements Command<String, String> {
    @Override
    public String execute(List<String> args, HashMapStorage<String, String> storage, AofLogger aofLogger){
        return "+PONG\r\n";
    }
}
