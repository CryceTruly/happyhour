package com.crycetruly.happyhour;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crycetruly.happyhour.utils.Handy;
import com.crycetruly.happyhour.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private final String USER_DB = "Users";
    private final String PLACE_DB = "Place_Users";
    EditText input_username;
    Toolbar toolbar;
    RelativeLayout relativeLayout;
    private EditText placeemailfield;
    private EditText placePassword, password2, reg_key;
    private ProgressDialog mProgress;
    private FirebaseAuth mAuth;
    RelativeLayout main;
    private static final String TAG = "RegisterActivity";
    private FirebaseFirestore mDatabase, mDatabaseUser, keys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initToolbar();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();
        mProgress = new ProgressDialog(this);
        placeemailfield = findViewById(R.id.emailInput);
        placePassword = findViewById(R.id.passwordInput);
        relativeLayout=findViewById(R.id.rel);
        password2 = findViewById(R.id.passwordInput2);
        input_username = findViewById(R.id.input_username);
        Button regbutton = findViewById(R.id.registerButton);
        regbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegistering();
            }
        });

    }

    private void startRegistering() {
        final String placeemail = placeemailfield.getText().toString().trim().toLowerCase();
        String placepassword = placePassword.getText().toString().trim();
        String placepassword2 = password2.getText().toString().trim();
        final String place_name = input_username.getText().toString().trim();


        if (!TextUtils.isEmpty(placeemail) && !Utils.isValidEmail(placeemail) && !TextUtils.isEmpty(place_name)) {

            Snackbar.make(relativeLayout,"Invalid email",Snackbar.LENGTH_SHORT).show();
        }


        if (!TextUtils.isEmpty(placeemail) && Utils.isValidEmail(placeemail) && !TextUtils.isEmpty(placepassword) && !TextUtils.isEmpty(place_name)) {


            if (TextUtils.isEmpty(place_name)) {
                Snackbar.make(relativeLayout,"Your Name is required",Snackbar.LENGTH_SHORT).show();


                return;
            }

            if (place_name.length() < 3) {
                Snackbar.make(relativeLayout,"Name is too short",Snackbar.LENGTH_SHORT).show();
            return;
            }
            if (placePassword.length() < 6) {
                Snackbar.make(relativeLayout,"Your password is too short,It should be atleast 6 characters",Snackbar.LENGTH_SHORT).show();
               return;
            }
            if (password2.length() < 6) {
                Snackbar.make(relativeLayout,"Your password is too short,It should be atleast 6 characters",Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (!Handy.doPasswordsMatch(placepassword, placepassword2)) {
                Snackbar.make(relativeLayout,"Your passwords dont match",Snackbar.LENGTH_SHORT).show();

                return;
            }

            mProgress.setMessage("Please wait...");


            mProgress.setCanceledOnTouchOutside(false);
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(placeemail, placepassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isComplete()) {
                        if (task.isSuccessful()) {

                            Map<String,Object> map = new HashMap();
                            map.put("name", place_name);
                            map.put("email", placeemail);
                            map.put("device_token", FirebaseInstanceId.getInstance().getToken());
                            map.put("type", "regular");
                            map.put("joined", System.currentTimeMillis());
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(Handy.getTrimmedName(place_name))
                                    .build();
                            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates);
                            mDatabase.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .set(map
                                    ).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        Intent i = new Intent(getBaseContext(), HomeActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.getMessage());
                                }
                            });


                        } else {
                            Snackbar.make(relativeLayout,"Registration failed try again later",Snackbar.LENGTH_SHORT).show();
                            mProgress.dismiss();
                        }
                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@android.support.annotation.NonNull Exception e) {
                    Snackbar.make(relativeLayout,e.getMessage(),Snackbar.LENGTH_SHORT).show();

                }
            });


        } else {
            Snackbar.make(relativeLayout,"Please fill all fields",Snackbar.LENGTH_SHORT).show();}


    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


}
