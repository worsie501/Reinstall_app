package com.example.reinstall_app.app_data;

public class Province {

    String provinceName;

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    @Override
    public String toString()
    {
        return provinceName;
    }
}
