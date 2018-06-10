package com.example.barry.firebasepushnotificationdemo.models;

public class Notifications {

    String from, message;

    public Notifications() {
        // empty constructor required
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Notifications(String from, String message) {
        this.from = from;
        this.message = message;
    }
}
