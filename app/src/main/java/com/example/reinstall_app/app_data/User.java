package com.example.reinstall_app.app_data;

public class User {

    long userId;
    String userPassword;
    String province;
    boolean loginStatus;

    public boolean isLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(boolean loginStatus) {
        this.loginStatus = loginStatus;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String location) {
        this.province = location;
    }


}
