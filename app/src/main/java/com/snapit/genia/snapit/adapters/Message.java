package com.snapit.genia.snapit.adapters;

/**
 * Created by Asierae on 27/01/2018.
 */

public class Message {
    private int id;
    private User user;
    private String message;
    private String date;

    public Message(int id, User user, String message, String date) {
        this.id = id;
        this.user = user;
        this.message = message;
        this.date = date;
    }
    public Message(){

    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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