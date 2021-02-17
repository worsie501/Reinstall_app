package com.example.reinstall_app.activity_classes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
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
import com.example.reinstall_app.app_data.ReinstallApplicationClass;
import com.example.reinstall_app.app_data.ReportedProblem;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class FeedFragment extends Fragment
{

    TextView tvFilter;

    RecyclerView rvFeed;
    RecyclerView.Adapter feedAdapter;
    RecyclerView.LayoutManager layoutManager;
    View v;

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    public FeedFragment(){}


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         v = inflater.inflate(R.layout.fragment_feed, container, false);

        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rvFeed = v.findViewById(R.id.rvFeed);
        rvFeed.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this.getActivity());
        rvFeed.setLayoutManager(layoutManager);

        mLoginFormView = v.findViewById(R.id.login_form);
        mProgressView = v.findViewById(R.id.login_progress);
        tvLoad = v.findViewById(R.id.tvLoad);

        tvFilter = v.findViewById(R.id.tvFilter);

        String activeWhereClause = "resolved = false AND fakeReport = false";

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setSortBy("created");
        queryBuilder.setWhereClause(activeWhereClause);
        int PAGESIZE = 80;
        queryBuilder.setPageSize(PAGESIZE);

        showProgress(true);
        tvLoad.setText("Retreiving info...please wait...");

        Backendless.Persistence.of(ReportedProblem.class).find(queryBuilder, new AsyncCallback<List<ReportedProblem>>() {
            @Override
            public void handleResponse(List<ReportedProblem> response) {

                ReinstallApplicationClass.activeProblems = response;
                feedAdapter = new FeedAdapter(getActivity(), response);
                rvFeed.setAdapter(feedAdapter);
                showProgress(false);
            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Toast.makeText(getActivity(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                showProgress(false);
            }
        });



        tvFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PopupMenu popup = new PopupMenu(getContext(), v);

                try{
                    Field[] fields = popup.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popup);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                popup.inflate(R.menu.feed_filter);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.fakeReports:

                                String fakeClause = "fakeReport = true";

                                DataQueryBuilder fakeQBuilder = DataQueryBuilder.create();
                                fakeQBuilder.setWhereClause(fakeClause);
                                fakeQBuilder.setPageSize(PAGESIZE);

                                showProgress(true);
                                tvLoad.setText("Retreiving info...please wait...");

                                Backendless.Persistence.of(ReportedProblem.class).find(fakeQBuilder, new AsyncCallback<List<ReportedProblem>>() {
                                    @Override
                                    public void handleResponse(List<ReportedProblem> response) {

                                        feedAdapter = new FeedAdapter(getActivity(), response);
                                        rvFeed.setAdapter(feedAdapter);
                                        showProgress(false);
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {

                                        Toast.makeText(getActivity(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                        showProgress(false);
                                    }
                                });

                                return true;

                            case R.id.verifiedReports:

                                String verifiedClause = "verifiedReport = true AND resolved = false";

                                DataQueryBuilder verifiedQBuilder = DataQueryBuilder.create();
                                verifiedQBuilder.setWhereClause(verifiedClause);
                                verifiedQBuilder.setPageSize(PAGESIZE);

                                showProgress(true);
                                tvLoad.setText("Retreiving info...please wait...");

                                Backendless.Persistence.of(ReportedProblem.class).find(verifiedQBuilder, new AsyncCallback<List<ReportedProblem>>() {
                                    @Override
                                    public void handleResponse(List<ReportedProblem> response) {

                                        feedAdapter = new FeedAdapter(getActivity(), response);
                                        rvFeed.setAdapter(feedAdapter);
                                        showProgress(false);
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {

                                        Toast.makeText(getActivity(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                        showProgress(false);
                                    }
                                });


                                return true;

                            case R.id.resolvedReports:

                                String whereClause = "resolved = true";

                                DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                                queryBuilder.setWhereClause(whereClause);
                                queryBuilder.setPageSize(PAGESIZE);

                                showProgress(true);
                                tvLoad.setText("Retreiving info...please wait...");

                                Backendless.Persistence.of(ReportedProblem.class).find(queryBuilder, new AsyncCallback<List<ReportedProblem>>() {
                                    @Override
                                    public void handleResponse(List<ReportedProblem> response) {

                                        feedAdapter = new FeedAdapter(getActivity(), response);
                                        rvFeed.setAdapter(feedAdapter);
                                        showProgress(false);
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {

                                        Toast.makeText(getActivity(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                        showProgress(false);
                                    }
                                });
                                return true;

                            case R.id.activeReports:


                                String activeWhereClause = "resolved = false AND fakeReport = false";
                                DataQueryBuilder aQueryBuilder = DataQueryBuilder.create();
                                aQueryBuilder.setSortBy("created");
                                aQueryBuilder.setWhereClause(activeWhereClause);
                                int PAGESIZE = 80;
                                aQueryBuilder.setPageSize(PAGESIZE);

                                showProgress(true);
                                tvLoad.setText("Retreiving info...please wait...");

                                Backendless.Persistence.of(ReportedProblem.class).find(aQueryBuilder, new AsyncCallback<List<ReportedProblem>>() {
                                    @Override
                                    public void handleResponse(List<ReportedProblem> response) {

                                        feedAdapter = new FeedAdapter(getActivity(), response);
                                        rvFeed.setAdapter(feedAdapter);
                                        showProgress(false);
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {

                                        Toast.makeText(getActivity(), "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                        showProgress(false);
                                    }
                                });


                                return true;

                            default:
                                return false;

                        }
                    }
                });

                popup.show();
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
