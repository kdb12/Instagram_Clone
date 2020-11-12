package com.example.instagramapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramapp.R;

import java.util.ArrayList;
import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder>
{
    Context context;
    List<String> mTags;
    List<String> tTags;
    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(context).inflate(R.layout.tag_view,parent,false);
        return new TagViewHolder(view);
    }

    public TagAdapter(Context context, List<String> mTags, List<String> tTags) {

        this.context = context;
        this.mTags=mTags;
        this.tTags=tTags;
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position)
    {
        holder.tag.setText("#"+mTags.get(position));
        holder.totalPosts.setText(tTags.get(position)+" POSTS");
    }

    @Override
    public int getItemCount()
    {
        return mTags.size();
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder
    {
        TextView tag;
        TextView totalPosts;
        public TagViewHolder(@NonNull View itemView)
        {
            super(itemView);
            tag=itemView.findViewById(R.id.my_hash_tag_element);
            totalPosts=itemView.findViewById(R.id.no_of_posts_element);
        }
    }

    public void updateLists(List<String> searchTag, List<String> postTag)
    {
        this.mTags=searchTag;
        this.tTags=postTag;
        notifyDataSetChanged();
    }

}
