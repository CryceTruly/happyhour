package com.crycetruly.happyhour;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.crycetruly.happyhour.utils.Tools;
import com.crycetruly.happyhour.utils.ViewAnimation;
import com.github.florent37.viewtooltip.ViewTooltip;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HappyHourDetailActivity extends AppCompatActivity {
    private TextView title, description, starttime, endtime;
    private static final String TAG = "HappyHourDetailActivity";
    private Context context = this;
    RelativeLayout main;
    private String doc;
    ImageView imageView;
    ArrayList<String> currentList = new ArrayList<>();
    String owner_id, ownername, name, contactmail, owneraddress, ownernameemail;
    int has;
    private View parent_view;

    private ImageButton bt_toggle_reviews, bt_toggle_warranty, bt_toggle_description;
    private View lyt_expand_reviews, lyt_expand_warranty, lyt_expand_description;
    private NestedScrollView nested_scroll_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_happy_hour_detail);
        init();
        initComponent();
        doc = getIntent().getStringExtra("doc");

        main = findViewById(R.id.main);
        imageView = findViewById(R.id.noti);

        FirebaseDatabase.getInstance().getReference().child("posts").child(doc).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                description.setText(dataSnapshot.child("description").getValue().toString());
                starttime.setText(dataSnapshot.child("startTime").getValue().toString());
                endtime.setText(dataSnapshot.child("endTime").getValue().toString());
                contactmail = dataSnapshot.child("email").getValue().toString();
                title.setText(dataSnapshot.child("title").getValue().toString());
                owner_id = dataSnapshot.child("owneridd").getValue().toString();
                FirebaseFirestore.getInstance().collection("businesses").document(owner_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ownername = documentSnapshot.get("name").toString();
                        owneraddress = documentSnapshot.get("address").toString();
                        ownernameemail = documentSnapshot.get("email").toString();

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("subs")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(owner_id)) {
                            FirebaseMessaging.getInstance().subscribeToTopic(owner_id);
                            Log.d(TAG, "onDataChange: has");
                            imageView.setImageResource(R.drawable.ic_notificationsoff);
                            has=1;
                        } else {
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(owner_id);
                            Log.d(TAG, "onDataChange: dont hv");
                            imageView.setImageResource(R.drawable.ic_notifications);
                            has=0;
                            showsnack();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

if (has==1) {
    imageView.setOnClickListener(v -> FirebaseDatabase.getInstance().getReference().child("subs")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .child(owner_id).removeValue()

            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    imageView.setImageResource(R.drawable.ic_notifications);
              //todo
                    //
                    // clear num.            ;
                    showsnack();
                }
            }));

}else {
    imageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Map map = new HashMap();
            map.put("businessname", ownername);
            FirebaseDatabase.getInstance().getReference().child("subs")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(owner_id).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    FirebaseMessaging.getInstance().subscribeToTopic(owner_id);
                    Map map1=new HashMap();
                    map1.put("user",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    FirebaseFirestore.getInstance().collection("businesses").document(owner_id).collection("subscribers")
                           .add(map1);
                    imageView.setImageResource(R.drawable.ic_notificationsoff);
                    Toast.makeText(context, "Watching for alerts from this business", Toast.LENGTH_SHORT).show();
                }
            });
        }
    });
}

        FirebaseDatabase.getInstance().getReference().child("subs").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild(doc)) {
                                imageView.setImageResource(R.drawable.ic_notificationsoff);

                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        FirebaseDatabase.getInstance().getReference().child("subs").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(owner_id).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, "You are nolonger watching out for this business offers", Toast.LENGTH_SHORT).show();
                                                //usubscribe
                                                FirebaseMessaging.getInstance().unsubscribeFromTopic(owner_id);
                                                Log.d(TAG, "onSuccess: unsubscinbed from " + owner_id);
                                            }
                                        });
                                    }
                                });

                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }

    private void showsnack() {
        Snackbar.make(nested_scroll_view, "To keep notified about about new offers like this this,tap on the notifications icon up", Snackbar.LENGTH_INDEFINITE)
                .setAction("ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        return;
                    }
                }).show();
    }

    private void init() {
        title = findViewById(R.id.title);
        description = findViewById(R.id.more);
        starttime = findViewById(R.id.startsat);
        endtime = findViewById(R.id.endsat);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initComponent() {
        // nested scrollview
        nested_scroll_view = (NestedScrollView) findViewById(R.id.nested_scroll_view);

        // section reviews
        bt_toggle_reviews = (ImageButton) findViewById(R.id.bt_toggle_reviews);
        lyt_expand_reviews = (View) findViewById(R.id.lyt_expand_reviews);
        bt_toggle_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSection(view, lyt_expand_reviews);
            }
        });

        // section warranty
        bt_toggle_warranty = (ImageButton) findViewById(R.id.bt_toggle_warranty);
        lyt_expand_warranty = (View) findViewById(R.id.bt_toggle_warranty);
        bt_toggle_warranty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSection(view, lyt_expand_warranty);
            }
        });

        // section description
        bt_toggle_description = (ImageButton) findViewById(R.id.bt_toggle_description);
        lyt_expand_description = (View) findViewById(R.id.lyt_expand_description);
        bt_toggle_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSection(view, lyt_expand_description);
            }
        });

        // expand first description
        toggleArrow(bt_toggle_description);
        lyt_expand_description.setVisibility(View.VISIBLE);

        ((FloatingActionButton) findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUsMail();
            }
        });
    }

    private void sendUsMail() {
        Log.d(TAG, "sendUsMail: sending email to ");

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", contactmail, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "About Offer");

        if (emailIntent.resolveActivity(getPackageManager()) != null) {

            startActivity(Intent.createChooser(emailIntent, "Email this Business Using..."));
        } else {
            Toast.makeText(getBaseContext(), "You dont have clients that can send emails", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleSection(View bt, final View lyt) {
        boolean show = toggleArrow(bt);
        if (show) {
            ViewAnimation.expand(lyt, new ViewAnimation.AnimListener() {
                @Override
                public void onFinish() {
                    Tools.nestedScrollTo(nested_scroll_view, lyt);
                }
            });
        } else {
            ViewAnimation.collapse(lyt);
        }
    }

    public boolean toggleArrow(View view) {
        if (view.getRotation() == 0) {
            view.animate().setDuration(200).rotation(180);
            return true;
        } else {
            view.animate().setDuration(200).rotation(0);
            return false;
        }
    }

}


