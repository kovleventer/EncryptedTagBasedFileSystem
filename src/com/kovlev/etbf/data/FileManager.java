package com.kovlev.etbf.data;

import com.kovlev.etbf.filesystem.File;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Manages file list
 */
public class FileManager extends Manager<File> {

    private static final int FILENAME_SIZE = 256;
    private static final int POINTER_SIZE = Long.BYTES;
    private static final int FILESIZE_SIZE = Long.BYTES;
    private static final int ID_SIZE = Long.BYTES;

    private long maxID;

    private HashMap<String, File> filesByFilename;
    private HashMap<Long, File> filesByID;

    /**
     * Constructor
     * @param fileCount How many files do we have
     * @param edm Main manager for accessing blocks
     * @throws Exception
     */
    public FileManager(long fileCount, EncryptedDataManager edm) throws Exception {
        super(fileCount, edm);
    }

    /**
     * Creates a new file in the container
     * @param filename The name of the new file
     * @return True if the file did not exist before
     * @throws Exception
     */
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

    /**
     * Modifies the file size of a given file and writes this information back to the database
     * @param file The file to modify
     * @param newFileSize New file size
     * @throws Exception
     */
    public void modifyFileSize(File file, long newFileSize) throws Exception {
        long index = blockIndexMap.get(file);
        long blockIndex = index / ENTRIES_PER_BLOCK;
        int indexInBlock = (int) (index % ENTRIES_PER_BLOCK);
        long blockToWrite = blocks.get((int) blockIndex);

        System.out.println("Filesize modified: " + newFileSize);

        edm.readBlock(blockToWrite).putLong(indexInBlock * entrySize() + FILENAME_SIZE + POINTER_SIZE, newFileSize);
    }

    /**
     * Modifies the name of a given file
     * @param f The file to modify
     * @param newName The new filename
     * @throws Exception
     */
    public void modifyFileName(File f, String newName) throws Exception {
        filesByFilename.remove(f.getFilename());
        long index = blockIndexMap.get(f);
        blockIndexMap.remove(f);

        f.setFilename(newName);
        filesByFilename.put(newName, f);

        blockIndexMap.put(f, index);

        long blockIndex = index / ENTRIES_PER_BLOCK;
        int indexInBlock = (int) (index % ENTRIES_PER_BLOCK);
        long blockToWrite = blocks.get((int) blockIndex);

        System.out.println("Filename modified: " + newName);

        edm.readBlock(blockToWrite).putString(newName, indexInBlock * entrySize(), FILENAME_SIZE);
    }

    // Getters

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
        System.out.println("FILENAME: " + fileName + " " + id);

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
