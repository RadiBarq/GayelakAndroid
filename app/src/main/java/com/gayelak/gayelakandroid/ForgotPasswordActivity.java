package com.gayelak.gayelakandroid;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
    }

    public void onClickResetPassword(View view)
    {
        String email = emailEditText.getText().toString();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {

                        if (task.isSuccessful()) {


                            Toast.makeText(ForgotPasswordActivity.this, "تم ارسال رسالة اعادة كلمة السر الى بريدك الالكتروني!", Toast.LENGTH_LONG).show();
                            finish();

                        }

                        else
                        {

                            Toast.makeText(ForgotPasswordActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();


                        }

                    }
                });
    }

}
