package com.kovlev.etbf.data;

import com.kovlev.etbf.util.ETBFSException;
import com.kovlev.etbf.util.EncryptionUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Manages the encrypted database
 */
public class EncryptedDataManager {

    private SortedMap<Long, Block> blockCache;

    private FileManager fileManager;
    private MasterBlockManager masterBlockManager;
    private TagManager tagManager;
    private RelationManager relationManager;

    private RandomAccessFile fileCursor;

    private final Path path;
    private final String password;

    /**
     * Opens or creates a new container
     * @param filepath The path of the container
     * @param password The password as String
     * @throws Exception
     */
    public EncryptedDataManager(String filepath, String password) throws Exception {
        blockCache = new TreeMap<>();
        path = Paths.get(filepath);
        this.password = password;

        if (Files.exists(path)) {
            openEncryptedContainer();
        } else {
            createEncryptedContainer();
        }
    }

    /**
     * Creates a new container and opens it
     * @throws Exception
     */
    private void createEncryptedContainer() throws Exception {
        Block masterBlock = MasterBlockManager.createEmptyMasterBlock();
        blockCache.put(0L, masterBlock);
        for (long i = 1; i <= 3; i++) {
            blockCache.put(i, new Block());
        }

        openEncryptedContainer();
    }

    /**
     * Opens an existing container
     * @throws Exception
     */
    private void openEncryptedContainer() throws Exception {
        fileCursor = new RandomAccessFile(path.toFile(), "rwd");
        masterBlockManager = new MasterBlockManager(readBlock(0));
        fileManager = new FileManager(masterBlockManager.getFileCount(), this);
        tagManager = new TagManager(masterBlockManager.getTagCount(), this);
        relationManager = new RelationManager(masterBlockManager.getRelationCount(), this);
    }

    /**
     * Adds a new block to the container
     * @return The new block's id
     */
    public long addNewBlock() {
        long newBlockIndex = masterBlockManager.getBlockCount();
        masterBlockManager.setBlockCount(newBlockIndex + 1);
        Block b = new Block();
        blockCache.put(newBlockIndex, b);
        return newBlockIndex;
    }

    /**
     * Encrypts a block and writes it back to the harddrive
     * @param blockId The ID of the block
     * @throws Exception
     */
    public void writeBlock(long blockId) throws Exception {
        if (!blockCache.containsKey(blockId)) {
            throw new ETBFSException("Block not yet loaded");
        }

        fileCursor.seek(blockId * Block.ENCRYPTED_SIZE);
        byte[] decrypted = blockCache.get(blockId).toArray();
        byte[] encrypted = EncryptionUtils.encrypt(decrypted, password);
        fileCursor.write(encrypted);

        if (blockId == 1) {
            EncryptionUtils.decrypt(encrypted, password);
        }
    }

    /**
     * Reads a block from the harddrive or from the cache, if it is already loaded
     * @param blockId The ID of the block
     * @return The readed block
     * @throws Exception
     */
    public Block readBlock(long blockId) throws Exception {
        if (!blockCache.containsKey(blockId)) {
            fileCursor.seek(blockId * Block.ENCRYPTED_SIZE);
            byte[] encrypted = new byte[Block.ENCRYPTED_SIZE];
            fileCursor.read(encrypted);
            byte[] decrypted = EncryptionUtils.decrypt(encrypted, password);
            blockCache.put(blockId, new Block(decrypted));
        }
        return blockCache.get(blockId);
    }

    // Getters for managers

    public FileManager getFileManager() {
        return fileManager;
    }

    public MasterBlockManager getMasterBlockManager() {
        return masterBlockManager;
    }

    public TagManager getTagManager() {
        return tagManager;
    }

    public RelationManager getRelationManager() {
        return relationManager;
    }

    /**
     * Writes all modified cached blocks to the harddrive
     * @throws Exception
     */
    public void flush() throws Exception {
        int cnt = 0;
        long time = System.currentTimeMillis();
        for (Map.Entry<Long, Block> e : blockCache.entrySet()) {
            if (e.getValue().isModified()) {
                writeBlock(e.getKey());
                e.getValue().resetModifiedFlag();
                System.out.println("WRITING BLOCK: " + e.getKey());
                cnt++;
            }
        }
        time = System.currentTimeMillis() - time;
        double speed = (double)cnt * Block.ENCRYPTED_SIZE / ((double)time / 1000 * 1024);
        System.out.println(speed + " kb/s");
    }

    /**
     * Closes the container by flushing it and closing the whole file
     * @throws Exception
     */
    public void close() throws Exception {
        flush();
        fileCursor.close();
    }
}
