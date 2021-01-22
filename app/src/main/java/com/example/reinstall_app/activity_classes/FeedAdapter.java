package com.example.reinstall_app.activity_classes;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.bumptech.glide.Glide;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.ProblemType;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

        ImageView ivReportPhoto, ivViewPhoto, ivViewMap;

        TextView tvName, tvDate, tvDescription, tvCategory;
        Button btnEdit, btnDelete, btnResolved;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivReportPhoto = itemView.findViewById(R.id.ivReportPhoto);

            ivViewPhoto = itemView.findViewById(R.id.ivViewPhoto);
            ivViewMap = itemView.findViewById(R.id.ivViewMap);

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
    public void onBindViewHolder(@NonNull final FeedAdapter.ViewHolder holder,final int i) {


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

            holder.btnDelete.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.btnEdit.setVisibility(View.GONE);
        }

        if(ReinstallApplicationClass.user.getProperty("role").equals("Municipality"))
        {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnResolved.setVisibility(View.VISIBLE);
        }


        Date date;
        date = reports.get(i).getCreated();
        //SimpleDateFormat format = new SimpleDateFormat("dd-mm-yyyy");
        holder.tvDate.setText(date.toString());
        holder.ivReportPhoto.setVisibility(View.GONE);


       holder.ivViewPhoto.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               holder.ivReportPhoto.setVisibility(View.VISIBLE);
               holder.feedMap.setVisibility(View.GONE);

           }
       });

       holder.ivViewMap.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               holder.ivReportPhoto.setVisibility(View.GONE);
               holder.feedMap.setVisibility(View.VISIBLE);
           }
       });


       holder.btnDelete.setOnClickListener(new View.OnClickListener() {
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
       });

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
