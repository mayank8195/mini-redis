package com.miniredis.storage;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StorageTest {

    @Test
    void testPutAndGet(){
        Storage<String, String> storage = new HashMapStorage<>(10);
        storage.put("key1", "value1");

        assertEquals("value1", storage.get("key1"), "The value retreived should match the value put in.");
    }

    @Test
    void testRemove(){
        Storage<String, String> storage = new HashMapStorage<>(10);
        storage.put("user:101", "Alice");
        storage.remove("user:101");

        assertNull(storage.get("user:101"), "After removal, the key should return null");
    }

    @Test
    void testSize(){
        Storage<String, Integer> storage = new HashMapStorage<>(10);
        storage.put("a", 1);
        storage.put("b", 2);

        assertEquals(2, storage.size(), "size should correctly reflect the number of key value pairs");
    }

    @Test
    void testEviction(){
        Storage<String, String> storage = new HashMapStorage<>(2);

        storage.put("A", "Apple");
        storage.put("B", "Banana");

        //Accessing "A" to make it most recently used
        storage.get("A");

        // this third put should trigger eviction of "key1"
        storage.put("C", "Cherry");

        assertNull(storage.get("B"), "B should be evicted because A was accessed most recently");
        assertNotNull(storage.get("A"), "A should still be there");
        assertNotNull(storage.get("C"), "C should also be there");
        assertEquals("Apple", storage.get("A"));
        assertEquals("Cherry", storage.get("C"));
    }
}
