package com.gayelak.gayelakandroid;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

public class ReportUserActivity extends AppCompatActivity {

    GridView gridView;
    static String reportedUserId;
    EditText descriptionEditText;
    private LottieAnimationView loadingAnimationView;
    final String[] reportCategory = {"offensive behaviour", "scammer", "did not show up in meetings", "suspecious behaviour",
            "not active", "selling illegal items","spammer", "selling fake items", "others"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_user);
        gridView = findViewById(R.id.gridView);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        loadingAnimationView = (LottieAnimationView) findViewById(R.id.loadingAnimationView);

        final ReportUserGridViewAdapter reportUserGridViewAdapter = new ReportUserGridViewAdapter(this);
        gridView.setAdapter(reportUserGridViewAdapter);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        loadingAnimationView.setVisibility(View.GONE);

       gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               ReportUserGridViewAdapter.selectedItem = position;
               reportUserGridViewAdapter.notifyDataSetChanged();

           }
       });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickReport(View view) {

        if (ReportUserGridViewAdapter.selectedItem == -1) {
            Toast.makeText(this, "الرجاء اختيار نوع البلاغ!", Toast.LENGTH_LONG).show();

        } else {

            addReport();
        }
    }

    private void playLoadingAnimation()
    {
        loadingAnimationView.playAnimation();
        loadingAnimationView.loop(true);
        loadingAnimationView.setVisibility(View.VISIBLE);
    }

    private void stopLoadingAnimation()
    {
        // If something goes wrong.
        loadingAnimationView.cancelAnimation();
        loadingAnimationView.setVisibility(View.GONE);
    }

    private void addReport()
    {
        stopLoadingAnimation();
        String reportType = reportCategory[ReportUserGridViewAdapter.selectedItem];
        String reportingUser = LoginActivity.user.UserId;
        String reportedUser = reportedUserId;
        String description = descriptionEditText.getText().toString();
        Report report = new Report(description, reportedUser, reportingUser,reportType);
        FirebaseDatabase.getInstance().getReference().child("users-reports").push().setValue(report).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    stopLoadingAnimation();
                    Toast.makeText(ReportUserActivity.this, "تم اضافة البلاغ بنجاح", Toast.LENGTH_LONG).show();
                    finish();
                } else {

                    Toast.makeText(ReportUserActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    stopLoadingAnimation();
                }
            }
        });
     }
}
