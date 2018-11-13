package com.kovlev.etbf.ui;

import com.kovlev.etbf.data.FileManager;
import com.kovlev.etbf.filesystem.File;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class FileList extends JPanel implements OpenedFileChangeListener {
    private FileManager fm;

    private MainFrame mf;

    private File activeFile;

    public FileList(FileManager fm, MainFrame mf) {
        super();
        this.fm = fm;
        this.mf = mf;

        setLayout(new FlowLayout(FlowLayout.LEFT) {
            @Override
            public Dimension preferredLayoutSize(Container container) {
                Dimension d = super.preferredLayoutSize(container);
                d.width = Math.min(500, d.width);
                return d;
            }
        });
        for (File f : fm.getFiles()) {
            add(new FileItem(f, this));
            //add(new JButton(f.getFilename()));
        }

    }

    @Override
    public void updateActiveFile(File f) {
        if (activeFile != null) {
            ((FileItem)getComponent((int) activeFile.getID() - 1)).setActive(false);
        }
        ((FileItem)getComponent((int)f.getID() - 1)).setActive(true);
        activeFile = f;
        try {
            mf.openFile(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
