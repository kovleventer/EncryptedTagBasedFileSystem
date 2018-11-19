package com.kovlev.etbf.new_new_ui;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.kovlev.etbf.filesystem.File;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TextFileViewer extends AbstractFileViewer {
    private JTextArea textArea;

    public TextFileViewer(File file) throws IOException {
        super(file);
        textArea = new JTextArea();
        textArea.setEditable(true);
        add(textArea, BorderLayout.CENTER);
        BufferedReader br = new BufferedReader(new InputStreamReader(file.getIS()));
        String line;
        while ((line = br.readLine()) != null) {
            textArea.append(line + "\n");
        }
        br.close();
    }

    @Override
    public InputStream getSavedData() {
        byte[] bytes = textArea.getText().getBytes();
        try {
            return ByteSource.wrap(bytes).openStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
