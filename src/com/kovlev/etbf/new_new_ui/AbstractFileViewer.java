package com.kovlev.etbf.new_new_ui;

import com.kovlev.etbf.filesystem.File;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractFileViewer extends JPanel {

    public AbstractFileViewer(File file) {
        super();
        setPreferredSize(new Dimension(750, getMaximumSize().height));
        setLayout(new BorderLayout());
        JLabel label = new JLabel(file.getFilename());
        label.setFont(new Font("Sans", Font.BOLD, 24));
        add(label, BorderLayout.NORTH);
    }

    public abstract InputStream getSavedData();

}
