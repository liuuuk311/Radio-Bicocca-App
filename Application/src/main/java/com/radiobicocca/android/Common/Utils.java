package com.radiobicocca.android.Common;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by lucap on 2/24/2018.
 */

public class Utils {

    public static String formatDate(String str){
        String splitted[] = str.split("T");
        str = splitted[0] + " " + splitted[1];
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date d = df.parse(str);
            str = df2.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String formateTime(String str){
        SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
        SimpleDateFormat df2 = new SimpleDateFormat("EEE, HH:mm", Locale.getDefault());
        try {
            Date d = df.parse(str);
            str = df2.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    public static String cleanStringDate(String str){
        String splitted[] = str.split("T");
        str = splitted[0] + " " + splitted[1];
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d = df.parse(str);
            str = df.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static Date getDateFromString(String str){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = null;
        try {
            d = df.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }
}
