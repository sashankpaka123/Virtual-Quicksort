import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;

/**
 * BufferPool manages file I/O through an in-memory buffer cache using
 * Least Recently Used (LRU) replacement.
 *
 * Each buffer stores a 4096-byte block from the file.
 * The BufferPool tracks performance stats including cache hits,
 * disk reads, and disk writes.
 *
 * @author Sashank
 * @version 4/11/2025
 */
public class BufferPool {

    private final int bufferSize = 4096;
    private final int numBuffers;
    private final RandomAccessFile raf;
    private final LinkedList<Buffer> lruList;

    private int cacheHits;
    private int diskReads;
    private int diskWrites;

    /**
     * Constructs a buffer pool with a given file and buffer capacity.
     *
     * @param file
     *            the binary file to manage
     * @param numBuffers
     *            number of 4KB buffers to keep in memory
     * @throws FileNotFoundException
     *             if the file cannot be opened
     */
    public BufferPool(File file, int numBuffers) throws FileNotFoundException {
        this.raf = new RandomAccessFile(file, "rw");
        this.numBuffers = numBuffers;
        this.lruList = new LinkedList<>();
    }


    /**
     * Retrieves a buffer containing the specified block number.
     * Loads from disk if not cached, and uses LRU for eviction.
     *
     * @param blockNum
     *            the block number to access
     * @return the buffer containing the block
     * @throws IOException
     *             if disk access fails
     */
    public Buffer acquireBuffer(int blockNum) throws IOException {
        for (int i = 0; i < lruList.size(); i++) {
            Buffer buffer = lruList.get(i);
            if (buffer.getBlockNum() == blockNum) {
                cacheHits++;
                lruList.remove(i);
                lruList.addFirst(buffer);
                return buffer;
            }
        }

        Buffer buffer = loadBlock(blockNum);

        if (lruList.size() >= numBuffers) {
            Buffer evicted = lruList.removeLast();
            if (evicted.isDirty()) {
                writeBlock(evicted);
            }
        }

        lruList.addFirst(buffer);
        return buffer;
    }


    /**
     * Loads a 4KB block from the file.
     *
     * @param blockNum
     *            the block number to load
     * @return a new Buffer object with file data
     * @throws IOException
     *             if file I/O fails
     */
    private Buffer loadBlock(int blockNum) throws IOException {
        byte[] data = new byte[bufferSize];
        raf.seek((long)blockNum * bufferSize);
        raf.readFully(data);
        diskReads++;
        return new Buffer(blockNum, data);
    }


    /**
     * Writes a dirty buffer's contents back to disk.
     *
     * @param buffer
     *            the buffer to write
     * @throws IOException
     *             if disk I/O fails
     */
    private void writeBlock(Buffer buffer) throws IOException {
        raf.seek((long)buffer.getBlockNum() * bufferSize);
        raf.write(buffer.getDataPointer());
        diskWrites++;
    }


    /**
     * Reads data from the virtual file into a given array.
     *
     * @param space
     *            the array to copy into
     * @param sz
     *            the number of bytes to copy
     * @param pos
     *            file offset to begin reading
     * @throws IOException
     *             if file I/O fails
     */
    public void getbytes(byte[] space, int sz, int pos) throws IOException {
        int blockNum = pos / bufferSize;
        int offset = pos % bufferSize;
        Buffer buffer = acquireBuffer(blockNum);
        System.arraycopy(buffer.getDataPointer(), offset, space, 0, sz);
    }


    /**
     * Writes data into the virtual file from a given array.
     *
     * @param space
     *            the data to insert
     * @param sz
     *            number of bytes to insert
     * @param pos
     *            file offset to begin writing
     * @throws IOException
     *             if file I/O fails
     */
    public void insert(byte[] space, int sz, int pos) throws IOException {
        int blockNum = pos / bufferSize;
        int offset = pos % bufferSize;
        Buffer buffer = acquireBuffer(blockNum);
        System.arraycopy(space, 0, buffer.getDataPointer(), offset, sz);
        buffer.markDirty();
    }


    /**
     * Writes all dirty buffers back to the file.
     *
     * @throws IOException
     *             if file I/O fails
     */
    public void flush() throws IOException {
        for (Buffer buffer : lruList) {
            if (buffer.isDirty()) {
                writeBlock(buffer);
            }
        }
    }


    /**
     * Closes the underlying file.
     *
     * @throws IOException
     *             if file closing fails
     */
    public void close() throws IOException {
        flush();
        raf.close();
    }


    /**
     * Returns the number of cache hits from buffer accesses.
     *
     * @return number of cache hits
     */
    public int getCacheHits() {
        return cacheHits;
    }


    /**
     * Returns the number of times a block was read from disk.
     *
     * @return number of disk reads
     */
    public int getDiskReads() {
        return diskReads;
    }


    /**
     * Returns the number of times a block was written back to disk.
     *
     * @return number of disk writes
     */
    public int getDiskWrites() {
        return diskWrites;
    }
}
