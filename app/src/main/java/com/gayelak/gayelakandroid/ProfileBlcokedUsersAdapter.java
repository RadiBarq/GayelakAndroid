package com.gayelak.gayelakandroid;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by radibarq on 1/23/18.
 */

public class ProfileBlcokedUsersAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private DatabaseReference blockedUsersRef;
    private ArrayList<String> blockedUsersNames;
    private ArrayList<String> blockedUsersKeys;

    ProfileBlcokedUsersAdapter(Context context)
    {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        blockedUsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("block");
        blockedUsersNames = new ArrayList<String>();
        blockedUsersKeys = new ArrayList<String>();
        getBlockedUsers();
    }

    @Override
    public int getCount() {

        return blockedUsersKeys.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = mInflater.inflate(R.layout.profile_blocked_users_layout, parent, false);
        TextView blockedUserNameTextView = view.findViewById(R.id.userName);
        Button removeBlockButton = view.findViewById(R.id.blockButton);
        String blockedUserName = blockedUsersNames.get(position);
        blockedUserNameTextView.setText(blockedUserName);
        final int currentPosition = position;

        removeBlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("block").child(blockedUsersKeys.get(currentPosition)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful())
                        {
                            Toast.makeText(mContext, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

        return view;
    }

    private void getBlockedUsers()
    {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                blockedUsersNames = new ArrayList<String>();
                blockedUsersKeys = new ArrayList<String>();

                for (DataSnapshot user: dataSnapshot.getChildren())
                {
                    String userKey = user.getKey();
                    String userName = user.getValue().toString();
                    blockedUsersNames.add(userName);
                    blockedUsersKeys.add(userKey);
                }

                ProfileBlcokedUsersAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(mContext, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        blockedUsersRef.addValueEventListener(listener);
    }
}
