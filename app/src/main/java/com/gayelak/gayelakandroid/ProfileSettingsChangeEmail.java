package com.gayelak.gayelakandroid;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileSettingsChangeEmail extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private EditText emailEdiText;
    private LottieAnimationView loadingAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings_change_email);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        emailEdiText = (EditText) findViewById(R.id.passwordText);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        loadingAnimationView = (LottieAnimationView) findViewById(R.id.loadingAnimationView);
        loadingAnimationView.setVisibility(View.GONE);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


    public void onClickChangeEmail(View view)
    {
        if (emailEdiText.getText().toString().matches(""))
        {
            Toast.makeText(this,"الرجاء تعبئة جميع الخانات", Toast.LENGTH_LONG).show();
        }

        else {

            playLoadingAnimation();
            currentUser.updateEmail(emailEdiText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        Toast.makeText(ProfileSettingsChangeEmail.this, "تم تغيير البريد الالكتروني بنجاح", Toast.LENGTH_LONG).show();
                        stopLoadingAnimation();
                        LoginActivity.user.Email = emailEdiText.getText().toString();
                        onBackPressed();
                    } else {
                        Toast.makeText(ProfileSettingsChangeEmail.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        stopLoadingAnimation();
                    }
                }
            });

        }
    }
}
