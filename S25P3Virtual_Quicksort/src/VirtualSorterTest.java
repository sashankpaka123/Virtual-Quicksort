import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Tests for the VirtualSorter class using BufferPool and student.TestCase.
 * Validates in-place sorting of binary files containing fixed-size records.
 *
 * Each record is 4 bytes: 2-byte key (used for sorting), 2-byte value
 * (ignored).
 *
 * @author Sashank
 * @version 4/11/2025
 */
public class VirtualSorterTest extends student.TestCase {

    private File file;

    private static final int BLOCKS = 10;
    private static final String FILE_BIN = "testSortBinary.bin";
    private static final String FILE_ASC = "testSortAscii.bin";

    /**
     * Sets up a binary file with 3 records:
     * (key, value) = (300, 1), (200, 2), (100, 3)
     */
    public void setUp() throws IOException {
        file = new File("testSort3.bin");
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            raf.writeShort(300);
            raf.writeShort(1);
            raf.writeShort(200);
            raf.writeShort(2);
            raf.writeShort(100);
            raf.writeShort(3);

            // pad the rest to 4096 bytes
            byte[] padding = new byte[4096 - 12];
            raf.write(padding);

            // Generate binary and ASCII files for testing
            FileGenerator binGen = new FileGenerator(FILE_BIN, BLOCKS);
            binGen.setSeed(123); // Deterministic
            binGen.generateFile(FileType.BINARY);

            FileGenerator ascGen = new FileGenerator(FILE_ASC, BLOCKS);
            ascGen.setSeed(456);
            ascGen.generateFile(FileType.ASCII);
        }
    }


    /**
     * Verifies sorting correctly orders records by key.
     */
    public void testSortThreeRecords() throws IOException {
        BufferPool pool = new BufferPool(file, 2);
        VirtualSorter.sort(pool, 0, 2);
        pool.flush();

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            short key1 = raf.readShort();
            raf.skipBytes(2);
            short key2 = raf.readShort();
            raf.skipBytes(2);
            short key3 = raf.readShort();

            assertEquals(100, key1);
            assertEquals(200, key2);
            assertEquals(300, key3);
        }
    }


    /**
     * Verifies sort works when only a single record is present.
     */
    public void testSortSingleRecord() throws IOException {
        File oneRecFile = new File("testSort1.bin");
        try (RandomAccessFile raf = new RandomAccessFile(oneRecFile, "rw")) {
            raf.writeShort(1234);
            raf.writeShort(5678);
            raf.write(new byte[4096 - 4]);
        }

        BufferPool pool = new BufferPool(oneRecFile, 1);
        VirtualSorter.sort(pool, 0, 0);
        pool.flush();

        try (RandomAccessFile raf = new RandomAccessFile(oneRecFile, "r")) {
            assertEquals(1234, raf.readShort());
            assertEquals(5678, raf.readShort());
        }
    }


    /**
     * Tests sorting a binary file using the virtual quicksort and buffer pool.
     * The file is sorted in-place, and correctness is verified using CheckFile.
     *
     * @throws Exception
     *             if sorting or file checking encounters an error
     */
    public void testBinarySort() throws Exception {
        File file1 = new File(FILE_BIN);
        BufferPool bp = new BufferPool(file1, 10);
        VirtualSorter.sort(bp, 0, (int)(file1.length() / 4) - 1);
        bp.flush();
        assertTrue(CheckFile.check(FILE_BIN));
    }


    /**
     * Tests sorting an ASCII file using the virtual quicksort and buffer pool.
     * The file is sorted in-place, and correctness is verified using CheckFile.
     *
     * @throws Exception
     *             if sorting or file checking encounters an error
     */
    public void testAsciiSort() throws Exception {
        File file2 = new File(FILE_ASC);
        BufferPool bp = new BufferPool(file2, 10);
        VirtualSorter.sort(bp, 0, (int)(file2.length() / 4) - 1);
        bp.flush();
        assertTrue(CheckFile.check(FILE_ASC));
    }
}
