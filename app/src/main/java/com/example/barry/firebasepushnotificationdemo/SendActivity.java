package com.example.barry.firebasepushnotificationdemo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SendActivity extends AppCompatActivity {

    private TextView user_id_view;

    private String mUserId;
    private String mUserName; // user name to send notif // UNUNUNUN USER NAME
    private String mCurrentId;

    private EditText mMessageView;
    private Button mSendBtn;
    private ProgressBar mMessageProgress;

    private FirebaseFirestore mFirestore;
    //private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        user_id_view = findViewById(R.id.user_id_view); // view to display user id
        mMessageView = findViewById(R.id.message_view);
        mSendBtn = findViewById(R.id.send_btn);
        mMessageProgress = findViewById(R.id.message_progress);

        mFirestore = FirebaseFirestore.getInstance();
        //we don't need authentication, just the id the user who is signed in
        mCurrentId = FirebaseAuth.getInstance().getUid();

        // get the user_id sent from the RecyclerAdapter
        mUserId = getIntent().getStringExtra("user_id");
        mUserName = getIntent().getStringExtra("user_name");  // get the intentExtra here // UNUNUNUN USER NAME
        
        //user_id_view.setText(mUserId); // set text to the user_id_view
        // this displays the selected user's id but if we want to display the name of the user instead
        // we have to set a new query (longer) or in our adapter, since we get the name of the user,
        // we can pass in the name along with the intent to avoid writing a lot of query just to get a name
        // come back here and retrieve it
        String sendToUser = "Send To : " + mUserName; // I created this var to avoid concat in the setText() below
        user_id_view.setText(sendToUser); // pass it to the view to display UNUNUNUN USER NAME
        // Now RUN

        // set click listener to the btn
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // store the text fromthe editext in a string var "message"
                String message = mMessageView.getText().toString();

                // check that user actually entered some text
                if (!TextUtils.isEmpty(message)) {

                    mMessageProgress.setVisibility(View.VISIBLE);

                    // create a hashmap for the data and put the message and who is sending the message
                    Map<String, Object> notificationMessage = new HashMap<>();
                    notificationMessage.put("message", message);
                    notificationMessage.put("from", mCurrentId);

                    // create a Notification sub-collection under Users collection in firebase
                    mFirestore.collection("Users/" + mUserId + "/Notifications")
                            .add(notificationMessage)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    Toast.makeText(SendActivity.this,
                                            "Notification sent", Toast.LENGTH_SHORT).show();

                                    mMessageView.setText(" "); // set edit text to blank

                                    mMessageProgress.setVisibility(View.INVISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(SendActivity.this,
                                    "Error :" + e.getMessage(), Toast.LENGTH_LONG).show();

                            mMessageProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
    }
}
