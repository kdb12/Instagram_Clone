package com.example.instagramapp.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagramapp.Adapters.PostAdapter;
import com.example.instagramapp.Modules.Post;
import com.example.instagramapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostDetailFragment extends Fragment {



    public PostDetailFragment()
    {

    }

    RecyclerView recyclerView;
    PostAdapter postAdapter;
    String postId;
    List<Post> postList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        postList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.post_detail_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        postId = getContext().getSharedPreferences("POSTED", Context.MODE_PRIVATE).getString("postId","none");

        postAdapter = new PostAdapter(getContext(),postList);
        recyclerView.setAdapter(postAdapter);

        FirebaseDatabase.getInstance().getReference().child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                postList.add(snapshot.getValue(Post.class));
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }
}