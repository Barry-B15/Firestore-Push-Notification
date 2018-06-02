package com.example.barry.firebasepushnotificationdemo.models;

public class Users extends UserId {

    String name, image;

    public Users() {
        // empty constructor required
    }

    public Users(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
