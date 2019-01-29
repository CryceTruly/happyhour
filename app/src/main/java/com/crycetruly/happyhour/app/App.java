package com.crycetruly.happyhour.app;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Elia on 3/8/2018.
 */

public class App extends MultiDexApplication{

    @Override
    public void onCreate() {
        super.onCreate();

        if (FirebaseApp.getApps(this)!=null){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
    }
}
