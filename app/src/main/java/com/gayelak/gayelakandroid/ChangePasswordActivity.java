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

public class ChangePasswordActivity extends AppCompatActivity {

    EditText passwordEditText;
    EditText repeatedNewPassword;
    private LottieAnimationView loadingAnimationView;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        passwordEditText = (EditText) findViewById(R.id.passwordText);
        repeatedNewPassword = (EditText) findViewById(R.id.repeatedPasswordText);
        loadingAnimationView = (LottieAnimationView) findViewById(R.id.loadingAnimationView);
        loadingAnimationView.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // handle arrow click here
            if (item.getItemId() == android.R.id.home) {
                finish(); // close this activity and return to preview activity (if there is any)
            }

            return super.onOptionsItemSelected(item);
        }

    public void onClickChangePassword(View view)
    {
        String newPassword = passwordEditText.getText().toString();
        String newRepeatedPassword = repeatedNewPassword.getText().toString();

        if (!newPassword.matches(newRepeatedPassword))
        {
            Toast.makeText(this,"كلمتا السر غير متطابقتان", Toast.LENGTH_LONG).show();
            passwordEditText.setText("");
            repeatedNewPassword.setText("");
        }

        else if (newPassword.matches("") || newRepeatedPassword.matches(""))
        {
            Toast.makeText(this,"الرجاء تعبئة جميع الخانات", Toast.LENGTH_LONG).show();
            passwordEditText.setText("");
            repeatedNewPassword.setText("");
            stopLoadingAnimation();
        }

        else
        {
            playLoadingAnimation();
            currentUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    Toast.makeText(ChangePasswordActivity.this, "تم تغيير كلمة السر بنجاح", Toast.LENGTH_LONG).show();
                    stopLoadingAnimation();
                    onBackPressed();
                }

                else {

                    Toast.makeText(ChangePasswordActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    passwordEditText.setText("");
                    repeatedNewPassword.setText("");
                    stopLoadingAnimation();
                }
            }
        });

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

}
