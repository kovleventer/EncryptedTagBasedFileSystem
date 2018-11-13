package com.kovlev.etbf.data;

import com.kovlev.etbf.util.ETBFSException;

import java.util.logging.Logger;

public class MasterBlockManager {
    public static final String SIGNATURE = "ETBFS container";
    public static final int SIGN_SIZE = 64;

    private Block masterBlock;

    private String signature;
    private long fileCount;
    private long tagCount;
    private long relationCount;
    private long blockCount;

    public static Block createEmptyMasterBlock() throws ETBFSException {
        Block b = new Block();
        b.putString(SIGNATURE, 0, SIGN_SIZE);
        b.position(SIGN_SIZE);
        b.putLong(0L); // No files
        b.putLong(0L); // No tags
        b.putLong(0L); // No files and tags
        b.putLong(4L); // 4 blocks by default
        return b;
    }

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

    private void updateMetaValues() {
        long[] values = { fileCount, tagCount, relationCount, blockCount };
        for (int i = 0; i < values.length; i++) {
            masterBlock.putLong(SIGN_SIZE + i * Long.BYTES, values[i]);
        }
    }
}
