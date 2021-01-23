package com.saher.authapp.model;

import com.google.firebase.firestore.Exclude;

public class Item {

    public static final String COLLECTION_NAME = "Itembook";
    public static final String FIELD_USER_ID = "userId";

    String name;
    String location;
    String price;
    String phonenumber;
    String description;
    String userId;
    String image;
    String id;

    public Item() {}

    public Item(String name, String location, String price, String phonenumber, String description, String userId, String image) {
        this.name = name;
        this.location = location;
        this.price = price;
        this.phonenumber = phonenumber;
        this.description = description;
        this.userId = userId;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
