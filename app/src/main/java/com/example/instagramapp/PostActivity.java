package com.example.instagramapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    SocialAutoCompleteTextView socialAutoCompleteTextView;
    Toolbar toolbar;
    TextView textView;
    ImageView imageView;
    Uri postUri;
    ProgressDialog pd;
    String imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        pd=new ProgressDialog(this);
        pd.setMessage("please wait....");
        socialAutoCompleteTextView=findViewById(R.id.description);
        toolbar=findViewById(R.id.post_activity_tool_bar);
        textView=findViewById(R.id.post_submit);
        imageView=findViewById(R.id.post_image);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("NEW POST");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().start(PostActivity.this);
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                uploadImage();
            }
        });


    }

    private void uploadImage()
    {
        final StorageReference storageReference;
        storageReference= FirebaseStorage.getInstance().getReference("Posts").child(System.currentTimeMillis()+"."+getFileExtension(postUri));
        if(postUri !=null)
        {
            StorageTask uploadTask=storageReference.putFile(postUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        displayMessage(task.getException().getMessage());
                    }
                    else
                    {
                        return storageReference.getDownloadUrl();
                    }
                    return null;
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                   Uri downloadUri= (Uri) task.getResult();
                   imgUri=downloadUri.toString();
                    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Posts");
                    String postId=databaseReference.push().getKey();

                    HashMap<String,Object> map=new HashMap<>();
                    map.put("postid",postId);
                    map.put("imageurl",imgUri);
                    map.put("description",socialAutoCompleteTextView.getText().toString());
                    map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                    databaseReference.child(postId).setValue(map);

                    DatabaseReference ref =FirebaseDatabase.getInstance().getReference().child("HashTags");

                    List<String> hashtags=  socialAutoCompleteTextView.getHashtags();
                    if(!hashtags.isEmpty())
                    {
                        for(String tag:hashtags)
                        {
                            map.clear();

                            map.put("tag",tag.toLowerCase());
                            map.put("postid",postId);

                            ref.child(tag.toLowerCase()).child(postId).setValue(map);
                        }
                    }
                    pd.dismiss();
                    startActivity(new Intent(PostActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    displayMessage(e.getMessage());
                }
            });

        }
        else
        {
            displayMessage("no image was selected");
        }
    }

    private String getFileExtension(Uri imgUri)
    {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(imgUri));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case android.R.id.home :
                displayMessage("back button pressed");
                startActivity(new Intent(PostActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }



    void displayMessage(String message)
    {
        Toast.makeText(PostActivity.this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)
        {
           CropImage.ActivityResult result=CropImage.getActivityResult(data);
           postUri=result.getUri();
           imageView.setImageURI(postUri);
        }
        else
        {
            displayMessage("try again !!! ");
        }
    }
}