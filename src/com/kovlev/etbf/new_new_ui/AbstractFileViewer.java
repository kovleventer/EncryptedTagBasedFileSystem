package com.kovlev.etbf.new_new_ui;

import com.kovlev.etbf.filesystem.File;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Displays a file as image, text, etc
 * This is a superclass for concrete images
 */
public abstract class AbstractFileViewer extends JPanel {

    /**
     * Constructor for AbstractFileViewer
     * Adds a label with the current file's name
     * @param file
     */
    public AbstractFileViewer(File file) {
        super();
        //setMinimumSize(new Dimension(750, 750));
        setLayout(new BorderLayout());
        JLabel label = new JLabel(file.getFilename());
        label.setFont(new Font("Sans", Font.BOLD, 24));
        add(label, BorderLayout.NORTH);
    }

    /**
     * If the fileviewer allows editing, this returns an inputstream for the changed data
     * @return The changed file data, which will be written back to the blocks
     */
    public abstract InputStream getSavedData();

}
