package com.example.reinstall_app.activity_classes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.City;
import com.example.reinstall_app.app_data.ReportedProblem;
import com.example.reinstall_app.app_data.Suburb;

import java.util.List;

public class StatisticsFragment extends Fragment
{

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;


    Spinner spnrLocation;

    RecyclerView rvList;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;

    View v;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        v = inflater.inflate(R.layout.fragment_statistics, container, false);

        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mLoginFormView = v.findViewById(R.id.login_form);
        mProgressView = v.findViewById(R.id.login_progress);
        tvLoad = v.findViewById(R.id.tvLoad);


        spnrLocation= v.findViewById(R.id.spnrLocation);

        rvList = v.findViewById(R.id.rvList);
        rvList.hasFixedSize();

        layoutManager = new LinearLayoutManager(getActivity());
        rvList.setLayoutManager(layoutManager);

        DataQueryBuilder spinBuilder = DataQueryBuilder.create();
        spinBuilder.setGroupBy("cityName");

        Backendless.Data.of(City.class).find(spinBuilder, new AsyncCallback<List<City>>() {
            @Override
            public void handleResponse(List<City> response) {

                ArrayAdapter<City> adapter1 = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,response);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spnrLocation.setAdapter(adapter1);
            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Toast.makeText(getActivity(), "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setSortBy("problemType");

        showProgress(true);
        tvLoad.setText("Retreiving info...please wait...");

        Backendless.Persistence.of(ReportedProblem.class).find(queryBuilder, new AsyncCallback<List<ReportedProblem>>() {
            @Override
            public void handleResponse(List<ReportedProblem> response) {

                myAdapter = new StatsAdapter(getActivity(), response);
                rvList.setAdapter(myAdapter);
                showProgress(false);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(getActivity(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
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
