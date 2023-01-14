package com.vivek.filemanager.model;

import java.io.File;
import java.io.Serializable;

public class MediaModel implements Serializable {
    public File file;
    public int count;

    public MediaModel(File file, int count) {
        this.file = file;
        this.count = count;
    }
}
