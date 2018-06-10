package com.example.barry.firebasepushnotificationdemo.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.barry.firebasepushnotificationdemo.MainActivity;
import com.example.barry.firebasepushnotificationdemo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mLoginBtn;
    private Button mRegPageBtn;

    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;  // VIDEO 4

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            sendToMain();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailField = findViewById(R.id.login_email);
        mPasswordField = findViewById(R.id.login_password);
        mLoginBtn = findViewById(R.id.login_btn);
        mRegPageBtn = findViewById(R.id.login_register_btn);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance(); // VIDEO 4

        mProgressBar = findViewById(R.id.login_progress);

        mRegPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(regIntent);
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

                    mProgressBar.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        //NNNNNNNNNNNNNNNNNNNNNNNN NOTIFICATIONS NNNNNNNNNNNNNNNNN
                                        // VIDEO 4
                                        /* for notification, we need the id of the user so comment out this and replace w/folowing
                                        sendToMain();
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        */
                                        // REPLACE WITH
                                        String token_id = FirebaseInstanceId.getInstance().getToken(); // get token id from firebase
                                        String current_id = mAuth.getCurrentUser().getUid(); // get current id of the user

                                        // store the token in firestore
                                        // 1st make a token map
                                        Map<String, Object> tokenMap = new HashMap<>();
                                        tokenMap.put("token_id", token_id);

                                        mFirestore.collection("Users") // ref the collection for Users
                                                .document(current_id) // put the current user id
                                                //.set(tokenMap) // set the usermap, it deletes name and image in fb and sets the token
                                                .update(tokenMap) // this updates with the token id, better
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        // move the old code here to send user to main
                                                        sendToMain();
                                                        mProgressBar.setVisibility(View.INVISIBLE);
                                                    }
                                                });
                                        // with this each time the user logs in,
                                        // we store the id of the device with which they
                                        // login to firebase db and retrieve that when we send notif

                                        // We need to remove this token when user signs out to
                                        // avoid using same token for others who may sign in with
                                        // the same device
                                        // Go to Profile, immediately after logout(), remove token from fb
                                        //NNNNNNNNNNNNNNNNNNNNNNNN NOTIFICATIONS End NNNNNNNNNNNNNNNNN
                                    } else {
                                        Toast.makeText(LoginActivity.this,
                                                "Error : " + task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();

                                        mProgressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                }
            }
        });
    }

    private void sendToMain() {

        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

}
