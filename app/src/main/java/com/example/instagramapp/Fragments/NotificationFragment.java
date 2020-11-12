package com.example.instagramapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.instagramapp.Adapters.NotificationAdapter;
import com.example.instagramapp.Modules.Notification;
import com.example.instagramapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class NotificationFragment extends Fragment {



    public NotificationFragment() {
        // Required empty public constructor
    }

    NotificationAdapter notificationAdapter;
    RecyclerView recyclerView;
    List<Notification> notificationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        notificationList=new ArrayList<>();
        recyclerView = view.findViewById(R.id.notification_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationAdapter= new NotificationAdapter(getContext(),notificationList);
        recyclerView.setAdapter(notificationAdapter);

        getNotificationsList();
        return view;
    }

    private void getNotificationsList()
    {
        FirebaseDatabase.getInstance().getReference().child("Notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Notification notification = dataSnapshot.getValue(Notification.class);


                        notificationList.add(notification);


                }
                Collections.reverse(notificationList);
                notificationAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}