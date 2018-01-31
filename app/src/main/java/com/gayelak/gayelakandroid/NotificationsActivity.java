package com.gayelak.gayelakandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class NotificationsActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        listView = findViewById(R.id.listView);
        NotificationsAdapter adapter = new NotificationsAdapter(this);
        listView.setAdapter(adapter);

    }
}
