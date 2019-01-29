package com.crycetruly.happyhour;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class MySubscriptionsActivity extends AppCompatActivity {

    ArrayList<String> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_subscriptions);
        RelativeLayout vieww = findViewById(R.id.view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ListView listView = findViewById(R.id.list);

        String[] subs = {};
        FirebaseDatabase.getInstance().getReference().child("subs").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            list.add(singleSnapshot.child("businessname").getValue(String.class));

                        }
                        if (list.size() == 0) {
                            Snackbar.make(vieww, "No business here yet", Snackbar.LENGTH_INDEFINITE).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        list.addAll(Arrays.asList(subs));


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> Snackbar.make(view, "Stop watching out", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(parent.getItemAtPosition(position));


                try {
                    Query query = FirebaseDatabase.getInstance().getReference().child("subs").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("businessname")
                            .equalTo(parent.getItemAtPosition(position)
                                    .toString());

                    query.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getBaseContext(), "Removed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IndexOutOfBoundsException ex) {
                    Toast.makeText(getBaseContext(), "oops see you later", Toast.LENGTH_SHORT).show();

                }


                adapter.notifyDataSetChanged();
            }
        }).show());


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