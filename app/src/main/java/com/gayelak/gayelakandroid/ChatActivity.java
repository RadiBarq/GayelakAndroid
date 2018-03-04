package com.gayelak.gayelakandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.common.ConnectionResult;

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

    // to know weather the user came from the messages or the item.
    public static boolean cameFromMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messageAdapter = new MessageAdapter(this);

        /// Related to the diaglog
        pDialog = new ProgressDialog(ChatActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
        Button fab = (Button) findViewById(R.id.fab);
        listView = (ListView) findViewById(R.id.list_of_messages);
        messages = new ArrayList<HashMap<String, String>>();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText input = (EditText) findViewById(R.id.input);
                if (!input.getText().toString().trim().matches("")) {
                    String messageText = input.getText().toString().trim();
                    ChatMessage chatMessage = new ChatMessage(messageText, LoginActivity.user.UserName);
                    LastMessage lastMessageForOtherUser = new LastMessage(messageText, "true");
                    LastMessage lastMessageForCurrentUser = new LastMessage(messageText, "false");
                  //  ChatRoom chatRoom = new ChatRoom(chatMessage, LoginActivity.user.UserName,ChatActivity.itemUserId);
                    myRef.push().setValue(chatMessage);
                    itemUserRef.push().setValue(chatMessage);
                    myRef.child("lastMessage").setValue(lastMessageForCurrentUser);
                    itemUserRef.child("lastMessage").setValue(lastMessageForOtherUser);
                    input.setText("");
                   // checker = 1;
                }
            }
        });

       // personList = new ArrayList<HashMap<String, String>>();

         //If it's already active chat
        //if (active == 1)
        //{
            setTitle(itemUserName);
            ActionBar actionBar = getActionBar();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //checker = 0;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            myRef = database.getInstance().getReference("Users").child(LoginActivity.user.UserId).child("chat").child(ChatActivity.itemUserId);
            itemUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(itemUserId).child("chat").child(LoginActivity.user.UserId);
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

        FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("chat").child(itemUserId).child("lastMessage").child("recent").setValue("false");

    }

    ValueEventListener postListener1 = new ValueEventListener() {
        @Override
        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

            messageAdapter = new MessageAdapter(ChatActivity.this);
            for (com.google.firebase.database.DataSnapshot child : dataSnapshot.getChildren()) {
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

            pDialog.dismiss();

            //    adapter = new SimpleAdapter(
            //          Chat.this, messages, R.layout.message_left,
            ///        new String[]{"messageText", "messageFrom"},
            //     new int[]{R.id.message_text, R.id.message_user}
            //);
            listView.setAdapter(messageAdapter);
            listView.setSelection(messageAdapter.getCount() - 1);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_action_bar, menu);
        return true;
    }
}
