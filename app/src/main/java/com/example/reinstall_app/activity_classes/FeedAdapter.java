package com.example.reinstall_app.activity_classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.ReinstallApplicationClass;
import com.example.reinstall_app.app_data.ReportedProblem;
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

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private List<ReportedProblem> reports;

    FeedItemClicked activity;

    public interface FeedItemClicked
    {
        void onItemClicked(int index);
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

        TextView tvName, tvDate, tvDescription, tvCategory;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDescription = itemView.findViewById(R.id.tvDescription);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);



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

                    activity.onItemClicked(reports.indexOf((ReportedProblem) v.getTag()));
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

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final FeedAdapter.ViewHolder holder,final int i) {

        holder.feedMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if(holder.feedMap!=null)
                {

                    float zoomLevel=16.0f;

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

        if(ReinstallApplicationClass.user.getUserId().equals(reports.get(i).getOwnerId()) || ReinstallApplicationClass.user.getProperty("role").equals("Municipality"))
        {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.btnEdit.setVisibility(View.GONE);
        }

        if(ReinstallApplicationClass.user.getProperty("role").equals("Municipality"))
        {
            holder.btnDelete.setVisibility(View.VISIBLE);
        }



        Date date;

        date = reports.get(i).getCreated();

        SimpleDateFormat format = new SimpleDateFormat("dd-mm-yyyy");

        holder.tvDate.setText(format.format(date));
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {

        if (holder.mapCurrent != null)
        {
          //  holder.mapCurrent.clear();
            holder.mapCurrent.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }
}
