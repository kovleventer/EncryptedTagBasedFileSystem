package com.kovlev.etbf.data;

import com.kovlev.etbf.util.ETBFSException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Represents a block
 */
public class Block {
    public static final int DEFAULT_SIZE = /*1024*1024*4*/1024;
    public static final int POINTER_SIZE = DEFAULT_SIZE + 16; /* Should be Long.BYTES (8), but AES requires block length to be divisible with 16 */
    public static final int ENCRYPTED_SIZE = POINTER_SIZE + 16;

    private ByteBuffer data;
    private long pointer;
    private boolean modified = false;

    public Block() {
        this.data = ByteBuffer.wrap(new byte[POINTER_SIZE]);
        this.pointer = 0;
        modified = true;
    }

    public Block(byte[] data) {
        this.data = ByteBuffer.wrap(data);
        this.pointer = this.data.getLong(DEFAULT_SIZE);
    }

    public byte[] toArray() {
        return data.array();
    }

    public long getPointer() {
        return pointer;
    }

    public void setPointer(long pointer) {
        this.pointer = pointer;
        data.putLong(DEFAULT_SIZE, pointer);
        modified = true;
    }

    public void resetModifiedFlag() {
        modified = false;
    }

    public boolean isModified() {
        return modified;
    }

    // Delegate methods

    public long getLong() {
        return data.getLong();
    }

    public long getLong(int index) {
        return data.getLong(index);
    }

    public String getString(int index, int maxLength) {
        data.position(index);
        byte[] rawBuf = new byte[maxLength];
        data.get(rawBuf);
        String s = new String(rawBuf);
        int i = s.indexOf("\0");
        if (i == -1) {
            return s;
        }
        return s.substring(0, i);
    }

    public void putLong(long value) {
        data.putLong(value);
        modified = true;
    }

    public void putLong(int index, long value) {
        data.putLong(index, value);
        modified = true;
    }

    public void putString(String value, int index, int maxLength) throws ETBFSException {
        byte[] stringBytes = value.getBytes();
        if (stringBytes.length > maxLength) {
            throw new ETBFSException("String too long to be packed");
        }
        data.position(index);
        data.put(stringBytes);
        modified = true;
    }

    public void position(int position) {
        data.position(position);
    }

    public int get(int index) {
        return data.get(index);
    }

    public void put(int index, byte b) {
        data.put(index, b);
        modified = true;
    }

    public void dump(String pathname) {
        try (FileOutputStream fos = new FileOutputStream(pathname)) {
            fos.write(data.array());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
