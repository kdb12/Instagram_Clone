package com.example.instagramapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.instagramapp.Adapters.TagAdapter;
import com.example.instagramapp.Adapters.UserAdapter;
import com.example.instagramapp.Modules.User;
import com.example.instagramapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {


    private DatabaseReference databaseReference;

    public SearchFragment() {
        // Required empty public constructor
    }

    UserAdapter userAdapter;
    SocialAutoCompleteTextView socialAutoCompleteTextView;
    ImageView imageView;
    RecyclerView nameRecyclerView, hashTagRecyclerView;
    List<User> userList;
    List<String> mTags;
    List<String> tTags;
    TagAdapter tagAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        nameRecyclerView = view.findViewById(R.id.name_recycle);
        hashTagRecyclerView = view.findViewById(R.id.name_hashTag);
        socialAutoCompleteTextView = view.findViewById(R.id.my_search_bar);
        imageView = view.findViewById(R.id.sf_search);
        userList = new ArrayList<>();
        mTags = new ArrayList<>();
        tTags = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("USERS");

        nameRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        hashTagRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        nameRecyclerView.setHasFixedSize(true);
        hashTagRecyclerView.setHasFixedSize(true);
        userAdapter = new UserAdapter(getContext(), userList, true);
        tagAdapter = new TagAdapter(getContext(), mTags, tTags);
        nameRecyclerView.setAdapter(userAdapter);
        hashTagRecyclerView.setAdapter(tagAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (TextUtils.isEmpty(socialAutoCompleteTextView.getText().toString())) {
                    userList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User u = dataSnapshot.getValue(User.class);
                        userList.add(u);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        socialAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterTag(s.toString());
            }
        });
        readTags();
        return view;
    }

    private void readTags() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("HashTags");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mTags.clear();
                tTags.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    mTags.add(dataSnapshot.getKey());
                    tTags.add(dataSnapshot.getChildrenCount() + " ");
                }
                tagAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void searchUser(String s) {
        Query query = FirebaseDatabase.getInstance().getReference().child("USERS").orderByChild("username").startAt(s).endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void filterTag(String s)
    {
        List<String> searchTag;
        List<String> postTag;

        searchTag=new ArrayList<>();
        postTag=new ArrayList<>();

        for(String p:mTags)
        {
            if(p.toLowerCase().contains(s.toLowerCase()))
            {
                searchTag.add(p);
                postTag.add(tTags.get(mTags.indexOf(p)));
            }

        }
        tagAdapter.updateLists(searchTag,postTag);

    }


}