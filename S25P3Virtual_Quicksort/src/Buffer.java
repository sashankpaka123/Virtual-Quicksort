import java.util.Arrays;

/**
 * The Buffer class represents a block of data that is stored temporarily in
 * memory.
 * It is part of a buffer pool mechanism that interacts with the disk file.
 * Each Buffer is associated with a block number and maintains a status
 * indicating
 * whether it has been modified (dirty) or not.
 * 
 * The Buffer class also provides methods to access and modify the block data,
 * as well as
 * marking the buffer as dirty when its contents are changed.
 * 
 * This class is designed to be used in conjunction with the BufferPool class
 * for efficient
 * management of file I/O operations using a caching mechanism.
 * 
 * @author Sashank Paka
 * @version 4/2/2025
 */
public class Buffer {

    private final int blockNum;
    private final byte[] data;
    private boolean isDirty;

    /**
     * Constructs a Buffer object with the given data and block number.
     * 
     * @param blockNum
     *            The block number associated with this buffer. This value is
     *            used to
     *            identify the specific block within the file that this buffer
     *            is managing.
     * @param data
     *            The data block represented by this buffer. The data is copied
     *            into a
     *            new array to ensure encapsulation and prevent external
     *            modification.
     */
    public Buffer(int blockNum, byte[] data) {
        this.blockNum = blockNum;
        this.data = Arrays.copyOf(data, data.length);
        this.isDirty = false;
    }


    /**
     * Retrieves the data contained within this buffer.
     * 
     * @return A reference to the byte array representing the buffer's data.
     */
    public byte[] getDataPointer() {
        return data;
    }


    /**
     * Retrieves the block number associated with this buffer.
     * 
     * @return The block number assigned to this buffer.
     */
    public int getBlockNum() {
        return blockNum;
    }


    /**
     * Checks if the buffer has been modified since it was last loaded or
     * written to disk.
     * 
     * @return true if the buffer is marked as dirty (modified), false
     *         otherwise.
     */
    public boolean isDirty() {
        return isDirty;
    }


    /**
     * Marks the buffer as dirty, indicating that its contents have been
     * modified
     * and need to be written back to disk before being discarded or replaced.
     */
    public void markDirty() {
        isDirty = true;
    }
}
