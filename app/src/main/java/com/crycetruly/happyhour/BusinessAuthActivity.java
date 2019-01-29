package com.crycetruly.happyhour;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crycetruly.happyhour.utils.Handy;
import com.crycetruly.happyhour.utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class BusinessAuthActivity extends AppCompatActivity {
    String address, name, phone, geopoints;
    private static final String TAG = "BusinessAuthActivity";
    TextView conf;
    LatLng geopoint;
    Double lat, lng;
    private EditText placeemailfield;
    private EditText placePassword, password2, reg_key;
    private ProgressDialog mProgress;
    private FirebaseAuth mAuth;
    RelativeLayout relativeLayout;
    private FirebaseFirestore mDatabase;
    private AppCompatSpinner appCompatSpinner;

    private String type="Uncategorized";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_auth);
        initToolbar();

        String[] types={"Food Processing","Construction","Real estate","Small manufacturing industries","Tourism and hospitality","Trading","Transport","Computer and internet services","Fashion","Energy"};
        appCompatSpinner=findViewById(R.id.type);
        appCompatSpinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,types));
        appCompatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type=String.valueOf(parent.getItemAtPosition(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Log.d(TAG, "onCreate: " + getIntent().getExtras());
        conf = findViewById(R.id.text);
        lat = getIntent().getDoubleExtra("lat", -16);
        lng = getIntent().getDoubleExtra("lng", 30);
        name = getIntent().getStringExtra("name");
        conf.setText("Configure " + name + "s business account");
        relativeLayout = findViewById(R.id.rel);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();
        mProgress = new ProgressDialog(this);
        placeemailfield = findViewById(R.id.emailInput);
        placePassword = findViewById(R.id.passwordInput);
        password2 = findViewById(R.id.passwordInput2);
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
        if (!TextUtils.isEmpty(placeemail) && !Utils.isValidEmail(placeemail)) {
            Snackbar.make(relativeLayout, "Inavalid Email", Snackbar.LENGTH_SHORT).show();

            return;
        }
        if (name.equals("")) {
            Toast.makeText(this, "Cant create account,previous step wasn`t filled properly", Toast.LENGTH_SHORT).show();
            return;

        }

        if (type.contains("Uncategorized")) {
            Snackbar.make(relativeLayout, "Choose Category", Snackbar.LENGTH_SHORT).show();
            return;

        }


        if (!TextUtils.isEmpty(placeemail) && Utils.isValidEmail(placeemail) &&
                !TextUtils.isEmpty(placepassword)) {

            if (placePassword.length() < 6) {
                Snackbar.make(relativeLayout, "Your password is too short,It should be atleast 6 characters", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (password2.length() < 6) {
                Snackbar.make(relativeLayout, "Your password is too short,It should be atleast 6 characters", Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (!Handy.doPasswordsMatch(placepassword, placepassword2)) {
                Snackbar.make(relativeLayout, "Your passwords dont match", Snackbar.LENGTH_SHORT).show();

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
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(Handy.getTrimmedName(name))
                                    .build();


                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(BusinessAuthActivity.this, "Please verify your Email address", Toast.LENGTH_SHORT).show();

                            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates);
                            Map<Object, Object> map = new HashMap();
                            map.put("name", name);
                            map.put("lat", lat);
                            map.put("lng", lng);
                            map.put("email", placeemail);
                            map.put("device_token", FirebaseInstanceId.getInstance().getToken());
                            map.put("type", "business");
                            map.put("verified", true);
                            map.put("category",type);
                            map.put("address","Not set yet ");

                            map.put("canpost", "false");
                            FirebaseDatabase.getInstance().getReference().child("businesses").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(map, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        }
                                    });
                            mDatabase.collection("businesses").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .set(map
                                    ).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        FirebaseMessaging.getInstance().subscribeToTopic(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        Intent i = new Intent(getBaseContext(), BusinessDashboardActivity.class);
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

                                }
                            });
                        } else {
                           Snackbar.make(relativeLayout, "Registration failed try again later", Snackbar.LENGTH_SHORT).show();
                            try {
                                mProgress.dismiss();
                            } catch (IllegalArgumentException ex) {

                            }
                        }
                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@android.support.annotation.NonNull Exception e) {
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });


        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }


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

    public void hideSoftKeyBoard(){
        InputMethodManager imm= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

     if (getWindow().getCurrentFocus()!=null) {
     }
     }

}


