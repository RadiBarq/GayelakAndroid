package com.gayelak.gayelakandroid;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileSettingsActivity extends AppCompatActivity {

    private ListView settingslistView;
    StorageReference profileImaegRef;
    CharSequence[] changeProfileImageOptions = {"الكاميرة" , "مكتبة الصور"};
    final int GALLERY_REQUEST = 1;
    final int CAMERA_REQUEST = 0;
    private LottieAnimationView loadingAnimationView;
    private View clickedView;
    ProfileSettingsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        settingslistView = (ListView) findViewById(R.id.settingsList);
        String[] listItems= {"الصورة الشخصية", "البريد الالكتروني", "اسم المستخدم", "كلمة المرور" ,"تسجيل الخروج"};
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        adapter = new ProfileSettingsAdapter(this, displayMetrics.heightPixels);
        settingslistView .setAdapter(adapter);
        loadingAnimationView= (LottieAnimationView) findViewById(R.id.loadingAnimationView);
        loadingAnimationView.setVisibility(View.GONE);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        settingslistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                if (position == 0)
                {//profile picture

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("اختيار الصورة بواسطة")
                            .setItems(changeProfileImageOptions, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // The 'which' argument contains the index position
                                    // of the selected item

                                    clickedView = view;

                                    if (which == 0)
                                    {
                                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        ProfileSettingsActivity.this.startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                                    }

                                    else if (which == 1)
                                    {
                                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                        photoPickerIntent.setType("image/*");
                                        ProfileSettingsActivity.this.startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                                    }
                                }
                            });

                    builder.setNegativeButton("Cancel", null);
                    builder.show();
                }

                else if (position == 1)
                {
                    //email
                    Intent intent = new Intent(ProfileSettingsActivity.this, ProfileSettingsChangeEmail.class);
                    startActivity(intent);
                }

                else if (position == 2)
                {
                    //username
                    Intent intent = new Intent(ProfileSettingsActivity.this, ProfileSettingsChangeUserName.class);
                    startActivity(intent);
                }

                else if (position == 3)
                {
                    // the password
                    Intent intent = new Intent(ProfileSettingsActivity.this, ChangePasswordActivity.class);
                    startActivity(intent);

                }

                else if (position == 4)
                {
                    Intent intent = new Intent(ProfileSettingsActivity.this, BlockedUsersActivity.class);
                    startActivity(intent);
                }

                else{
                    //logout button should be empty like that.
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        adapter = new ProfileSettingsAdapter(this, displayMetrics.heightPixels);
        settingslistView.deferNotifyDataSetChanged();
        //settingslistView.setAdapter(adapter);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST:
                    Uri selectedImage = data.getData();
                    try {

                        ProfileSettingsActivity.this.playLoadingAnimation();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                       StorageReference storageRef =  FirebaseStorage.getInstance().getReference().child("Profile_Pictures").child(LoginActivity.user.UserId).child("Profile.jpg");
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                        byte[] bimapData = baos.toByteArray();

                        UploadTask uploadTask = storageRef.putBytes(bimapData);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Toast.makeText(ProfileSettingsActivity.this, exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                ProfileSettingsActivity.this.stopLoadingAnimation();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                                ProfileSettingsActivity.this.stopLoadingAnimation();
                                Toast.makeText(ProfileSettingsActivity.this,"تم تغير صورتك لن تلاحظ التغير الى بعد اعادة تشغيل التطبيق!" ,Toast.LENGTH_LONG).show();
                                finish();
                                overridePendingTransition( 0, 0);
                                startActivity(getIntent());
                                overridePendingTransition( 0, 0);

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Glide.get(ProfileSettingsActivity.this).clearDiskCache();

                                    }
                                }).start();
                            }
                        });

                        //PostItemActivity.postedItemImages[PostItemActivity.clickedItem] = bitmap;
                        // this.viewPagerPostItemAdapter.notifyDataSetChanged();
                       // this.viewPager.setAdapter(viewPagerPostItemAdapter);
                        // carImage.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }

                    break;

                case CAMERA_REQUEST:

                    try {
                        if (resultCode == RESULT_OK) {

                            ProfileSettingsActivity.this.playLoadingAnimation();
                            Bundle extras = data.getExtras();
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            StorageReference storageRef =  FirebaseStorage.getInstance().getReference().child("Profile_Pictures").child(LoginActivity.user.UserId).child("Profile.jpg");
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                            byte[] bimapData = baos.toByteArray();

                            UploadTask uploadTask = storageRef.putBytes(bimapData);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    Toast.makeText(ProfileSettingsActivity.this, exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    ProfileSettingsActivity.this.stopLoadingAnimation();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                                    ProfileSettingsActivity.this.stopLoadingAnimation();
                                    Toast.makeText(ProfileSettingsActivity.this,"تم تغير صورتك لن تلاحظ التغير الى بعد اعادة تشغيل التطبيق!" ,Toast.LENGTH_LONG).show();
                                    finish();
                                    overridePendingTransition( 0, 0);
                                    startActivity(getIntent());
                                    overridePendingTransition( 0, 0);

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            Glide.get(ProfileSettingsActivity.this).clearDiskCache();

                                        }
                                    }).start();
                                }
                            });
                        }
                    } catch (Exception e) {
                        ProfileSettingsActivity.this.stopLoadingAnimation();
                        Log.i("TAG", "Some exception " + e);
                    }

                    break;
            }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
