import java.io.IOException;

/**
 * Implements a Quicksort algorithm for sorting a binary file using
 * a buffer pool. All file accesses are mediated through the buffer pool
 * to simulate virtual memory behavior.
 * 
 * @author Sashank
 * @version 4/11/2025
 */
public class VirtualSorter {

    private static final int RECORD_SIZE = 4;

    /**
     * Recursively sorts a virtual file using Quicksort via a buffer pool.
     *
     * @param bufferPool
     *            the buffer pool used for file access
     * @param left
     *            left index of the region to sort
     * @param right
     *            right index of the region to sort
     * @throws IOException
     *             if file I/O fails
     */
    public static void sort(BufferPool bufferPool, int left, int right)
        throws IOException {
        while (left < right) {
            int pivot = partition(bufferPool, left, right);

            if (pivot == left && pivot == right) {
                return;
            }

            if (pivot - left < right - pivot) {
                sort(bufferPool, left, pivot - 1);
                left = pivot + 1;
            }
            else {
                sort(bufferPool, pivot + 1, right);
                right = pivot - 1;
            }
        }
    }


    /**
     * Partitions the array around a pivot element.
     *
     * @param bufferPool
     *            the buffer pool used for file access
     * @param left
     *            left index of partition
     * @param right
     *            right index of partition
     * @return final index of pivot
     * @throws IOException
     *             if file I/O fails
     */
    private static int partition(BufferPool bufferPool, int left, int right)
        throws IOException {
        short pivotKey = readKey(bufferPool, left);
        int i = left + 1;
        int j = right;

        while (i <= j) {
            while (i <= right && readKey(bufferPool, i) < pivotKey) {
                i++;
            }
            while (j >= left + 1 && readKey(bufferPool, j) > pivotKey) {
                j--;
            }
            if (i < j) {
                swap(bufferPool, i, j);
                i++;
                j--;
            }
            else {
                break;
            }
        }

        swap(bufferPool, left, j);
        return j;
    }


    /**
     * Reads the 2-byte key from the record at the given index.
     *
     * @param bufferPool
     *            the buffer pool
     * @param index
     *            the record index
     * @return the key as a short
     * @throws IOException
     *             if file I/O fails
     */
    private static short readKey(BufferPool bufferPool, int index)
        throws IOException {
        int pos = index * RECORD_SIZE;
        byte[] keyBytes = new byte[2];
        bufferPool.getbytes(keyBytes, 2, pos);
        return (short)(((keyBytes[0] & 0xFF) << 8) | (keyBytes[1] & 0xFF));
    }


    /**
     * Swaps the two 4-byte records at the given indexes.
     *
     * @param bufferPool
     *            the buffer pool
     * @param i
     *            first record index
     * @param j
     *            second record index
     * @throws IOException
     *             if file I/O fails
     */
    private static void swap(BufferPool bufferPool, int i, int j)
        throws IOException {
        if (i == j) {
            return;
        }

        int pos1 = i * RECORD_SIZE;
        int pos2 = j * RECORD_SIZE;

        byte[] temp1 = new byte[4];
        byte[] temp2 = new byte[4];

        bufferPool.getbytes(temp1, 4, pos1);
        bufferPool.getbytes(temp2, 4, pos2);

        bufferPool.insert(temp2, 4, pos1);
        bufferPool.insert(temp1, 4, pos2);
    }
}
