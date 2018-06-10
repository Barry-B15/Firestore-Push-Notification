package com.example.barry.firebasepushnotificationdemo.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.barry.firebasepushnotificationdemo.R;
import com.google.firebase.messaging.RemoteMessage;

/**
 * This doc links were heavily used for this notification section
 * https://firebase.google.com/docs/cloud-messaging/android/client?hl=ja
 * https://developer.android.com/reference/android/app/Notification.Builder
 * https://developer.android.com/training/notify-user/build-notification#action
 * https://developer.android.com/training/notify-user/build-notification
 *
 *
 * * 1. Create a new java class FirebaseMessagingService .
 *      we need the firebase messaging srvice to send notif to users
 *      - have the class extend FirebaseMesssagingService
 *
 * 2. add this to manifest,
 *
 *      <service
 *          android: name = ".FirebaseMessagingService" >
 *          <intent-filter>
 *              <action android: name = "com.google.firebase.MESSAGING_EVENT" />
 *          </ intent-filter>
 *      </ service>
 * doc at:
 * https://firebase.google.com/docs/cloud-messaging/android/client?hl=ja

 3. aDD  the messaging dependecy to gradle if you havent done that already

 4. For more complicated app
 *  consider using the .MyFirebaseInstanceIDService in the manifest Vs our method
 *  using token in Registration and Login class as we did before in this video
 *  of course thats just another approach
 *  See doc:
 *  https://firebase.google.com/docs/cloud-messaging/android/client?hl=ja
 * 5. Override onMessageReceivedRemoteMessage
 *
 * 6. add a notification builder
 * https://developer.android.com/reference/android/app/Notification.Builder
 * https://developer.android.com/training/notify-user/build-notification#action
 *
         NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
             .setSmallIcon(R.drawable.notification_icon)
             .setContentTitle(textTitle)
             .setContentText(textContent)
             .setPriority(NotificationCompat.PRIORITY_DEFAULT);

 * 7. add a default_notification_channel_id to manifest
 *      <meta-data
 *      android: name = "com.google.firebase.messaging.default_notification_channel_id"
 *      android: value = "@ string / default_notification_channel_id" />
 *
 *      doc: https://firebase.google.com/docs/cloud-messaging/android/client?hl=ja
 *
 *      create the string/default_notification_channel_id in res/value/string to fix the error
 *
 *      - ADD THE CODE HERE
 *      - SET AN ID FOR THE NOTIF
 *
 *      NOW RUN
 *
 * 8. Create a NotificationActivity class
 *
 * 9. Create an intent here just below the notif builder
 *
 * 10. add a intent filter to NotificationActivity() in Manifest
 *      <activity android:name=".NotificationActivity">
 *          <intent-filter>
 *          <action android:name="com.example.barry.firebasepushnotificationdemo.TARGETNOTIFICATION" />  //any random name that doesn't match other things in the app will do
 *          <category android:name="android.intent.category.DEFAULT" />
 *          </intent-filter>
 *      </activity>
 *
 *11. Go back to the node js file, add a click action to the payload
 *      click_action="the random name added in manifest NotificationActivity for action"
 *
 *12. Come back here to get the notif, define the click_action string
 *      ** then pass that click action to the intent
 *
 *13. Go back to the node js file,
 *      create the data node to handle the messages
 *      inside data , creaate a "message" and a "from_id" vars
 * 14. back here:
 *      Create a string for the dataMessage for the message and dataFrom for the from_id
 *
 *15. pass the message from firebaseMessaging service using intent extra
 *      pass the dataMessage and dataFrom as intent extras in our intent
 *
 * 16. Now go to the NotificationActivity to get the notif
 *
 *
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    //private static final int NOTIFICATION_ID = 1;
    //private static final String CHANNEL_ID = "Zini_CH_01";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String messageTitle = remoteMessage.getNotification().getTitle();
        String messageBody = remoteMessage.getNotification().getBody();

        // string to receive the click action, this takes user to the notif page when the notif is clicked
        // pass the click_action to the intent
        // make sure to add the click_action also to the nodes.js file
        String click_action = remoteMessage.getNotification().getClickAction();

        //14. string for message
        String dataMessage = remoteMessage.getData().get("message");
        String dataFrom = remoteMessage.getData().get("from_user_id");

        // add a notification builder
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this,
                        getString(R.string.default_notification_channel_id)) // make the 2nd param get the default we just created
                .setSmallIcon(R.mipmap.ic_launcher) // use the ic_launcher or any
                .setContentTitle(messageTitle) // above message title
                .setContentText(messageBody);  // above message body as defined
                //.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                //.setVibrate(new long[] {0, 100, 100, 100, 100, 100} for vibration

        // create an intent
        Intent resultIntent = new Intent(click_action); // click_action is added to the intent here
        //15. pass the strings as intent extras
        resultIntent.putExtra("message", dataMessage);
        resultIntent.putExtra("from_user_id", dataFrom);


        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        mBuilder.setContentIntent(resultPendingIntent);


        //after the notif builder we need a notif id
        // set an id for the notif
        int mNotificationId = (int) System.currentTimeMillis(); // GENERATE random id each time user visits notif

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE); //get an instance of the notif mgr service

        // My notification wasn't showing in android 8, read that it needed to create channel
        // see full code below. I am using this if statement to achieve the same thing; WORKING
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            /* simple way to create channel and enable other stuff
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, dataFrom, mNotificationId);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[] {0, 100, 100, 100, 100, 100});
            notificationChannel.setDescription("My Channel");
            */

            mNotifyMgr.notify(mNotificationId, mBuilder.build()); // build the notif and issue it
        }
        else {
            mNotifyMgr.notify(mNotificationId, mBuilder.build()); // build the notif and issue it
        }

        //createNotificationChannel();
    }

    //For  version O+
    // READ FURTHER: https://developer.android.com/training/notify-user/channels
    //https://stackoverflow.com/questions/45395669/notifications-fail-to-display-in-android-oreo-api-26
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //CharSequence name = getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, dataFrom, importance); //new NotificationChannel(CHANNEL_ID, name, importance);

            Notification notification = new Notification.Builder(this,
                    getString(R.string.default_notification_channel_id))
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setChannelId(CHANNEL_ID)
                    .build();

            NotificationManager mNotificationMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotificationMgr.createNotificationChannel(notificationChannel);

            mNotificationMgr.notify(mNotificationId, mBuilder.build());
        }*/

    /*//This video says we need to create channels for notif to work in android version 8+
    // https://youtu.be/Qo2IxY9eDhw
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Personal Notifications";
            String description = "Include all the personal notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);

            notificationChannel.setDescription(description);

            // create notif channel
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }*/
}
