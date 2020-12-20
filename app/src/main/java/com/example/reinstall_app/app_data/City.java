package com.example.reinstall_app.app_data;

public class City extends Municipality {

    String cityName;
    String municipalityName;


    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public String getMunicipalityName() {
        return municipalityName;
    }

    @Override
    public void setMunicipalityName(String municipalityName) {
        this.municipalityName = municipalityName;
    }

    @Override
    public String toString()
    {
        return cityName;
    }
}
