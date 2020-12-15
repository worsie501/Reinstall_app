package com.example.reinstall_app;

public class Municipality extends District{

    String municipalityName;
    String districtName;
    String email;

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
