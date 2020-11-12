package com.example.instagramapp.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramapp.MainActivity;
import com.example.instagramapp.Modules.Comment;
import com.example.instagramapp.Modules.User;
import com.example.instagramapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder>
{
    Context context;
    List<Comment> commentList;
    String postId;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.comment_item,parent,false));
    }

    public CommentAdapter(Context context, List<Comment> commentList, String postId)
    {
        this.context = context;
        this.commentList = commentList;
        this.postId = postId;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position)
    {
        final Comment comment = commentList.get(position);

        holder.cmtText.setText(comment.getComment());

        FirebaseDatabase.getInstance().getReference().child("USERS").child(comment.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                holder.cmtPublisher.setText(user.getUsername());

                if(user.getImageurl().equals("default"))
                {
                    holder.circleImageView.setImageResource(R.drawable.instagram);
                }
                else
                {
                    Picasso.get().load(user.getImageurl()).into(holder.circleImageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.cmtText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("publisherId",comment.getPublisher());
                context.startActivity(intent);
            }
        });

        holder.cmtPublisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("publisherId",comment.getPublisher());
                context.startActivity(intent);
            }
        });

        holder.circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("publisherId",comment.getPublisher());
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(comment.getPublisher().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    final AlertDialog alertDialog = builder.create();

                    alertDialog.setTitle("DELETE");
                    alertDialog.setMessage("DO YOU WANT TO DELETE THIS COMMENT??");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).child(comment.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                    Toast.makeText(context,"COMMENT DELETED",Toast.LENGTH_SHORT).show();}
                                    else
                                    {
                                        Toast.makeText(context,"COMMENT NOT DELETED",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();

                        }
                    });
                    alertDialog.show();
                }
                else
                {

                }

                return true;
            }
        });

    }

    @Override
    public int getItemCount()
    {
        return commentList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView cmtText,cmtPublisher;
        CircleImageView circleImageView;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);

            cmtText=itemView.findViewById(R.id.comment_recycle_user_comment);
            cmtPublisher=itemView.findViewById(R.id.comment_recycle_username);

            circleImageView=itemView.findViewById(R.id.comment_recycle_profile);
        }
    }
}
