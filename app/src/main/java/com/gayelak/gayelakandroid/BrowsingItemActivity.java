package com.gayelak.gayelakandroid;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BrowsingItemActivity extends AppCompatActivity {

    MultiTouchViewPager viewPager;
    private LottieAnimationView loadingAnimationView;
    public static String itemId;
    private DatabaseReference itemRef;
    private DatabaseReference userRef;
    Item item;
    TextView priceTextView;
    TextView distanceTextView;
    TextView titleTextView;
    TextView descriptionTextView;
    ImageView userImage;
    String itemUserId;
    StorageReference itemUserImage;
    StorageReference imagesRef;
    ArrayList<Uri> imagesUri;
    private LottieAnimationView animationView;
    int gettingImagesCounter = 0;
    boolean isItFavouriteItem = false;

    // this is to solve the recursive issue of profile and item;
    static boolean cameFromProfile = false;
    boolean isItOwnItem = false;
    Button contactButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browsing_item);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initializeLayoutComponents();
        viewPager = (MultiTouchViewPager) findViewById(R.id.viewPager);
        imagesUri = new ArrayList<Uri>();
        loadingAnimationView= (LottieAnimationView) findViewById(R.id.loadingAnimationView);
        itemRef = FirebaseDatabase.getInstance().getReference().child("items").child(itemId);
        getItemdData();
        animationView = (LottieAnimationView) findViewById(R.id.lottieAnimationView);
        loadingAnimationView = (LottieAnimationView) findViewById(R.id.loadingAnimationView);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId);
        playLoadingAnimation();
    }

    private void checkIfItIsFavouriteItem()
    {
        FirebaseDatabase.getInstance().getReference("Users").child(LoginActivity.user.UserId).child("favourites").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(itemId)) {
                    // run some code
                    animationView.setProgress(1f);
                    isItFavouriteItem = true;
                }

                else
                {
                    animationView.setProgress(0.00f);
                    isItFavouriteItem = false;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void onClickContact(View view)
    {
        // it's not own item
        if (!item.userId.matches(LoginActivity.user.UserId)) {
             DatabaseReference  currentBlockRef = FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("block");

             ValueEventListener currentUserListener = new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {

                     if (dataSnapshot.hasChild(item.userId))
                     {
                         Toast.makeText(BrowsingItemActivity.this, "هاذا المستخدم على قائمة المحذورين لا يمكن التواصل معه!", Toast.LENGTH_LONG).show();



                     }

                     else
                     {
                         ValueEventListener itemUserListener = new ValueEventListener() {
                             @Override
                             public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(LoginActivity.user.UserId))
                                    {
                                        Toast.makeText(BrowsingItemActivity.this, "لا يمكن التواصل مع هاذا المستخدم في الوقت الراهن!", Toast.LENGTH_LONG).show();
                                    }

                                    else
                                    {
                                        ChatActivity.itemUserId = item.userId;
                                        ChatActivity.itemUserName = item.displayName;
                                        Intent intent = new Intent(BrowsingItemActivity.this, ChatActivity.class);
                                        startActivity(intent);
                                    }
                             }

                             @Override
                             public void onCancelled(DatabaseError databaseError) {


                                 Toast.makeText(BrowsingItemActivity.this,databaseError.getMessage(), Toast.LENGTH_LONG).show();

                             }
                         };

                         DatabaseReference itemUserBlokRef = FirebaseDatabase.getInstance().getReference().child("Users").child(item.userId).child("block");
                         itemUserBlokRef.addListenerForSingleValueEvent(itemUserListener);
                     }
                 }

                 @Override
                 public void onCancelled(DatabaseError databaseError) {

                     Toast.makeText(BrowsingItemActivity.this,databaseError.getMessage(), Toast.LENGTH_LONG).show();
                 }
             };

             currentBlockRef.addListenerForSingleValueEvent(currentUserListener);
        }

        // mark the item as sold, so basically delete it
        else
        {
            //FirebaseDatabase.getInstance().getReference().child("items").child(itemId).removeValue().addOnCompleteListener();
            FirebaseDatabase.getInstance().getReference().child("items-location").child(itemId).removeValue();
            FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("items").child(itemId).removeValue();
            FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("sold_items").child(itemId).setValue(" ");
            finish();
        }
    }

    public void onClickProfileImage(View view)
    {
        if (cameFromProfile == false) {
            ProfileActivity.userId = item.userId;
            ProfileActivity.userName = item.displayName;
            Intent profileActivity = new Intent(BrowsingItemActivity.this, ProfileActivity.class);
            startActivity(profileActivity);
        }

        else {

            this.onBackPressed();
            cameFromProfile = false;
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

    private void getStorageData()
    {
        itemUserImage = FirebaseStorage.getInstance().getReference().child("Profile_Pictures").child(itemUserId).child("Profile.jpg");
        Glide.with(this /* context */)
                .using(new FirebaseImageLoader())
                .load(itemUserImage).animate(android.R.anim.fade_in).thumbnail(Glide.with(this).load(R.drawable.spinner_gif)).crossFade().diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(userImage);
    }
    private void initializeLayoutComponents(){

        priceTextView = (TextView) findViewById(R.id.price);
        distanceTextView = (TextView) findViewById(R.id.distance);
        titleTextView = (TextView) findViewById(R.id.title);
        descriptionTextView = (TextView) findViewById(R.id.descriptionEditText);
        userImage = (ImageView) findViewById(R.id.profileImage);
        contactButton = (Button) findViewById(R.id.contactButton);
    }

    private void getItemdData()
    {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
               item = dataSnapshot.getValue(Item.class);
               checkIfItIsFavouriteItem();
               initializeUri();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText(BrowsingItemActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        };

      itemRef.addListenerForSingleValueEvent(postListener);
    }


    public void onClickAnimation(View view)
    {
        playAnimation(view);
    }

    private void playAnimation(View view)
    {
        if (isItFavouriteItem == false) {

            addFavouritesToFirebase(view);
        }

        else
        {
            removeFavouritesFromFirebase(view);
        }
    }

    private void addFavouritesToFirebase(final View view)
    {
        // add to favourites
        userRef.child("favourites").child(itemId).setValue("", new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if (databaseError == null) {
                    // we put that here to make the animation faster in this case.
                    isItFavouriteItem = true;
                    animationView.playAnimation();
                    animationView.loop(false);

                    String notificationId = LoginActivity.user.UserId + itemId;
                    Long tsLong = System.currentTimeMillis() / 1000;
                    String stringLong = tsLong.toString();
                    int timestamp = Integer.parseInt(stringLong);
                    Notification notification = new Notification(itemId, "true", timestamp, "favourite", LoginActivity.user.UserId, LoginActivity.user.UserName, notificationId);
                    FirebaseDatabase.getInstance().getReference().child("Users").child(itemUserId).child("notifications").child(notificationId).setValue(notification, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError == null)
                            {

                            }

                            else {

                                //Toast.makeText(BrowsingItemActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                Snackbar.make(view,databaseError.getMessage() , Snackbar.LENGTH_LONG)
                                        .setAction(databaseError.getMessage(), null).show();
                            }
                        }
                    });
                }

                else {

                    //Toast.makeText(BrowsingItemActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    Snackbar.make(view,databaseError.getMessage() , Snackbar.LENGTH_LONG)
                            .setAction(databaseError.getMessage(), null).show();
                }
            }
        });
    }

    private void removeFavouritesFromFirebase(final View view)
    {
        userRef.child("favourites").child(itemId).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if (databaseError == null) {

                    isItFavouriteItem = false;
                    animationView.setProgress(0.0f);
                    animationView.pauseAnimation();
                }
                else {

                    Snackbar.make(view,databaseError.getMessage() , Snackbar.LENGTH_LONG)
                            .setAction(databaseError.getMessage(), null).show();
                }
            }
        });
    }

    private void initializeUri()
    {
        gettingImagesCounter = 0;
       final Uri [] uriArray = new Uri[item.imagesCount];
        imagesRef = FirebaseStorage.getInstance().getReference().child("Items_Photos").child(itemId);
        for (int i = 0; i < item.imagesCount; i++)
        {
            final int imageNumber = i + 1;
            imagesRef.child(imageNumber + ".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    uriArray[imageNumber - 1] = uri;
                    if(gettingImagesCounter == item.imagesCount - 1) {

                        for (int j = 0; j < item.imagesCount; j++)
                        {
                            imagesUri.add(uriArray[j]);
                        }

                        ItemViewPagerAdapter itemViewPagerAdapter = new ItemViewPagerAdapter(BrowsingItemActivity.this, item.imagesCount, itemId, imagesUri);
                        viewPager.setAdapter(itemViewPagerAdapter);
                        priceTextView.setText(item.price);
                        titleTextView.setText(item.title);
                        descriptionTextView.setText(item.description);
                        distanceTextView.setText("١كم");
                        itemUserId = item.userId;
                        if (LoginActivity.user.UserId.matches(itemUserId))
                        {
                            isItOwnItem = true;
                            animationView.setVisibility(View.GONE);
                            contactButton.setText("تم بيع هاذا المنتج");
                        }
                        getStorageData();
                        stopLoadingAnimation();
                    }
                    gettingImagesCounter++;
                };

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.w("URI_ERROR_IN_ITEM", exception.getLocalizedMessage());
                }
            });
        }
    }
}
