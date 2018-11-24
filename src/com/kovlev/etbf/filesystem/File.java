package com.kovlev.etbf.filesystem;

import com.kovlev.etbf.data.Block;
import com.kovlev.etbf.data.EncryptedDataManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a file
 */
public class File extends Entry {
    protected String filename;
    protected long fileSize;
    protected long id;
    protected List<Long> blocks;

    private EncryptedDataManager edm;
    private InputStream is;
    private OutputStream os;
    private long wPos, rPos;

    /**
     * Constructs a file
     * @param filename Ne name of the file
     * @param firstBlockID The id of the first block, on which the file data starts (this hold true even for files with a size of 0)
     * @param fileSize The size of the file
     * @param id The id of the file (in order to speed up navigation)
     * @param edm
     */
    public File(String filename, long firstBlockID, long fileSize, long id, EncryptedDataManager edm) {
        this.filename = filename;
        this.fileSize = fileSize;
        blocks = new ArrayList<>();
        blocks.add(firstBlockID);
        this.edm = edm;
        this.id = id;

        is = new BlockInputStream();
        os = new BlockOutputStream();
        wPos = 0;
        rPos = 0;
    }

    // Getters, setters

    public String getFilename() {
        return filename;
    }

    public void setFilename(String newFilename) {
        filename = newFilename;
    }

    public long getID() {
        return id;
    }

    public long getFileSize() {
        return fileSize;
    }

    public InputStream getIS() {
        return is;
    }

    public OutputStream getOS() {
        return os;
    }

    /**
     * Input stream, from which the user can read bytes from the blocks seamlessly
     */
    private class BlockInputStream extends InputStream {

        @Override
        public int read() throws IOException {
            if (rPos >= fileSize) {
                return -1;
            }

            int blockIndex = getBlockIndexByPosition(rPos);

            try {
                for (int i = blocks.size(); i <= blockIndex; i++) {
                    long pointer = edm.readBlock(blocks.get(blocks.size() - 1)).getPointer();
                    if (pointer == 0) {
                        return -1;
                    }
                    blocks.add(pointer);
                }

                int ret = edm.readBlock(blocks.get(blockIndex)).get(getIndexInBlock(rPos));
                rPos++;
                return ret;
            } catch (Exception e) {
                return -1;
            }
        }

        @Override
        public void close() throws IOException {
            rPos = 0;
            super.close();
        }

        @Override
        public int available() throws IOException {
            return rPos == fileSize ? 0 : 1;
        }
    }

    /**
     * Custom outputstream, which hides new block allocations
     * Also supports bulk write
     */
    private class BlockOutputStream extends OutputStream {

        private boolean bulk = false;

        @Override
        public void write(int i) throws IOException {
            try {
                if (getBlockIndexByPosition(wPos) >= blocks.size()) {
                    long currentPointer = edm.readBlock(blocks.get(blocks.size() - 1)).getPointer();
                    if (currentPointer == 0) {
                        long newBlockIndex = edm.addNewBlock();
                        edm.readBlock(blocks.get(blocks.size() - 1)).setPointer(newBlockIndex);
                        blocks.add(newBlockIndex);
                    } else {
                        blocks.add(currentPointer);
                    }
                }
                edm.readBlock(blocks.get(getBlockIndexByPosition(wPos))).put(getIndexInBlock(wPos), (byte)i);
                if (wPos == fileSize) {
                    fileSize++;
                    if (!bulk) {
                        edm.getFileManager().modifyFileSize(File.this, fileSize);
                    }
                }
                wPos++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void write(byte[] bytes, int start, int end) throws IOException {
            bulk = true;
            super.write(bytes, start, end);
            bulk = false;
            fileSize = wPos;
            try {
                edm.getFileManager().modifyFileSize(File.this, fileSize);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void close() throws IOException {
            wPos = 0;
            super.close();
        }
    }

    /**
     * @param position File cursor position
     * @return The currently active block's index in the block list (this must be translated to block id)
     */
    private int getBlockIndexByPosition(long position) {
        return (int) (position / Block.DEFAULT_SIZE);
    }

    /**
     * @param position File cursor position
     * @return The currently active byte's position in the active block
     */
    private int getIndexInBlock(long position) {
        return (int) (position % Block.DEFAULT_SIZE);
    }

    @Override
    public int hashCode() {
        return filename.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof File)) return false;
        File f = (File) o;
        return f.filename.equals(filename);
    }
}
