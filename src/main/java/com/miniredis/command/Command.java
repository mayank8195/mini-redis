package com.miniredis.command;

import com.miniredis.storage.HashMapStorage;
import com.miniredis.persistence.AofLogger;
import java.util.List;

public interface Command<K, V> {
    String execute(List<String> args, HashMapStorage<K, V> storage, AofLogger aofLogger);
}
