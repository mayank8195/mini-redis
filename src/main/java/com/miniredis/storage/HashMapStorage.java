package com.miniredis.storage;

import com.miniredis.eviction.EvictionStrategy;
import com.miniredis.eviction.LRUEvictionStrategy;
import java.util.HashMap;
import java.util.Map;

public class HashMapStorage<K, V> implements Storage<K, V>{
    //This is where the data actually lives in memory
    private final Map<K, V> internalMap;
    private final EvictionStrategy<K> evictionStrategy;
    private final int capacity;

    public HashMapStorage(int capacity){
        this.capacity = capacity;
        this.internalMap = new HashMap<>();
        this.evictionStrategy = new LRUEvictionStrategy<>();
    }

    @Override
    public void put(K key, V value){
        //If at capacity, and new key incoming, need to evict
        if(internalMap.size() >= capacity && !internalMap.containsKey(key)){
            K evictedKey = evictionStrategy.evict();
            if(evictedKey != null){
                internalMap.remove(evictedKey);
                System.out.println("Evicted key: " + evictedKey);
            }
        }

        internalMap.put(key, value);
        evictionStrategy.keyAccessed(key);
    }

    @Override
    public V get(K key){
        if(!internalMap.containsKey(key)) return null;

        //Notify LRUCache that this key is "fresh" again
        evictionStrategy.keyAccessed(key);
        return internalMap.get(key);
    }

    @Override
    public void remove(K key){
        internalMap.remove(key);
        evictionStrategy.remove(key);
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