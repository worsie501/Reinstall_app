package com.example.reinstall_app.activity_classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.Suburb;

import java.util.List;

public class HotSpotAdapter extends RecyclerView.Adapter<HotSpotAdapter.ViewHolder> {


    final private List<Suburb> hotspots;
    ItemClicked activity;

    public interface ItemClicked
    {
        void onItemClicked(int index);
    }

    public HotSpotAdapter(Context context, List<Suburb> list)
    {
        hotspots = list;
        activity = (ItemClicked) context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        TextView tvSuburb, tvNumReports;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSuburb = itemView.findViewById(R.id.tvProblemType);
            tvNumReports = itemView.findViewById(R.id.tvNumReportsProblems);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    activity.onItemClicked(hotspots.indexOf(v.getTag()));

                }
            });
        }
    }

    @NonNull
    @Override
    public HotSpotAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hotspot_row_layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HotSpotAdapter.ViewHolder holder, int position) {

        holder.itemView.setTag(hotspots.get(holder.getAdapterPosition()));

        int totalReports = hotspots.get(position).getTotalReports();

        holder.tvNumReports.setText(String.valueOf(totalReports));
        holder.tvSuburb.setText(hotspots.get(position).getSuburbName());


    }

    @Override
    public int getItemCount() {
        return hotspots.size();
    }
}
