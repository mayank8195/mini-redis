package com.miniredis.eviction;

/**
 * Interface for eviction logic.
 * Using generics <k> because the strategy only needs to know the Key to evict.
 */

public interface EvictionStrategy<K> {
    //Notifying the strategy that a KEY was touched    
    void keyAccessed(K key);

    //Asking the strategy: "which key to delete?"
    K evict();

    //Removing a key from the strategy (used if a key is manually deleted)
    void remove(K key);
}
