package com.kovlev.etbf.ui;

import com.kovlev.etbf.filesystem.File;

public interface OpenedFileChangeListener {
    void updateActiveFile(File f);
}
