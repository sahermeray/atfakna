package com.saher.authapp.model;

public class UserSetting {
    String UserId,UserCountry,UserImage,UserLanguage;

    public UserSetting(){}

    public UserSetting(String userId, String userCountry, String userImage, String userLanguage) {
        UserId = userId;
        UserCountry = userCountry;
        UserImage = userImage;
        UserLanguage = userLanguage;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUserCountry() {
        return UserCountry;
    }

    public void setUserCountry(String userCountry) {
        UserCountry = userCountry;
    }

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String userImage) {
        UserImage = userImage;
    }

    public String getUserLanguage() {
        return UserLanguage;
    }

    public void setUserLanguage(String userLanguage) {
        UserLanguage = userLanguage;
    }
}


