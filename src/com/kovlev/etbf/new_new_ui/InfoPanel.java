package com.kovlev.etbf.new_new_ui;

import com.kovlev.etbf.data.EncryptedDataManager;
import com.kovlev.etbf.filesystem.File;
import com.kovlev.etbf.filesystem.Tag;

import javax.swing.*;
import java.awt.*;

public class InfoPanel extends JPanel {
    private JPanel fileNameRow, fileSizeRow, tagsRow;
    private EncryptedDataManager edm;

    public InfoPanel(EncryptedDataManager edm) {
        super();
        this.edm = edm;
        setLayout(new GridLayout(20, 1));
        create(null);
    }

    public void create(File file) {
        if (file != null) {
            removeAll();

            fileNameRow = new JPanel();
            fileNameRow.add(new JLabel("Filename:"));
            fileNameRow.add(new JLabel(file.getFilename()));

            fileSizeRow = new JPanel();
            fileSizeRow.add(new JLabel("File size:"));
            fileSizeRow.add(new JLabel(file.getFileSize() + ""));

            tagsRow = new JPanel();
            tagsRow.add(new JLabel("Tags:"));
            StringBuilder s = new StringBuilder();
            for (Tag t : edm.getRelationManager().getTagsOfFile(file)) {
                s.append(t.getTagString()).append(" ");
            }
            tagsRow.add(new JLabel(s.toString()));

            add(fileNameRow);
            add(fileSizeRow);
            add(tagsRow);

            revalidate();
            repaint();
        }
    }
}
