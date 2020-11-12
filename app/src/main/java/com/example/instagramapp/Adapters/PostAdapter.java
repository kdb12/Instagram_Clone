package com.example.instagramapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramapp.CommentActivity;
import com.example.instagramapp.FollowersActivity;
import com.example.instagramapp.Fragments.PostDetailFragment;
import com.example.instagramapp.Fragments.ProfileFragment;
import com.example.instagramapp.MainActivity;
import com.example.instagramapp.Modules.*;
import com.example.instagramapp.Modules.User;
import com.example.instagramapp.R;
import com.example.instagramapp.StartActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder>
{

    public Context context;
    public List<Post> mPosts;

    FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> mPosts)
    {
        this.context = context;
        this.mPosts = mPosts;
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.post_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position)
    {


        final Post post=mPosts.get(position);
        Picasso.get().load(post.getImageurl()).placeholder(R.drawable.instagram).into(holder.postImage);
        holder.description.setText(post.getDescription());

        isLiked(post.getPostid().toString(),holder.like);


        FirebaseDatabase.getInstance().getReference().child("USERS").child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);

                holder.userName.setText(user.getUsername());
                holder.authorName.setText(user.getName());

                Picasso.get().load(user.getImageurl()).placeholder(R.drawable.instagram).into(holder.profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag().equals("Like")) {
                    Toast.makeText(context, "liked", Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);
                    if(post.getPublisher().equals(firebaseUser.getUid()))
                    {

                    }else {
                        addNotification(post);
                    }
                }
                    else
                {
                    Toast.makeText(context,"disliked",Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid()).child(firebaseUser.getUid()).removeValue();

                }
            }
        });

        noOfLikesPosts(post.getPostid(),holder.noOfLikes);


        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,CommentActivity.class);
                intent.putExtra("postId",post.getPostid());
                intent.putExtra("authorId",post.getPublisher());

                context.startActivity(intent);
            }
        });

        holder.noOfComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,CommentActivity.class);
                intent.putExtra("postId",post.getPostid());
                intent.putExtra("authorId",post.getPublisher());

                context.startActivity(intent);
            }
        });

        readNoOfComments(post.getPostid(),holder.noOfComments);

        isSaved(post.getPostid(),holder.save);

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.save.getTag().equals("save"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostid()).setValue(true);
                    Snackbar snackbar = Snackbar.make(v,"POST SAVED",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostid()).removeValue();
                    Snackbar snackbar = Snackbar.make(v,"POST UNSAVED",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });

        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("proId",post.getPublisher()).apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment(),null).commit();
            }
        });

        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("proId",post.getPublisher()).apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment(),null).commit();
            }
        });

        holder.authorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("proId",post.getPublisher()).apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment(),null).commit();
            }
        });

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getSharedPreferences("POSTED", Context.MODE_PRIVATE).edit().putString("postId",post.getPostid()).apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PostDetailFragment(),null).commit();
            }
        });

        holder.noOfLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FollowersActivity.class);
                intent.putExtra("id",post.getPostid());
                intent.putExtra("title","Likes");
                context.startActivity(intent);
            }
        });
    }

    private void addNotification(Post post)
    {
        HashMap<String,Object> map = new HashMap<>();
        map.put("postId",post.getPostid());
        map.put("userId",post.getPublisher());
        map.put("fromPost", true);
        map.put("text","Liked your post");

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(post.getPublisher()).push().setValue(map);
    }

    private void isSaved(final String postid, final ImageView save)
    {
        FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postid).exists())
                {
                    save.setImageResource(R.drawable.ic_saved);
                    save.setTag("saved");
                }
                else
                {
                    save.setImageResource(R.drawable.ic_save);
                    save.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readNoOfComments(String s, final TextView noOfComments)
    {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(s).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() != 0){
                noOfComments.setText(snapshot.getChildrenCount()+" Comments");}
                else
                {
                    noOfComments.setText("Be A First To comment");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void noOfLikesPosts(String s, final TextView nl)
    {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(s).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getChildrenCount() != 0){
                nl.setText(snapshot.getChildrenCount()+" Likes");}
                else
                {
                    nl.setText("Be A First To Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public void isLiked(final String s, final ImageView like)
    {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(s).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists())
                {
                    like.setImageResource(R.drawable.ic_liked);
                    like.setTag("Liked");
                }
                else
                {
                    like.setImageResource(R.drawable.ic_like);
                    like.setTag("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView profileImage;
        ImageView postImage;
        ImageView like;
        ImageView comment;
        ImageView save;
        ImageView more;
        TextView userName;
        TextView noOfLikes;
        TextView authorName;
        TextView noOfComments;
        SocialTextView description;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);

            profileImage=itemView.findViewById(R.id.user_profile_image);
            postImage=itemView.findViewById(R.id.image_post);
            like=itemView.findViewById(R.id.like);
            comment=itemView.findViewById(R.id.comment);
            save=itemView.findViewById(R.id.save);
            more=itemView.findViewById(R.id.more);

            userName=itemView.findViewById(R.id.username_post);
            noOfLikes=itemView.findViewById(R.id.no_of_likes);
            authorName=itemView.findViewById(R.id.name_of_author);
            noOfComments=itemView.findViewById(R.id.no_of_comments);
            description=itemView.findViewById(R.id.description_post);

        }
    }
}
