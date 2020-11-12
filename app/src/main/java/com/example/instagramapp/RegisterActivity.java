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

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText txtUserName,txtName,txtEmail,txtPassword;
    TextView txtLogin;
    Button btnRegister;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtUserName=findViewById(R.id.txt_username);
        txtName=findViewById(R.id.txt_name);
        txtEmail=findViewById(R.id.txt_email);
        txtPassword=findViewById(R.id.txt_password);
        txtLogin=findViewById(R.id.goToLogin);
        pd=new ProgressDialog(this);
        pd.setMessage("PLEASE WAIT");

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        firebaseAuth=FirebaseAuth.getInstance();
        btnRegister=findViewById(R.id.btn_register2);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName=txtUserName.getText().toString();
                String name=txtName.getText().toString();
                String email=txtEmail.getText().toString();
                String password=txtPassword.getText().toString();

                if(TextUtils.isEmpty(userName)||TextUtils.isEmpty(name)||TextUtils.isEmpty(email)||TextUtils.isEmpty(password))
                {
                    displayMessage("EMPTY CREDENTIALS");
                }
                else if(password.length() < 6)
                {
                    displayMessage("SHORT PASSWORD");
                }
                else
                {
                    registerUser(userName,name,email,password);
                }

            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });

    }

    private void registerUser(final String userName, final String name, final String email, final String password)
    {
        pd.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                HashMap<String,Object> map = new HashMap<>();
                map.put("name",name);
                map.put("email",email);
                map.put("username",userName);
                map.put("id",firebaseAuth.getCurrentUser().getUid());
                map.put("password",password);
                map.put("bio",".");
                map.put("imageurl","default");

                databaseReference.child("USERS").child(firebaseAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            pd.dismiss();
                            displayMessage("UPDATE YOUR PROFILE");
                            startActivity(new Intent(RegisterActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        }
                        if(task.isCanceled())
                        {
                            pd.dismiss();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                displayMessage(e.getMessage());
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                pd.dismiss();
                displayMessage("REGISTRATION FAILED");
            }
        });
    }

    void displayMessage(String message)
    {
        Toast.makeText(RegisterActivity.this,message,Toast.LENGTH_SHORT).show();
    }
}