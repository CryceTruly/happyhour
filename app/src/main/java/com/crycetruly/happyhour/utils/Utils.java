package com.crycetruly.happyhour.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Elia on 9/8/2017.
 */

public class Utils {
        public static String makeUsername(String name){
            String userName=name.trim().toLowerCase();
            userName=userName.replace(" ","");
            return userName;
        }


    //---reversed date sorts

    public static long fitnessNumber() {
        long x = System.currentTimeMillis();

        long y = -x;


        return y;
    }


    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
    public static int nowTime(){
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd");
       String today= simpleDateFormat.format(calendar.getTime());
       return Integer.parseInt(today);
    }

    public static String getReversedNow() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");

        String current = sdf.format(c.getTime());

        String input = current;

        // getBytes() method to convert string
        // into bytes[].
        byte[] strAsByteArray = input.getBytes();

        byte[] result
                = new byte[strAsByteArray.length];

        // Store result in reverse order into the
        // result byte[]
        for (int i = 0; i < strAsByteArray.length; i++) {
            result[i]
                    = strAsByteArray[strAsByteArray.length - i - 1];
        }

        return new String(result);
    }

    public String timestampToTime(long time){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
        String times=simpleDateFormat.format(time);

        return times;
    }

}
