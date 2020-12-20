package com.example.reinstall_app.app_data;

public class District extends Province {

    String districtName;
    String provinceName;

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    @Override
    public String toString()
    {
        return districtName;
    }
}
