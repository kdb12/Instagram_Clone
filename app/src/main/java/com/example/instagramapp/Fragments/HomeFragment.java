package com.example.instagramapp.Fragments;

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
import com.example.instagramapp.Modules.User;
import com.example.instagramapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment
{

    List<Post> posts;
    RecyclerView recyclerView;
    PostAdapter postAdapter;

    List<String> followingList;


    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView=view.findViewById(R.id.recycle_post);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        posts=new ArrayList<>();
        followingList=new ArrayList<>();
        postAdapter=new PostAdapter(getContext(),posts);
        recyclerView.setAdapter(postAdapter);
        readFollowingUsers();

        return view;
    }

    private void readFollowingUsers()
    {
        FirebaseDatabase.getInstance().getReference().child("follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    followingList.add(dataSnapshot.getKey());
                }

                followingList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

                readPosts();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readPosts()
    {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Post post=dataSnapshot.getValue(Post.class);
                    for(String userId : followingList)
                    {

                            if(post.getPublisher().equals(userId))
                            {
                                posts.add(post);
                            }

                    }

                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}