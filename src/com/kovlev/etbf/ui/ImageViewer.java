package com.kovlev.etbf.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageViewer extends FileViewer {
    private Image bi;
    private JPanel panel;

    public ImageViewer(InputStream is, OutputStream os) throws IOException {
        super(is, os);
        displayFile();
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                graphics.drawImage(bi, 0, 0, getSize().width, getSize().height, this);
            }
        };
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void displayFile() throws IOException {
        System.out.println("READEDIN");
        bi = ImageIO.read(is);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (bi != null) {
            System.out.println("PAINTED");
            graphics.drawImage(bi, 0, 0, getSize().width, getSize().height, this);
            revalidate();
        }
    }
}
