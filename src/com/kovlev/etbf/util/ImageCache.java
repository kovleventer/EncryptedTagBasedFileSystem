package com.kovlev.etbf.util;

import com.kovlev.etbf.filesystem.File;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class ImageCache {
    private HashMap<SizedImage, Image> cache;

    private static ImageCache inst = new ImageCache();
    public static ImageCache instance() { return inst; }

    private ImageCache() {
        cache = new HashMap<>();
    }

    public Image get(int w, int h, FileType type) throws IOException {
        String fname = "res/" + type.toString().toLowerCase() + ".png";
        SizedImage sizedImage = new SizedImage(w, h, fname);
        if (!cache.containsKey(sizedImage)) {
            Image unscaled = ImageIO.read(new FileInputStream(fname));
            //cache.put(sizedImage, unscaled.getScaledInstance(w, h, Image.SCALE_SMOOTH));
            cache.put(sizedImage, unscaled);
        }
        return cache.get(sizedImage);
    }

    private static class SizedImage {
        int w, h;
        String filename;
        public SizedImage(int w, int h, String filename) {
            this.w = w;
            this.h = h;
            this.filename = filename;
        }

        @Override
        public int hashCode() {
            return filename.hashCode() ^ w ^ h;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof SizedImage)) return false;
            SizedImage si = (SizedImage) o;
            return si.w == w && si.h == h && filename.equals(si.filename);
        }
    }
}
