package com.gayelak.gayelakandroid;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    EditText emailEditText;
    EditText passwordEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText =  (EditText) findViewById(R.id.passwordEditText);

    }

    public void onClickForgotPassword(View view)
    {

        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);

    }

    public void onClickRegister(View view)
    {

        Intent intent = new Intent(this, RegisterActivity.class
        );
        startActivity(intent);


    }



    public void onClickLogin(View view) {

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information

                    FirebaseUser user = mAuth.getCurrentUser();



                    // updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
            // ...
        });

    }

}


