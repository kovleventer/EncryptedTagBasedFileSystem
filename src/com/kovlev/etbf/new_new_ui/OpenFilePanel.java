package com.kovlev.etbf.new_new_ui;

import com.kovlev.etbf.filesystem.File;

import javax.swing.*;
import java.io.IOException;

public class OpenFilePanel extends JPanel {

    private AbstractFileViewer fileViewer;

    public AbstractFileViewer getFileViewer() {
        return fileViewer;
    }

    public OpenFilePanel() throws IOException {
        super();
        create(null);
    }

    public void create(File file) throws IOException {
        if (file != null) {
            removeAll();
            fileViewer = FileViewerFactory.getFileViewer(file);
            add(fileViewer);
        }
    }
}
