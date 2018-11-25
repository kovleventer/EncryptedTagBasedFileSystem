package com.kovlev.etbf.data;

import com.kovlev.etbf.filesystem.Tag;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the database table for tags
 */
public class TagManager extends Manager<Tag> {

    private static final int TAG_SIZE = 64;
    private static final int ID_SIZE = Long.BYTES;

    private long maxID;

    private Map<String, Tag> tagsByName;
    public Map<Long, Tag> tagsByID;

    public TagManager(long count, EncryptedDataManager edm) throws Exception {
        super(count, edm);
    }

    /**
     * Adds a new tag
     * @param tagname The name of the tag
     * @return True if the tag did not exist before
     * @throws Exception
     */
    public boolean addTag(String tagname) throws Exception {
        if (tagsByName.containsKey(tagname)) {
            return false;
        }

        maxID++;
        long blockIndex = add(new Tag(tagname, maxID));
        int indexInBlock = getFirstAvailableIndex();

        edm.readBlock(blockIndex).putString(tagname, indexInBlock, TAG_SIZE);
        edm.readBlock(blockIndex).putLong(indexInBlock + TAG_SIZE, maxID);

        edm.getMasterBlockManager().setTagCount(tagsByName.size());

        return true;
    }

    // Getters

    public Tag getTag(String name) {
        if (!tagsByName.containsKey(name)) return null;
        return tagsByName.get(name);
    }

    public Tag getTagByID(long tagID) {
        if (!tagsByID.containsKey(tagID)) return null;
        return tagsByID.get(tagID);
    }

    public Collection<Tag> getTags() {
        return tagsByName.values();
    }

    @Override
    protected long firstBlock() {
        return 2;
    }

    @Override
    protected int entrySize() {
        return TAG_SIZE + ID_SIZE;
    }

    @Override
    protected Tag readInfoIntoEntry(Block b, int start) {
        String s = b.getString(start, TAG_SIZE);
        long id = b.getLong(start + TAG_SIZE);
        return new Tag(s, id);
    }

    @Override
    protected void addEntryToEntryCollection(Tag entry) {
        System.out.println("TAG: " + entry + " " + entry.getID());
        tagsByName.put(entry.getTagString(), entry);
        tagsByID.put(entry.getID(), entry);
        if (entry.getID() > maxID) maxID = entry.getID();
    }

    @Override
    protected void createEntries() {
        tagsByName = new HashMap<>();
        tagsByID = new HashMap<>();
        maxID = 0;
    }

    @Override
    protected int getEntriesSize() {
        return tagsByName.size();
    }
}
