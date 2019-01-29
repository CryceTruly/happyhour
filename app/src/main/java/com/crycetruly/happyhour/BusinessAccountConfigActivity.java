package com.crycetruly.happyhour;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class BusinessAccountConfigActivity extends AppCompatActivity {
    TextView name, email1, email2, address, description, phone;
    TextView category, count;
    private static final String TAG = "BusinessAccountConfigAc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_account_config);
        init();

        initToolbar();
        setData();
    }

    private void setData() {
        FirebaseFirestore.getInstance().collection("businesses").document(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).addSnapshotListener((documentSnapshot, e) -> {
            name.setText(documentSnapshot.get("name").toString());
            email1.setText(documentSnapshot.get("email").toString());
            email2.setText(documentSnapshot.get("semail").toString());
            phone.setText(documentSnapshot.get("phone").toString());
            category.setText(documentSnapshot.get("category").toString());
            address.setText(documentSnapshot.get("address").toString());


        });
        FirebaseFirestore.getInstance().collection("businesses").document(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).collection("subscribers").get()
                .addOnCompleteListener(


                        task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "setData: ran settig");
                                count.setText(task.getResult().getDocuments().size());
                            }
                        });

    }


    private void init() {
        name = findViewById(R.id.name);
        email1 = findViewById(R.id.email);
        email2 = findViewById(R.id.email2);
        address = findViewById(R.id.address);
        description = findViewById(R.id.description);
        phone = findViewById(R.id.phone);
        count = findViewById(R.id.count);
        category = findViewById(R.id.catlisting);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile and Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.businessprofile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit) {
            startActivity(new Intent(this, EditBusinessProfileActivity.class));
        } else {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
