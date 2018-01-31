package com.gayelak.gayelakandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ListView;

public class MessagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        ListView listView;
        listView = findViewById(R.id.listView);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        MessagesListViewAdapter messagesListViewAdapter = new MessagesListViewAdapter(this, displayMetrics.heightPixels);
        listView.setAdapter(messagesListViewAdapter);

    }
}
