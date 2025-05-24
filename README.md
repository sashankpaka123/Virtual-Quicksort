# Virtual Quicksort

## Overview
**Virtual Quicksort is a Java-based file sorting system that uses a modified in-place Quicksort algorithm to sort large binary files stored on disk. Instead of sorting in memory, the algorithm operates directly on a disk-backed file through a custom buffer pool that uses the Least Recently Used (LRU) replacement policy to manage memory-efficient access to 4096-byte data blocks.

This project simulates a virtual memory environment and is designed for performance under constrained buffer access, mimicking real-world I/O-bound scenarios.

## Features
- Sorts binary files of 4-byte records (2-byte key + 2-byte value)
- Custom buffer pool to reduce disk I/O with LRU caching
- In-place file sorting using a modified **Quicksort** algorithm
- Tracks and outputs runtime statistics:
  - Total execution time (ms)
  - Number of cache hits
  - Number of disk reads and writes
- Automatically flushes buffers to disk after sort completion
- Efficient for large files that don’t fit entirely in memory

## File Format
Each record in the input file is 4 bytes:
- **2 bytes**: short key (used for sorting)
- **2 bytes**: short value (data associated with the key)

The file must be a binary file and a multiple of 4096 bytes in size.

## Usage

java Quicksort <data-file-name> <num-buffers> <stat-file-name>
