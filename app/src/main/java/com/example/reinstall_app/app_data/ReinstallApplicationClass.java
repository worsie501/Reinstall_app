package com.example.reinstall_app.app_data;

import android.app.Application;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;

import java.util.List;

public class ReinstallApplicationClass extends Application {

    //backendless
    public static final String APPLICATION_ID = "2FDB9EDF-3E2F-5329-FF04-61FE182AB200";
    public static final String API_KEY = "FA15AED0-1910-4378-B9BF-533012EBD11A";
    public static final String SERVER_URL = "https://api.backendless.com";

    //CometChat
    public static final String APP_ID = "287029e7414b03b"; // Replace with your App ID
    public static final  String REGION = "eu"; // Replace with your App Region ("eu" or "us")
    public static final String AUTH_KEY = "a0fbe854d2de97abaf72dd1ce105ed2c978b9ce1"; // Replace with your App Auth Key
    public static final String GROUP_ID = "Group_id";


    public static BackendlessUser user;

    public static List<ProblemType> problemTypes;

    public static List<ReportedProblem> reportedProblems;

    public static List<ReportedProblem> activeProblems;

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

        AppSettings appSettings = new AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(REGION).build();

        CometChat.init(this, APP_ID,appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successMessage) {

            }
            @Override
            public void onError(CometChatException e) {

            }
        });
    }
}
