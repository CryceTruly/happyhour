package com.crycetruly.happyhour.utils;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by Elia on 10/22/2017.
 */

public class FirebaseMethods {
    //--------------------------THE DEVICE TOKEN IS RETURNED
    public static String getToken() {
        return FirebaseInstanceId.getInstance().getToken();
    }

}
