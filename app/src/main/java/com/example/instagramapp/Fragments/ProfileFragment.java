package com.example.instagramapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instagramapp.Adapters.PhotoAdapter;
import com.example.instagramapp.EditProfileActivity;
import com.example.instagramapp.FollowersActivity;
import com.example.instagramapp.MainActivity;
import com.example.instagramapp.Modules.Post;
import com.example.instagramapp.Modules.User;
import com.example.instagramapp.R;
import com.example.instagramapp.StartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {



    public ProfileFragment()
    {
        // Required empty public constructor
    }

    NavigationView navigationView;
    ImageView options;
    DrawerLayout drawerLayout;
    TextView posts,followers,following;
    TextView username;
    TextView authorname,bio;
    CircleImageView circleImageView;
    FirebaseUser fuser;
    String proId;
    RecyclerView recyclerView,recyclerView2;
    Button editProfile;
    PhotoAdapter photoAdapter;
    ImageButton btnPost,btnSave;
    List<Post> postList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        postList = new ArrayList<>();
        drawerLayout=view.findViewById(R.id.drawer);
        navigationView=view.findViewById(R.id.nav_options);
        options=view.findViewById(R.id.options);
        recyclerView2=view.findViewById(R.id.saved_post_recycle_profile);
        btnPost = view.findViewById(R.id.img_btn_post);
        btnSave = view.findViewById(R.id.img_btn_save);
        editProfile=view.findViewById(R.id.btn_edit_profile);

        fuser=FirebaseAuth.getInstance().getCurrentUser();

        String myProfileId=getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("proId","none");

        if(myProfileId.equals("none"))
        {
            proId=fuser.getUid();
        }
        else
        {
            proId = myProfileId;
            getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().clear().apply();
        }


        recyclerView = view.findViewById(R.id.post_recycle_profile);
        posts=view.findViewById(R.id.cntpost);
        followers=view.findViewById(R.id.cntfollowers);
        following=view.findViewById(R.id.cntfollowing);

        username=view.findViewById(R.id.profile_fragment_user_name);
        authorname=view.findViewById(R.id.authorname);
        posts=view.findViewById(R.id.cntpost);
        bio=view.findViewById(R.id.biotext);

        recyclerView2.setHasFixedSize(true);
        recyclerView.setHasFixedSize(true);

        circleImageView=view.findViewById(R.id.profile_fragment_user_image);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.logout)
                {
                    FirebaseAuth.getInstance().signOut();
                    getContext().startActivity(new Intent(getContext(),StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    getActivity().finish();
                }
                if(item.getItemId() == R.id.cng_pass)
                {
                    ChangePasswordDialogFragment dialogFragment=new ChangePasswordDialogFragment();
                    dialogFragment.show(((FragmentActivity)getContext()).getSupportFragmentManager(),"mydialog");

                }
                return true;
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerOpen(GravityCompat.END))
                {
                    drawerLayout.closeDrawer(GravityCompat.END);
                }
                else
                {
                    drawerLayout.openDrawer(GravityCompat.END);
                }

            }
        });

        if(proId.equals(fuser.getUid()))
        {
            editProfile.setText("EDIT PROFILE");
        }
        else
        {
            FirebaseDatabase.getInstance().getReference().child("follow").child(fuser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(proId).exists())
                    {
                        editProfile.setText("FOLLOWING");
                    }
                    else
                    {
                        editProfile.setText("FOLLOW");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bText = editProfile.getText().toString();

                if(bText.equals("EDIT PROFILE"))
                {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }
                else if(bText.equals("FOLLOW"))
                {
                    FirebaseDatabase.getInstance().getReference().child("follow").child(fuser.getUid()).child("following").child(proId).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("follow").child(proId).child("followers").child(fuser.getUid()).setValue(true);
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("follow").child(fuser.getUid()).child("following").child(proId).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("follow").child(proId).child("followers").child(fuser.getUid()).removeValue();

                }
            }
        });

        noOfFollowersAndFollowing();
        noOfPosts();
        getImageUsernameName();

        getPostLists();

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        recyclerView2.setLayoutManager(new GridLayoutManager(getContext(),3));
        photoAdapter = new PhotoAdapter(getContext(),postList);
        recyclerView.setAdapter(photoAdapter);
        recyclerView2.setAdapter(photoAdapter);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPostLists();
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView2.setVisibility(View.GONE);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSavedPostList();
                recyclerView2.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",proId);
                intent.putExtra("title","Following");
                getContext().startActivity(intent);
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",proId);
                intent.putExtra("title","Followers");
                getContext().startActivity(intent);
            }
        });

        return view;
    }

    private void getSavedPostList()
    {
        final ArrayList<String> saved =new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Saves").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                saved.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    saved.add(dataSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                postList.clear();

                for(DataSnapshot dataSnapshot2 : snapshot1.getChildren())
                {
                    Post post = dataSnapshot2.getValue(Post.class);

                    for(String e : saved)
                    {
                        if(e.equals(post.getPostid().toString()))
                        {
                            postList.add(post);
                        }
                    }
                }
                Collections.reverse(postList);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostLists()
    {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                postList.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Post post = dataSnapshot.getValue(Post.class);
                    if(post.getPublisher().equals(proId))
                    {
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                photoAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void getImageUsernameName()
    {
        FirebaseDatabase.getInstance().getReference().child("USERS").child(proId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                authorname.setText(user.getName());
                username.setText(user.getUsername());
                bio.setText(user.getBio());

                if(user.getImageurl().equals("default"))
                {
                    circleImageView.setImageResource(R.drawable.instagram);
                }
                else
                {
                    Picasso.get().load(user.getImageurl()).into(circleImageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void noOfPosts()
    {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count=0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Post post = dataSnapshot.getValue(Post.class);
                    if(post.getPublisher().equals(proId))
                    {
                        count++;
                    }
                }
                posts.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void noOfFollowersAndFollowing()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("follow").child(proId);
        reference.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}