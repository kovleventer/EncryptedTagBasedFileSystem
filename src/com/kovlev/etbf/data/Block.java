package com.kovlev.etbf.data;

import com.kovlev.etbf.util.ETBFSException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Represents a block
 * A block is an atomic entity which can be read from and written to harddrive
 * Each block has a data section and a pointer to the next block
 */
public class Block {
    // Size of usable spce
    public static final int DEFAULT_SIZE = 400/*1024*/;

    // Size with pointer to next block
    public static final int POINTER_SIZE = DEFAULT_SIZE + 16; /* Should be Long.BYTES (8), but AES requires block length to be divisible with 16 */

    // Encrypted size
    public static final int ENCRYPTED_SIZE = POINTER_SIZE + 16;

    private ByteBuffer data;
    private long pointer;
    private boolean modified = false;

    /**
     * Creates a new empty block
     */
    public Block() {
        this.data = ByteBuffer.wrap(new byte[POINTER_SIZE]);
        this.pointer = 0;
        modified = true;
    }

    /**
     * Creates a Block with raw data in it
     * @param data Raw data, a ByteBuffer will be wrapped over it
     */
    public Block(byte[] data) {
        this.data = ByteBuffer.wrap(data);
        this.pointer = this.data.getLong(DEFAULT_SIZE);
    }

    /**
     * Converts the block back into raw data in order to save to hardrive
     * @return The raw data
     */
    public byte[] toArray() {
        return data.array();
    }

    /**
     * Getter for pointer
     * @return The pointer
     */
    public long getPointer() {
        return pointer;
    }

    /**
     * Setter for pointer
     * @param pointer The new pointer value
     */
    public void setPointer(long pointer) {
        this.pointer = pointer;
        data.putLong(DEFAULT_SIZE, pointer);
        modified = true;
    }

    /**
     * Marks the block as unmodified (for example after a flush)
     */
    public void resetModifiedFlag() {
        modified = false;
    }

    /**
     * Getter for modified flag
     * @return Modified flag
     */
    public boolean isModified() {
        return modified;
    }

    // Delegate methods for ByteBuffer
    // Setters also set the modified flag, which indicates that the block must be written back to harddrive

    public long getLong() {
        return data.getLong();
    }

    public long getLong(int index) {
        return data.getLong(index);
    }

    /**
     * Retrieves a String from the ByteBuffer
     * @param index Index of the first byte of the String
     * @param maxLength The maximal length of the String
     * @return The String value
     */
    public String getString(int index, int maxLength) {
        data.position(index);
        byte[] rawBuf = new byte[maxLength];
        data.get(rawBuf);
        int i;
        for (i = 0; i < rawBuf.length && ((rawBuf[i] & 0xFF) != 0); i++) { }
        String s = new String(rawBuf, 0, i);
        return s;
    }

    public void putLong(long value) {
        data.putLong(value);
        modified = true;
    }

    public void putLong(int index, long value) {
        data.putLong(index, value);
        modified = true;
    }

    /**
     * Puts a String into the ByteBuffer
     * Since this method is not provided by ByteBuffer, own implementation is used
     * @param value The String to put into ByteBuffer
     * @param index Index of the first byte of the string in ByteBuffer
     * @param maxLength Maximal length of the String (will be used for a check)
     * @throws ETBFSException In case our String is too long
     */
    public void putString(String value, int index, int maxLength) throws ETBFSException {
        byte[] fill = new byte[maxLength];
        byte[] stringBytes = value.getBytes();
        if (stringBytes.length > maxLength) {
            throw new ETBFSException("String too long to be packed");
        }
        data.position(index);
        data.put(fill);
        data.position(index);
        data.put(stringBytes);
        modified = true;
    }

    public void position(int position) {
        data.position(position);
    }

    public int get(int index) {
        return data.get(index) & 0xFF;
    }

    public void put(int index, byte b) {
        data.put(index, b);
        modified = true;
    }

    /**
     * Dumps a block into a file
     * @param pathname The file to save the dumped bytes
     */
    public void dump(String pathname) {
        try (FileOutputStream fos = new FileOutputStream(pathname)) {
            fos.write(data.array());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
