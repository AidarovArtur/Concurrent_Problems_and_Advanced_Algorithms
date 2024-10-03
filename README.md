# Concurrent Problems and Advanced Data Structures

This repository contains a collection of Java implementations for classic concurrency problems and custom data structures. These projects demonstrate my expertise in multi-threading, synchronization, and algorithm optimization, skills essential for back-end system development and performance improvement.

## Table of Contents
- [Key Projects](#key-projects)
  - [Auction House System](#auction-house-system)
  - [Readers-Writers Problem](#readers-writers-problem)
  - [Custom Map Implementation](#custom-map-implementation)
  - [Producer-Consumer Problem](#producer-consumer-problem)
  - [Pipeline Processing System](#pipeline-processing-system)
  - [Prime Number Finder](#prime-number-finder)
- [How to Run](#how-to-run)

## Key Projects

### 1. Auction House System
- **Description**: Implements a multi-threaded auction system where multiple artists create NFTs, auctioneers manage auctions, and collectors place bids. Uses blocking queues to synchronize auction offers and handle concurrent access.
- **Topics Covered**: Multi-threading, concurrency control, blocking queues, synchronization.

### 2. Readers-Writers Problem
- **Description**: Solves the classic Readers-Writers problem, showcasing the ability to manage concurrent access to shared resources using synchronization techniques.
- **Topics Covered**: Synchronization, resource management, concurrency.

### 3. Custom Map Implementation
- **Description**: A custom hash-based map data structure that resolves collisions and optimizes key-value retrieval and insertion.
- **Topics Covered**: Data structures, hashing, collision resolution, algorithm optimization.

### 4. Producer-Consumer Problem
- **Description**: Solves the Producer-Consumer problem with proper coordination between producer and consumer threads using semaphores and shared buffers.
- **Topics Covered**: Synchronization, thread coordination, semaphores.

### 5. Pipeline Processing System
- **Description**: Implements a multi-stage processing pipeline where each stage runs in a separate thread, improving performance through task parallelization.
- **Topics Covered**: Multi-stage processing, threading, performance optimization.

### 6. Prime Number Finder
- **Description**: A prime number finding algorithm implemented in both single-threaded and multi-threaded versions to highlight performance improvements from parallelization.
- **Topics Covered**: Parallel processing, algorithm optimization, multi-threading.

## How to Run
Each project in the repository can be compiled and run separately. To compile and execute any file, use the following commands in your terminal:

```bash
# Compile
javac <filename>.java

# Run
java <filename>
