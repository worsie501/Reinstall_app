package com.example.reinstall_app.app_data;

import android.app.Application;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import java.util.ArrayList;
import java.util.List;

public class ReinstallApplicationClass extends Application {

    public static final String APPLICATION_ID = "2FDB9EDF-3E2F-5329-FF04-61FE182AB200";
    public static final String API_KEY = "FA15AED0-1910-4378-B9BF-533012EBD11A";
    public static final String SERVER_URL = "https://api.backendless.com";

    public static BackendlessUser user;

    public static List<ProblemType> problemTypes;

    public static List<Suburb> suburbList;

    public static List<Suburb> hotspotList;

    public static Resident resident;

    @Override
    public void onCreate() {
        super.onCreate();

        Backendless.setUrl( SERVER_URL );
        Backendless.initApp( getApplicationContext(),
                APPLICATION_ID,
                API_KEY );

    }
}
