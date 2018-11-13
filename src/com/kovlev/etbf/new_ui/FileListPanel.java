package com.kovlev.etbf.new_ui;

import com.kovlev.etbf.filesystem.File;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class FileListPanel extends JPanel {
    private List<FileEntry> entryList;

    public FileListPanel(Controller c) {
        super();
        setBackground(Color.BLUE);
        setLayout(new FlowLayout());

    }


    public void updateSelection(Collection<File> files) {
        entryList = new ArrayList<>();
        for (File f : files) {
            FileEntry fe = new FileEntry(f, this);
            entryList.add(fe);
            add(fe);
        }
    }
}
