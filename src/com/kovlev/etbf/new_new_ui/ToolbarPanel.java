package com.kovlev.etbf.new_new_ui;

import com.google.common.io.ByteStreams;
import com.kovlev.etbf.data.EncryptedDataManager;
import com.kovlev.etbf.filesystem.File;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class ToolbarPanel extends JPanel {
    private EncryptedDataManager edm;
    private File file;
    private OpenFilePanel openFilePanel;
    private FileListModel fileListModel;

    public ToolbarPanel(EncryptedDataManager edm, OpenFilePanel opf, FileListModel flm) {
        super();
        this.edm = edm;
        openFilePanel = opf;
        fileListModel = flm;
        JButton bSave = new JButton("Save file");
        JButton bAddTag = new JButton("Add tag");
        JButton bAddFile = new JButton("Add new file");
        JButton bCreateFile = new JButton("Create empty file");
        JButton bFlush = new JButton("Flush");
        JButton bRename = new JButton("Rename file");

        add(bSave);
        add(bAddTag);
        add(bAddFile);
        add(bCreateFile);
        add(bFlush);
        add(bRename);

        bSave.addActionListener(actionEvent -> {
            try {
                if (file != null) {
                    InputStream is = openFilePanel.getFileViewer().getSavedData();
                    byte[] buffer = ByteStreams.toByteArray(is);
                    file.getOS().write(buffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        bAddTag.addActionListener(actionEvent -> {
            if (file != null) {
                String tag = JOptionPane.showInputDialog(new JFrame(), "Name of the new tag");
                try {
                    edm.getTagManager().addTag(tag);
                    edm.getRelationManager().addRelation(file.getFilename(), tag);
                    fileListModel.updateSelection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        bAddFile.addActionListener(actionEvent -> {
            JFileChooser jfc = new JFileChooser();
            int returnValue = jfc.showOpenDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                try {
                    java.io.File selectedFile = jfc.getSelectedFile();
                    byte[] buffer = Files.readAllBytes(selectedFile.toPath());
                    String filename = selectedFile.getName();
                    edm.getFileManager().addFile(filename);
                    edm.getFileManager().getFile(filename).getOS().write(buffer);
                    fileListModel.updateSelection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        bCreateFile.addActionListener(actionEvent -> {
            String filename = JOptionPane.showInputDialog(new JFrame(), "Name of the new file");
            try {
                edm.getFileManager().addFile(filename);
                fileListModel.updateSelection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        bFlush.addActionListener(actionEvent -> {
            try {
                edm.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        bRename.addActionListener(actionEvent -> {
            if (file != null) {
                String filename = JOptionPane.showInputDialog(new JFrame(), "New name of the file");
                try {
                    edm.getFileManager().modifyFileName(file, filename);
                    fileListModel.updateSelection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void update(File file) {
        this.file = file;
    }
}
