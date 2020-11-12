package com.example.instagramapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramapp.Fragments.ProfileFragment;
import com.example.instagramapp.MainActivity;
import com.example.instagramapp.Modules.User;
import com.example.instagramapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>
{

    Context context;
    List<User> Users;
    boolean isFragment;
    FirebaseUser firebaseUser;
    public UserAdapter(Context context, List<User> users, boolean isFragment)
    {
        this.context = context;
        Users = users;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(context).inflate(R.layout.view_of_recycler,parent,false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder holder, int position)
    {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final User u=Users.get(position);
        holder.username.setText(u.getUsername());
        holder.name.setText(u.getName());
        Picasso.get().load(u.getImageurl()).placeholder(R.drawable.instagram).into(holder.profile);
        isFollowing(u.getId(),holder.btnFollow);
        if(firebaseUser.getUid().equals(u.getId()))
        {
            holder.btnFollow.setVisibility(View.GONE);
        }
        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //Toast.makeText(context,"clicked follow",Toast.LENGTH_SHORT).show();
                if(holder.btnFollow.getText().equals("follow"))
                {
                    FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following").child(u.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("follow").child(u.getId()).child("followers").child(firebaseUser.getUid()).setValue(true);
                    addNotification(u.getId());

                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following").child(u.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("follow").child(u.getId()).child("followers").child(firebaseUser.getUid()).removeValue();

                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFragment)
                {
                    context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("proId",u.getId()).apply();

                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment(),null).commit();
                }
                else
                {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("publisherId",u.getId());
                    context.startActivity(intent);
                }
            }
        });
    }

    private void addNotification(String id)
    {
        HashMap<String,Object> map = new HashMap<>();
        map.put("postId"," ");
        map.put("userId",firebaseUser.getUid());
        map.put("fromPost",false);
        map.put("text","Started following you");

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(id).push().setValue(map);
    }

    private void isFollowing(final String id, final Button btnFollow)
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(id).exists())
                {
                    btnFollow.setText("following");
                }
                else
                {
                    btnFollow.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return Users.size();
    }



    public static class UserViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView profile;
        TextView username;
        TextView name;
        Button btnFollow;
        public UserViewHolder(@NonNull View itemView)
        {
            super(itemView);
            profile=itemView.findViewById(R.id.profile_image);
            username=itemView.findViewById(R.id.username);
            name=itemView.findViewById(R.id.name);
            btnFollow=itemView.findViewById(R.id.btn_follow);
        }
    }
}
