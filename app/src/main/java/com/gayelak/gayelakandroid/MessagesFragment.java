package com.gayelak.gayelakandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MessagesFragment extends Fragment {


    ListView listView;
    private LottieAnimationView loadingAnimationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_messages, container, false);
        listView = view.findViewById(R.id.listView);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        loadingAnimationView = (LottieAnimationView) view.findViewById(R.id.loadingAnimationView);
        loadingAnimationView.setVisibility(View.GONE);
        MessagesListViewAdapter messagesListViewAdapter = new MessagesListViewAdapter(getActivity(), displayMetrics.heightPixels, loadingAnimationView);
        listView.setAdapter(messagesListViewAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                CharSequence userOperations[] = new CharSequence[]{"حذر المستخدم","حذف المستخدم","التبليغ عن المستخدم"};
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("الرسائل").setItems(userOperations,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String clickedUserKey = MessagesListViewAdapter.usersKeys.get(position);
                        String userName = MessagesListViewAdapter.usersNames.get(position);
                        // The 'which' argument contains the index position
                        // of the selected item
                        if (which == 0)
                        {
                            //block user
                            FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("chat").child(clickedUserKey).removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Users").child(clickedUserKey).child("chat").child(LoginActivity.user.UserId).removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("block").child(clickedUserKey).setValue(userName);
                        }

                        else if (which == 1)
                        {
                            //delete user here to delete the user
                            FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("chat").child(clickedUserKey).removeValue();

                        }

                        else if (which == 2)
                        {
                            //report user
                            ReportUserActivity.reportedUserId = clickedUserKey;
                            Intent reportUserIntent = new Intent(getActivity(), ReportUserActivity.class);
                            startActivity(reportUserIntent);
                        }
                    }
                });

                builder.setNegativeButton("اغلاق", null);
                builder.show();
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final String clickedUserId = MessagesListViewAdapter.usersKeys.get(position);

                ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get Post object and use the values to update the UI
                        String userName = dataSnapshot.getValue().toString();
                        ChatActivity.itemUserId = clickedUserId;
                        ChatActivity.itemUserName = userName;
                        ChatActivity.cameFromMessages = true;
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                };

                FirebaseDatabase.getInstance().getReference().child("Users").child(clickedUserId).child("UserName").addListenerForSingleValueEvent(postListener);
            }
        });
        
        return view;
    }
}
