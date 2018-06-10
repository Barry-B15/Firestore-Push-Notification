package com.example.barry.firebasepushnotificationdemo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.barry.firebasepushnotificationdemo.account.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */

/**
 *  vid 3: send notifications
 * https://youtu.be/pm6W9k9CP64?list=PLGCjwl1RrtcRHjHyZAxm_Mq4qvtnundo0
 *
 *DISPLAY USER DETAILS
 * 1. add circular image view to fragment_profile
 *      - add a text view for user name and any other needed view
 *      - init the views
 *
 * 2. get user id with fb firestore
 *      create the var and init fb firestore
 *      create the user_id var and init it also
 *
 * 3. get/retrieve data from the userId
 * RUN
 * 4. To fix delay in data on slide back
 *      add this line to Main.java: mMainPager.setOffScreenPageLimit(2)
 *
 * 5. to retrieve user image, add glide dependences to gradle app and project
 *      - add code here
 */
public class ProfileFragment extends Fragment {

private Button mLogoutBtn;

private FirebaseAuth mAuth;
private FirebaseFirestore mFirestore;
private String mUserId;

private CircleImageView mProfileImage;
private TextView mProfileName;
private EditText mProfileStatus;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        view.findViewById(R.id.fragment_profile).requestFocus(); // put softkeyboard focus on parent

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUserId = mAuth.getCurrentUser().getUid();

        mLogoutBtn = view.findViewById(R.id.profile_logout);
        mProfileImage = view.findViewById(R.id.profile_image);
        mProfileName = view.findViewById(R.id.profile_name);
        mProfileStatus = view.findViewById(R.id.profile_status);

        // Retrieve user data
        mFirestore.collection("Users")
                .document(mUserId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        // get the uID as string
                        String user_name = documentSnapshot.getString("name");
                        String user_image = documentSnapshot.getString("image");

                        // display the user data
                        mProfileName.setText(user_name);

                        // placeholder for image
                        RequestOptions placeholderOption = new RequestOptions();
                        placeholderOption.placeholder(R.mipmap.ic_launcher);

                        // add the placeholderOption to glide
                        Glide.with(container.getContext()) // container.getContext since we are using fragment
                                .setDefaultRequestOptions(placeholderOption) // add the above placeholder
                                .load(user_image)   // what to load - user_image as we defined
                                .into(mProfileImage); // where to display - on the profile_image view

                    }
                });

        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                // for Notifications, move this codes in to the onSuccessListener below
                mAuth.signOut();
                Intent loginIntent = new Intent(container.getContext(), LoginActivity.class);
                startActivity(loginIntent);
                */

                //NNNNNNNNNNNNNNNNNNNNNNNN NOTIFICATIONS Remove token NNNNNNNNNNNNNNNNN
                // VIDEO 4
                // create a hashmap for the token
                Map<String, Object> tokenMapRemove = new HashMap<>();
                //tokenMapRemove.put("token_id", "");  // this sets the token id to blank (token_id = "")
                tokenMapRemove.put("token_id", FieldValue.delete()); // this deletes the token id

                mFirestore.collection("Users")
                        .document(mUserId)
                        .update(tokenMapRemove)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                mAuth.signOut();
                                Intent loginIntent = new Intent(container.getContext(), LoginActivity.class);
                                startActivity(loginIntent);
                            }
                        }); //NOW RUN
                //NNNNNNNNNNNNNNNNNNNNNNNN NOTIFICATIONS EndNNNNNNNNNNNNNNNNN


            }
        });
        return view;
    }

}
