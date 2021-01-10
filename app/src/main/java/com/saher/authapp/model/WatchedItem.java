package com.saher.authapp.model;

public class WatchedItem {

    public static final String COLLECTION_NAME = "useritem";

    String userId;
    String itemIdl;

    public WatchedItem(){}

    public WatchedItem(String userId, String itemIdl) {
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
