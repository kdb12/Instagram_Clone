package com.example.instagramapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity
{
    LinearLayout linearLayout;
    ImageView imageView;
    Button btnLogin,btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        linearLayout=findViewById(R.id.linearlayotid);
        imageView=findViewById(R.id.insta_logo);
        btnLogin=findViewById(R.id.btn_login);
        btnRegister=findViewById(R.id.btn_register);

        linearLayout.animate().alpha(0f).setDuration(1);

        TranslateAnimation translateAnimation=new TranslateAnimation(0,0,0,-1000);
        translateAnimation.setDuration(1000);
        translateAnimation.setFillAfter(false);
        translateAnimation.setAnimationListener(new MyAnimationListener());

        imageView.setAnimation(translateAnimation);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this,RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });

    }

    class MyAnimationListener implements Animation.AnimationListener
    {

        @Override
        public void onAnimationStart(Animation animation)
        {

        }

        @Override
        public void onAnimationEnd(Animation animation)
        {
            imageView.clearAnimation();
            imageView.setVisibility(View.INVISIBLE);
            linearLayout.animate().alpha(1f).setDuration(1000);
        }

        @Override
        public void onAnimationRepeat(Animation animation)
        {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            startActivity(new Intent(StartActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }
    }
}