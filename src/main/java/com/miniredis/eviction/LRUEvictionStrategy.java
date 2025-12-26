package com.miniredis.eviction;

import java.util.HashMap;
import java.util.Map;

public class LRUEvictionStrategy<K> implements EvictionStrategy<K> {
    private final Map<K, Node<K>> nodeMap;
    private final Node<K> head, tail;

    public LRUEvictionStrategy(){
        this.nodeMap = new HashMap<>();
        // Dummy nodes to simplify boundary logic
        this.head = new Node<>(null);
        this.tail = new Node<>(null);
        head.next = tail;
        tail.prev = head;
    }

    private void addNodeToHead(Node<K> node){
        //Logic: Insert node between head and head.next
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node<K> node){
        // Bridging the gap by removing the node
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    @Override
    public void keyAccessed(K key){
        // If exists, move to head. if new, add to head
        if(nodeMap.containsKey(key)){
            Node<K> node = nodeMap.get(key);
            removeNode(node);
            addNodeToHead(node);
        }
        else {
            Node<K> newNode = new Node<>(key);
            nodeMap.put(key, newNode);
            addNodeToHead(newNode);
        }
    }

    @Override
    public K evict(){
        // Remove the node right before the tail dummy
        // If only dummy node exists, return null
        if(head.next == null) return null; 

        Node<K> toRemove = tail.prev;
        removeNode(toRemove);
        nodeMap.remove(toRemove.key);
        return toRemove.key;
    }

    @Override
    public void remove(K key){
        Node<K> node = nodeMap.remove(key);
        if (node != null){
            removeNode(node);
        }
    }
}
