package com.example.instagramapp.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.instagramapp.MainActivity;
import com.example.instagramapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ChangePasswordDialogFragment extends DialogFragment
{
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());

        LayoutInflater inflater=getActivity().getLayoutInflater();
        final View view=inflater.inflate(R.layout.change_password,null);
        builder.setView(view);

        final MaterialEditText oldP,newP;
        oldP=view.findViewById(R.id.old_password);
        newP=view.findViewById(R.id.new_password);

        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(TextUtils.isEmpty(oldP.getText().toString()) || TextUtils.isEmpty(newP.getText().toString()))
                {
                    Toast.makeText(getContext(), "EMPTY FIELDS", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
                else if(newP.getText().length() <6)
                {
                    Toast.makeText(getContext(), "SHORT PASSWORD", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
                else
                {
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    final String email = user.getEmail();
                    AuthCredential credential = EmailAuthProvider.getCredential(email, oldP.getText().toString());

                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(newP.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()) {

                                            dismiss();
                                        } else {
                                            FirebaseDatabase.getInstance().getReference().child("USERS").child(user.getUid()).child("password").setValue(newP);
                                            Toast.makeText(getContext(), "Password Successfully Modified", Toast.LENGTH_SHORT).show();
                                            dismiss();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

        builder.setNeutralButton("DISCARD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });


        return builder.create();
    }
}
