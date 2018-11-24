package com.kovlev.etbf.new_new_ui;

import com.kovlev.etbf.filesystem.File;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Displays the file as an image
 */
public class ImageFileViewer extends AbstractFileViewer {
    private BufferedImage image;

    private File f;

    /**
     * Inner class to display the image
     */
    private class ImagePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            graphics.drawImage(image, 0, 0, this);
        }
    }

    public ImageFileViewer(File file) {
        super(file);
        setPreferredSize(new Dimension(700, 700));
        f = file;
        try {
            image = ImageIO.read(file.getIS());
            file.getIS().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        add(new ImagePanel(), BorderLayout.CENTER);
    }

    @Override
    public InputStream getSavedData() {
        return f.getIS();
    }
}
