package com.example.instagramapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText txtEmail,txtPassword;
    TextView textView;
    Button btnLogin;
    FirebaseAuth firebaseAuth;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmail=findViewById(R.id.login_email);
        txtPassword=findViewById(R.id.login_password);
        firebaseAuth=FirebaseAuth.getInstance();
        textView=findViewById(R.id.go_to_register);
        pd=new ProgressDialog(this);
        pd.setMessage("PLEASE WAIT");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });

        btnLogin=findViewById(R.id.login_button);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email,password;
                email=txtEmail.getText().toString();
                password=txtPassword.getText().toString();
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
                {
                    displayMessage("CREDENTIALS EMPTY");
                }
                else
                {
                    pd.show();
                    loginUser(email, password);
                }
            }
        });
    }

    private void loginUser(String email, String password)
    {
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    pd.dismiss();
                    displayMessage("LOGGED IN SUCCESSFULLY");
                    startActivity(new Intent(LoginActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                displayMessage(e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                pd.dismiss();
            }
        });
    }
    void displayMessage(String message)
    {
        Toast.makeText(LoginActivity.this,message,Toast.LENGTH_SHORT).show();
    }
}