package com.example.reinstall_app.activity_classes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.example.reinstall_app.R;

import java.util.List;

public class ChatGroupsList extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_groups_list);

        getGroupList();

    }

    private void getGroupList() {

        GroupsRequest groupsRequest = new GroupsRequest.GroupsRequestBuilder().build();

        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List<Group> list) {
                updateUI(list);
            }
            @Override
            public void onError(CometChatException e) {

            }
        });

    }

    private void updateUI(List<Group> list) {

        RecyclerView groupsRecyclerView = findViewById(R.id.rvChatGroupList);
        groupsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        GroupsAdapter groupsAdapter = new GroupsAdapter(this, list);
        groupsRecyclerView.setAdapter(groupsAdapter);
    }


}