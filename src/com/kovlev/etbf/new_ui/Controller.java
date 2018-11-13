package com.kovlev.etbf.new_ui;

import com.kovlev.etbf.data.EncryptedDataManager;
import com.kovlev.etbf.data.MasterBlockManager;
import com.kovlev.etbf.filesystem.File;
import com.kovlev.etbf.filesystem.Tag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Controller {
    private EncryptedDataManager edm;
    private MainFrame ui;

    private File currentFile;
    private Set<Tag> selection;

    public Controller() throws Exception {
        edm = new EncryptedDataManager("kutya.xcr", "asd");
        ui = new MainFrame(this);
        MasterBlockManager mbm = edm.getMasterBlockManager();
        String status = mbm.getBlockCount() + " blocks; " + mbm.getFileCount() + " files; " + mbm.getTagCount() + " tags; " + mbm.getRelationCount() + " relations";
        ui.setStatus(status);
        selection = new HashSet<>();
        ui.updateSelection(edm.getFileManager().getFiles());
    }

    public void setCurrentFile(File newCurrentFile) {
        currentFile = newCurrentFile;
        ui.updateCurrentFile(currentFile);
    }

    public void addTag(String tag) {
        if (selection.add(edm.getTagManager().getTag(tag))) {
            Set<File> files = null;
            for (Tag t : selection) {
                if (files == null) {
                    files = new HashSet<>(edm.getRelationManager().getFilesWithTag(t));
                } else {
                    files.retainAll(edm.getRelationManager().getFilesWithTag(t));
                }
            }
            ui.updateSelection(files);
        }
    }

    public void close() throws Exception {
        edm.flush();
    }
}
