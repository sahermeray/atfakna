package com.saher.authapp;

public class Item {
    String name,location,price,phonenumber,description,id;
    public Item(){}

    public Item(String name,String location,String price,String id){
        this.name=name;
        this.location=location;
        this.price=price;
        this.id=id;
    }

    public Item(String name, String location, String price, String phonenumber, String description,String id) {
        this.name = name;
        this.location = location;
        this.price = price;
        this.phonenumber = phonenumber;
        this.description = description;
        this.id=id;
    }
    public String getId(){ return id;}

    public void setId(String id){ this.id=id;}

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
