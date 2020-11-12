package com.example.instagramapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramapp.Fragments.PostDetailFragment;
import com.example.instagramapp.Fragments.ProfileFragment;
import com.example.instagramapp.Modules.Notification;
import com.example.instagramapp.Modules.Post;
import com.example.instagramapp.Modules.User;
import com.example.instagramapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder>
{
    private Context context;
    List<Notification> notificationList;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.notification_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position)
    {
        final Notification notification = notificationList.get(position);

        holder.notificationText.setText(notification.getText());

        getUsernameAndProfile(holder.userName,holder.profileImage,notification.getUserId());

        if(notification.isFromPost())
        {

            holder.postImage.setVisibility(View.VISIBLE);
            FirebaseDatabase.getInstance().getReference().child("Posts").child(notification.getPostId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Picasso.get().load(snapshot.getValue(Post.class).getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.postImage);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
        {

            holder.postImage.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notification.isFromPost())
                {
                    context.getSharedPreferences("POSTED",Context.MODE_PRIVATE).edit().putString("postId",notification.getPostId()).apply();
                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PostDetailFragment(),null).commit();
                }
                else
                {
                    context.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().putString("proId",notification.getUserId()).apply();
                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment(),null).commit();
                }
            }
        });
    }

    private void getUsernameAndProfile(final TextView userName, final CircleImageView profileImage, String userId)
    {
        FirebaseDatabase.getInstance().getReference().child("USERS").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                userName.setText(user.getUsername());
                Picasso.get().load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(profileImage);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount()
    {
        return notificationList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView profileImage;
        TextView userName,notificationText;
        ImageView postImage;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);


            profileImage = itemView.findViewById(R.id.notification_image);
            userName = itemView.findViewById(R.id.notification_username);
            notificationText = itemView.findViewById(R.id.notification_text);
            postImage = itemView.findViewById(R.id.notification_post);


        }
    }



}
