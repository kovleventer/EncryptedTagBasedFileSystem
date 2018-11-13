package com.kovlev.etbf.new_ui;

import com.kovlev.etbf.filesystem.File;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Set;

public class MainFrame extends JFrame {
    private ToolbarPanel toolbarPanel;
    private InfoPanel infoPanel;
    private FileListPanel fileListPanel;
    private OpenFilePanel openFilePanel;
    private StatusPanel statusPanel;

    public static final String TITLE = "ETBFS container";

    public MainFrame(Controller c) {
        super(TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        toolbarPanel = new ToolbarPanel(c);
        infoPanel = new InfoPanel(c);
        fileListPanel = new FileListPanel(c);
        openFilePanel = new OpenFilePanel(c);
        statusPanel = new StatusPanel(c);
        add(toolbarPanel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.WEST);
        add(fileListPanel, BorderLayout.CENTER);
        add(openFilePanel, BorderLayout.EAST);
        add(statusPanel, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(1000, 1000));
        pack();
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setStatus(String status) {
        statusPanel.setStatus(status);
    }

    public void updateCurrentFile(File f) {

    }


    public void updateSelection(Collection<File> files) {
        fileListPanel.updateSelection(files);
    }
}
