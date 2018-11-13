package com.kovlev.etbf.filesystem;

public class Tag extends Entry {
    private String tagString;
    private long id;

    public Tag(String tagString, long id) {
        this.tagString = tagString;
        this.id = id;
    }

    @Override
    public String toString() {
        return tagString;
    }

    @Override
    public int hashCode() {
        return tagString.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tag)) return false;
        return tagString.equals(((Tag) o).tagString);
    }

    public void setString(String newTagString) {
        tagString = newTagString;
    }


    public String getTagString() {
        return tagString;
    }

    public long getID() {
        return id;
    }
}
