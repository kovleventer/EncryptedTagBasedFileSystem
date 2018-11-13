package com.kovlev.etbf.data;

import com.kovlev.etbf.filesystem.Entry;

import java.util.*;

public abstract class Manager<E extends Entry> {
    protected Map<E, Long> blockIndexMap;
    protected List<Long> blocks;

    protected EncryptedDataManager edm;
    protected long count;
    protected final int ENTRIES_PER_BLOCK = Block.DEFAULT_SIZE / entrySize();

    protected abstract long firstBlock();

    protected abstract int entrySize();

    protected abstract E readInfoIntoEntry(Block b, int start);

    protected abstract void addEntryToEntryCollection(E entry);

    protected abstract void createEntries();

    protected abstract int getEntriesSize();

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

    protected int getFirstAvailableIndex() {
        return ((getEntriesSize() - 1) % ENTRIES_PER_BLOCK) * entrySize();
    }

    protected boolean isExpansionNeeded(int size) {
        if (ENTRIES_PER_BLOCK == 1) {
            return size != 1;
        }
        return size % ENTRIES_PER_BLOCK == 1 && size > ENTRIES_PER_BLOCK;
    }
}
