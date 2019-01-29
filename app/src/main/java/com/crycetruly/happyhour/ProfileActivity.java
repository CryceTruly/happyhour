package com.crycetruly.happyhour;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {
TextView name,email,loc,build;
LinearLayout linearLayout1,linearLayout;
    private static final String TAG = "ProfileActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        build=findViewById(R.id.buildversion);

        String version= BuildConfig.VERSION_NAME;
        build.setText("Build Version "+version);

         linearLayout1=findViewById(R.id.contact);

        linearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUsEmail();
            }
        });


        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        loc=findViewById(R.id.loc);

        name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        loc.setText("Location not ready");

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
           Location.locateUser(getBaseContext());
            }
        });

        thread.start();

try {
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                try {
                    loc.setText(documentSnapshot.get("address").toString());
                }catch (Exception e){

                }
            }

    });
}catch (NullPointerException e){

}

         linearLayout=findViewById(R.id.sub);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),MySubscriptionsActivity.class));
            }
        });

        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Account and Settings");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Snackbar.make(linearLayout, "Log me out", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signoutUser();
                }
            }).show();


        }
        return super.onOptionsItemSelected(item);
    }

    private void sendUsEmail() {

        Log.d(TAG, "sendUsEmail: ");

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "happyhourwinnergroup@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact");

        if (emailIntent.resolveActivity(getPackageManager())!=null){

            startActivity(Intent.createChooser(emailIntent, "Contact us via..."));
        }else {
            Toast.makeText(this, "No apps can send an email on your device,manually send us an email at crycetruly@gmail.com",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void signoutUser() {
        AuthUI.getInstance().signOut(this);
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
