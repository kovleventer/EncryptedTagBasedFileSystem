package com.kovlev.etbf.data;

import com.kovlev.etbf.filesystem.File;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class FileManager extends Manager<File> {

    private static final int FILENAME_SIZE = 256;
    private static final int POINTER_SIZE = Long.BYTES;
    private static final int FILESIZE_SIZE = Long.BYTES;
    private static final int ID_SIZE = Long.BYTES;

    private long maxID;

    private HashMap<String, File> filesByFilename;
    private HashMap<Long, File> filesByID;

    public FileManager(long fileCount, EncryptedDataManager edm) throws Exception {
        super(fileCount, edm);
    }

    public boolean addFile(String filename) throws Exception {
        if (filesByFilename.containsKey(filename)) {
            return false;
        }

        long newPointer = edm.addNewBlock();
        maxID++;
        File f = new File(filename, newPointer, 0, maxID, edm);
        long currentBlockIndex = add(f);
        int startIndex = getFirstAvailableIndex();
        System.out.println("Added file: " + filename + " to block " + newPointer + "; managed in block " + currentBlockIndex + " (start index:) " + startIndex + " max: " + maxID);
        edm.readBlock(currentBlockIndex).putString(filename, startIndex, FILENAME_SIZE);
        edm.readBlock(currentBlockIndex).putLong(startIndex + FILENAME_SIZE, newPointer);
        edm.readBlock(currentBlockIndex).putLong(startIndex + FILENAME_SIZE + POINTER_SIZE, 0);
        edm.readBlock(currentBlockIndex).putLong(startIndex + FILENAME_SIZE + POINTER_SIZE + FILESIZE_SIZE, maxID);
        edm.getMasterBlockManager().setFileCount(filesByFilename.size());

        return true;
    }

    public void modifyFileSize(File file, long newFileSize) throws Exception {
        long index = blockIndexMap.get(file);
        long blockIndex = index / ENTRIES_PER_BLOCK;
        int indexInBlock = (int) (index % ENTRIES_PER_BLOCK);
        long blockToWrite = blocks.get((int) blockIndex);

        System.out.println("Filesize modified: " + newFileSize);

        edm.readBlock(blockToWrite).putLong(indexInBlock * entrySize() + FILENAME_SIZE + POINTER_SIZE, newFileSize);
    }

    public void modifyFileName(File f, String newName) {
        // TODO
    }

    public File getFile(String filename) {
        if (filesByFilename.containsKey(filename)) {
            return filesByFilename.get(filename);
        }
        return null;
    }

    public File getFileByID(long fileID) {
        if (filesByID.containsKey(fileID)) {
            return filesByID.get(fileID);
        }
        return null;
    }

    public Collection<File> getFiles() {
        return filesByID.values();
    }

    @Override
    protected long firstBlock() {
        return 1;
    }

    @Override
    protected int entrySize() {
        return FILENAME_SIZE + POINTER_SIZE + FILESIZE_SIZE + ID_SIZE;
    }

    @Override
    protected File readInfoIntoEntry(Block b, int start) {
        String fileName = b.getString(start, FILENAME_SIZE);
        long blockStart = b.getLong(start + FILENAME_SIZE);
        long fileSize = b.getLong(start + FILENAME_SIZE + POINTER_SIZE);
        long id = b.getLong(start + FILENAME_SIZE + POINTER_SIZE + FILESIZE_SIZE);

        if (id > maxID) maxID = id;
        System.out.println("FILENAME: " + fileName + " " + maxID);

        return new File(fileName, blockStart, fileSize, id, edm);
    }

    @Override
    protected void addEntryToEntryCollection(File entry) {
        filesByFilename.put(entry.getFilename(), entry);
        filesByID.put(entry.getID(), entry);
    }

    @Override
    protected void createEntries() {
        filesByFilename = new HashMap<>();
        filesByID = new HashMap<>();
        maxID = 0;
    }

    @Override
    protected int getEntriesSize() {
        return filesByFilename.size();
    }
}
