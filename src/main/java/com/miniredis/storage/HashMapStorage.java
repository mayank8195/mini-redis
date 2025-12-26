package com.miniredis.storage;

import java.util.HashMap;
import java.util.Map;

public class HashMapStorage<K, V> implements Storage<K, V>{
    //This is where the data actually lives in memory
    private final Map<K, V> internalMap;

    public HashMapStorage(){
        this.internalMap = new HashMap<>();
    }

    @Override
    public void put(K key, V value){
        internalMap.put(key, value);
    }

    @Override
    public V get(K key){
        return internalMap.get(key);
    }

    @Override
    public void remove(K key){
        internalMap.remove(key);
    }

    @Override
    public boolean contains(K key){
        return internalMap.containsKey(key);
    }

    @Override
    public int size(){
        return internalMap.size();
    }
}