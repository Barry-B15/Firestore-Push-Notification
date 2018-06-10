package com.example.barry.firebasepushnotificationdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 1. add a text view to the xml file
 *      init it here
 * 2. go create an intent in the FirebaseNotificationService class
 * 3. following  16. In FirebaseMessagingService class
 *          get the intent from FirebaseMessagingService class,
 *          store the values in the 2 strings for data message and dataFrom in onCreate
 *
 * 4. set the strings to the textView to display
 *
 * 5. open the power shell now,(be sure you are signed in to the  firestore)
 *      change directory to the project and deploy
 *
 *      RUN
 */
public class NotificationActivity extends AppCompatActivity {

    private TextView mNotifdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // get the intent from FirebaseMessagingService class, store in the strings
        String dataMessage = getIntent().getStringExtra("message");
        String dataFrom = getIntent().getStringExtra("from_user_id");

        mNotifdata = findViewById(R.id.notif_text);

        // set the data to text passing the 2 strings
        mNotifdata.setText(" FROM : " + dataFrom + " | MESSAGE : " + dataMessage);
    }
}
