package com.kovlev.etbf.ui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

public abstract class FileViewer extends JPanel {
    protected InputStream is;
    protected OutputStream os;

    abstract public void displayFile() throws IOException;

    public FileViewer(InputStream is, OutputStream os) {
        super();
        this.is = is;
        this.os = os;
        setPreferredSize(new Dimension(500, getPreferredSize().height));
    }
}
