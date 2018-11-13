package com.kovlev.etbf.new_ui;

import com.kovlev.etbf.filesystem.File;
import com.kovlev.etbf.util.FileType;
import com.kovlev.etbf.util.ImageCache;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

public class FileEntry extends JPanel implements MouseListener {
    private JLabel filenameLabel;
    private JPanel imagePanel;

    private File containedFile;
    private FileListPanel parent;

    public FileEntry(File f, FileListPanel parent) {
        super();
        addMouseListener(this);
        filenameLabel = new JLabel(f.getFilename());
        imagePanel = new JPanel() {
            @Override
            public void paint(Graphics graphics) {
                super.paint(graphics);
                try {
                    graphics.drawImage(ImageCache.instance().get(70, 70, FileType.getFileType(f.getFilename())), 70, 70, this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }


    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        //parent;
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}
