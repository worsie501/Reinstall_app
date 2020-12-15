package com.example.reinstall_app;

public class Suburb extends  City{

    String suburbName;
    String cityName;

    public String getSuburbName() {
        return suburbName;
    }

    public void setSuburbName(String suburbName) {
        this.suburbName = suburbName;
    }

    @Override
    public String getCityName() {
        return cityName;
    }

    @Override
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
