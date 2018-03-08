package com.gayelak.gayelakandroid;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MessagesActivity extends AppCompatActivity {

    private LottieAnimationView loadingAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        ListView listView;
        listView = findViewById(R.id.listView);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        loadingAnimationView = (LottieAnimationView) findViewById(R.id.loadingAnimationView);
        loadingAnimationView.setVisibility(View.GONE);
        MessagesListViewAdapter messagesListViewAdapter = new MessagesListViewAdapter(this, displayMetrics.heightPixels, loadingAnimationView);
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
                                Intent reportUserIntent = new Intent(MessagesActivity.this, ReportUserActivity.class);
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
                        Intent intent = new Intent(MessagesActivity.this, ChatActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Toast.makeText(MessagesActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        // ...
                    }
                };

                FirebaseDatabase.getInstance().getReference().child("Users").child(clickedUserId).child("UserName").addListenerForSingleValueEvent(postListener);
            }
        });

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}


