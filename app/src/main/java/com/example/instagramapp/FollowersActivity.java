package com.example.instagramapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.instagramapp.Adapters.UserAdapter;
import com.example.instagramapp.Modules.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {

    String id,title;
    List<String> idList;

    RecyclerView recyclerView;
    Toolbar toolbar;
    UserAdapter userAdapter;
    List<User> userList;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        Intent intent = getIntent();
        userList=new ArrayList<>();

        id=intent.getStringExtra("id");
        title=intent.getStringExtra("title");

        recyclerView=findViewById(R.id.recycler_view_follow);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        toolbar=findViewById(R.id.toolbar_follower_activity);

        setSupportActionBar(toolbar);
        try{
        getSupportActionBar().setTitle(title);}
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        idList=new ArrayList<>();

        userAdapter = new UserAdapter(this,userList,false);
        recyclerView.setAdapter(userAdapter);

        switch (title)
        {
            case "Followers" : getFollowers(); break;

            case "Following" : getFollowing(); break;

            case "Likes" : getLikes(); break;

            default: Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show(); break;
        }


    }

    private void getLikes() {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    idList.add(dataSnapshot.getKey());
                }
                readUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers()
    {
        FirebaseDatabase.getInstance().getReference().child("USERS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    User user = dataSnapshot.getValue(User.class);

                    for(String p:idList)
                    {
                        if(p.equals(user.getId()))
                        {
                            userList.add(user);
                        }
                    }
                }
                Collections.reverse(userList);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowing()
    {
        FirebaseDatabase.getInstance().getReference().child("follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    idList.add(dataSnapshot.getKey());
                }
                readUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers()
    {
        FirebaseDatabase.getInstance().getReference().child("follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    idList.add(dataSnapshot.getKey());
                }
                readUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            finish();
        }
        return true;
    }
}