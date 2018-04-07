package com.snapit.genia.snapit.adapters;

/**
 * Created by Asierae on 27/01/2018.
 */

public class User {

    private int id;
    private String name;
    private String username;
    private String date;
    private String avatar;
    private String background;
    private int followers;
    private int following;
    private String apiKey;
    private String requesKey;
    private String bio="";

    public User(int id, String name, String username, String date, String avatar) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.date = date;
        this.avatar = avatar;
    }
    public User(int id, String username, String background,int followers,int following,String date, String avatar) {
        this.id = id;
        this.username = username;
        this.date = date;
        this.avatar = avatar;
        this.background=background;
        this.followers=followers;
        this.following=following;
    }
    public User(int id, String username, String background,int followers,int following,String bio,String date, String avatar) {
        this.id = id;
        this.username = username;
        this.date = date;
        this.avatar = avatar;
        this.background=background;
        this.followers=followers;
        this.following=following;
        this.bio=bio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getRequesKey() {
        return requesKey;
    }

    public void setRequesKey(String requesKey) {
        this.requesKey = requesKey;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
