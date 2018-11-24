package com.kovlev.etbf.new_new_ui;

import com.kovlev.etbf.data.EncryptedDataManager;
import com.kovlev.etbf.filesystem.File;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FileListModel extends AbstractListModel {

    private EncryptedDataManager edm;

    public FileListModel(EncryptedDataManager edm) {
        this.edm = edm;
        cachedTags = new ArrayList<>();
        currentSelection = new ArrayList<>();
        currentSelection.addAll(edm.getFileManager().getFiles());
    }

    private ArrayList<File> currentSelection;

    /**
     * Getter for acessing a file on a specific index
     * @param idx The index to access
     * @return The file
     */
    public File fileAt(int idx) {
        return currentSelection.get(idx);
    }

    private ArrayList<String> cachedTags;

    /**
     * Updates the selection, because the file list changed, but our selection did not
     */
    public void updateSelection() {
        updateSelection(cachedTags);
    }

    /**
     * Updates the selection, because the selection has changed
     * @param tags The tags of the new selection
     */
    public void updateSelection(ArrayList<String> tags) {
        Set<File> current = new HashSet<>(edm.getFileManager().getFiles());
        for (String tag : tags) {
            current.retainAll(edm.getRelationManager().getFilesWithTag(tag));
        }
        currentSelection.clear();
        currentSelection.addAll(current);
        cachedTags = tags;
        fireContentsChanged(this, 0, getSize() - 1);
    }

    @Override
    public int getSize() {
        return currentSelection.size();
    }

    @Override
    public Object getElementAt(int i) {
        return currentSelection.get(i).getFilename();
    }
}
