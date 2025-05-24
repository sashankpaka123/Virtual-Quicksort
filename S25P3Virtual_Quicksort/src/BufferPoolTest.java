import java.io.*;
import student.TestCase;

/**
 * Test class for the BufferPool class, focusing on LRU behavior,
 * buffer reuse, and read/write tracking.
 * 
 * @author Tester
 * @version 4/11/2025
 */
public class BufferPoolTest extends TestCase {

    private File testFile;
    private BufferPool pool;

    /**
     * Sets up a temporary file for testing.
     */
    public void setUp() throws IOException {
        testFile = new File("testData.bin");
        try (RandomAccessFile raf = new RandomAccessFile(testFile, "rw")) {
            for (int i = 0; i < 4096 * 3; i++) {
                raf.writeByte(i % 256);
            }
        }
        pool = new BufferPool(testFile, 2);
    }


    /**
     * Tests buffer acquisition, reuse, and LRU eviction behavior.
     * Specifically verifies that least recently used block is evicted
     * and recently used blocks are reused (producing cache hits).
     *
     * @throws IOException
     *             if disk I/O fails
     */
    public void testBufferReuseAndLRUEviction() throws IOException {
        byte[] block = new byte[10];

        // Load block 1 first (4096)
        pool.getbytes(block, 10, 4096);
        assertEquals(1, pool.getDiskReads());

        // Load block 0 (0)
        pool.getbytes(block, 10, 0);
        assertEquals(2, pool.getDiskReads());

        // Access block 0 again (should be cache hit)
        pool.getbytes(block, 10, 0);
        assertEquals(1, pool.getCacheHits());

        // Access block 2 (8192) — should evict block 1
        pool.getbytes(block, 10, 8192);
        assertEquals(3, pool.getDiskReads());

        // Re-access block 1 (4096) — should be reloaded, not cached
        pool.getbytes(block, 10, 4096);
        assertEquals(4, pool.getDiskReads());
    }


    /**
     * Tests buffer dirty marking and flush behavior.
     * Ensures insert marks buffers dirty and flush writes them.
     *
     * @throws IOException
     *             if disk I/O fails
     */
    public void testInsertAndFlush() throws IOException {
        byte[] block = new byte[] { 11, 22, 33, 44 };

        // Insert into two blocks
        pool.insert(block, 4, 0);
        assertEquals(1, pool.getDiskReads());

        pool.insert(block, 4, 4096);
        assertEquals(2, pool.getDiskReads());

        // Flush both dirty blocks
        pool.flush();
        assertEquals(2, pool.getDiskWrites());
    }


    /**
     * Cleans up temporary test files after tests are complete.
     */
    public void tearDown() {
        if (testFile.exists()) {
            testFile.delete();
        }
    }
}
