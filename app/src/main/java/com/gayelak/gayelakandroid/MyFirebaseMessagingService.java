package com.gayelak.gayelakandroid;

/**
 * Created by radibarq on 3/13/18.
 */

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static final String NOTIFICATION_CHANNEL_ID = "my_channel_01";
    public static final String NOTIFICATION_CHANNEL_NAME = "normal";
    public static final String MESSAGE_CHANNEL_NAME = "message";
    public static  final String MESSAGE_CHANNEL_ID = "my_channel_02";
    public static HashMap<String, Integer> messagesCountForEachUser = new HashMap<String, Integer>();
    public  static HashMap<String, Integer> messagesCount = new HashMap<String, Integer>();


    public static int notificationsNumber = 0;
    // this is for the messages channel id to make more than one channel for the notififications.
    public static int messagesCounter = 300;
    public static  int messagesNumber = 0;

    public static boolean theUserTexting = false;



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        String title = remoteMessage.getData().get("title");


        // here the user received notification
        if(title.matches("notification"))
        {
            sendNormalNotification( remoteMessage.getData().get("body"), remoteMessage.getData().get("userName"), remoteMessage.getData().get("itemId"));
        }

        // here the user received message

      if (title.matches("message"))
      {

          if (!theUserTexting) {

              sendMessageNotification(remoteMessage.getData().get("body"), remoteMessage.getData().get("userName"), remoteMessage.getData().get("senderId"));

          }
      }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void handleNow() {

        Log.d(TAG, "Short lived task is done.");
    }

    private  void sendMessageNotification(String messageText, String senderUserName, String senderUserId)
    {
        FirebaseStorage.getInstance().getReference().child("Profile_Pictures").child(senderUserId).child("Profile.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                long[] pattern = {500, 500, 500, 500, 500};
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                CharSequence channelName = MESSAGE_CHANNEL_NAME;
                int importance = NotificationManager.IMPORTANCE_LOW;

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
                    notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.BLUE);
                    notificationChannel.enableVibration(true);
                    notificationChannel.setVibrationPattern(pattern);
                    notificationManager.createNotificationChannel(notificationChannel);
                }

                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                intent.putExtra("notificationType", "message");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);



                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));

                imageLoader.loadImage(uri.toString(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        // Do whatever you want with Bitmap

                        int currentMessagesCount = 1;

                        if (messagesCountForEachUser.get(senderUserId) == null)
                        {
                            messagesCountForEachUser.put(senderUserId, currentMessagesCount);
                            messagesCounter++;
                            messagesCount.put(senderUserId, messagesCounter);
                        }

                        else {

                            currentMessagesCount = messagesCountForEachUser.get(senderUserId);
                            currentMessagesCount++;
                            messagesCountForEachUser.put(senderUserId, currentMessagesCount);

                        }

                        Bitmap bit= loadedImage;
                        bit = getCircleBitmap(bit);
                        NotificationCompat.Builder notificationBuilder = null;
                        notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                                .setLargeIcon(bit)
                                .setSmallIcon(R.drawable.gayelak_logo_transparent)
                                .setContentTitle(senderUserName)
                                .setContentText(messageText)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .setVibrate(pattern)
                                .setNumber(currentMessagesCount)
                                .setLights(Color.BLUE, 1, 1)
                                .setSound(defaultSoundUri);
                        //.setContentIntent(pendingIntent);
                        notificationManager.notify(messagesCount.get(senderUserId) /* ID of notification */, notificationBuilder.build());

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.w("URI_ERROR_IN_ITEM", exception.getLocalizedMessage());
            }
        });

    }

    private void sendNormalNotification(String messageBody, String userName, String itemId) {

          final String[] notificationBody = {""};

           FirebaseStorage.getInstance().getReference().child("Items_Photos").child(itemId).child("1.jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
               @Override
               public void onSuccess(Uri uri) {


                if (messageBody.matches("favourite"))
                {

                    notificationBody[0] = "تم الاعجاب بمنتجك من قبل " + userName;

                }

                else if (messageBody.matches("dsicarded"))
                {
                    notificationBody[0] = "تم حذف المنتج الخاص بك لعدم مطابقة الصورة للمواصفات!";

                }

                //it's a message coming to the user
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                intent.putExtra("notificationType", "normal");

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                long[] pattern = {500, 500, 500, 500, 500};
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                CharSequence channelName = NOTIFICATION_CHANNEL_NAME;
                int importance = NotificationManager.IMPORTANCE_LOW;
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
                    notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.BLUE);
                    notificationChannel.enableVibration(true);
                    notificationChannel.setVibrationPattern(pattern);
                    notificationManager.createNotificationChannel(notificationChannel);
                }

                   ImageLoader imageLoader = ImageLoader.getInstance();
                   imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));
                   notificationsNumber++;

                   imageLoader.loadImage(uri.toString(), new SimpleImageLoadingListener() {
                       @Override
                       public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                           // Do whatever you want with Bitmap

                           Bitmap bit= loadedImage;
                           NotificationCompat.Builder notificationBuilder = null;


                           notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                                   .setLargeIcon(bit)
                                   .setSmallIcon(R.drawable.gayelak_logo_transparent)
                                   .setContentTitle("Gayelak")
                                   .setContentText(notificationBody[0])
                                   .setAutoCancel(true)
                                   .setVibrate(pattern)
                                   .setNumber(notificationsNumber)
                                   .setContentIntent(pendingIntent)
                                   .setLights(Color.BLUE, 1, 1)
                                   .setSound(defaultSoundUri);
                           //.setContentIntent(pendingIntent);

                           notificationManager.notify(234 /* ID of notification */, notificationBuilder.build());

                       }
                   });
            }

        }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception exception) {
                   Log.w("URI_ERROR_IN_ITEM", exception.getLocalizedMessage());
               }
           });

    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {

        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        return output;

    }

}