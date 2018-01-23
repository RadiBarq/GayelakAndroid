package com.gayelak.gayelakandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class BlockedUsersActivity extends AppCompatActivity {

    private ListView blockedListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_users);
        blockedListView = (ListView) findViewById(R.id.blockedList);
        ProfileBlcokedUsersAdapter profileBlcokedUsersAdapter = new ProfileBlcokedUsersAdapter(this);
        blockedListView.setAdapter(profileBlcokedUsersAdapter);


    }
}
