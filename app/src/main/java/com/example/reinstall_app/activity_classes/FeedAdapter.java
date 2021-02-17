package com.example.reinstall_app.activity_classes;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.bumptech.glide.Glide;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.FlaggedReports;
import com.example.reinstall_app.app_data.ProblemType;
import com.example.reinstall_app.app_data.ProblemVerifications;
import com.example.reinstall_app.app_data.ReinstallApplicationClass;
import com.example.reinstall_app.app_data.ReportedProblem;
import com.example.reinstall_app.app_data.Suburb;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import static android.content.Context.BATTERY_SERVICE;
import static com.example.reinstall_app.app_data.ReinstallApplicationClass.activeProblems;
import static com.example.reinstall_app.app_data.ReinstallApplicationClass.problemTypes;
import static com.example.reinstall_app.app_data.ReinstallApplicationClass.suburbList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private List<ReportedProblem> reports;
    private Context context;
    FeedItemClicked activity;




    public interface FeedItemClicked
    {
        void onFeedItemClicked(int index);
    }

    public FeedAdapter (Context context, List<ReportedProblem> list)
    {
        reports = list;
        activity = (FeedItemClicked) context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback
    {
        GoogleMap mapCurrent;
        MapView feedMap;

        ImageView ivReportPhoto, ivUrgency, ivMore;

        TextView tvName, tvDate, tvDescription, tvCategory;
        Button btnEdit, btnDelete, btnResolved;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivReportPhoto = itemView.findViewById(R.id.ivReportPhoto);

            ivUrgency = itemView.findViewById(R.id.ivUrgency);
            ivMore = itemView.findViewById(R.id.ivMore);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnResolved = itemView.findViewById(R.id.btnResolved);

            feedMap = (MapView) itemView.findViewById(R.id.feedMap);

            if(feedMap != null)
            {
                feedMap.onCreate(null);
                feedMap.onResume();
                feedMap.getMapAsync(this);
            }


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onFeedItemClicked(reports.indexOf((ReportedProblem) v.getTag()));
                }
            });

        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(itemView.getContext());
            mapCurrent = googleMap;

        }

    }


    @NonNull
    @Override
    public FeedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reports_feed_layout, parent, false);

            context = parent.getContext();

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final FeedAdapter.ViewHolder holder,final int i)  {


        holder.feedMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if(holder.feedMap!=null)
                {

                    float zoomLevel = 16.0f;

                    LatLng latLng = new LatLng(reports.get(i).getY(), reports.get(i).getX() );

                    MarkerOptions markerOptions=new MarkerOptions();

                    markerOptions.position(latLng);

                    holder.mapCurrent.addMarker(new MarkerOptions().position(latLng));

                    holder.mapCurrent.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));


                }
            }
        });

        holder.itemView.setTag(reports.get(i));

        holder.tvName.setText(reports.get(i).getUserName());
        holder.tvCategory.setText(reports.get(i).getProblemType());
        holder.tvDescription.setText(reports.get(i).getDescription());
        holder.btnDelete.setVisibility(View.GONE);
        holder.btnEdit.setVisibility(View.GONE);
        holder.btnResolved.setVisibility(View.GONE);


        Glide.with(context).load(reports.get(i).getPhoto()).into(holder.ivReportPhoto);

        if(ReinstallApplicationClass.user.getUserId().equals(reports.get(i).getOwnerId()) || ReinstallApplicationClass.user.getProperty("role").equals("Municipality"))
        {
            //Edit will be implemented at a later stage;
           //holder.btnEdit.setVisibility(View.VISIBLE);
           // holder.btnDelete.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.btnEdit.setVisibility(View.GONE);
        }

        if(ReinstallApplicationClass.user.getProperty("role").equals("Municipality"))
        {
           // holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnResolved.setVisibility(View.VISIBLE);
        }


        if(reports.get(i).isResolved())
        {
            holder.btnResolved.setVisibility(View.GONE);
        }



        Date date;
        date = reports.get(i).getCreated();
        //SimpleDateFormat format = new SimpleDateFormat("dd-mm-yyyy");
        holder.tvDate.setText(date.toString());
        holder.ivReportPhoto.setVisibility(View.GONE);

        if(reports.get(i).getReportUrgency().equals("Low"))
            {

                holder.ivUrgency.setImageResource(R.drawable.low);

            }
        else if(reports.get(i).getReportUrgency().equals("Medium"))
        {
            holder.ivUrgency.setImageResource(R.drawable.medium);
        }
        else if(reports.get(i).getReportUrgency().equals("High"))
        {
            holder.ivUrgency.setImageResource(R.drawable.high);
        }
        else if(reports.get(i).getReportUrgency().equals("Critical"))
        {
            holder.ivUrgency.setImageResource(R.drawable.critical);
        }


        holder.ivUrgency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Urgency: " + reports.get(i).getReportUrgency(), Toast.LENGTH_SHORT).show();
            }
        });


        //Popup menu
        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popup = new PopupMenu(context, v);

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


                popup.inflate(R.menu.popup_menu);

                MenuItem verifyMenuItem = popup.getMenu().findItem(R.id.verify);
                MenuItem reportMenuItem = popup.getMenu().findItem(R.id.reportAsFake);

                if(reports.get(i).isVerifiedReport() || reports.get(i).isFakeReport() || reports.get(i).isResolved() )
                {
                    verifyMenuItem.setVisible(false);
                    reportMenuItem.setVisible(false);
                }
                else
                {
                    verifyMenuItem.setVisible(true);
                    reportMenuItem.setVisible(true);
                }

                
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId())
                        {

                            case R.id.reportAsFake:

                                int PAGESIZE = 80;
                                DataQueryBuilder verifyQBuilder = DataQueryBuilder.create();
                                verifyQBuilder.setPageSize(PAGESIZE);


                                Backendless.Persistence.of(ProblemVerifications.class).find(verifyQBuilder, new AsyncCallback<List<ProblemVerifications>>() {
                                    @Override
                                    public void handleResponse(List<ProblemVerifications> response) {

                                        boolean verificationFlag = false;

                                        for(int k = 0; k < response.size(); k++ )
                                        {
                                            if(response.get(k).getVerifiedProblem().equals(ReinstallApplicationClass.activeProblems.get(i).getObjectId()) &&
                                                    response.get(k).getVerifiedBy().equals(ReinstallApplicationClass.user.getEmail()))
                                            {

                                                verificationFlag = true;
                                                k = response.size();
                                            }
                                            else
                                            {

                                                verificationFlag = false;

                                            }
                                        }

                                        if(verificationFlag == false)
                                        {

                                            DataQueryBuilder flagQBuilder = DataQueryBuilder.create();
                                            flagQBuilder.setPageSize(PAGESIZE);

                                            Backendless.Persistence.of(FlaggedReports.class).find(flagQBuilder, new AsyncCallback<List<FlaggedReports>>() {
                                                @Override
                                                public void handleResponse(List<FlaggedReports> response) {

                                                    boolean flag = false;

                                                    for(int k = 0; k < response.size(); k++ )
                                                    {
                                                        if(response.get(k).getProblemId().equals(ReinstallApplicationClass.activeProblems.get(i).getObjectId()) &&
                                                                response.get(k).getReportedBy().equals(ReinstallApplicationClass.user.getEmail()))
                                                        {

                                                            flag = true;
                                                            k = response.size();
                                                        }
                                                        else
                                                        {

                                                            flag = false;

                                                        }
                                                    }

                                                    if(flag == false )
                                                    {

                                                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                                                        bottom_notification bottomNotification = new bottom_notification();
                                                        bottomNotification.show(activity.getSupportFragmentManager(), "bottomNotificiation");

                                                        FlaggedReports flaggedReport = new FlaggedReports();
                                                        flaggedReport.setProblemId(ReinstallApplicationClass.activeProblems.get(i).getObjectId());
                                                        flaggedReport.setReportedBy(ReinstallApplicationClass.user.getEmail());

                                                        Backendless.Persistence.save(flaggedReport, new AsyncCallback<FlaggedReports>() {
                                                            @Override
                                                            public void handleResponse(FlaggedReports response) {

                                                            }

                                                            @Override
                                                            public void handleFault(BackendlessFault fault) {

                                                                Toast.makeText(context, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();

                                                            }
                                                        });


                                                        ReinstallApplicationClass.activeProblems.get(i).setFakeReportCount(ReinstallApplicationClass.activeProblems.get(i).getFakeReportCount() + 1);

                                                        Backendless.Persistence.save(ReinstallApplicationClass.activeProblems.get(i), new AsyncCallback<ReportedProblem>() {
                                                            @Override
                                                            public void handleResponse(ReportedProblem response) {

                                                            }

                                                            @Override
                                                            public void handleFault(BackendlessFault fault) {
                                                                Toast.makeText(context, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                        String activeWhereClause = "resolved = false AND fakeReport = false";

                                                        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                                                        queryBuilder.setSortBy("created");
                                                        queryBuilder.setWhereClause(activeWhereClause);
                                                        queryBuilder.setPageSize(PAGESIZE);

                                                        Backendless.Persistence.of(ReportedProblem.class).find(queryBuilder, new AsyncCallback<List<ReportedProblem>>() {
                                                            @Override
                                                            public void handleResponse(List<ReportedProblem> response) {

                                                                ReinstallApplicationClass.activeProblems = response;

                                                            }

                                                            @Override
                                                            public void handleFault(BackendlessFault fault) {

                                                                Toast.makeText(context, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();

                                                            }
                                                        });


                                                        if(ReinstallApplicationClass.activeProblems.get(i).getFakeReportCount() >= 3 ||
                                                                ReinstallApplicationClass.user.getProperty("role").equals("Municipality") )
                                                        {

                                                            ReinstallApplicationClass.activeProblems.get(i).setFakeReport(true);

                                                            Backendless.Persistence.save(ReinstallApplicationClass.activeProblems.get(i), new AsyncCallback<ReportedProblem>() {
                                                                @Override
                                                                public void handleResponse(ReportedProblem response) {
                                                                    notifyItemRemoved(i);
                                                                }

                                                                @Override
                                                                public void handleFault(BackendlessFault fault) {
                                                                    Toast.makeText(context, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });


                                                            for(int j = 0; j < problemTypes.size(); j++)
                                                            {

                                                                if(problemTypes.get(j).getProblemName().equals(activeProblems.get(i).getProblemType()))
                                                                {

                                                                    //Toast.makeText(context, "" + problemTypes.get(i).getObjectId(), Toast.LENGTH_SHORT).show();

                                                                    problemTypes.get(j).setTotalProblems(problemTypes.get(j).getTotalProblems() - 1);

                                                                    Backendless.Data.of(ProblemType.class).save(ReinstallApplicationClass.problemTypes.get(j), new AsyncCallback<ProblemType>() {
                                                                        @Override
                                                                        public void handleResponse(ProblemType response) {

                                                                            // Toast.makeText(context, "Decreased " + response.getProblemName() + " : " + response.getTotalProblems(), Toast.LENGTH_SHORT).show();
                                                                        }

                                                                        @Override
                                                                        public void handleFault(BackendlessFault fault) {
                                                                            Toast.makeText(context, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                    break;
                                                                }

                                                            }

                                                            for(int j = 0; j < suburbList.size(); j++)
                                                            {

                                                                if(suburbList.get(j).getSuburbName().equals(activeProblems.get(i).getSuburb()))
                                                                {

                                                                    // Toast.makeText(context, "" + suburbList.get(i).getObjectId(), Toast.LENGTH_SHORT).show();

                                                                    ReinstallApplicationClass.suburbList.get(j).setTotalReports(suburbList.get(j).getTotalReports() - 1);

                                                                    Backendless.Persistence.save(suburbList.get(j), new AsyncCallback<Suburb>() {
                                                                        @Override
                                                                        public void handleResponse(Suburb response) {
                                                                          //  Toast.makeText(context, "Suburb problem count Decreased", Toast.LENGTH_LONG).show();
                                                                        }

                                                                        @Override
                                                                        public void handleFault(BackendlessFault fault) {

                                                                            Toast.makeText(context, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();

                                                                        }
                                                                    });

                                                                    break;
                                                                }
                                                            }


                                                        }

                                                    }
                                                    else
                                                    {

                                                        Toast.makeText(context, "Report could only be made once", Toast.LENGTH_SHORT).show();
                                                    }

                                                }

                                                @Override
                                                public void handleFault(BackendlessFault fault) {

                                                    Toast.makeText(context, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();

                                                }
                                            });

                                        }
                                        else
                                        {
                                            Toast.makeText(context, "Unable to report, verification already made.", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {

                                    }
                                });


                                return true;

                            case R.id.verify:

                                int VPAGESIZE = 80;

                                DataQueryBuilder reportedQBuilder = DataQueryBuilder.create();
                                reportedQBuilder.setPageSize(VPAGESIZE);

                                Backendless.Persistence.of(FlaggedReports.class).find(reportedQBuilder, new AsyncCallback<List<FlaggedReports>>() {
                                    @Override
                                    public void handleResponse(List<FlaggedReports> response) {

                                        boolean reportedFlag = false;

                                        for(int k = 0; k < response.size(); k++ )
                                        {
                                            if(response.get(k).getProblemId().equals(ReinstallApplicationClass.activeProblems.get(i).getObjectId()) &&
                                                    response.get(k).getReportedBy().equals(ReinstallApplicationClass.user.getEmail()))
                                            {

                                                reportedFlag = true;
                                                k = response.size();
                                            }
                                            else
                                            {

                                                reportedFlag = false;

                                            }
                                        }

                                        if(reportedFlag == false)
                                        {


                                            DataQueryBuilder verificationQBuilder = DataQueryBuilder.create();
                                            verificationQBuilder.setPageSize(VPAGESIZE);

                                            Backendless.Persistence.of(ProblemVerifications.class).find(verificationQBuilder, new AsyncCallback<List<ProblemVerifications>>() {
                                                @Override
                                                public void handleResponse(List<ProblemVerifications> response) {

                                                    boolean flag = false;

                                                    for(int k = 0; k < response.size(); k++ )
                                                    {
                                                        if(response.get(k).getVerifiedProblem().equals(ReinstallApplicationClass.activeProblems.get(i).getObjectId()) &&
                                                                response.get(k).getVerifiedBy().equals(ReinstallApplicationClass.user.getEmail()))
                                                        {

                                                            flag = true;
                                                            k = response.size();
                                                        }
                                                        else
                                                        {

                                                            flag = false;

                                                        }
                                                    }

                                                    if(flag == false ) {

                                                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                                                        BottomVerifyNotice bottomVerifyNotice = new BottomVerifyNotice();
                                                        bottomVerifyNotice.show(activity.getSupportFragmentManager(), "verifyNotice");

                                                        ProblemVerifications problemVerification = new ProblemVerifications();
                                                        problemVerification.setVerifiedBy(ReinstallApplicationClass.user.getEmail());
                                                        problemVerification.setVerifiedProblem(ReinstallApplicationClass.activeProblems.get(i).getObjectId());

                                                        Backendless.Persistence.save(problemVerification, new AsyncCallback<ProblemVerifications>() {
                                                            @Override
                                                            public void handleResponse(ProblemVerifications response) {

                                                            }

                                                            @Override
                                                            public void handleFault(BackendlessFault fault) {

                                                                Toast.makeText(context, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();

                                                            }
                                                        });


                                                        ReinstallApplicationClass.activeProblems.get(i).setVerificationCount(ReinstallApplicationClass.activeProblems.get(i).getVerificationCount() + 1);


                                                        Backendless.Persistence.save(ReinstallApplicationClass.activeProblems.get(i), new AsyncCallback<ReportedProblem>() {
                                                            @Override
                                                            public void handleResponse(ReportedProblem response) {

                                                            }

                                                            @Override
                                                            public void handleFault(BackendlessFault fault) {

                                                                Toast.makeText(context, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();

                                                            }
                                                        });

                                                        String activeWhereClause = "resolved = false AND fakeReport = false";

                                                        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                                                        queryBuilder.setSortBy("created");
                                                        queryBuilder.setWhereClause(activeWhereClause);
                                                        queryBuilder.setPageSize(VPAGESIZE);

                                                        Backendless.Persistence.of(ReportedProblem.class).find(queryBuilder, new AsyncCallback<List<ReportedProblem>>() {
                                                            @Override
                                                            public void handleResponse(List<ReportedProblem> response) {

                                                                ReinstallApplicationClass.activeProblems = response;

                                                            }

                                                            @Override
                                                            public void handleFault(BackendlessFault fault) {

                                                                Toast.makeText(context, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();

                                                            }
                                                        });


                                                        if (ReinstallApplicationClass.activeProblems.get(i).getVerificationCount() >= 3 ||
                                                                ReinstallApplicationClass.user.getProperty("role").equals("Municipality")) {

                                                            ReinstallApplicationClass.activeProblems.get(i).setVerifiedReport(true);

                                                            Backendless.Persistence.save(ReinstallApplicationClass.activeProblems.get(i), new AsyncCallback<ReportedProblem>() {
                                                                @Override
                                                                public void handleResponse(ReportedProblem response) {

                                                                    Toast.makeText(activity, "Report has been verified!", Toast.LENGTH_SHORT).show();

                                                                }

                                                                @Override
                                                                public void handleFault(BackendlessFault fault) {

                                                                    Toast.makeText(context, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();

                                                                }
                                                            });


                                                        }
                                                    }
                                                    else
                                                    {

                                                        Toast.makeText(context, "Reports can only by verified once!", Toast.LENGTH_SHORT).show();

                                                    }

                                                }

                                                @Override
                                                public void handleFault(BackendlessFault fault) {

                                                    Toast.makeText(context, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();

                                                }
                                            });

                                        }
                                        else
                                        {

                                            Toast.makeText(context, "Unable to verify, Report already made", Toast.LENGTH_SHORT).show();

                                        }

                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {

                                        Toast.makeText(context, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });


                                return true;

                            case R.id.viewPhoto:
                                holder.ivReportPhoto.setVisibility(View.VISIBLE);
                                holder.feedMap.setVisibility(View.GONE);
                                return true;

                            case R.id.viewMap:
                                holder.ivReportPhoto.setVisibility(View.GONE);
                                holder.feedMap.setVisibility(View.VISIBLE);
                                return true;

                            default:
                                return false;

                        }

                    }
                });
                popup.show();
            }
        });

        ///////////////////////////////////////////////////////////////////////////RESOLVE REPORT IF USER IS REGISTERED AS A MUNICIPALITY
        holder.btnResolved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ReinstallApplicationClass.activeProblems.get(i).setResolved(true);

                Backendless.Persistence.save(ReinstallApplicationClass.activeProblems.get(i), new AsyncCallback<ReportedProblem>() {
                    @Override
                    public void handleResponse(ReportedProblem response) {


                        Toast.makeText(context, "Report marked as Resolved", Toast.LENGTH_SHORT).show();
                        notifyItemRemoved(i);

                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {

                        Toast.makeText(context, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


                for(int j = 0; j < problemTypes.size(); j++)
                {

                    if(problemTypes.get(j).getProblemName().equals(activeProblems.get(i).getProblemType()))
                    {

                        //Toast.makeText(context, "" + problemTypes.get(i).getObjectId(), Toast.LENGTH_SHORT).show();

                        problemTypes.get(j).setTotalProblems(problemTypes.get(j).getTotalProblems() - 1);

                        Backendless.Data.of(ProblemType.class).save(ReinstallApplicationClass.problemTypes.get(j), new AsyncCallback<ProblemType>() {
                            @Override
                            public void handleResponse(ProblemType response) {

                                // Toast.makeText(context, "Decreased " + response.getProblemName() + " : " + response.getTotalProblems(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Toast.makeText(context, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    }

                }

                for(int j = 0; j < suburbList.size(); j++)
                {

                    if(suburbList.get(j).getSuburbName().equals(activeProblems.get(i).getSuburb()))
                    {

                        // Toast.makeText(context, "" + suburbList.get(i).getObjectId(), Toast.LENGTH_SHORT).show();

                        ReinstallApplicationClass.suburbList.get(j).setTotalReports(suburbList.get(j).getTotalReports() - 1);

                        Backendless.Persistence.save(suburbList.get(j), new AsyncCallback<Suburb>() {
                            @Override
                            public void handleResponse(Suburb response) {
                                Toast.makeText(context, "Suburb problem count Decreased", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {

                                Toast.makeText(context, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

                        break;
                    }
                }



            }
        });


        ///////////////////////////////////////////////////////////////////////////////////////////////DELETE REPORT IF USER OWNS REPORT
     /*  holder.btnDelete.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               for(int j = 0; j < problemTypes.size(); j++)
               {

                   if(problemTypes.get(j).getProblemName().equals(reports.get(i).getProblemType()))
                   {

                       //Toast.makeText(context, "" + problemTypes.get(i).getObjectId(), Toast.LENGTH_SHORT).show();

                       problemTypes.get(j).setTotalProblems(problemTypes.get(j).getTotalProblems() - 1);

                       Backendless.Data.of(ProblemType.class).save(ReinstallApplicationClass.problemTypes.get(j), new AsyncCallback<ProblemType>() {
                           @Override
                           public void handleResponse(ProblemType response) {

                              // Toast.makeText(context, "Decreased " + response.getProblemName() + " : " + response.getTotalProblems(), Toast.LENGTH_SHORT).show();
                           }

                           @Override
                           public void handleFault(BackendlessFault fault) {
                               Toast.makeText(context, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                           }
                       });
                       break;
                   }

               }

               for(int j = 0; j < suburbList.size(); j++)
               {

                   if(suburbList.get(j).getSuburbName().equals(reports.get(i).getSuburb()))
                   {

                       // Toast.makeText(context, "" + suburbList.get(i).getObjectId(), Toast.LENGTH_SHORT).show();

                       ReinstallApplicationClass.suburbList.get(j).setTotalReports(suburbList.get(j).getTotalReports() - 1);

                       Backendless.Persistence.save(suburbList.get(j), new AsyncCallback<Suburb>() {
                           @Override
                           public void handleResponse(Suburb response) {
                               Toast.makeText(context, "Suburb problem count Decreased", Toast.LENGTH_LONG).show();
                           }

                           @Override
                           public void handleFault(BackendlessFault fault) {

                               Toast.makeText(context, "Error: "+fault.getMessage(), Toast.LENGTH_SHORT).show();

                           }
                       });

                       break;
                   }
               }

               Backendless.Persistence.of(ReportedProblem.class).remove(reports.get(i), new AsyncCallback<Long>() {
                   @Override
                   public void handleResponse(Long response) {
                       notifyItemRemoved(i);
                       Toast.makeText(context, "Report successfully removed!", Toast.LENGTH_SHORT).show();
                   }

                   @Override
                   public void handleFault(BackendlessFault fault) {
                       Toast.makeText(context, "Error: " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                   }
               });

           }
       });*/

    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {

        if (holder.mapCurrent != null)
        {
            holder.mapCurrent.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }
}
