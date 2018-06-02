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
 * To Send Notifications, we 1st store that in Firebase,
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
 *
 *
 *
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
