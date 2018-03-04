package com.gayelak.gayelakandroid;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class NotificationsAdapter extends BaseAdapter{

    Context context;
    LayoutInflater layoutInflater;
    DatabaseReference notificationsReference;
    ArrayList<Notification> notifications;
    StorageReference itemImageRef;
    StorageReference userImageRef;
    private LottieAnimationView loadingAnimationView;

    public NotificationsAdapter(Context context, LottieAnimationView loadingAnimationView)
    {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        notificationsReference = FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.user.UserId).child("notifications");
        notifications = new ArrayList<Notification>();
        this.loadingAnimationView = loadingAnimationView;
        playLoadingAnimation();
        getNotifications(this);
    }


    private void playLoadingAnimation() {

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
    
    private void getNotifications(final NotificationsAdapter adapter)
    {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
               for (DataSnapshot notification: dataSnapshot.getChildren())
               {
                    notifications.add(notification.getValue(Notification.class));
               }

               adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                // ...
            }
        };

        notificationsReference.addListenerForSingleValueEvent(postListener);
    }

    @Override
    public int getCount()
    {
        return notifications.size();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // here where we want to return the item.
        if (position == notifications.size() - 1)
        {
            stopLoadingAnimation();
        }

        View view = layoutInflater.inflate(R.layout.notifications_layout, parent, false);
        ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = 300;
            view.setLayoutParams(params);
        TextView notificationText = view.findViewById(R.id.notificationText);
        TextView notificationTime = view.findViewById(R.id.timeText);
        ImageView userImageView = view.findViewById(R.id.userImage);
        ImageView itemImage = view.findViewById(R.id.itemImage);
        Notification notification = notifications.get(position);
        itemImageRef = FirebaseStorage.getInstance().getReference().child("Items_Photos").child(notification.itemId).child("1.jpeg");
        userImageRef = FirebaseStorage.getInstance().getReference().child("Profile_Pictures").child(notification.userId).child("Profile.jpg");

        if (notification.recent.matches("true"))
        {
            notificationText.setTextColor(Color.parseColor("#000000"));
            notificationsReference.child(notification.notificationId).child("recent").setValue("false");
        }

        if (notification.type == "discarded")
        {
            notificationText.setText(Constants.notificationDiscardedText);
        }

        else
        {
            notificationText.setText(Constants.notificationFavouriteText + " " + notification.userName);
        }

        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(itemImageRef).animate(android.R.anim.fade_in).thumbnail(Glide.with(context).load(R.drawable.spinner_gif)).crossFade()
                .into(itemImage);

        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(userImageRef).animate(android.R.anim.fade_in).thumbnail(Glide.with(context).load(R.drawable.spinner_gif)).crossFade()
                .into(userImageView);
        String date = getDateCurrentTimeZone(notification.timestamp);
        notificationTime.setText(date);
        return view;
    }

    private   String getDateCurrentTimeZone(double timestamp) {

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
}
