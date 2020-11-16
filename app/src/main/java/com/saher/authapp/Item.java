package com.saher.authapp;

public class Item {
    String name,location,price,phonenumber,description;
    public Item(){}

    public Item(String name,String location,String price){
        this.name=name;
        this.location=location;
        this.price=price;
    }

    public Item(String name, String location, String price, String phonenumber, String description) {
        this.name = name;
        this.location = location;
        this.price = price;
        this.phonenumber = phonenumber;
        this.description = description;
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
}
