package com.kovlev.etbf.data;

import com.kovlev.etbf.util.ETBFSException;

import java.util.logging.Logger;

/**
 * Manages the first block with index 0
 */
public class MasterBlockManager {
    public static final String SIGNATURE = "ETBFS container";
    public static final int SIGN_SIZE = 64;
    public static final long VERSION = 1;

    private Block masterBlock;

    private String signature;
    private long fileCount;
    private long tagCount;
    private long relationCount;
    private long blockCount;

    /**
     * Creates a new master block, with default values, signatures, etc
     * @return The created block
     * @throws ETBFSException
     */
    public static Block createEmptyMasterBlock() throws ETBFSException {
        Block b = new Block();
        b.putString(SIGNATURE, 0, SIGN_SIZE);
        b.position(SIGN_SIZE);
        b.putLong(0L); // No files
        b.putLong(0L); // No tags
        b.putLong(0L); // No files and tag association
        b.putLong(4L); // 4 blocks by default
        b.putLong(VERSION);
        return b;
    }

    /**
     * Constructor for master block manager
     * @param b The master block
     * @throws Exception
     */
    public MasterBlockManager(Block b) throws Exception {
        this.masterBlock = b;
        signature = b.getString(0, SIGN_SIZE);

        if (!SIGNATURE.equals(signature)) {
            throw new ETBFSException("Invalid Signature");
        }
        b.position(SIGN_SIZE);
        fileCount = b.getLong();
        tagCount = b.getLong();
        relationCount = b.getLong();
        blockCount = b.getLong();
        System.out.println("-----------------------------");
        System.out.println(signature);
        System.out.println(fileCount + " files; " + tagCount + " tags; " + relationCount + " relations; " + blockCount + " blocks");
        System.out.println("-----------------------------");
    }

    // Getters, setters

    public long getFileCount() {
        return fileCount;
    }

    public void setFileCount(long fileCount) {
        this.fileCount = fileCount;
        updateMetaValues();
    }

    public long getTagCount() {
        return tagCount;
    }

    public void setTagCount(long tagCount) {
        this.tagCount = tagCount;
        updateMetaValues();
    }

    public long getRelationCount() {
        return relationCount;
    }

    public void setRelationCount(long relationCount) {
        this.relationCount = relationCount;
        updateMetaValues();
    }

    public long getBlockCount() {
        return blockCount;
    }

    public void setBlockCount(long blockCount) {
        this.blockCount = blockCount;
        updateMetaValues();
    }

    /**
     * Writes all meta values back into the block
     */
    private void updateMetaValues() {
        long[] values = { fileCount, tagCount, relationCount, blockCount };
        for (int i = 0; i < values.length; i++) {
            masterBlock.putLong(SIGN_SIZE + i * Long.BYTES, values[i]);
        }
    }
}
