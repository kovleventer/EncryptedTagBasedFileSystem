package com.kovlev.etbf.data;

import com.kovlev.etbf.filesystem.Entry;

import java.util.*;

/**
 * Abstract class to manage database "tables"
 * @param <E> Stored record
 */
public abstract class Manager<E extends Entry> {
    protected Map<E, Long> blockIndexMap;
    protected List<Long> blocks;

    protected EncryptedDataManager edm;
    protected long count;
    protected final int ENTRIES_PER_BLOCK = Block.DEFAULT_SIZE / entrySize();

    /**
     * Index of the first block
     * @return 1, 2, or 3 depending on implementation
     */
    protected abstract long firstBlock();

    /**
     * @return The size of a record
     */
    protected abstract int entrySize();

    /**
     * Reads an entry from a block
     * @param b The block to read from
     * @param start Offset in bytes
     * @return The Entry initialized with values from the block
     */
    protected abstract E readInfoIntoEntry(Block b, int start);

    /**
     * Registers an entry into various collections
     * @param entry The entry to register
     */
    protected abstract void addEntryToEntryCollection(E entry);

    /**
     * Extended constructor, since reading in is done by Manager's constructor
     * And the specific collections would not exist in inherited classes yet
     */
    protected abstract void createEntries();

    /**
     * @return How many entries do we have
     */
    protected abstract int getEntriesSize();

    /**
     * Constructor, which reads in a specific table of our database
     * @param count How many entries do we have stored
     * @param edm Main manager for accessing blocks
     * @throws Exception
     */
    public Manager(long count, EncryptedDataManager edm) throws Exception {
        this.edm = edm;
        this.count = count;
        createEntries();
        blockIndexMap = new HashMap<>();
        blocks = new ArrayList<>();


        int blocksUsed = (int)Math.ceil((double)count / ENTRIES_PER_BLOCK);
        if (blocksUsed == 0) {
            blocks.add(firstBlock());
        }

        long currentBlockIndex = firstBlock();
        for (int i = 0; i < blocksUsed; i++) {
            Block b = edm.readBlock(currentBlockIndex);
            long entriesInBlock = i == blocksUsed - 1 ? count % ENTRIES_PER_BLOCK : ENTRIES_PER_BLOCK;
            if (entriesInBlock == 0) entriesInBlock = ENTRIES_PER_BLOCK;
            for (int j = 0; j < entriesInBlock; j++) {
                E entry = readInfoIntoEntry(b, j * entrySize());
                addEntryToEntryCollection(entry);
                blockIndexMap.put(entry, (long) blockIndexMap.size());
            }
            blocks.add(currentBlockIndex);

            currentBlockIndex = edm.readBlock(currentBlockIndex).getPointer();
        }

    }

    /**
     * Adds an entry to
     *  a) The specific collections
     *  b) The end of the table
     * @param entry The entry to add
     * @return The Block to which the entry was added to
     * @throws Exception
     */
    protected long add(E entry) throws Exception {
        addEntryToEntryCollection(entry);
        blockIndexMap.put(entry, (long) blockIndexMap.size());
        long currentBlockIndex = blocks.get(blocks.size() - 1);
        if (isExpansionNeeded(getEntriesSize())) {
            long newBlockIndex = edm.addNewBlock();
            edm.readBlock(currentBlockIndex).setPointer(newBlockIndex);
            currentBlockIndex = newBlockIndex;
            blocks.add(currentBlockIndex);
        }
        return currentBlockIndex;
    }

    /**
     * @return The position of the first available byte in the last used block
     */
    protected int getFirstAvailableIndex() {
        return ((getEntriesSize() - 1) % ENTRIES_PER_BLOCK) * entrySize();
    }

    /**
     * Checks whether a new block should be added
     * @param size How many entries do we have right now
     * @return True if a new block must be allocated
     */
    protected boolean isExpansionNeeded(int size) {
        if (ENTRIES_PER_BLOCK == 1) {
            return size != 1;
        }
        return size % ENTRIES_PER_BLOCK == 1 && size > ENTRIES_PER_BLOCK;
    }
}
