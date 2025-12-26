package com.miniredis.eviction;

/**
 * The building block of Doubly Linked List.
 */

public class Node<K> {
    K key;
    Node<K> prev;
    Node<K> next;

    public Node(K key){
        this.key = key;
    }
}
