package com.example.reinstall_app.app_data;

import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.graphics.Point;
import android.media.Image;

import com.backendless.geo.GeoPoint;

import java.util.Date;

public class ReportedProblem {

    String description;
    String userName;
    String problemType;
    String city;
    String suburb;
    Date updated;
    String ownerId;
    Date created;
    double x, y;
    String photo;
    String objectId;
    String reportUrgency;

    public String getReportUrgency() {
        return reportUrgency;
    }

    public void setReportUrgency(String reportUrgency) {
        this.reportUrgency = reportUrgency;
    }


    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

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
