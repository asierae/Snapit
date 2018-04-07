package com.snapit.genia.snapit.adapters;

import java.io.Serializable;

/**
 * Created by Asierae on 22/02/2018.
 */

public class Photo implements Serializable{
    private int id;
    private String user;
    private String path;
    private String message;
    private String date;

    public Photo(String path){
        this.path=path;
    }

    public Photo(int id, String user, String path, String message,String date) {
        this.id = id;
        this.user = user;
        this.path = path;
        this.message = message;
        this.date=date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
