import java.io.*;
import java.util.*;

/**
 * Enum representing the type of file to be generated.
 * Can be either BINARY or ASCII.
 * 
 * This enum helps specify the format in which the file will be generated.
 * 
 * @author Sashank
 * @version 2025-04-02
 */
enum FileType {
    /**
     * Represents a binary file format where keys and values are stored as raw
     * binary data.
     */
    BINARY,
    /**
     * Represents an ASCII file format where keys are characters from 'A' to
     * 'Z',
     * and values are always represented as two spaces (' ').
     */
    ASCII
}




/**
 * Generates a test data file of records. Each record is 4 bytes:
 * 2 bytes for the key (a java short, used for sorting),
 * 2 bytes for the value (a java short).
 * A group of 1024 records is a block. Depending on the method, you can
 * generate two types of files: ASCII or raw binary shorts.
 * 
 * In ASCII mode, the records are constrained to specific values
 * which align with specific ASCII values for visual inspection.
 * In Binary mode, the keys and values of a record are in the range [1-30000).
 * 
 * @author Sashank
 * @version 2025-04-02
 */
public class FileGenerator {

    /** Number of bytes used to store the key. */
    static public final int BYTES_IN_KEY = Short.BYTES;

    /** Number of bytes used to store the value. */
    static public final int BYTES_IN_VALUE = Short.BYTES;

    /** Number of bytes per record, which is the sum of key and value bytes. */
    static public final int BYTES_PER_RECORD = BYTES_IN_KEY + BYTES_IN_VALUE;

    /** Number of records per block (4096 bytes / 4 bytes per record). */
    static public final int RECORDS_PER_BLOCK = 1024;

    /** Number of bytes per block (4096 bytes). */
    static public final int BYTES_PER_BLOCK = RECORDS_PER_BLOCK
        * BYTES_PER_RECORD;

    /** The number of blocks to generate in the file. */
    private final int numBlocks;

    /** The name of the file to generate. */
    private final String fname;

    /** Random number generator for generating random keys and values. */
    private Random rng;

    /**
     * Creates a FileGenerator object for making random files of data.
     * 
     * @param fname
     *            the file name (example 'oneBlock.txt' or 'data.bin')
     * @param numBlocks
     *            the number of blocks of data in the file.
     *            Each block is 4096 bytes.
     */
    public FileGenerator(String fname, int numBlocks) {
        this.numBlocks = numBlocks;
        this.fname = fname;
        rng = new Random();
    }


    /**
     * Sets the rng seed to make generation deterministic instead of random.
     * Files generated using the same seed will be exactly the same.
     * Useful for consistent testing.
     * 
     * @param seed
     *            the seed to set for the random number generator.
     */
    public void setSeed(long seed) {
        rng.setSeed(seed);
    }


    /**
     * Generates a file using the given setup.
     * 
     * @param ft
     *            the type of file being generated, either BINARY or ASCII.
     */
    public void generateFile(FileType ft) {
        DataOutputStream dos;
        try {
            dos = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(fname)));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("ERROR: File not found. See System.err");
            return; // Exit method early, dos is unusable already
        }

        try {
            if (ft == FileType.ASCII) {
                generateAsciiFile(dos);
            }
            else if (ft == FileType.BINARY) {
                generateBinaryFile(dos);
            }

            dos.flush();
            dos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println(
                "ERROR: IOException in file generation. See System.err");
        }
    }


    /**
     * Generates a file of random ASCII records.
     * Each record has a key representing a character 'A' to 'Z' and
     * a value represented by two spaces ' '.
     * 
     * @param dos
     *            The data output stream to write data to.
     * @throws IOException
     *             If writing to the file encounters an issue.
     */
    private void generateAsciiFile(DataOutputStream dos) throws IOException {
        int randKey;
        short blankVal = 8224; // Raw binary data representing a double-space
        int asciiOffset = 8257; // Offset to reach ASCII range starting at 'A'
        int range = 26; // Number of characters from 'A' to 'Z'.

        for (int i = 0; i < numBlocks; i++) {
            for (int j = 0; j < RECORDS_PER_BLOCK; j++) {
                randKey = Math.abs(rng.nextInt() % range) + asciiOffset;
                dos.writeShort(randKey); // Write the key
                dos.writeShort(blankVal); // Write the value
            }
        }
    }


    /**
     * Generates a file of random binary records.
     * Record keys and values are shorts in the range [1-30000).
     * 
     * @param dos
     *            The data output stream to write data to.
     * @throws IOException
     *             If writing to the file encounters an issue.
     */
    private void generateBinaryFile(DataOutputStream dos) throws IOException {
        int randKey;
        int randVal;
        int minRand = 1; // Minimum random short
        int range = 30000 - minRand; // Maximum random short - Minimum random
                                     // short

        for (int i = 0; i < numBlocks; i++) {
            for (int j = 0; j < RECORDS_PER_BLOCK; j++) {
                randKey = Math.abs(rng.nextInt() % range) + minRand;
                randVal = Math.abs(rng.nextInt() % range) + minRand;
                dos.writeShort((short)randKey);
                dos.writeShort((short)randVal);
            }
        }
    }
}
