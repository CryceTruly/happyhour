package com.crycetruly.happyhour;

import android.Manifest;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crycetruly.happyhour.adapters.AdapterSuggestionSearch;
import com.crycetruly.happyhour.model.HappyHour;
import com.crycetruly.happyhour.utils.ViewAnimation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "HomeActivity";
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    Marker mMarker;

    //variables
    private boolean mlocationpermissionsgranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private ArrayList<HappyHour> machines = new ArrayList<>();
    private RelativeLayout test;
    BottomNavigationView bottomNavigationItemView;
    FloatingActionButton recommend;
    DatabaseReference mDatabase;
    Query query;
    Double lat,lng;

    Location loc2 = new Location("");

    //vars that are sarch specific

    private EditText et_search;
    private ImageButton bt_clear, bt_back;

    private ProgressBar progress_bar;
    private LinearLayout lyt_no_result;

    private RecyclerView recyclerSuggestion;
    private AdapterSuggestionSearch mAdapterSuggestion;
    private LinearLayout lyt_suggestion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("posts");
        mDatabase.keepSynced(true);
        setContentView(R.layout.activity_home);

        hideKeyboard();
        recommend = findViewById(R.id.recommend);
        getLocationPermission();
        checkAuth();

        initToolbar();
        initComponent();
        recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();

                query = mDatabase;
                initMap();
            }
        });


        if (mlocationpermissionsgranted) {
            initMap();
            getDeviceLocation();
        }
        LinearLayout linearLayout=findViewById(R.id.lin1);
        LinearLayout linearLayout2=findViewById(R.id.linlist);
        linearLayout.setOnClickListener(view->{
            startActivity(new Intent(getBaseContext(),ProfileActivity.class));
        });
        linearLayout2.setOnClickListener(view->{
            Intent intent=new Intent(getBaseContext(),HappyHourListActivity.class);
            this.overridePendingTransition(R.anim.left_right,
                    R.anim.right_left);
            startActivity(intent);

            });




        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    Snackbar.make(progress_bar,"No Happy hours around",Snackbar.LENGTH_LONG).show();
                }else {
                    Log.d(TAG, "onDataChange:"+ String.valueOf(dataSnapshot.getChildrenCount())
                            + " Happy hours around" );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initToolbar() {
    }

    private void initComponent() {
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        lyt_no_result = (LinearLayout) findViewById(R.id.lyt_no_result);

        lyt_suggestion = (LinearLayout) findViewById(R.id.lyt_suggestion);
        et_search = (EditText) findViewById(R.id.et_search);
        et_search.addTextChangedListener(textWatcher);

        bt_clear = (ImageButton) findViewById(R.id.bt_clear);
        bt_back = (ImageButton) findViewById(R.id.bt_back);
        bt_clear.setVisibility(View.GONE);
        recyclerSuggestion = (RecyclerView) findViewById(R.id.recyclerSuggestion);

        recyclerSuggestion.setLayoutManager(new LinearLayoutManager(this));
        recyclerSuggestion.setHasFixedSize(true);

        //set data and list adapter suggestion
        mAdapterSuggestion = new AdapterSuggestionSearch(this);
        recyclerSuggestion.setAdapter(mAdapterSuggestion);
        showSuggestionSearch();
        mAdapterSuggestion.setOnItemClickListener(new AdapterSuggestionSearch.OnItemClickListener() {
            @Override
            public void onItemClick(View view, String viewModel, int pos) {
                et_search.setText(viewModel);
                ViewAnimation.collapse(lyt_suggestion);
                hideKeyboard();
                searchAction();
            }
        });

        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search.setText("");
            }
        });

        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard();
                    searchAction();
                    return true;
                }
                return false;
            }
        });

        et_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showSuggestionSearch();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                return false;
            }
        });
    }

    private void showSuggestionSearch() {
        mAdapterSuggestion.refreshItems();
        ViewAnimation.expand(lyt_suggestion);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence c, int i, int i1, int i2) {
            if (c.toString().trim().length() == 0) {
                bt_clear.setVisibility(View.GONE);
            } else {
                bt_clear.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence c, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private void hideKeyboard() {
        Log.d(TAG, "hideKeyboard: Hidden Softinput keyboard");
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void searchAction() {
        progress_bar.setVisibility(View.VISIBLE);
        ViewAnimation.collapse(lyt_suggestion);
        lyt_no_result.setVisibility(View.GONE);

        final String query = et_search.getText().toString().trim();
        if (!query.equals("")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progress_bar.setVisibility(View.GONE);
                    lyt_no_result.setVisibility(View.VISIBLE);
                }
            }, 2000);
            mAdapterSuggestion.addSearchHistory(query);
        } else {
            Toast.makeText(this, "Please fill search input", Toast.LENGTH_SHORT).show();
        }
    }



    private void checkAuth() {
        Log.d(TAG, "checkAuth: ");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d(TAG, "checkAuth: user is not available");
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } else {
            Log.d(TAG, "checkAuth:checkAuth: user is  available");
            FirebaseFirestore.getInstance().collection("businesses").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addSnapshotListener(this,(snap, options) -> {
                        Log.d(TAG, "onEvent: " + snap.getId());
                        if (snap.exists()) {
                            Log.d(TAG, "onEvent: business user exists");
                            startActivity(new Intent(getBaseContext(), BusinessDashboardActivity.class));
                            finish();
                        } else {
                            Log.d(TAG, "checkAuth: all cool");
                        }
                    });
        }
    }

    private void sendToBusiness() {
        Log.d(TAG, "sendToBusiness: ");
        Intent i = new Intent(getBaseContext(), BusinessAuthActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: Map is ready");

        mMap = googleMap;

        Toast.makeText(this, "map is ready", Toast.LENGTH_SHORT).show();
        if (mlocationpermissionsgranted) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                getDeviceLocation();
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);

            //mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: Getting device's current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mlocationpermissionsgranted) {
                Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = task.getResult();
                            if (currentLocation != null)
                               try {
//                                   Log.d(TAG, "onComplete: curremt location " + currentLocation.toString());

                                   lat = currentLocation.getLatitude();
                                   lng = currentLocation.getLongitude();

                                   loc2.setLatitude(lat);
                                   loc2.setLongitude(lng);

                                   movecamera(new LatLng(lat, lng), DEFAULT_ZOOM);
                                   mMap.setInfoWindowAdapter(new CustomInfoWindow(getBaseContext()));
                               }catch (NullPointerException ee) {
                                   lat = -16.0;
                                   lng = 30.1234;
                                   Toast.makeText(getBaseContext(), "unable to get current location", Toast.LENGTH_SHORT).show();


                                   movecamera(new LatLng(lat, lng), DEFAULT_ZOOM);
                                   mMap.setInfoWindowAdapter(new CustomInfoWindow(getBaseContext()));

                               }
                            // mMap.clear();
                        } else {
                            Log.d(TAG, "onComplete: location is null");
                            Toast.makeText(getBaseContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }

        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException:" + e.getMessage());

        }
    }

    private void movecamera(LatLng latLng, float zoom) {
        Log.d(TAG, "Movecamera: moving the camera to: lat:" + latLng.latitude + ",lng:" + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        MarkerOptions markwrOptions = new MarkerOptions();
        markwrOptions.snippet("Am here");
        markwrOptions.position(new LatLng(latLng.latitude, latLng.longitude));
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.add);
        markwrOptions.icon(icon);
        mMap.addMarker(markwrOptions);


    }

    private void initMap() {

        Query query = mDatabase;
        Log.d(TAG, "initMap: initialising map");
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                if (mlocationpermissionsgranted) {
                    if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mMap.setMyLocationEnabled(true);


                    FirebaseDatabase.getInstance().getReference().child("posts").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(final DataSnapshot singlesnap, String s) {
                            Log.d(TAG, "onChildAdded: got some hhappys " + singlesnap);
                            machines.add(singlesnap.getValue(HappyHour.class));

                            Double lat2 = singlesnap.child("lat").getValue(Double.class);
                            Double lng2 = singlesnap.child("lng").getValue(Double.class);

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                                            lat2, lng2)


                                    , 15));
                            ViewAnimation.collapse(lyt_suggestion);
                            hideKeyboard();

                            Location loc1 = new Location("");
                            loc1.setLatitude(singlesnap.child("lat").getValue(Double.class));
                            loc1.setLongitude(singlesnap.child("lng").getValue(Double.class));


                            float distanceInMeters = loc1.distanceTo(loc2);

             float dms = 1000 / distanceInMeters;
             float distance = (distanceInMeters < 1000) ? distanceInMeters : distanceInMeters / 1000;
             String prefix = (distanceInMeters < 1000) ? "Distance fr0m your location(M)" : "Distance from your location(KM)";

                            Log.d(TAG, "onChildAdded: Distance to all " + distanceInMeters);
                            final MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(new LatLng(singlesnap.child("lat").getValue(Double.class)
                                    , singlesnap.child("lng").getValue(Double.class)));
                            markerOptions.title(singlesnap.child("idd").getValue(String.class));
                            String snipet = "decs: " + singlesnap.child("description").getValue(String.class) + "\n" +
                                    prefix + " " + String.format("%.2f", distance) + "\n" +
                                    "Where: " + singlesnap.child("ownername").getValue(String.class) + "\n" +
                                    "Starttime: " + singlesnap.child("startTime").getValue(String.class) + "\n";
                            markerOptions.snippet(snipet);
                            mMap.setInfoWindowAdapter(new CustomInfoWindow(getBaseContext()));
                            mMap.setOnInfoWindowClickListener(marker -> {
                                Intent i = new Intent(getBaseContext(), HappyHourDetailActivity.class);
                                i.putExtra("doc", marker.getTitle());
                                startActivity(i);
                            });


                            //markerOptions.icon(BitmapDescriptorFactory.fromPath(singlesnap.child("image").getValue(String.class)));
                            mMap.addMarker(markerOptions);

                            if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                    HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            } else {
                                Log.d(TAG, "onChildAdded: permissions ok");
                                mMap.setMyLocationEnabled(true);
                            }

                        }


                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

    }
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: Getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.SEND_SMS,Manifest.permission.CALL_PHONE};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mlocationpermissionsgranted = true;
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: Called");
        mlocationpermissionsgranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 1; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mlocationpermissionsgranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: Permission failed");
                            return;
                        } else {
                            mlocationpermissionsgranted = true;
                            //initialise out map
                            initMap();
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                }
            }
        }
    }

    @Override
    protected void onStart() {
        checkAuth();
        initMap();
        super.onStart();
    }
    private double meterDistanceBetweenPoints(Double lat_a, Double lng_a, Double lat_b, Double lng_b) {
        float pk = (float) (180.f/Math.PI);

        float a1 = (float) (lat_a / pk);
        float a2 = (float) (lng_a / pk);
        float b1 = (float) (lat_b / pk);
        float b2 = (float) (lng_b / pk);

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }

    //--
//
//    Location locationB = new Location("point B");
//
//locationB.setLatitude(latB);
//locationB.setLongitude(lngB);
//
//    float distance = locationA.distanceTo(locationB);
//
//





}





