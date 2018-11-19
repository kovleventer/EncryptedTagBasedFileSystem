package com.kovlev.etbf.new_new_ui;

import com.kovlev.etbf.filesystem.File;

import java.io.IOException;
import java.util.regex.Pattern;

public class FileViewerFactory {
    public static AbstractFileViewer getFileViewer(File file) throws IOException {
        String[] splitted = file.getFilename().split(Pattern.quote("."));
        String extension = splitted[splitted.length - 1];
        switch (extension) {
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
                return new ImageFileViewer(file);
            case "txt":
            default:
                return new TextFileViewer(file);
        }
    }
}
