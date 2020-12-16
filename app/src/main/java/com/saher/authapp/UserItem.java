package com.saher.authapp;

public class UserItem {
    String userId;
    String itemIdl;

    public  UserItem(){}

    public UserItem(String userId, String itemIdl) {
        this.userId = userId;
        this.itemIdl = itemIdl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItemIdl() {
        return itemIdl;
    }

    public void setItemIdl(String itemIdl) {
        this.itemIdl = itemIdl;
    }
}
