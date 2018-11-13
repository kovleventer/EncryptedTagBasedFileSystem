package com.kovlev.etbf.ui;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class TextViewer extends FileViewer {
    private JTextArea textArea;

    public TextViewer(InputStream is, OutputStream os) throws IOException {
        super(is, os);
        textArea = new JTextArea();
        textArea.setEditable(true);
        setLayout(new BorderLayout());
        add(textArea, BorderLayout.CENTER);
        displayFile();
    }

    @Override
    public void displayFile() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            textArea.append(line + "\n");
        }
        br.close();
    }
}
