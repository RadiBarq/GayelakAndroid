package com.gayelak.gayelakandroid;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.common.ConnectionResult;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
//import com.firebase.client.DataSnapshot;
//import com.firebase.client.FirebaseError;
//import com.google.firebase.auth.api.model.StringList;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {


    ListView listView;
    SimpleAdapter adapter;
    String myJSON;
    ArrayList<HashMap<String, String>> personList;
    public static ArrayList<HashMap<String, String>> messages;
    JSONArray peoples = null;
    DatabaseReference myRef;
    private ProgressDialog pDialog;
    EditText input;
 //   DatabaseReference user1Ref;
 //   DatabaseReference user2Ref;
   // int checker;
    // Related to the active chat
    public static int active;
   // public static String key;
  //  public static String name;
    MessageAdapter messageAdapter;
    public static String itemUserId;
    public static String itemUserName;
    DatabaseReference itemUserRef;
    public static String itemId;
    // to know weather the user came from the messages or the item.
    public static boolean cameFromMessages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messageAdapter = new MessageAdapter(this);
        Toolbar customToolBar = (Toolbar) findViewById(R.id.toolbar);
        customToolBar.setTitle(""); // it check if there is title or not here.
        setSupportActionBar(customToolBar);
        //showToolBar(customToolBar, this);
        /// Related to the diaglog
        pDialog = new ProgressDialog(ChatActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
        Button fab = (Button) findViewById(R.id.fab);
        listView = (ListView) findViewById(R.id.list_of_messages);
        messages = new ArrayList<HashMap<String, String>>();
        setupToolBarItems(customToolBar, this);
        input = (EditText) findViewById(R.id.input);
        input.setImeActionLabel("ارسال", EditorInfo.IME_ACTION_DONE);

        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!input.getText().toString().trim().matches("")) {

                        String messageText = input.getText().toString().trim();
                        ChatMessage chatMessage = new ChatMessage(messageText, LoginActivity.user.UserId);
                        LastMessage lastMessageForOtherUser = new LastMessage(messageText, "true");
                        LastMessage lastMessageForCurrentUser = new LastMessage(messageText, "false");
                        //  ChatRoom chatRoom = new ChatRoom(chatMessage, LoginActivity.user.UserName,ChatActivity.itemUserId);
                        myRef.push().setValue(chatMessage);
                        itemUserRef.push().setValue(chatMessage);

                        myRef.child("item-id").setValue(itemId);
                        myRef.child("user-id").setValue(itemUserId);
                        myRef.child("user-name").setValue(LoginActivity.user.UserName);
                        itemUserRef.child("item-id").setValue(itemId);
                        itemUserRef.child("user-id").setValue(LoginActivity.user.UserId);
                        itemUserRef.child("user-name").setValue(itemUserName);
                        myRef.child("lastMessage").setValue(lastMessageForCurrentUser);
                        itemUserRef.child("lastMessage").setValue(lastMessageForOtherUser);
                        PushMessage pushMessage = new PushMessage(LoginActivity.user.UserId, ChatActivity.itemUserId, messageText);
                        FirebaseDatabase.getInstance().getReference().child("messages").push().setValue(pushMessage);
                        input.setText("");
                        // checker = 1;
                    }
                    return true;
                }
                return false;
            }
        });

       // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!input.getText().toString().trim().matches("")) {

                    String messageText = input.getText().toString().trim();
                    ChatMessage chatMessage = new ChatMessage(messageText, LoginActivity.user.UserId);
                    LastMessage lastMessageForOtherUser = new LastMessage(messageText, "true");
                    LastMessage lastMessageForCurrentUser = new LastMessage(messageText, "false");
                    //  ChatRoom chatRoom = new ChatRoom(chatMessage, LoginActivity.user.UserName,ChatActivity.itemUserId);
                    myRef.push().setValue(chatMessage);
                    itemUserRef.push().setValue(chatMessage);

                    myRef.child("item-id").setValue(itemId);
                    myRef.child("user-id").setValue(itemUserId);
                    myRef.child("user-name").setValue(LoginActivity.user.UserName);
                    itemUserRef.child("item-id").setValue(itemId);
                    itemUserRef.child("user-id").setValue(LoginActivity.user.UserId);
                    itemUserRef.child("user-name").setValue(itemUserName);
                    myRef.child("lastMessage").setValue(lastMessageForCurrentUser);
                    itemUserRef.child("lastMessage").setValue(lastMessageForOtherUser);
                    PushMessage pushMessage = new PushMessage(LoginActivity.user.UserId, ChatActivity.itemUserId, messageText);
                    FirebaseDatabase.getInstance().getReference().child("messages").push().setValue(pushMessage);
                    input.setText("");



                   // checker = 1;
                }
            }
        });

       // personList = new ArrayList<HashMap<String, String>>();

         //If it's already active chat
        //if (active == 1)
        //{

             //customToolBar.setTitle(itemUserName);
             //customToolBar.setTitle(itemUserName);

        //   getSupportActionBar().setLogo(R.drawable.nike_shoes);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
           // getSupportActionBar().setDisplayShowTitleEnabled(true);
           // getSupportActionBar().setTitle(itemUserName);
            //getSupportActionBar().setElevation(0);
            //checker = 0;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            myRef = database.getInstance().getReference("Users").child(LoginActivity.user.UserId).child("chat").child(ChatActivity.itemUserId + itemId);
            itemUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(itemUserId).child("chat").child(LoginActivity.user.UserId + itemId);
            myRef.addValueEventListener(postListener1);


       // }

        // If it's not a new chat
        //else  {

           // setTitle(ChatActivity.itemUserName);
            ///ActionBar actionBar = getActionBar();
           //// getSupportActionBar().setDisplayHomeAsUpEnabled(true);
          //  checker = 0;

           // FirebaseDatabase database = FirebaseDatabase.getInstance();

            //myRef = database.getInstance().getReference("Chat").push();

           // String unique = myRef.getKey();

            //myRef = database.getInstance().getReference("Chat").child(unique).child(LoginActivity.user.UserName).child(ChatActivity.itemUserId);
            //user1Ref = database.getInstance().getReference("Users").child(LoginActivity.user.UserName).child("chat");

           /// user2Ref = database.getInstance().getReference("Users").child(ChatActivity.itemUserId).child("chat");
           // user1Ref.child(ChatActivity.itemUserId).setValue(unique);
            //user2Ref.child(LoginActivity.user.UserName).setValue(unique);
            //myRef.addValueEventListener(postListener);
        //}

      //  FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("chat").child(itemUserId + itemId).child("lastMessage").child("recent").setValue("false");

    }

    ValueEventListener postListener1 = new ValueEventListener() {
        @Override
        public void onDataChange(com.google.firebase.database.DataSnapshot outerDataSnapshot) {


            ValueEventListener checkingBlockListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(LoginActivity.user.UserId))
                    {
                       // Toast.makeText(ChatActivity.this, "لا يمكن التواصل مع هاذا المستخدم في الوقت الراهن!", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    else {

                        messageAdapter = new MessageAdapter(ChatActivity.this);
                        for (com.google.firebase.database.DataSnapshot child : outerDataSnapshot.getChildren()) {

                            String messageKey = child.getKey();

                            if (!messageKey.matches("lastMessage") && !messageKey.matches("item-id") && !messageKey.matches("user-id") && !messageKey.matches("user-name")) {

                                HashMap<String, String> message = new HashMap<String, String>();
                                for (com.google.firebase.database.DataSnapshot child2 : child.getChildren()) {

                                    if (child2.getKey().matches("messageText")) {
                                        message.put("messageText", child2.getValue().toString());
                                    } else if (child2.getKey().matches("messageFrom")) {
                                        message.put("messageFrom", child2.getValue().toString());
                                    }
                                }

                                messageAdapter.addMessage(message);

                            }
                        }

                        pDialog.dismiss();

                        //    adapter = new SimpleAdapter(
                        //          Chat.this, messages, R.layout.message_left,
                        ///        new String[]{"messageText", "messageFrom"},
                        //     new int[]{R.id.message_text, R.id.message_user}
                        //);
                        listView.setAdapter(messageAdapter);
                        listView.setSelection(messageAdapter.getCount() - 1);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Log.e(databaseError.getMessage(), databaseError.getDetails());
                }
            };

            // checking the user blocked or not.
            FirebaseDatabase.getInstance().getReference().child("Users").child(itemUserId).child("block").addListenerForSingleValueEvent(checkingBlockListener);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

             Log.e(databaseError.getMessage(), databaseError.getDetails());
        }
    };

    ValueEventListener postListener = new ValueEventListener() {


        @Override
        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {


            for (com.google.firebase.database.DataSnapshot child : dataSnapshot.getChildren()) {
                HashMap<String, String> message = new HashMap<String, String>();

                for (com.google.firebase.database.DataSnapshot child2 : child.getChildren()) {

                    if (child2.getKey().matches("messageFrom")) {
                        message.put("messageText", child2.getValue().toString());
                    } else if (child2.getKey().matches("messageFrom")) {
                        message.put("messageFrom", child2.getValue().toString());
                    }
                }

                messageAdapter.addMessage(message);
            }

            pDialog.dismiss();
            listView.setAdapter(messageAdapter);
            listView.setSelection(messageAdapter.getCount() - 1);

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {



        }
    };


    public void onClickItem(View view)
    {
        BrowsingItemActivity.itemId = itemId;
        Intent itemIntent = new Intent(ChatActivity.this, BrowsingItemActivity.class);

        startActivity(itemIntent);
    }

    public void onClickUser(View view)
    {
        ProfileActivity.userId = itemUserId;
        ProfileActivity.userName = itemUserName;
        Intent userIntent = new Intent(ChatActivity.this, ProfileActivity.class);
        startActivity(userIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                onBackPressed();
                break;

            case R.id.show_item:
                BrowsingItemActivity.itemId = itemId;
                Intent itemIntent = new Intent(ChatActivity.this, BrowsingItemActivity.class);
                startActivity(itemIntent);
                break;

            case R.id.safety_tips:
                Toast.makeText(this, "عذرا، هاذه الخدمة غير متوفرة حاليا!", Toast.LENGTH_LONG).show();
                break;

            case R.id.block_user:
                //block user
                blockUser();
              //  FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("chat").child(itemUserId).removeValue();
              //  FirebaseDatabase.getInstance().getReference().child("Users").child(itemUserId).child("chat").child(LoginActivity.user.UserId).removeValue();
                //FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("block").child(itemUserId).setValue(itemUserName);
                break;


            case R.id.remove_chat:

                FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("chat").child(itemUserId + itemId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                    }
                });
                break;

            case R.id.report_user:
                ReportUserActivity.reportedUserId = itemUserId;
                Intent reportUserIntent = new Intent(ChatActivity.this, ReportUserActivity.class);
                startActivity(reportUserIntent);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void blockUser()
    {
        ValueEventListener deleteCurrentUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot message: dataSnapshot.getChildren())
                {
                    String blockedUserKey =  (String) message.child("user-id").getValue();

                    if (blockedUserKey.matches(itemUserId))
                    {
                        String messageKey = message.getKey();
                        FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("chat").child(messageKey).removeValue();
                    }
                }

                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(databaseError.getMessage(),  databaseError.getDetails());
            }
        };

        ValueEventListener deleteOtherUserListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot message: dataSnapshot.getChildren())
                {
                    String blockedUserKey =  (String) message.child("user-id").getValue();

                    if (blockedUserKey.matches(LoginActivity.user.UserId))
                    {
                        String messageKey = message.getKey();
                        FirebaseDatabase.getInstance().getReference().child("Users").child(itemUserId).child("chat").child(messageKey).removeValue();
                    }
                }

                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("chat").addListenerForSingleValueEvent(deleteCurrentUserListener);
        FirebaseDatabase.getInstance().getReference().child("Users").child(itemUserId).child("chat").addListenerForSingleValueEvent(deleteOtherUserListener);
        FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("block").child(itemUserId).setValue(itemUserName);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyFirebaseMessagingService.theUserTexting = true;
    }

    @Override
    protected void onPause() {

        super.onPause();
        MyFirebaseMessagingService.theUserTexting = false;

    }

    private void setupToolBarItems(Toolbar toolbar, final AppCompatActivity activity)
    {
        ImageView itemImageView =  (ImageView) findViewById(R.id.toolbar_item_image);
        ImageView userImageView =  (ImageView) findViewById(R.id.toolbar_user_image);
        TextView buyTextView = (TextView) findViewById(R.id.toolbar_buy_text);
        buyTextView.setVisibility(View.GONE);
        itemImageView.setBackgroundColor(Color.parseColor(Constants.lightGray));
        StorageReference itemImageRef = FirebaseStorage.getInstance().getReference().child("Items_Photos").child(itemId).child("1.jpeg");
        StorageReference userImageRef = FirebaseStorage.getInstance().getReference().child("Profile_Pictures").child(itemUserId).child("Profile.jpg");

        Glide.with(this)
            .using(new FirebaseImageLoader())
            .load(itemImageRef).animate(android.R.anim.fade_in)
            .into(itemImageView);

        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(userImageRef).animate(android.R.anim.fade_in).thumbnail(Glide.with(this).load(R.drawable.spinner_gif)).crossFade()
                .into(userImageView);

        buyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }
}
