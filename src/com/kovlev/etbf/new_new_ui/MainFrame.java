package com.kovlev.etbf.new_new_ui;

import com.google.common.io.ByteStreams;
import com.kovlev.etbf.data.EncryptedDataManager;

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
    private ToolbarPanel toolbarPanel;

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
        toolbarPanel = new ToolbarPanel(edm, openFilePanel, flm);
        list.setModel(flm);
        list.addListSelectionListener(listSelectionEvent -> {
            if (!listSelectionEvent.getValueIsAdjusting()) {
                int idx = list.getSelectedIndex();
                infoPanel.create(flm.fileAt(idx));
                toolbarPanel.update(flm.fileAt(idx));
                try {
                    openFilePanel.create(flm.fileAt(idx));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        tagPanel = new TagPanel(flm);

        add(tagPanel, BorderLayout.SOUTH);
        add(list, BorderLayout.EAST);
        add(infoPanel, BorderLayout.WEST);
        add(openFilePanel, BorderLayout.CENTER);
        add(toolbarPanel, BorderLayout.NORTH);

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
