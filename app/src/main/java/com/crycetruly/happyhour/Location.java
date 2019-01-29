package com.crycetruly.happyhour;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Elia on 17/06/2018.
 */

public class Location {
    private FusedLocationProviderClient mClient;
    private Context context;
    private static String nameret;
    private static final String TAG = "Location";
    public Location(Context context) {
        this.context = context;
    }

    public static String locateUser(Context context) {
        FusedLocationProviderClient mClient = new FusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }

        mClient= LocationServices.getFusedLocationProviderClient(context);
        mClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
            @Override
            public void onSuccess(android.location.Location location) {
                try {


                    if (!String.valueOf(location.getLatitude()).equals("")) {
                        try {

                            Log.d(TAG, "onSuccess: fond location ");
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("lat", location.getLatitude());
                            map.put("lng", location.getLongitude());
                            ;
                            Geocoder geocoder = new Geocoder(context);
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            Address address = addresses.get(0);

                            nameret = address.getCountryName();
                            Log.d(TAG, "onSuccess: adress" + address);

                            map.put("adminarea", address.getAdminArea());
                            map.put("address", address.getLocality() + "," + address.getAdminArea());

                            FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .update(map).addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    Log.d(TAG, "onSuccess: location updated");
                                }
                            });


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                    }
                }catch (NullPointerException e) {
                    Log.d(TAG, "onSuccess: ");


                }
                }
        });
        Log.d(TAG, "locateUser: returning "+nameret);
        return "Location";
    }
}
