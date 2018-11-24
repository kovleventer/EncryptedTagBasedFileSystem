package com.kovlev.etbf.new_new_ui;

import com.kovlev.etbf.filesystem.File;

import javax.swing.*;
import java.io.IOException;

/**
 * Panel to store a fileViewer
 */
public class OpenFilePanel extends JPanel {

    private AbstractFileViewer fileViewer;

    public AbstractFileViewer getFileViewer() {
        return fileViewer;
    }

    public OpenFilePanel() throws IOException {
        super();
        create(null);
    }

    /**
     * Creates a new fileViewer for the given file
     * @param file The opened file
     * @throws IOException
     */
    public void create(File file) throws IOException {
        if (file != null) {
            removeAll();
            fileViewer = FileViewerFactory.getFileViewer(file);
            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.getViewport().add(fileViewer);
            scrollPane.getViewport().setPreferredSize(fileViewer.getPreferredSize());
            add(scrollPane);
        }
    }
}
