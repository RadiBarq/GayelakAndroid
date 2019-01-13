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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileSettingsChangeUserName extends AppCompatActivity {

    private LottieAnimationView loadingAnimationView;
    EditText userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings_change_user_name);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        loadingAnimationView = (LottieAnimationView) findViewById(R.id.loadingAnimationView);
        loadingAnimationView.setVisibility(View.GONE);
        userName = (EditText) findViewById(R.id.passwordText);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
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

    public void onClickChangeUserName(View view)
    {
        if (userName.getText().toString().matches(""))
        {
            Toast.makeText(this,"الرجاء تعبئة جميع الخانات", Toast.LENGTH_LONG).show();
        }


        else {

            playLoadingAnimation();
            FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("UserName").setValue(userName.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileSettingsChangeUserName.this, "تم تفير اسم المستخدم بنجاح", Toast.LENGTH_LONG).show();
                        stopLoadingAnimation();
                        LoginActivity.user.UserName = userName.getText().toString();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(userName.getText().toString()).build();
                        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates);
                        onBackPressed();

                    } else {

                        Toast.makeText(ProfileSettingsChangeUserName.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        stopLoadingAnimation();

                    }
                }
            });
        }
    }
}
