package com.example.barry.firebasepushnotificationdemo.account;

import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 2. add click listener to the image btn
 * 3. override onActivityResult
 * 4. def a uri to store the image
 *      - init it to null in onCreate
 *      - get the data in onActivityResult
 *      - set the to the btn to display
 * 5. add listener to the register btn
 *      In a real app, do image handling in a separate activity
 * 6. Add Firebase Storage
 *      declare and init the Storage ref
 *
 * 7. Add the Firebase Firtestore and the Auth
 *      Init both also
 */
public class RegistrationActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    //1
    private CircleImageView mImageBtn;
    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mRegBtn;
    private Button mLoginPageBtn;

    //vid 2.4 uri to store image
    private Uri imageUri;

    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private ProgressBar mRegProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //vid 2.4 init the uri to null
        imageUri = null;

        // init the storage ref, adding .child helps to also create the dir
        mStorage = FirebaseStorage.getInstance().getReference().child("images");
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        // 2.
        mImageBtn = findViewById(R.id.register_image_btn);
        mNameField = findViewById(R.id.register_name);
        mEmailField = findViewById(R.id.register_email);
        mPasswordField = findViewById(R.id.register_password);
        mRegBtn = findViewById(R.id.register_btn);
        mLoginPageBtn = findViewById(R.id.register_login_btn);

        mRegProgressBar = findViewById(R.id.reg_progress);

        mLoginPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        //vid 2.5 add click listener to regBtn
        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (imageUri != null) {

                    // show progress
                    mRegProgressBar.setVisibility(View.VISIBLE);

                    final String name = mNameField.getText().toString();
                    String email = mEmailField.getText().toString();
                    String password = mPasswordField.getText().toString();

                    // check that the fields are not empty
                    if (!TextUtils.isEmpty(name)
                            && !TextUtils.isEmpty(email)
                            && !TextUtils.isEmpty(password)) {
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {

                                            // for final app, send a user to the setup activity
                                            // store the user data in string
                                            final String user_id = mAuth.getCurrentUser().getUid();
                                            StorageReference user_profile = mStorage.child(user_id + ".jpg");

                                            user_profile.putFile(imageUri)
                                                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> uploadTask) {

                                                    if (uploadTask.isSuccessful()) {

                                                        String download_url = uploadTask.getResult().getDownloadUrl().toString();

                                                        Map<String, Object> userMap = new HashMap<>();
                                                        userMap.put("name", name);
                                                        userMap.put("image", download_url);

                                                        mFirestore.collection("Users")
                                                                .document(user_id)
                                                                .set(userMap)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {

                                                                        // hide progress
                                                                        mRegProgressBar.setVisibility(View.INVISIBLE);

                                                                        // if success
                                                                        sendToMain();
                                                                    }
                                                                });
                                                    } else {
                                                        Toast.makeText(RegistrationActivity.this,
                                                                "Error : " + uploadTask.getException().getMessage(),
                                                                Toast.LENGTH_SHORT).show();

                                                        // hide progress
                                                        mRegProgressBar.setVisibility(View.INVISIBLE);
                                                    }
                                                }
                                            });

                                        } else {
                                            Toast.makeText(RegistrationActivity.this,
                                                    "Error : " + task.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();

                                            // hide progress
                                            mRegProgressBar.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                });


                    }
                }
            }
        });

        //vid 2.2 add click listener
        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Photo"), PICK_IMAGE);
            }
        });

    }

    //send user to main if reg is successful
    private void sendToMain() {
        Intent mainIntent = new Intent(RegistrationActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    //vid 2.3 override onActivityResult

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            mImageBtn.setImageURI(imageUri); // set the imageUri to the btn
        }
    }
}
