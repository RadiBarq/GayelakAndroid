package com.gayelak.gayelakandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity{

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private EditText mUserNameView;
    private FirebaseAuth mAuth;
    private LottieAnimationView animationView;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
       // populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mUserNameView = (EditText) findViewById(R.id.userName);
        animationView = (LottieAnimationView) findViewById(R.id.lottieAnimationView);
        animationView.setVisibility(View.GONE);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mEmailView.requestFocus();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mUserNameView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String userName = mUserNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password)) {

            mPasswordView.setError("كلمة السر المدخلة يجب ان تكون اكثر من ٥ خانات");
            focusView = mPasswordView;
            cancel = true;

        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("هاذا الحقل مطلوب");
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError("هاذا البريد الالكتروني غير صحيح");
            focusView = mEmailView;
            cancel = true;
        }

         if (TextUtils.isEmpty(userName))
        {
                mUserNameView.setError("هاذا الحقل مطلوب");
                focusView = mUserNameView;
                cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
           // showProgress(true);

            firebaseAuth();
        }
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

    private void firebaseAuth()
    {
        playAnimation();
        mAuth = FirebaseAuth.getInstance();
        // Store values at the time of the login attempt.
        final  String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        final String userName = mUserNameView.getText().toString();


        mAuth.createUserWithEmailAndPassword(email, password).
            addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    // Checking if user is registered successfully.
                    if(task.isSuccessful()){

                        // If user registered successfully then show this toast message.

                        String instanceId = FirebaseInstanceId.getInstance().getToken();
                        String userId = task.getResult().getUser().getUid();
                        User user = new User(email, userId, userName, instanceId);
                        mDatabase.child("Users").child(userId).setValue(user);
                        uploadPicture(userId);


                    }else{

                        stopAnimation();
                        Toast.makeText(RegisterActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }

                    // Hiding the progress dialog after all task complete.
                   // progressDialog.dismiss();

                }
            });
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads

                Toast.makeText(RegisterActivity.this, exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                stopAnimation();

                //TODO Move to login screen
                Toast.makeText(RegisterActivity.this, "تم تسجيلك في جايلك بنجاح", Toast.LENGTH_LONG).show();

            }
        });

    }

    private boolean isEmailValid(String email) {

        return email.contains("@");
    }


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 6;
    }



    /**
     * Shows the progress UI and hides the login form.
     */

}

