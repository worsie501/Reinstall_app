package com.example.reinstall_app.app_data;

import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.graphics.Point;

import com.backendless.geo.GeoPoint;

import java.util.Date;

public class ReportedProblem {

    String description, userName, problemType, city, suburb;
    Date created;
    double x, y;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProblemLocation() {
        return userName;
    }

    public void setProblemLocation(String problemLocation) {
        this.userName = problemLocation;
    }

    public String getProblemType() {
        return problemType;
    }

    public void setProblemType(String problemType) {
        this.problemType = problemType;
    }



}
