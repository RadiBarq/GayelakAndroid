package com.gayelak.gayelakandroid;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText;
    EditText passwordEditText;
    public static User user;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText =  (EditText) findViewById(R.id.passwordEditText);
        animationView = (LottieAnimationView) findViewById(R.id.lottieAnimationView);
        animationView.setVisibility(View.GONE);

    }

    public void onClickForgotPassword(View view)
    {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    private void playAnimation()
    {
        animationView.playAnimation();
        animationView.loop(true);
        animationView.setVisibility(View.VISIBLE);
    }

    private void stopAnimation()
    {
        // If something goes wrong.
        animationView.cancelAnimation();
        animationView.setVisibility(View.GONE);

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
        playAnimation();


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    final FirebaseUser user = mAuth.getCurrentUser();

                    String userName = user.getDisplayName();
                    String instanceId = FirebaseInstanceId.getInstance().getToken();
                    String email = user.getEmail();
                    String userId = user.getUid();

                    LoginActivity.user = new User(email, userId, userName, instanceId);
                    FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("instanceId").setValue(instanceId);
                    loginSuccessful();

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                    stopAnimation();

                }
            }
            // ...
        });
    }

    private void loginSuccessful()
    {
        stopAnimation();
        Intent intent = new Intent(this, BrowsingActivity.class);
        startActivity(intent);
    }
}
