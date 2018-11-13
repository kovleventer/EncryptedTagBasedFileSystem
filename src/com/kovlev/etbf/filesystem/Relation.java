package com.kovlev.etbf.filesystem;

public class Relation extends Entry {
    private long fileID;
    private long tagID;

    public Relation(long fileID, long tagID) {
        this.fileID = fileID;
        this.tagID = tagID;
    }

    @Override
    public int hashCode() {
        return (int)((fileID) ^ (fileID >> 32) ^ tagID ^ (tagID >> 32));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Relation)) return false;
        Relation r = (Relation)o;
        return r.fileID == fileID && r.tagID == tagID;
    }

    public long getFileID() {
        return fileID;
    }

    public long getTagID() {
        return tagID;
    }
}
