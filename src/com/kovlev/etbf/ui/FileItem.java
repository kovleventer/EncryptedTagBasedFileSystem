package com.kovlev.etbf.ui;

import com.kovlev.etbf.filesystem.File;
import com.kovlev.etbf.util.FileType;
import com.kovlev.etbf.util.ImageCache;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

public class FileItem extends JPanel implements MouseListener {

    private File f;
    private JPanel image;
    private JLabel filename;

    private OpenedFileChangeListener ofcl;

    private boolean active;

    public FileItem(File f, OpenedFileChangeListener ofcl) {
        super();
        this.f = f;
        this.ofcl = ofcl;
        active = false;

        image = new JPanel() {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                try {
                    graphics.drawImage(ImageCache.instance().get(getWidth(), getHeight(), FileType.getFileType(f.getFilename())), getWidth() / 2 - 35, 0, 70, 70, this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Dimension d = new Dimension(70, 70);
        image.setMinimumSize(d);
        image.setPreferredSize(d);
        image.setMaximumSize(d);
        image.setBackground(Color.WHITE);
        setBackground(Color.WHITE);
        filename = new JLabel(f.getFilename());
        filename.setForeground(Color.BLACK);
        setLayout(new BorderLayout());
        add(image, BorderLayout.NORTH);
        add(filename, BorderLayout.SOUTH);
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        if (!active) {
            ofcl.updateActiveFile(f);
        }
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        if (!active) {
            setBackground(Color.GRAY);
            image.setBackground(Color.GRAY);
            filename.setForeground(Color.WHITE);
        }
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        if (!active) {
            setBackground(Color.WHITE);
            image.setBackground(Color.WHITE);
            filename.setForeground(Color.BLACK);
        }
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            image.setBackground(Color.BLACK);
            setBackground(Color.BLACK);
            filename.setForeground(Color.WHITE);
        } else {
            image.setBackground(Color.WHITE);
            setBackground(Color.WHITE);
            filename.setForeground(Color.BLACK);
        }
    }
}
