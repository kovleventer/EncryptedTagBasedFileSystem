package com.kovlev.etbf.util;

import com.kovlev.etbf.filesystem.File;
import com.kovlev.etbf.ui.FileViewer;
import com.kovlev.etbf.ui.ImageViewer;
import com.kovlev.etbf.ui.TextViewer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;

public enum FileType {
    IMAGE,
    TEXT;

    public static FileType getFileType(String filename) {
        String[] splitted = filename.split(Pattern.quote("."));
        String extension = splitted[splitted.length - 1];
        switch (extension) {
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
                return IMAGE;
            case "txt":
            default:
                return TEXT;
        }
    }

    public FileViewer getFileViewer(File f) throws IOException {
        InputStream is = f.getIS();
        OutputStream os = f.getOS();
        switch (this) {
            case IMAGE:
                return new ImageViewer(is, os);
            case TEXT:
                return new TextViewer(is, os);
        }
        return null;
    }

    /*public BufferedImage loadImage() throws IOException {
        String fname = "res/" + this.toString().toLowerCase() + ".png";
        InputStream is = getClass().getResourceAsStream(fname);
        //System.out.println(fname);
        return ImageIO.read(new FileInputStream(fname));
    }*/
}