package com.crycetruly.happyhour;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.balysv.materialripple.MaterialRippleLayout;
import com.crycetruly.happyhour.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Button amab, login;
    EditText password;
    TextView email_sign_in_button;
    private static final String TAG = "MainActivity";
    private static final int PLACE_PICKER_REQUEST = 1;
    LinearLayout linearLayout;
    private GoogleApiClient mGoogleApiClient;
    private AutoCompleteTextView mEmailView;
    private ProgressBar progressBar;
    private CardView loginlayout;
    MaterialRippleLayout rippleLayout;
    TextView textView;
    AppBarLayout appbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        rippleLayout = findViewById(R.id.ripple);
        textView = findViewById(R.id.text);
        appbar = findViewById(R.id.appbar);

        loginlayout = findViewById(R.id.login_form);
        progressBar = findViewById(R.id.login_progress);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.emaill);
        password = findViewById(R.id.password);
        email_sign_in_button = findViewById(R.id.email_sign_in_button);

        email_sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSigningIn();
            }
        });


        amab = findViewById(R.id.bss);

        amab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                    Snackbar.make(linearLayout, "Cant set up a business account you need to update google play services", Snackbar.LENGTH_LONG).show();
                    int responseCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MainActivity.this);
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(responseCode, MainActivity.this, 0);
                    dialog.show();
                    dialog.setCancelable(false);


                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }


            }
        });

        Button reg = findViewById(R.id.reg);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), RegisterActivity.class));
            }
        });
    }

    private void startSigningIn() {
        String email = mEmailView.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("Email required");
            return;
        }

        if (!Utils.isValidEmail(email)) {
            mEmailView.setError("Invalid email");
            return;
        }


        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {
            if (pass.length() < 6) {
                Snackbar.make(mEmailView, "Your password is too short,It should be atleast 6 characters", Snackbar.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            loginlayout.setVisibility(View.GONE);
            appbar.setVisibility(View.INVISIBLE);
            email_sign_in_button.setEnabled(false);
            rippleLayout.setBackgroundColor(Color.WHITE);
            email_sign_in_button.setTextColor(Color.parseColor("#5677fc"));
            email_sign_in_button.setText("signing in...");

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {


                    if (task.isSuccessful()) {
                        loginUser();


                    } else {
                        email_sign_in_button.setEnabled(true);
                        email_sign_in_button.setText("Log In");
                        rippleLayout.setBackgroundColor(Color.parseColor("#039BE5"));
                        email_sign_in_button.setTextColor(Color.WHITE);
                        progressBar.setVisibility(View.GONE);
                        loginlayout.setVisibility(View.VISIBLE);
                        appbar.setVisibility(View.VISIBLE);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@android.support.annotation.NonNull Exception e) {
                    String res = e.getMessage();
                    if (res.contains("no user")) {
                        Snackbar.make(progressBar, "Sorry account does not exist,create one as a business or a customer", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(progressBar, res, Snackbar.LENGTH_LONG).show();
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(3000);
                                    Snackbar.make(progressBar, "Trouble signing in,Reset Password", Snackbar.LENGTH_LONG).setAction("ok", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startActivity(new Intent(getBaseContext(), ResetPasswordActivity.class));
                                        }
                                    }).show();
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });
                        thread.start();


                    }


                }
            });
        } else {
            Snackbar.make(progressBar, "All fields are required,please check your email and a password", Snackbar.LENGTH_SHORT).show();

        }
    }

    private void loginUser() {
        Log.d(TAG, "loginUser: ");
        Intent i = new Intent(getBaseContext(), HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        Toast.makeText(this, "Authenticated with " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
        startActivity(i);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(getBaseContext(), data);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                if (place != null) {
                    Intent i = new Intent(getBaseContext(), BusinessAuthActivity.class);
                    String name = (String) place.getName();
                    try {
                        String phone = (String) place.getPhoneNumber();
                        i.putExtra("phone", phone);
                    } catch (NullPointerException e) {

                    }
                    LatLng geopoints = place.getLatLng();
                    i.putExtra("lat", geopoints.latitude);
                    i.putExtra("lng", geopoints.longitude);

                    i.putExtra("name", name);
                    i.putExtra("geopoint", geopoints);
                    try {
                        String address = (String) place.getAddress();
                        i.putExtra("address", address);
                    } catch (NullPointerException e) {

                    }

                    if (place.getName().length() > 0)
                        startActivity(i);
                    else
                        Toast.makeText(this, "Cannot set up a business account at this time", Toast.LENGTH_SHORT).show();
                    return;
                }


            }
        }
    }

    private void init() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)

                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Snackbar.make(linearLayout, "Could not connect to google places", Snackbar.LENGTH_SHORT).show();
                    }
                })

                .build();

    }

    public void gotoresend(View view) {
        startActivity(new Intent(getBaseContext(), ResetPasswordActivity.class));
    }
}
