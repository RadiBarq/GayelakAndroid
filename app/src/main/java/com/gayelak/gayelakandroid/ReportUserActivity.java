package com.gayelak.gayelakandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

public class ReportUserActivity extends AppCompatActivity {


    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_user);
        gridView = findViewById(R.id.gridView);
        ReportUserGridViewAdapter reportUserGridViewAdapter = new ReportUserGridViewAdapter(this);
        gridView.setAdapter(reportUserGridViewAdapter);


    }
}
