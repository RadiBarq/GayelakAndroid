package com.gayelak.gayelakandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class WelcomeActivity extends AppCompatActivity {


    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private LottieAnimationView animationView;
    private LoginButton loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        String notificationExtraIntent = getIntent().getStringExtra("notificationType");
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        animationView = (LottieAnimationView) findViewById(R.id.lottieAnimationView);
        animationView.setVisibility(View.GONE);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {

                        // App code
                        Log.d("facebook canceled", "facebook:onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code

                        Log.d("facebook error", "facebook:onError", exception);
                    }
                });




        if(user!=null)
        {
            String userName = user.getDisplayName();
            String instanceId = FirebaseInstanceId.getInstance().getToken();
            String email = user.getEmail();
            String userId = user.getUid();
            LoginActivity.user = new User(email, userId, userName, instanceId);
            //FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("instanceId").setValue(instanceId);
            Intent intent = new Intent(this, BrowsingActivity.class);

            if(notificationExtraIntent!= null)
            {
                if (notificationExtraIntent.matches("normal")) {

                    intent.putExtra("notificationType", "normal");
                }

                else if (notificationExtraIntent.matches("message"))
                {
                    intent.putExtra("notificationType", "message");
                }
            }

            startActivity(intent);
        }

        else
        {
          // user is signed out.
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("facebook authentication", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("firebase authentication", "signInWithCredential:success");
                            playAnimation();
                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {

                                    if (snapshot.hasChild(currentUser.getUid())) {

                                        //already a user

                                        stopAnimation();

                                        FirebaseUser currentUser = mAuth.getCurrentUser();
                                        String userName = currentUser.getDisplayName();
                                        String instanceId = FirebaseInstanceId.getInstance().getToken();
                                        String email = currentUser.getEmail();
                                        String userId = currentUser.getUid();
                                        LoginActivity.user = new User(email, userId, userName, instanceId);
                                        String notificationExtraIntent = getIntent().getStringExtra("notificationType");
                                        //FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("instanceId").setValue(instanceId);
                                        Intent intent = new Intent(WelcomeActivity.this, BrowsingActivity.class);

                                        if(notificationExtraIntent!= null)
                                        {
                                            if (notificationExtraIntent.matches("normal")) {

                                                intent.putExtra("notificationType", "normal");
                                            }

                                            else if (notificationExtraIntent.matches("message"))
                                            {
                                                intent.putExtra("notificationType", "message");
                                            }
                                        }

                                        startActivity(intent);

                                    }

                                    else
                                    {
                                        // this is not a user
                                        String instanceId = FirebaseInstanceId.getInstance().getToken();
                                        String userId = currentUser.getUid();
                                        User user = new User(currentUser.getEmail(), userId, currentUser.getDisplayName(), instanceId);
                                        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).setValue(user);
                                        uploadPicture(userId);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            //updateUI(user);
                        } else {

                            // If sign in fails, display a message to the user.
                            Log.w("firebase authentication", "signInWithCredential:failure", task.getException());
                            Toast.makeText(WelcomeActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                          //  updateUI(null);
                        }

                        // ...
                    }
                });
    }


    public void onClickRegister(View view)
    {

        Intent intent = new Intent(this, RegisterActivity.class);
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

    private void uploadPicture(String userId)
    {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        storageRef = storageRef.child("Profile_Pictures").child(userId).child("Profile.jpg");
        //    ImageView imageView =  new ImageView(this);
        // imageView.setImageResource(R.drawable.profile_picture);
        //  imageView.setDrawingCacheEnabled(true);
        //  imageView.buildDrawingCache();

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.profile_picture);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads

                Toast.makeText(WelcomeActivity.this, exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                stopAnimation();

                FirebaseUser currentUser = mAuth.getCurrentUser();
                String userName = currentUser.getDisplayName();
                String instanceId = FirebaseInstanceId.getInstance().getToken();
                String email = currentUser.getEmail();
                String userId = currentUser.getUid();
                LoginActivity.user = new User(email, userId, userName, instanceId);
                String notificationExtraIntent = getIntent().getStringExtra("notificationType");
                //FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("instanceId").setValue(instanceId);
                Intent intent = new Intent(WelcomeActivity.this, BrowsingActivity.class);

                if(notificationExtraIntent!= null)
                {
                    if (notificationExtraIntent.matches("normal")) {

                        intent.putExtra("notificationType", "normal");
                    }

                    else if (notificationExtraIntent.matches("message"))
                    {
                        intent.putExtra("notificationType", "message");
                    }
                }

                startActivity(intent);


                //TODO Move to login screen
              //  Toast.makeText(WelcomeActivity.this, "تم تسجيلك في جايلك بنجاح", Toast.LENGTH_LONG).show();

            }
        });

    }

    public void onClickLogin(View view)
    {

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }


    public void onClickFacebook(View view)
    {

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

    }


    // ...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



    private void loginSuccessful()
    {
        Intent intent = new Intent(this, BrowsingActivity.class);
        startActivity(intent);
    }

}