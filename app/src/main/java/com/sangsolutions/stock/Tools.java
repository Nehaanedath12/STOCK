package com.sangsolutions.stock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


public class Tools {
SharedPreferences preferences;
SharedPreferences.Editor editor;
    public static String ConvertDate(String date){
        String year =  date.substring(0,4);
        String month =  date.substring(4,6);
        String day =  date.substring(6,8);
        return year + "-"+ month + "-" + day;

    }

    public static String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    public static String dateFormat(String dateToFormat){
        DateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = originalFormat.parse(dateToFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return targetFormat.format(Objects.requireNonNull(date));
    }

    public static String dateFormat2(String dateToFormat){
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = originalFormat.parse(dateToFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return targetFormat.format(Objects.requireNonNull(date));
    }

    public static void logWrite(String fnName, Exception exception, Context context) {

        Log.d("logWriteException",exception.toString());
        if(Tools.isConnected(context)) {
//            try {
//                FirebaseCrashlytics.getInstance().recordException(exception);
//                AndroidNetworking.get("http://" + new Tools().getIP(context) + URLs.LogCreate)
//                        .addQueryParameter("logmessage", fnName)
//                        .addQueryParameter("filename", exception.toString())
//                        .setPriority(Priority.MEDIUM)
//                        .build().
//                        getAsString(new StringRequestListener() {
//                            @Override
//                            public void onResponse(String response) {
//                                Log.d("logWrite", response);
//                            }
//
//                            @Override
//                            public void onError(ANError anError) {
//                                Log.d("logWrite", anError.toString());
//
//                            }
//                        });
//            }catch (Exception e){
//                e.printStackTrace();
//            }
        }
    }


    public String getIP(Context context){
    preferences = context.getSharedPreferences("Settings",Context.MODE_PRIVATE);
    if(preferences!=null){
        return preferences.getString("IP","");
    }
    return "";
    }

    @SuppressWarnings("SameReturnValue")
    public boolean setIP(Context context, String IP){
        preferences = context.getSharedPreferences("Settings",Context.MODE_PRIVATE);
        editor = preferences.edit();
        if(editor!=null){
        editor.putString("IP",IP).apply();
        }
        return true;
    }

    public static boolean isValidIP(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.IP_ADDRESS.matcher(target).matches());
    }





    public static boolean isConnected(Context context){
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }




}
