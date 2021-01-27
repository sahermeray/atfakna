package com.saher.authapp.model;

public class UserSetting {

    public static String FIELD_USER_ID = "userId";
    public static final String COLLECTION_NAME = "UserSetting";

    String UserId,UserCountry,UserImage,UserLanguage;

    public UserSetting(){}

    public UserSetting(String userId, String userCountry, String userImage, String userLanguage) {
        this.UserId = userId;
        this.UserCountry = userCountry;
        this.UserImage = userImage;
        this.UserLanguage = userLanguage;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        this.UserId = userId;
    }

    public String getUserCountry() {
        return UserCountry;
    }

    public void setUserCountry(String userCountry) {
        this.UserCountry = userCountry;
    }

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String userImage) {
        this.UserImage = userImage;
    }

    public String getUserLanguage() {
        return UserLanguage;
    }

    public void setUserLanguage(String userLanguage) {
        this.UserLanguage = userLanguage;
    }
}


