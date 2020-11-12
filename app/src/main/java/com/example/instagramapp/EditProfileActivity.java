package com.example.instagramapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagramapp.Modules.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    ImageView close;
    TextView txtSave,txtChange;

    MaterialEditText editName,editBio,editUserName,editEmail;
    CircleImageView profile;
    FirebaseUser firebaseUser;
    DatabaseReference ref;

    Uri imgUri;
    StorageReference storageReference;
    StorageTask uploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        ref= FirebaseDatabase.getInstance().getReference().child("USERS");

        close = findViewById(R.id.close_edit);
        txtSave = findViewById(R.id.edit_save);
        txtChange =findViewById(R.id.txt_change);
        storageReference=FirebaseStorage.getInstance().getReference().child("UPLOADS");

        editName=findViewById(R.id.edit_name);
        editBio=findViewById(R.id.edit_bio);
        editUserName=findViewById(R.id.edit_user_name);
        editEmail=findViewById(R.id.edit_email);



        profile=findViewById(R.id.profile_edit_image);

        readCurrent();
        txtChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).setActivityTitle("SELECT PROFILE").start(EditProfileActivity.this);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).setActivityTitle("SELECT PROFILE").start(EditProfileActivity.this);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfile();
            }
        });

    }

    private void uploadProfile()
    {
        HashMap<String,Object> map =new HashMap<>();

        map.put("name",editName.getText().toString());
        map.put("bio",editBio.getText().toString());
        map.put("username",editUserName.getText().toString());

        ref.child(firebaseUser.getUid()).updateChildren(map);

        displayMessage("DETAILS SAVED");
    }

    private void readCurrent()
    {
        ref.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                editName.setText(user.getName());
                editEmail.setText(user.getEmail());
                editUserName.setText(user.getUsername());
                editBio.setText(user.getBio());
                Picasso.get().load(user.getImageurl()).placeholder(R.drawable.instagram).into(profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imgUri =  result.getUri();
            uploadImage();
        }
        else
        {
            displayMessage("something went wrong !!! ");
        }
    }

    private void uploadImage()
    {
        final ProgressDialog pd=new ProgressDialog(EditProfileActivity.this);
        pd.setMessage("UPDATING PROFILE IMAGE");
        pd.show();

        if(imgUri != null)
        {
            final StorageReference fileRef = storageReference.child(System.currentTimeMillis()+".jpeg") ;

            uploadTask = fileRef.putFile(imgUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Task<Uri> then(@NonNull Task task) throws Exception {

                    if(!task.isSuccessful())
                    {
                        throw task.getException();

                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful())
                    {
                        String downloadUri = task.getResult().toString();

                        ref.child(firebaseUser.getUid()).child("imageurl").setValue(downloadUri);
                        pd.dismiss();
                    }
                    else
                    {
                        displayMessage("upload failed");
                    }
                }
            });
        }
        else
        {
            displayMessage("no image was selected");
            pd.dismiss();
        }
    }

    void displayMessage(String message)
    {
        Toast.makeText(EditProfileActivity.this,message,Toast.LENGTH_SHORT).show();
    }
}