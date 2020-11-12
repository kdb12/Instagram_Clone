package com.example.instagramapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramapp.Fragments.PostDetailFragment;
import com.example.instagramapp.Modules.Post;
import com.example.instagramapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.MyViewHolder>
{
    Context context;
    List<Post> postList;

    public PhotoAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    public PhotoAdapter() {
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.photo_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        final Post post = postList.get(position);
        Picasso.get().load(post.getImageurl()).placeholder(R.drawable.instagram).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getSharedPreferences("POSTED", Context.MODE_PRIVATE).edit().putString("postId",post.getPostid()).apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PostDetailFragment(),null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.photo_post);
        }
    }


}
