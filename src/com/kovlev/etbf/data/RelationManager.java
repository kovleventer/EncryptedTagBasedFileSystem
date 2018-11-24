package com.kovlev.etbf.data;

import com.kovlev.etbf.filesystem.File;
import com.kovlev.etbf.filesystem.Tag;
import com.kovlev.etbf.util.BiMultimap;
import com.kovlev.etbf.filesystem.Relation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manages file tag associations
 */
public class RelationManager extends Manager<Relation> {

    private static final int FILE_ID_SIZE = Long.BYTES;
    private static final int TAG_ID_SIZE = Long.BYTES;

    private BiMultimap<File, Tag> relationship;
    private HashSet<Relation> rels;

    private TagManager tm;
    private FileManager fm;

    public RelationManager(long count, EncryptedDataManager edm) throws Exception {
        super(count, edm);
    }

    // Getters

    public Set<File> getFilesWithTag(String tagname) {
        return getFilesWithTag(tm.getTag(tagname));
    }

    public Set<File> getFilesWithTag(Tag tag) {
        return relationship.getKeys(tag);
    }

    public Set<Tag> getTagsOfFile(String filename) {
        return getTagsOfFile(fm.getFile(filename));
    }

    public Set<Tag> getTagsOfFile(File file) {
        return relationship.getValues(file);
    }

    /**
     * Assigns a tag to a file
     * @param filename The name of the file
     * @param tagname The name of the tag
     * @return True if the association did not exist before
     * @throws Exception
     */
    public boolean addRelation(String filename, String tagname) throws Exception {
        File f = fm.getFile(filename);
        Tag t = tm.getTag(tagname);
        long fileID = f.getID();
        long tagID = t.getID();
        Relation r = new Relation(fileID, tagID);
        if (rels.contains(r)) {
            return false;
        }

        long currentBlockIndex = add(r);
        int startIndex = getFirstAvailableIndex();

        edm.readBlock(currentBlockIndex).putLong(startIndex, fileID);
        edm.readBlock(currentBlockIndex).putLong(startIndex + FILE_ID_SIZE, tagID);
        edm.getMasterBlockManager().setRelationCount(rels.size());

        return true;
    }

    @Override
    protected long firstBlock() {
        return 3;
    }

    @Override
    protected int entrySize() {
        return FILE_ID_SIZE + TAG_ID_SIZE;
    }

    @Override
    protected Relation readInfoIntoEntry(Block b, int start) {
        long fileID = b.getLong(start);
        long tagID = b.getLong(start + FILE_ID_SIZE);

        return new Relation(fileID, tagID);
    }

    @Override
    protected void addEntryToEntryCollection(Relation entry) {
        System.out.println("RELATION " + fm.getFileByID(entry.getFileID()).getFilename() + "(" + entry.getFileID() + ") ~~~ " + tm.getTagByID(entry.getTagID()) + "(" + entry.getTagID() + ")");
        rels.add(entry);
        relationship.put(fm.getFileByID(entry.getFileID()), tm.getTagByID(entry.getTagID()));
    }

    @Override
    protected void createEntries() {
        tm = edm.getTagManager();
        fm = edm.getFileManager();
        rels = new HashSet<>();
        relationship = new BiMultimap<>();
    }

    @Override
    protected int getEntriesSize() {
        return rels.size();
    }
}
