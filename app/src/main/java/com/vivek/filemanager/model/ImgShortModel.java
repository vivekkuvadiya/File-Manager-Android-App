package com.vivek.filemanager.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ImgShortModel implements Serializable {
    public ArrayList<ImgModel> imgModels;
    public String date;

    public ImgShortModel(ArrayList<ImgModel> imgModels, String date) {
        this.imgModels = imgModels;
        this.date = date;
    }
}
