package com.kovlev.etbf.ui;

import com.kovlev.etbf.data.EncryptedDataManager;
import com.kovlev.etbf.data.MasterBlockManager;
import com.kovlev.etbf.filesystem.File;
import com.kovlev.etbf.util.FileType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;

public class MainFrame extends JFrame {
    private FileViewer fileViewer;

    private EncryptedDataManager edm;

    // UI
    private JLabel statusText;
    private JPanel fileViewerContainer;

    public MainFrame() throws Exception {
        super("ETBFS manager");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }

        edm = new EncryptedDataManager("xd.xcr", "pass");
        edm.getFileManager().addFile("shit.jpg");
        edm.getFileManager().addFile("kutya.txt");
        for (int i = 0; i < 100; i++) {
            edm.getFileManager().addFile("asd" + i + ".png");
        }
        fileViewer = null;

        JToolBar toolbar = new JToolBar();

        JPanel statusPanel = new JPanel();
        statusText = new JLabel();
        MasterBlockManager mbm = edm.getMasterBlockManager();
        statusText.setText(mbm.getFileCount() + " files; " + mbm.getTagCount() + " tags; " + mbm.getRelationCount() + " relations; " + mbm.getBlockCount() + " blocks");
        statusPanel.add(statusText);
        fileViewerContainer = new JPanel();
        fileViewerContainer.setPreferredSize(new Dimension(300, fileViewerContainer.getPreferredSize().height));
        fileViewerContainer.setLayout(new BorderLayout());
        //fileViewerContainer.add(fileViewer);

        setLayout(new BorderLayout());
        getContentPane().add(toolbar, BorderLayout.NORTH);
        getContentPane().add(statusPanel, BorderLayout.SOUTH);
        add(new JScrollPane(new FileList(edm.getFileManager(), this), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
        add(new JScrollPane(fileViewerContainer, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.EAST);
        //getContentPane().add(new FileItem(edm.getFileManager().getFile("kutya.txt")), BorderLayout.CENTER);
        pack();


        setMinimumSize(new Dimension(500, 500));
        /*setPreferredSize(new Dimension(500, 500));
        setMaximumSize(new Dimension(500, 500));*/

        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                try {
                    edm.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void openFile(File f) throws IOException {
        FileViewer fw = FileType.getFileType(f.getFilename()).getFileViewer(f);
        fileViewerContainer.removeAll();
        if (fw != null) {
            fileViewerContainer.add(fw, BorderLayout.CENTER);
        }
        fileViewerContainer.revalidate();
    }




}
