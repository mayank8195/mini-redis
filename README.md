# Mini-Redis: High-Performance, Wire-Compatible KV Store

A high-performance, multi-threaded, in-memory key-value store built in Java. This project is designed to be a wire-compatible clone of Redis, supporting the official Redis Serialization Protocol (RESP) and resilient data persistence.

## Key Features

* **Wire Compatibility:** Fully compatible with `redis-cli` and official Redis libraries.
* **High Concurrency:** Implements a thread-per-connection model to handle multiple simultaneous clients.
* **Intelligent Eviction:** Custom LRU (Least Recently Used) cache engine with  operations.
* **Durability:** Append-Only File (AOF) persistence for crash recovery and state restoration.
* **Clean Architecture:** Modular command-execution engine utilizing the Command Design Pattern for high extensibility.

---

## Architecture & Design

### 1. Networking Layer

The server utilizes a `ServerSocket` listening on port `6379`. Each incoming connection is handed off to a dedicated worker thread, ensuring that slow clients do not block the entire system.

### 2. Protocol Parser (RESP)

Implements a custom parser for the **Redis Serialization Protocol (RESP)**. It decodes raw byte streams into structured command lists, handling bulk strings and arrays according to official Redis specifications.

### 3. Storage Engine & LRU Eviction

The core storage is a `HashMap` paired with a custom **Doubly Linked List**.

* **HashMap:** Provides  lookup.
* **Doubly Linked List:** Tracks access order.
* **Thread Safety:** Synchronized access ensures data integrity during high-contention concurrent writes.

### 4. Persistence (AOF)

To ensure durability, the server implements an **Append-Only File**. Every state-changing command (like `SET`) is logged to disk. Upon startup, the server "replays" this log to reconstruct the in-memory state.

---

## Getting Started

### Prerequisites

* Java 17 or higher
* Maven 3.6+
* `redis-cli` (optional, for manual testing)

### Installation & Running

1. Clone the repository:
```bash
git clone https://github.com/yourusername/mini-redis.git
cd mini-redis

```


2. Build the project:
```bash
mvn clean compile

```


3. Start the server:
```bash
mvn exec:java -Dexec.mainClass="com.miniredis.network.RedisServer"

```

---

## Testing

### Automated Test Suite

The project includes a robust testing suite focusing on integration and stress testing:

* **Integration Tests:** Verifies PING/PONG and basic GET/SET flows.
* **Concurrency Tests:** Uses a thread pool to simulate 500+ simultaneous requests to verify thread-safety and lock performance.
* **Persistence Tests:** Validates that data survives a full server restart via the AOF logs.

Run all tests via Maven:

```bash
mvn test

```

---

## Future Roadmap (Planned Improvements)

* **I/O Multiplexing (Java NIO):** Transition from thread-per-connection to a non-blocking Event Loop using `Selectors` to scale to 10k+ concurrent connections.
* **AOF Log Compaction:** Implement a background "rewrite" process to compress the AOF file by removing redundant command history.
* **Advanced Data Types:** Support for Redis `LISTS`, `SETS`, and `HASHES`.
* **TTL (Time-To-Live):** Passive and active key expiration logic using background maintenance threads.

---

<img width="2816" height="1536" alt="Gemini_Generated_Image_2xu3h52xu3h52xu3" src="https://github.com/user-attachments/assets/69f28893-cb2e-471c-a9cd-23ae3213d7ab" />
