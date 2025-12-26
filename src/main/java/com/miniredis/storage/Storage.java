package com.miniredis.storage;
/**
 * Interface for the Key-Value Store.
 * Using generics <k, v> allows to store any data type.
 */

public interface Storage<K, V> {
    void put(K key, V value);
    V get(K key);
    void remove(K key);
    boolean contains(K key);
    int size();
}
