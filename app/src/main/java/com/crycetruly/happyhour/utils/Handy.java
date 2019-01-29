package com.crycetruly.happyhour.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

public class Handy {
    //--------------------REFRESHES THE GALLERY TO SHOW THE SAVED PCTURES--------------------//
    public static void scanFile(Context context, Uri imageUri) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(imageUri);
        context.sendBroadcast(scanIntent);

    }

    public static boolean doPasswordsMatch(String pass1,String pass2){

        if (pass1.equals(pass2)){
            return true;
        }else
            return false;
    }
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static String getTrimmedName(String userDisplayName) {


        String name = userDisplayName.toLowerCase();
        String fullName = "";

        if (name.contains(" ")) {
            int total = name.indexOf(" ");
            String fname = name.substring(0, total);
            char fl = name.charAt(0);
            String fletter = String.valueOf(fl).toUpperCase();
            String lastName = name.substring(total);
            char lll = name.charAt(total + 1);
            String lletter = String.valueOf(lll).toUpperCase();
            String firstName = fletter.concat(fname.substring(1));
            String lName = lletter.concat(lastName.substring(2));

            fullName = firstName.concat(" ").concat(lName);

            return fullName;

        } else if (!name.contains(" ")) {
            String fname = name.substring(0);
            char fl = name.charAt(0);
            String fletter = String.valueOf(fl).toUpperCase();
            String firstName = fletter.concat(fname.substring(1));
            return firstName;
        } else {
            int total = name.indexOf(" ");
            String fname = name.substring(0, total);
            char fl = name.charAt(0);
            String fletter = String.valueOf(fl).toUpperCase();
            String lastName = name.substring(total);
            char lll = name.charAt(total + 1);
            String lletter = String.valueOf(lll).toUpperCase();
            String firstName = fletter.concat(fname.substring(1));
            String lName = lletter.concat(lastName.substring(2));

            fullName = firstName.concat(" ").concat(lName);
            return fullName;

        }
    }
}
