package com.example.reinstall_app.app_data;

import com.example.reinstall_app.app_data.District;

public class Municipality extends District {

    String municipalityName;
    String districtName;
    String email;
    String password;

    @Override
    public String getDistrictName() {
        return districtName;
    }

    @Override
    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDistrict() {
        return districtName;
    }

    public void setDistrict(String district) {
        this.districtName = district;
    }


    public String getMunicipalityName() {
        return municipalityName;
    }

    public void setMunicipalityName(String municipalityName) {
        this.municipalityName = municipalityName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
