import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Demonstrates sorting of two records and file copying.
 * Uses ByteBuffer for record manipulation.
 * Also demonstrates file copying using Java NIO file operations.
 * 
 * @author Sashank
 * @version 2025-04-02
 */
public class Examples {

    /**
     * The length of a single record in bytes. Each record consists of two
     * shorts
     * (2 bytes each), making the total length 4 bytes.
     */
    final static int REC_BYTES_LENGTH = 4;

    /**
     * Demonstrates sorting and copying operations.
     * 
     * @param args
     *            Unused.
     */
    public static void main(String[] args) {
        byte[] someTwoRecs = { 3, 4, 8, 8, 2, 5, 9, 9 };
        sortTwoRecords(someTwoRecs);

        for (byte b : someTwoRecs) {
            System.out.print(b + " ");
        }
        System.out.println();

        Path src = Paths.get("src", "Examples.java");
        Path dest = Paths.get("src", "ExamplesCopy.java");

        try {
            Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File copied successfully.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Sorts two records in the given byte array.
     * 
     * @param recs
     *            Byte array containing two records.
     */
    public static void sortTwoRecords(byte[] recs) {
        assert recs.length == 2 * REC_BYTES_LENGTH;

        ByteBuffer bf = ByteBuffer.wrap(recs);
        short key0 = bf.getShort();
        short key1 = bf.getShort(REC_BYTES_LENGTH);

        if (key1 < key0) {
            byte[] swapArea = new byte[REC_BYTES_LENGTH];
            System.arraycopy(recs, 0, swapArea, 0, REC_BYTES_LENGTH);
            System.arraycopy(recs, REC_BYTES_LENGTH, recs, 0, REC_BYTES_LENGTH);
            System.arraycopy(swapArea, 0, recs, REC_BYTES_LENGTH,
                REC_BYTES_LENGTH);
        }
    }
}
