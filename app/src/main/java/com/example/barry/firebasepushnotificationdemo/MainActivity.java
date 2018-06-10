package com.example.barry.firebasepushnotificationdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.barry.firebasepushnotificationdemo.account.LoginActivity;
import com.example.barry.firebasepushnotificationdemo.utils.PagerViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**Vid 1: UI Design
 * https://youtu.be/xkA53Ga6R1k?list=PLGCjwl1RrtcRHjHyZAxm_Mq4qvtnundo0
 * 1. add a linear horizontal view to main.java
 *      - add 3 textViews to it
 * 2. add a view pager below that
 * 3. create the fragments
 * 4. init the fragments here
 * 5. Create an adapter "PagerViewAdapter" to hold the fragments in utils
 *      - declare it here in main
 *      - add code to PagerViewAdapter
 *      - init the adapter here and get support fragment manager
 * 6. create a fragment background in drawable to give a uniform background tp all fragments
 *      add that to the 3 fragment xml layout pages
 * 7. add onPageChange listener to main
 * 8. Create login and register acttivities
 * 9. When user 1st start the app, send user to login page
 * 10. Add code to the register page
 * 11. Connect app to firestore
 * 12. use fb auth to check that user is ssigned in (replace the old intent to login)
 * 13. override onStart() method
 *
 * vid 2: login logout function
 * https://youtu.be/t8SU2PXHzmU?list=PLGCjwl1RrtcRHjHyZAxm_Mq4qvtnundo0
 *
 *1. Change imageBtn in RegistrationActivity to CircleImage view
 *      - also change in Registration . java to Circle Image view
 * 2. Also add click listener to the image btn while in Registration
 *
 * vid 3: send notifications
 * https://youtu.be/pm6W9k9CP64?list=PLGCjwl1RrtcRHjHyZAxm_Mq4qvtnundo0
 *
 *DISPLAY USER DETAILS
 * 1. add circular image view to fragment_profile
 *      - add a text view for user name and any other needed view
 *      add code to profileFragment
 *
 * 2. To display the list of users so we can select who to send notification,
 *      add Recycler dependencies to gradle and add a recycler to Users fragment
 *
 * 3. Create a model class Users for the users
 * 4. create an adapter for Users recycler
 *
 * Preceeding part in UserFragment
 *
 * To add OnClick Listener to our user item we need a user id
 *      - define the user_id in onStart() of UserFragment
 *      - CREATE a new model class UserId(), have ie extend the UserId
 *      - Have the Users class extend the UserId
 *      - append .withId(user_id) to Users users = .... in onStart() of UserFragment
 *      - In UsersRecyclerAdapter onBindViewHolder(),
 *              define the String user_id
 *              and set the onClick listener to it
 *      - Create a new SendActivity class to handle the send notifications
 *      - Add an intent in the onClick listener def above to send users to the sendActivity()
 * RUN
 *
 * ----------------------------------------------
 * To Send Notifications, 1st STORE DATA TO FIRESTORE
 * then from there we use the cloud func to push it to the devices
 *
 *  IN SEND ACTIVITY
 * - 1ST WE HAVE A TEXTVIEW  which will display who we are sending the notif
 * - add an edit text for the messageand a button to sendActivity
 * - assign everything and add a click listener to the btn
 * - Retrieve data from edit text and store that in a string "message"
 * - Store the firebase collection
 * - create a hash map for the data
 * - put the message and the current user id into the map
 * - the create the firestore collection under Users collection so each user will have
 *      their own Notification collection
 *      add the map to it
 *
 * - We also need to get the user name that we are sending notif
 *      here we pass a new query (harder) or
 *      pass in the user name in the adapter (easier) with the intent
 *      then assign it in the Send activity
 *
 *---------------------------------------------------------
 * SEND NOTIFICATION; SEND TO USERS
 * Video 4: https://youtu.be/Abh3r9hh5gw?list=PLGCjwl1RrtcRHjHyZAxm_Mq4qvtnundo0
 *
 * PPPPPPPPPPPP PREPARE THE CLOUD USAGE PPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
 * 1. In firebase, left panel we see "(--) Functions"
 *      we are going to use that to write functions to send notification
 *      to use firebase cloud function, we need NPM node .js to write the functions
 *      so go to npmjs.com to download and install
 *      make sure you install Node with npm (I have this already from another app)
 *
 * 2. click functions in firebase, > get started > see this:
 *          $ npm install -g firebase-tools
 *          copy without the $
 *
 * 3. open the pc window power shell > paste : npm install -g firebase-tools > enter
 *      This will install the firebase tool for npm
 *
 * 4. Back to Firebase > Function Dashboard > Continue > Finish
 *
 * 5. Create a folder FirebasePushNotification on Desktop
 *
 * 6.. In the Shell, change dir to the file just created, just type in:
 *      cd/Users/barry/Desktop  Enter (this changes to the Desktop)
 *      cd/FirebasePushNotif    Enter (this takes us into the new file)
 *
 * 7.  C:/Users/barry/Desktop/FirebasePushNotif > firebase login  (login to firebase)
 *          this will show Already logged in and the email address since I am logged in
 *      - continue the new line with
 *          firebase init functions   ## this helped to create the 3 folders, saw from another video
 *          XX# firebase init      Enter (to init firebase) ### This didn't create the functions folder
 *
         *  Follow thru the leads
         *   This gave me an option to cont (Y/N)
         *   - type in Y Enter
         *   Also to add dependencies, Y and cont
         *
         *   This shows firebase options to choose from,
         *   use the arrow key > move to
         *      Functions: Configure and deploy > Enter
         *
         *   It shows a list of my FB projects to choose from
         *   Use the arrow to move down to the needed project
         *      FirebasePushNotificationDemo > Enter
         *
         *  Initialization completed
         *  It didn't give me the option to select what language to like in the video
         *  Hope it works (could this be from my installation or from my past usages
 *
 * 8. open the FirebasePushNotif folder on desktop (3 files inside)
 *      open the functions folder (3 files , 1 folder auto created )
 *      open index.js with a text editor to add our functions
 *      add the following code
 *
 *      'use-strict'
 *
 *      const functions = require('firebase-functions');
 *      const admin = require('firebase-admin');
 *
 *      // where to export the notif: firestore document Users with user id and notif id
  *       exports.sendNotification = functions.firestore.document("Users/{user_id}/Notifications/{notification_id}").onWrite(event=> {
  *
         // create 2 var for the above func
 *      const user_id = event.params.user_id;
 *       const notification_id = event.params.notification_id;
 *
 *       // log to console
 *      console.log("User ID : " + user_id + " | Notification ID : " + notification_id);
 *
 *      });
 *
 * 9. Deploy to firebase: To deploy
 *      Go back to the Shell, make sure you are still in the same folder
 *      Type in:
 *          firebase deploy enter  OR
 *          (firebase deploy --only functions) enter
 *
     *  It will take like 5 mins, so wait. when done you will see
     *  Deploy completed
 *
 * 10. Now go to firebase Functions Dashboard to see that sendNotification has been added
 *
 * 11. Send a notification > open the Log > see the log
 *
 *  PPPPPPPPPPPP PREPARE THE CLOUD USAGE END PPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
 *
 * Firebase only sends notifications to specific groups (which you have to subscribe to),
 *          devices, or a device token id, not to user IDs
 *
 * To send to a single person we need a token id, lets try to do that
 * 1. Go to the Login page, login btn
 *      Edit to update the user data with a token id
 *
 * 2. Go to Profile fragment imediately after logout to remove this token when user logs out
 *
 * 3. When someone creates a new account, lets also set a token so we can send them notif
 *  Go to Registration() class, add the token_id to the hash map
 *
 * 4. Now go back to edit the index.js file in
 *  Functions in FirebasePushNotifications on Desktop
 *  deploy and test
 *
 * 5. Add payload to actually send the notif
 *      - add firebase-messaging dependency to gradle or the messages wont get sent
 *
 * ---------------------------------------------------------
 * Video 5: Send notif in foreground
 * https://youtu.be/sQez4bRDmEg?list=PLGCjwl1RrtcRHjHyZAxm_Mq4qvtnundo0
 *
 * 1. Create a new java class FirebaseMessagingService .
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
 *
 *  RUN AFTER COMPLETING CODE IN FirebaseMessagingService
 *  (Server side seems okay but I'm not getting the notification on device)
 *
 * 5. Create a new NotificationActivity to display the notifs
 *
 * ---------------------------------------------------------------
 * SHOWING LIST OF NOTIFICATIONS
 * similar to what we did with the users
 * 1. create a recycler view to the fragment_notification xml file
 * 2. Create a "single_notification" layout to handle single notifications
 * 3. create a model class "Notifications"
 * 4. create a "Notifications Adapter" class
 *
 */
public class MainActivity extends AppCompatActivity {

    //v1.4.0
    private TextView mProfileLabel;
    private TextView mUsersLabel;
    private TextView mNotifications;

    private ViewPager mMainPager;

    //v1.5.1
    private PagerViewAdapter mPagerViewAdapter;

    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentuser = mAuth.getCurrentUser();

        if (currentuser == null) {
            sendToLogin();
        }
    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 12. replace this intent with FirebaseAuth to check if user is signed In
        /*// v1.9 When user 1st start the app, send user to login page
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);*/
        mAuth = FirebaseAuth.getInstance();

        //v1.4.1
        mProfileLabel = findViewById(R.id.profileLabel);
        mUsersLabel = findViewById(R.id.usersLabel);
        mNotifications = findViewById(R.id.notificationLabel);

        mMainPager = findViewById(R.id.mainPager);

        //vid 3: fix delay in display when we slide back anf forth on user data
        mMainPager.setOffscreenPageLimit(2); // default is 1 but now we can set to any numb

        //v1.5.3
        mPagerViewAdapter = new PagerViewAdapter(getSupportFragmentManager());
        mMainPager.setAdapter(mPagerViewAdapter); // set the mainpager to mPagerViewAdapter
        //Run

        // optional listen for clixks on the tabs
        mProfileLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mMainPager.setCurrentItem(0);
            }
        });

        mUsersLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainPager.setCurrentItem(1);
            }
        });

        mNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainPager.setCurrentItem(2);
            }
        });

        // v1.7
        mMainPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                changeTabs(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    // method to handle page/tab changes
    private void changeTabs(int position) {

        if (position == 0) {
            mProfileLabel.setTextColor(getColor(R.color.textTabBright));
            mProfileLabel.setTextSize(22);

            mUsersLabel.setTextColor(getColor(R.color.textTabLight));
            mUsersLabel.setTextSize(16);

            mNotifications.setTextColor(getColor(R.color.textTabLight));
            mNotifications.setTextSize(16);
        }

        if (position == 1) {
            mProfileLabel.setTextColor(getColor(R.color.textTabLight));
            mProfileLabel.setTextSize(16);

            mUsersLabel.setTextColor(getColor(R.color.textTabBright));
            mUsersLabel.setTextSize(22);

            mNotifications.setTextColor(getColor(R.color.textTabLight));
            mNotifications.setTextSize(16);
        }

        if (position == 2) {
            mProfileLabel.setTextColor(getColor(R.color.textTabLight));
            mProfileLabel.setTextSize(16);

            mUsersLabel.setTextColor(getColor(R.color.textTabLight));
            mUsersLabel.setTextSize(16);

            mNotifications.setTextColor(getColor(R.color.textTabBright));
            mNotifications.setTextSize(22);
        }
    }
}
