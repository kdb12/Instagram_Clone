package com.example.instagramapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.instagramapp.Fragments.HomeFragment;
import com.example.instagramapp.Fragments.NotificationFragment;
import com.example.instagramapp.Fragments.ProfileFragment;
import com.example.instagramapp.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
{
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    Fragment selector;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar=findViewById(R.id.MATOOLBAR);
        bottomNavigationView=findViewById(R.id.btmnav);

        setSupportActionBar(toolbar);

        if(findViewById(R.id.fragment_container) != null)
        {
            if(savedInstanceState !=null)
            {
                return;
            }
            selector=new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selector,null).commit();
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.ic_home:
                        selector=new HomeFragment();
                        break;
                    case R.id.ic_search:
                        selector=new SearchFragment();
                        break;
                    case R.id.ic_post:
                        selector=null;

                        startActivity(new Intent(MainActivity.this,PostActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                        break;
                    case R.id.ic_notifications:
                        selector=new NotificationFragment();
                        break;
                    case R.id.ic_profile:
                        selector=new ProfileFragment();
                        break;

                }

                if(selector!=null)
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selector,null).commit();
                }
                return true;
            }
        });

        Bundle intent = getIntent().getExtras();

        if(intent != null)
        {
            String proId = intent.getString("publisherId");

            getSharedPreferences("PROFILE",MODE_PRIVATE).edit().putString("proId",proId).apply();

            //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment(),null).commit();

            bottomNavigationView.setSelectedItemId(R.id.ic_profile);
        }
        else
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selector,null).commit();
        }

    }
}