package com.example.reinstall_app.activity_classes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.ReinstallApplicationClass;
import com.example.reinstall_app.app_data.ReportedProblem;
import com.example.reinstall_app.app_data.Resident;
import com.example.reinstall_app.app_data.Suburb;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment
{

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;


    TextView tvWelcome, tvDescryption, tvReportsTotal, tvActiveResidents;

    RecyclerView rvList;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;

    View v ;

    ArrayList<Suburb> aboveTen = new ArrayList<>();

    public DashboardFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        tvWelcome = v.findViewById(R.id.tvWelcm);
        tvDescryption = v.findViewById(R.id.tvDesc);
        tvReportsTotal = v.findViewById(R.id.tvReportsTotal);
        tvActiveResidents = v.findViewById(R.id.tvActiveResidents);

        mLoginFormView = v.findViewById(R.id.login_form);
        mProgressView = v.findViewById(R.id.login_progress);
        tvLoad = v.findViewById(R.id.tvLoad);

        rvList = v.findViewById(R.id.rvList);
        rvList.hasFixedSize();

        layoutManager = new LinearLayoutManager(this.getActivity());
        rvList.setLayoutManager(layoutManager);


        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setSortBy("suburbName");

        showProgress(true);
        tvLoad.setText("Retreiving info...please wait...");

        int PAGESIZE = 80;
        queryBuilder.setPageSize(PAGESIZE);

        Backendless.Persistence.of(Suburb.class).find(queryBuilder, new AsyncCallback<List<Suburb>>() {
            @Override
            public void handleResponse(List<Suburb> response) {

                ReinstallApplicationClass.suburbList = response;

                for (int i = 0; i < response.size(); i++)
                {
                    if(response.get(i).getTotalReports() >= 1)
                    {
                        Suburb sub = response.get(i);
                        aboveTen.add(sub);

                    }
                }
                ReinstallApplicationClass.hotspotList = aboveTen;
                myAdapter = new HotSpotAdapter(getActivity(), aboveTen);
                rvList.setAdapter(myAdapter);
                showProgress(false);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getActivity(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                showProgress(false);
            }
        });

        //Updated where clause for Active reports
        String whereClause = "resolved = false AND fakeReport = false";
        DataQueryBuilder probQueryBuilder = DataQueryBuilder.create();
        probQueryBuilder.setWhereClause(whereClause);

        Backendless.Persistence.of(ReportedProblem.class).find(probQueryBuilder, new AsyncCallback<List<ReportedProblem>>() {
            @Override
            public void handleResponse(List<ReportedProblem> response) {
                int totalReports = 0;

                totalReports = response.size();

                tvReportsTotal.setText(String.valueOf(totalReports));
            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Toast.makeText(getActivity(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


        DataQueryBuilder residentQueryBuilder = DataQueryBuilder.create();

        Backendless.Persistence.of(Resident.class).find(residentQueryBuilder, new AsyncCallback<List<Resident>>() {
            @Override
            public void handleResponse(List<Resident> response) {

               tvActiveResidents.setText(String.valueOf(response.size()));

            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });


    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


}
