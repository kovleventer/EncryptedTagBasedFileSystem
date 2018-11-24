package com.kovlev.etbf.new_new_ui;

import com.google.common.io.ByteStreams;
import com.kovlev.etbf.data.EncryptedDataManager;
import com.kovlev.etbf.filesystem.File;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MainFrame extends JFrame {
    public static final String TITLE = "ETBFS container";

    private JList list;
    private FileListModel flm;
    private TagPanel tagPanel;
    private InfoPanel infoPanel;
    private OpenFilePanel openFilePanel;
    private File activeFile;

    private EncryptedDataManager edm;

    public MainFrame(String filepath, String password) throws Exception {
        super(TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        list = new JList();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setPreferredSize(new Dimension(300, getPreferredSize().height));
        edm = new EncryptedDataManager(filepath, password);

        flm = new FileListModel(edm);
        infoPanel = new InfoPanel(edm);
        openFilePanel = new OpenFilePanel();
        list.setModel(flm);
        list.addListSelectionListener(listSelectionEvent -> {
            //if (!listSelectionEvent.getValueIsAdjusting()) {
                int idx = list.getSelectedIndex();
                infoPanel.create(flm.fileAt(idx));
                activeFile = flm.fileAt(idx);
                try {
                    openFilePanel.create(flm.fileAt(idx));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            //}
        });

        tagPanel = new TagPanel(flm);

        add(tagPanel, BorderLayout.SOUTH);
        add(list, BorderLayout.EAST);
        add(infoPanel, BorderLayout.WEST);
        add(openFilePanel, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        JMenuItem saveFile = new JMenuItem("Save current file");
        JMenuItem addTag = new JMenuItem("Add new tag to current file");
        JMenuItem rename = new JMenuItem("Rename current file");
        JMenu menuContainer = new JMenu("Container");
        JMenuItem addFile = new JMenuItem("Add new file");
        JMenuItem createNewFile = new JMenuItem("Create new file");
        JMenuItem flush = new JMenuItem("Flush contaner");

        saveFile.addActionListener(actionEvent -> {
            try {
                if (activeFile != null) {
                    InputStream is = openFilePanel.getFileViewer().getSavedData();
                    byte[] buffer = ByteStreams.toByteArray(is);
                    activeFile.getOS().write(buffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        addTag.addActionListener(actionEvent -> {
            if (activeFile != null) {
                String tag = JOptionPane.showInputDialog(new JFrame(), "Name of the new tag");
                try {
                    edm.getTagManager().addTag(tag);
                    edm.getRelationManager().addRelation(activeFile.getFilename(), tag);
                    flm.updateSelection();
                    infoPanel.create(activeFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        rename.addActionListener(actionEvent -> {
            if (activeFile != null) {
                String filename = JOptionPane.showInputDialog(new JFrame(), "New name of the file");
                try {
                    edm.getFileManager().modifyFileName(activeFile, filename);
                    flm.updateSelection();
                    infoPanel.create(activeFile);
                    openFilePanel.create(activeFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        addFile.addActionListener(actionEvent -> {
            JFileChooser jfc = new JFileChooser();
            int returnValue = jfc.showOpenDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                try {
                    java.io.File selectedFile = jfc.getSelectedFile();
                    byte[] buffer = Files.readAllBytes(selectedFile.toPath());
                    String filename = selectedFile.getName();
                    edm.getFileManager().addFile(filename);
                    edm.getFileManager().getFile(filename).getOS().write(buffer);
                    flm.updateSelection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        createNewFile.addActionListener(actionEvent -> {
            String filename = JOptionPane.showInputDialog(new JFrame(), "Name of the new file");
            try {
                edm.getFileManager().addFile(filename);
                flm.updateSelection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        flush.addActionListener(actionEvent -> {
            try {
                edm.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        menuFile.add(saveFile);
        menuFile.add(addTag);
        menuFile.add(rename);
        menuContainer.add(addFile);
        menuContainer.add(createNewFile);
        menuContainer.add(flush);
        menuBar.add(menuFile);
        menuBar.add(menuContainer);

        setJMenuBar(menuBar);
        //add(toolbarPanel, BorderLayout.NORTH);

        setPreferredSize(new Dimension(1000, 1000));
        pack();
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                try {
                    edm.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
