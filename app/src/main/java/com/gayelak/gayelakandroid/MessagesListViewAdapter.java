package com.gayelak.gayelakandroid;

import android.content.Context;
import android.graphics.Color;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.gayelak.gayelakandroid.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by radibarq on 1/30/18.
 */

public class MessagesListViewAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    double screenHeight;
    DatabaseReference messagesRef;
    static ArrayList<String> usersKeys;
    static ArrayList<String> usersNames;
    ArrayList<LastMessage> lastMessages;
    private HashMap <String, LastMessage> keyLastMessage;
    private LottieAnimationView loadingAnimationView;
    private Map<String, Double> sortedMessages;

    MessagesListViewAdapter(Context context, double screenHeight, LottieAnimationView loadingAnimationView) {

        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.screenHeight = screenHeight;
        messagesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("chat");
        usersKeys = new ArrayList<String>();
        lastMessages = new ArrayList<LastMessage>();
        sortedMessages = new HashMap<String, Double>();
        keyLastMessage = new HashMap<String, LastMessage>();
        usersNames = new ArrayList<String>();
        this.loadingAnimationView = loadingAnimationView;
        getMessages();
    }

    @Override
    public int getCount() {
        return usersKeys.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        view = mLayoutInflater.inflate(R.layout.messages_list_view_layout, parent, false);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = (int) (screenHeight / 10);
        view.setLayoutParams(params);
        final TextView contactNameTextField = (TextView) view.findViewById(R.id.contactName);
        final TextView lastMessageTextField = (TextView) view.findViewById(R.id.lasMessage);
        final TextView dateTextViewTextField = (TextView) view.findViewById(R.id.dateTextView);
        final ImageView profileImageView = (ImageView) view.findViewById(R.id.imageView);
        final String userKey = usersKeys.get(position);
        final LastMessage lastMessage = lastMessages.get(position);
        final int currentPoisition = position;

        ValueEventListener userNameListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String userName = dataSnapshot.getValue().toString();
                StorageReference contactImageRef = FirebaseStorage.getInstance().getReference().child("Profile_Pictures").child(userKey).child("Profile.jpg");
                Glide.with(mContext)
                        .using(new FirebaseImageLoader())
                        .load(contactImageRef).animate(android.R.anim.fade_in).thumbnail(Glide.with(mContext).load(R.drawable.spinner_gif)).crossFade()
                        .into(profileImageView);
                contactNameTextField.setText(userName);
                usersNames.add(userName);
                lastMessageTextField.setText(lastMessage.message);
                String date = getDateCurrentTimeZone(lastMessage.time);
                dateTextViewTextField.setText(date);

                if (lastMessage.recent.matches("true"))
                {
                    lastMessageTextField.setTextColor(Color.parseColor("#000000"));
                }

                if (currentPoisition == usersKeys.size() - 1)
                {
                    stopLoadingAnimation();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText(mContext, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        FirebaseDatabase.getInstance().getReference().child("Users").child(userKey).child("UserName").addListenerForSingleValueEvent(userNameListener);
        return view;
    }

    private void getMessages() {

        ValueEventListener postListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                playLoadingAnimation();
                usersKeys = new ArrayList<String>();
                usersNames = new ArrayList<String>();
                lastMessages = new ArrayList<LastMessage>();
                sortedMessages = new HashMap<String, Double>();
                keyLastMessage = new HashMap<String, LastMessage>();

                for (DataSnapshot message : dataSnapshot.getChildren()) {

                    String key = message.getKey();
                    LastMessage lasMessage = message.child("lastMessage").getValue(LastMessage.class);
                   // usersKeys.add(key);
                    //lastMessages.add(lasMessage);
                    keyLastMessage.put(key, lasMessage);
                    sortedMessages.put(key, lasMessage.time);
                }

                if (sortedMessages.size() == 0)
                {
                    stopLoadingAnimation();
                    MessagesListViewAdapter.this.notifyDataSetChanged();

                }

                else {

                    sortedMessages = sortByValue(sortedMessages);
                    addMessagesFromMapToTheArray(sortedMessages);
                    MessagesListViewAdapter.this.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText(mContext, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        messagesRef.addValueEventListener(postListener);
    }

    private String getDateCurrentTimeZone(double timestamp) {

        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis( (long) timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", new Locale("ar"));
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);

        }catch (Exception e) {

        }

        return "";
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

    private Map<String, Double> sortByValue(Map<String, Double> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<String, Double>> list =
                new LinkedList<Map.Entry<String, Double>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        /*
        //classic iterator example
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }*/

        return sortedMap;
    }

    public  <K, V> void addMessagesFromMapToTheArray(Map<K, V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {

                   usersKeys.add(entry.getKey().toString());
                   lastMessages.add(keyLastMessage.get(entry.getKey().toString()));
        }
    }

}