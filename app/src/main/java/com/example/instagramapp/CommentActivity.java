package com.example.instagramapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagramapp.Adapters.CommentAdapter;
import com.example.instagramapp.Modules.Comment;
import com.example.instagramapp.Modules.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    EditText addComment;
    CircleImageView profile;
    TextView post;
    Toolbar toolbar;

    RecyclerView recyclerView;
    CommentAdapter commentAdapter;

    List<Comment> commentList;

   String postId,authorId;

   FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        toolbar=findViewById(R.id.comment_tool_bar);
        commentList=new ArrayList<>();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("COMMENTS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        addComment=findViewById(R.id.comment_text);
        post=findViewById(R.id.post_comment);
        profile=findViewById(R.id.cmt_image_profile);


        Intent intent=this.getIntent();
        postId=intent.getStringExtra("postId");
        authorId=intent.getStringExtra("authorId");

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        readProfile();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(addComment.getText()))
                {
                    Toast.makeText(CommentActivity.this,"EMPTY COMMENT",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    putComment();
                }
            }
        });


        recyclerView=findViewById(R.id.comment_recycle);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        readComments();

        commentAdapter=new CommentAdapter(this,commentList,postId);

        recyclerView.setAdapter(commentAdapter);
    }

    private void readComments()
    {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    commentList.add(comment);
                }

                commentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void putComment()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);

        String id = reference.push().getKey();

        HashMap<String,Object> cmt=new HashMap<>();

        cmt.put("id",id);
        cmt.put("comment",addComment.getText().toString());
        cmt.put("publisher",firebaseUser.getUid());

        reference.child(id).setValue(cmt).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(CommentActivity.this,"COMMENT ADDED",Toast.LENGTH_SHORT).show();
                    addComment.setText("");
                }
                else
                {
                    Toast.makeText(CommentActivity.this,task.getException().getMessage().toString() ,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void readProfile()
    {
        FirebaseDatabase.getInstance().getReference().child("USERS").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);

                if(user.getImageurl().equals("default"))
                {
                    profile.setImageResource(R.drawable.instagram);
                }
                else
                {
                    Picasso.get().load(user.getImageurl()).into(profile);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if(item.getItemId() ==  android.R.id.home)
        {
            finish();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }
}