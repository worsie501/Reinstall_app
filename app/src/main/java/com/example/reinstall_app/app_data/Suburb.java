package com.example.reinstall_app.app_data;

import com.example.reinstall_app.app_data.City;

public class Suburb extends City {

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
