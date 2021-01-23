package com.example.reinstall_app.activity_classes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.models.Group;
import com.example.reinstall_app.R;
import com.example.reinstall_app.app_data.Suburb;

import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupViewHolder> {

    private List<Group> groups;
    private Context context;

    public GroupsAdapter(Context context, List<Group> groups){
        this.groups = groups;
        this.context = context;
    }


    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GroupViewHolder(LayoutInflater.from(context).inflate(R.layout.group_list_row_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        holder.bind(groups.get(position));
    }

    @Override
    public int getItemCount() {return groups.size(); }

        class GroupViewHolder extends RecyclerView.ViewHolder
        {

            TextView tvGroupName;
            LinearLayout containerLayout;


            public GroupViewHolder(@NonNull View itemView) {
                super(itemView);

                tvGroupName = itemView.findViewById(R.id.tvGroupName);
                containerLayout = itemView.findViewById(R.id.containerLayout);
            }

            public void bind(Group group){

                tvGroupName.setText(group.getName());
                containerLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ChatActivity.start(context, group.getGuid());

                    }
                });
            }
        }

    }

