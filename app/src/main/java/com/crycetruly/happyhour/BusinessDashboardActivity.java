package com.crycetruly.happyhour;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crycetruly.happyhour.dialog.DialogFullscreenFragment;
import com.crycetruly.happyhour.model.HappyHour;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BusinessDashboardActivity extends AppCompatActivity {
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;

    private RecyclerView mList;
    FirebaseRecyclerAdapter adapter;
    private ProgressBar mProgress;
    private FirebaseFirestore firebaseFirestore;
    private static final String TAG = "BusinessDashboardActivi";

    public static final int DIALOG_QUEST_CODE = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_dashboard);
        toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Business Account");
        getSupportActionBar().setSubtitle(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        mProgress = findViewById(R.id.progress_bar);

        mList = findViewById(R.id.list);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        RelativeLayout relativeLayout = findViewById(R.id.main);
        Query query = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("owneridd")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {

                    mProgress.setVisibility(View.INVISIBLE);


                    try {
                        Snackbar.make(relativeLayout, "You have no happy hour history", Snackbar.LENGTH_LONG)

                                .show();
                    } catch (IllegalArgumentException ee) {

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseRecyclerOptions<HappyHour> options =
                new FirebaseRecyclerOptions.Builder<HappyHour>()
                        .setQuery(query, HappyHour.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<HappyHour, HappyHourHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HappyHourHolder holder, int position, @NonNull HappyHour model) {
                holder.setDetail(model.getDescription());
                holder.setName(model.getStartTime() + " " + model.getEndTime());

                mProgress.setVisibility(View.INVISIBLE);

                holder.view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Snackbar.make(relativeLayout, "Invalidate Offer", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    adapter.getRef(position).removeValue();
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(getBaseContext(), "Happy Hour Invalidated", Toast.LENGTH_SHORT).show();

                                } catch (Exception ee) {
                                    Toast.makeText(getBaseContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }).show();
                        return true;
                    }
                });

            }

            @Override
            public HappyHourHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_happyhour_layout, parent, false);

                return new HappyHourHolder(view);
            }

        };

        mList.setAdapter(adapter);

    }

    public static class HappyHourHolder extends RecyclerView.ViewHolder {
        private View view;

        public HappyHourHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setName(String name) {
            TextView textView = view.findViewById(R.id.name);
            textView.setText(name);
        }

        public void setDetail(String desc) {
            TextView textView = view.findViewById(R.id.desc);
            textView.setText(desc);
        }
    }

    @Override
    public void onStart() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseFirestore.getInstance().collection("businesses").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addSnapshotListener((documentSnapshot, e) -> {
                            Double lat = Double.valueOf(documentSnapshot.get("lat").toString());
                            Double lng = Double.valueOf(documentSnapshot.get("lng").toString());

                            if (!documentSnapshot.contains("showsIn")) {
                                Geocoder geocoder = new Geocoder(getBaseContext());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(lat, lng, 1);

                                    Address address = addresses.get(0);

                                    String showsIn = address.getAdminArea();
                                    Map<String, Object> map = new HashMap();
                                    map.put("showsIn", showsIn);
                                    FirebaseFirestore.getInstance().collection("businesses").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess: location set");
                                        }
                                    });


                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }

                        });
            }
        });

        thread.start();
        adapter.startListening();
        super.onStart();
    }

    @Override
    public void onStop() {
        adapter.stopListening();
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bssmain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.add) {
            showDialogFullscreen();
        } else if (item.getItemId() == R.id.account) {

            AuthUI.getInstance().signOut(getBaseContext()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    Toast.makeText(getBaseContext(), "signed out", Toast.LENGTH_SHORT).show();
                }
            });
        } else {

            startActivity(new Intent(getBaseContext(), BusinessAccountConfigActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    private void showDialogFullscreen() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogFullscreenFragment newFragment = new DialogFullscreenFragment();
        newFragment.setRequestCode(DIALOG_QUEST_CODE);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
        newFragment.setOnCallbackResult(new DialogFullscreenFragment.CallbackResult() {
            @Override
            public void sendResult(int requestCode, Object obj) {
                if (requestCode == DIALOG_QUEST_CODE) {
                    displayDataResult((HappyHour) obj);
                }
            }
        });
    }

    private void displayDataResult(HappyHour event) {
        ((TextView) findViewById(R.id.tv_email)).setText(event.email);
        ((TextView) findViewById(R.id.tv_name)).setText(event.title);
        ((TextView) findViewById(R.id.tv_location)).setText(event.description);
        ((TextView) findViewById(R.id.tv_from)).setText(event.startTime);
        ((TextView) findViewById(R.id.tv_to)).setText(event.endTime);
        ((TextView) findViewById(R.id.tv_allday)).setText(event.allowcomments.toString());
        ((TextView) findViewById(R.id.tv_timezone)).setText(event.timezone);
    }
}
