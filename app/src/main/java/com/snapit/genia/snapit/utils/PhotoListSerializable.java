package com.snapit.genia.snapit.utils;

import com.snapit.genia.snapit.adapters.Photo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Asierae on 13/03/2018.
 */

public class PhotoListSerializable implements Serializable {
    private ArrayList<Photo> photoList;

    public PhotoListSerializable(ArrayList<Photo> list){
        this.photoList=list;
    }

    public ArrayList<Photo> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(ArrayList<Photo> photoList) {
        this.photoList = photoList;
    }
}
