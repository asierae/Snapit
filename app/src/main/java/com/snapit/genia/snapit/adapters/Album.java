package com.snapit.genia.snapit.adapters;

/**
 * Created by Asierae on 31/01/2018.
 */

public class Album {

    private int id;
    private int numOfPics;
    private String u1;
    private String u2;
    private String date;
    private String thumbnail;
    private boolean fav;
    private String hide;

    public Album() {
    }

    public Album(int id, int numOfPics,String u1, String u2, String thumbnail,String date,String h) {
        this.id = id;
        this.u1=u1;
        this.u2=u2;
        this.numOfPics = numOfPics;
        this.thumbnail = thumbnail;
        this.fav=false;
        this.date=date;
        this.hide=h;
    }


    public int getNumOfPics() {
        return numOfPics;
    }

    public void setNumOfPics(int numOfPics) {
        this.numOfPics = numOfPics;
    }

    public String getU1() {
        return u1;
    }

    public void setU1(String u1) {
        this.u1 = u1;
    }

    public String getU2() {
        return u2;
    }

    public void setU2(String u2) {
        this.u2 = u2;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }






    public int getNumOfPcs() {
        return numOfPics;
    }

    public void setNumOfSongs(int numOfSongs) {
        this.numOfPics = numOfSongs;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public String getThumbnail() {
        return thumbnail;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHide() {
        return hide;
    }

    public void setHide(String hide) {
        this.hide = hide;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
