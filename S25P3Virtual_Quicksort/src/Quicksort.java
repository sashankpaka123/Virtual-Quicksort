import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * The class containing the main method.
 *
 * @author Sashank Paka
 * @version 4/2/2025
 */

// On my honor:
//
// - I have not used source code obtained from another student,
// or any other unauthorized source, either modified or
// unmodified.
//
// - All source code and documentation used in my program is
// either my original work, or was derived by me from the
// source code published in the textbook for this course.
//
// - I have not discussed coding details about this project with
// anyone other than my partner (in the case of a joint
// submission), instructor, ACM/UPE tutors or the TAs assigned
// to this course. I understand that I may discuss the concepts
// of this program with other students, and that another student
// may help me debug my program so long as neither of us writes
// anything during the discussion or modifies any computer file
// during the discussion. I have violated neither the spirit nor
// letter of this restriction.

public class Quicksort {

    private static final int RECORD_SIZE = 4;
    private static BufferPool bufferPool;

    /**
     * The main method that initializes the BufferPool, sorts the file, and
     * writes statistics.
     *
     * @param args
     *            The command-line arguments: data-file-name, number of buffers,
     *            and statistics file name.
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Usage: java Quicksort "
                + "<data-file-name> <numb-buffers> <stat-file-name>");
            return;
        }

        String dataFileName = args[0];
        int numBuffers = Integer.parseInt(args[1]);
        String statFileName = args[2];

        try {
            File dataFile = new File(dataFileName);
            bufferPool = new BufferPool(dataFile, numBuffers);

            long startTime = System.currentTimeMillis();
            VirtualSorter.sort(bufferPool, 0, (int)(dataFile.length()
                / RECORD_SIZE) - 1);
            long endTime = System.currentTimeMillis();

            bufferPool.flush();
            writeStatistics(statFileName, dataFileName, endTime - startTime);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Writes sorting statistics to the output file using RandomAccessFile.
     *
     * @param statFileName
     *            The name of the statistics file to write to.
     * @param dataFileName
     *            The name of the data file being sorted.
     * @param timeTaken
     *            The time taken for sorting, in milliseconds.
     * @throws IOException
     *             If an I/O error occurs.
     */
    private static void writeStatistics(
        String statFileName,
        String dataFileName,
        long timeTaken)
        throws IOException {

        try (RandomAccessFile raf = new RandomAccessFile(statFileName, "rw")) {
            raf.seek(raf.length()); // Append to the end of the file

            String stats = String.format(
                "Data File: %s\nCache Hits: %d\nDisk Reads: %d\nDisk Writes: "
                    + "%d\nTime Taken: %d ms\n------------------------------\n",
                dataFileName, bufferPool.getCacheHits(), bufferPool
                    .getDiskReads(), bufferPool.getDiskWrites(), timeTaken);

            raf.write(stats.getBytes());
        }
    }
}
