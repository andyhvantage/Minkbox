package com.minkbox.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Function {

    public String getDateTime() {
        Calendar cal;
        int day, year, hour, minute, second, num;
        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        num = cal.get(Calendar.MONTH);
        String month = "";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11) {
            month = months[num];
        }

        year = cal.get(Calendar.YEAR);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
        second = cal.get(Calendar.SECOND);

        // "addedon": "27:Dec:2014:1:45:34" DD:MMM:YYYY:HH:MM:SS
        // String shour;
        String sday;
        String sminute;
        String ssecond;

        // if (hour < 10) {
        // shour = "0" + hour;
        // } else {
        // shour = "" + hour;
        // }

        if (day < 10) {
            sday = "0" + day;
        } else {
            sday = "" + day;
        }
        if (minute < 10) {
            sminute = "0" + minute;
        } else {
            sminute = "" + minute;
        }
        if (second < 10) {
            ssecond = "0" + second;
        } else {
            ssecond = "" + second;
        }

        return sday + ":" + month.charAt(0) + "" + month.charAt(1) + ""
                + month.charAt(2) + ":" + year + ":" + hour + ":" + sminute
                + ":" + ssecond;

    }

    public boolean CheckGpsEnableOrNot(Context context) {
        boolean gpsStatus = false;
        try {
            LocationManager locationManager = (LocationManager) context
                    .getSystemService(context.LOCATION_SERVICE);
            gpsStatus = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
            gpsStatus = false;
        }
        return gpsStatus;
    }

    public String getRadiusfromText(String text) {
        if (text.equalsIgnoreCase("Choose Radius")) {
            return "0";
        } else if (text.equalsIgnoreCase("3 Miles (~5 km)")) {
            return "5";
        } else if (text.equalsIgnoreCase("5 Miles (~8 km)")) {
            return "8";
        } else if (text.equalsIgnoreCase("10 Miles (~15 km)")) {
            return "15";
        } else if (text.equalsIgnoreCase("20 Miles (~30 km)")) {
            return "30";
        } else if (text.equalsIgnoreCase("30 Miles (~50 km)")) {
            return "50";
        } else if (text.equalsIgnoreCase("50 Miles (~80 km)")) {
            return "80";
        }
        return "0";
    }

    public static boolean isEmailValid(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public Address getLatLongFromGivenAddress(String addresses, Context context) {
        double lat = 0.0, lng = 0.0;

        Geocoder coder = new Geocoder(context);
        List<Address> address = new ArrayList<Address>();

        try {
            address = coder.getFromLocationName(addresses, 5);
            if (address == null) {
                return null;
            }
            System.out.println("adress----------------" + address);

            Address location = address.get(0);
            lat = location.getLatitude();
            lng = location.getLongitude();
            return location;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void showGpsSettingsAlert(String tittle, String message, final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle(tittle);

        // Setting Dialog Message
        alertDialog.setMessage(message);   // "GPS is not enabled. Do you want to go to settings menu ?"

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
//						MainActivity.mainactivityinstance.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void cancelNotification(Context ctx, int notifyId) {
        try
        {
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
            nMgr.cancel(notifyId);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String generateUniqueId()
    {

        String uniqueid = "";
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZ";
        int string_length = 4;

        for (int i=0; i<string_length; i++) {
            int rnum = (int) Math.floor(Math.random() * chars.length());
            uniqueid += chars.substring(rnum,rnum+1);
        }
        return uniqueid;
    }

    public void sendSMSMessage(Context context, String mobileno) {
        Log.i("Send SMS", "");

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(mobileno, null, "Thank you for Ordering Food, you are one step away. We require this verification code for security purpose. Please enter this code "+AppPreferences.getRandomNo(context), null, null);
            Toast.makeText(context, "SMS sent.",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context,
                    "SMS faild, please try again.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

}