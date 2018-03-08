package com.gayelak.gayelakandroid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.airbnb.lottie.LottieAnimationView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Currency;
import java.util.Locale;

public class PostItemActivity extends AppCompatActivity {

    ViewPager viewPager;
    public static Bitmap[] postedItemImages = {null, null, null, null};
    public static int clickedItem = 0;
    // this is related to the description edit_text
    EditText descriptionEditText;
    int lastSpecialRequestsCursorPosition = 0;
    String specialRequests = "";
    final int GALLERY_REQUEST = 1;
    final int CAMERA_REQUEST = 0;
    ViewPagerPostItemAdapter viewPagerPostItemAdapter;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    EditText titleEditText;
    EditText priceEditText;
    RadioGroup radioGroup;
    private FirebaseAuth mAuth;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location currentLocation;
    private RadioButton localeRadioButton;
    private LottieAnimationView animationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerPostItemAdapter = new ViewPagerPostItemAdapter(this);
        viewPager.setAdapter(viewPagerPostItemAdapter);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        titleEditText = (EditText) findViewById(R.id.title);
        priceEditText = (EditText) findViewById(R.id.price);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        mAuth = FirebaseAuth.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // getting the local currency here.
        Locale defaultLocale = Locale.getDefault();
        Currency currency = Currency.getInstance(defaultLocale);
        String currencyCode = currency.getCurrencyCode();
        localeRadioButton = (RadioButton) findViewById(R.id.radio_local);
        localeRadioButton.setText(currencyCode);
        getCurrentLocation();
        animationView = (LottieAnimationView) findViewById(R.id.lottieAnimationView);
        animationView.setVisibility(View.GONE);

        // This is to limit the lines of the description to six
        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lastSpecialRequestsCursorPosition = descriptionEditText.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                descriptionEditText.removeTextChangedListener(this);

                if (descriptionEditText.getLineCount() > 6) {
                    descriptionEditText.setText(specialRequests);
                    descriptionEditText.setSelection(lastSpecialRequestsCursorPosition);
                } else
                    specialRequests = descriptionEditText.getText().toString();

                descriptionEditText.addTextChangedListener(this);
            }
        });

        // add back arrow to toolbar
        if (getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

      setTitle("بيع منتجاتك");
    }

    // this will be called from the adapter my lord.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST:
                    Uri selectedImage = data.getData();
                    try {

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        PostItemActivity.postedItemImages[PostItemActivity.clickedItem] = bitmap;
                        // this.viewPagerPostItemAdapter.notifyDataSetChanged();
                        this.viewPager.setAdapter(viewPagerPostItemAdapter);
                        // carImage.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }

                    break;

                case CAMERA_REQUEST:

                    try {
                        if (resultCode == RESULT_OK) {
                            Bundle extras = data.getExtras();
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            PostItemActivity.postedItemImages[PostItemActivity.clickedItem] = imageBitmap;
                            this.viewPager.setAdapter(viewPagerPostItemAdapter);
                        }
                    } catch (Exception e) {
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
    @SuppressLint("MissingPermission")
    public void onClickPost(View view) {

        String descirption = descriptionEditText.getText().toString();
        String title = titleEditText.getText().toString();
        String price = priceEditText.getText().toString();
        int imagesCount = getNumberOfImages();
        double favourites = 0;
        Long tsLong = System.currentTimeMillis() / 1000;
        String stringLong = tsLong.toString();
        int timestamp = Integer.parseInt(stringLong);
        String userId = mAuth.getCurrentUser().getUid();
        String category = "category-others";
        String displayName = LoginActivity.user.UserName;
        int indexOfSelectedItemOnRadioGroup = radioGroup.indexOfChild(findViewById(radioGroup.getCheckedRadioButtonId()));
        String currency;

        if (indexOfSelectedItemOnRadioGroup == 0) {
            currency = localeRadioButton.getText().toString();
        } else {

            currency = "$";
        }

        if (currentLocation == null) {

            Toast.makeText(PostItemActivity.this, "الرجاء تفعيل خدمة المواقع على جهازك قبل اضافة المنتج",
                    Toast.LENGTH_LONG).show();

        } else if (imagesCount == 0) {

            Toast.makeText(PostItemActivity.this, "الرجاء اضافة صورة واحدة للمنتج على الاقل",
                    Toast.LENGTH_LONG).show();
        } else {

            if (price.matches("")) {

                price = "غير محدد";

            }

            playAnimation();
            uploadItem(descirption, title, price, imagesCount, favourites, timestamp, userId, category, displayName, currency);
            //uploadPictures();
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

    private void uploadPictures(String itemId) {

        int counter = 1;

        for (int i = 0; i <= 3; i++) {
            if (PostItemActivity.postedItemImages[i] != null) {

                FirebaseStorage storage = FirebaseStorage.getInstance();
                storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                storageRef = storageRef.child("Items_Photos").child(itemId).child(counter + ".jpeg");

                Bitmap bitmap = PostItemActivity.postedItemImages[i];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] data = baos.toByteArray();


                UploadTask uploadTask = storageRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads

                        Toast.makeText(PostItemActivity.this, exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        return;

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                    }
                });

                counter++;
            }
        }
        uploadGeoLocationData(itemId, currentLocation);
    }

    private void uploadItem(String description, String title, String price, int imagesCount, double favourites, int timestamp, String userId, String category, String displayName, String currency) {
        Item item = new Item(category, currency, description, displayName, favourites, imagesCount, price, timestamp, title, userId);
        database = database.child("items");
        final String itemKey = database.push().getKey();
        database.child(itemKey).setValue(item, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if (databaseError != null) {

                    Toast.makeText(PostItemActivity.this, databaseError.getMessage().toString(), Toast.LENGTH_LONG).show();

                } else {

                    uploadPictures(itemKey);
                }
            }
        });
        //uploadGeoLocationData(itemKey, currentLocation);
    }

    private void getCurrentLocation() {


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return;
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            currentLocation = location;

                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(PostItemActivity.this, "عزيزي المستخدم لا تستطيع اضافة منتجك على جايلك بدون السماح لجايك من استخدام خدمة المواقع على جهازك!", Toast.LENGTH_LONG);
                }

                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private int getNumberOfImages() {
        int counter = 0;

        for (int i = 0; i <= 3; i++) {

            if (PostItemActivity.postedItemImages[i] != null) {

                counter++;
            }
        }

        return counter;
    }

    private void uploadGeoLocationData(final String itemId, Location itemLocation) {

        DatabaseReference geoLocationDatabaseRef = FirebaseDatabase.getInstance().getReference().child("items-location");
        GeoFire geoFire = new GeoFire(geoLocationDatabaseRef);

        // here should be itemLocation.getLatitude(), itemLocation.getLongitude() instead of these static data my lordy lord
        geoFire.setLocation(itemId, new GeoLocation(37.785834, -122.406417), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

                if (error != null) {

                 Toast.makeText(PostItemActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();

                }

                else
                {
                  postingSuccessful(itemId);
                }
            }
        });
    }

    private void postingSuccessful(String itemId)
    {
        stopAnimation();
        FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("items").child(itemId).setValue("");
        Toast.makeText(PostItemActivity.this, "posting items works", Toast.LENGTH_SHORT).show();
    }

}