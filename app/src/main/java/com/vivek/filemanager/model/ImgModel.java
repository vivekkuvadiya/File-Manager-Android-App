package com.vivek.filemanager.model;

import java.io.File;
import java.io.Serializable;

public class ImgModel implements Serializable {

    public File file;
    public String date;
    public String duration;

    public ImgModel(File file, String date,String duration) {
        this.file = file;
        this.date = date;
        this.duration = duration;
    }
}
