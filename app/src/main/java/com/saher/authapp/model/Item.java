package com.saher.authapp.model;

public class Item {
    String name,location,price,phonenumber,description,id,uniqueID,image;
    public Item(){}

    public Item(String name,String location,String price,String id,String uniqueID,String image){
        this.name=name;
        this.location=location;
        this.price=price;
        this.id=id;
        this.uniqueID=uniqueID;
        this.image=image;


    }

    public Item(String name, String location, String price, String phonenumber, String description,String id,String uniqueID,String image) {
        this.name = name;
        this.location = location;
        this.price = price;
        this.phonenumber = phonenumber;
        this.description = description;
        this.id=id;
        this.uniqueID=uniqueID;
        this.image=image;


    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUniqueID(){return uniqueID;}

    public void setUniqueID(String uniqueID){this.uniqueID=uniqueID;}

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
