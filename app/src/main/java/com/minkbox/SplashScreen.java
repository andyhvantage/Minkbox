package com.minkbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.Toast;
import com.minkbox.service.ProductNameList;
import com.minkbox.utils.AppConstants;
import com.minkbox.utils.AppPreferences;
import com.minkbox.utils.BaseActivity;
import com.minkbox.utils.ConnectionDetector;
import com.minkbox.utils.Function;

public class SplashScreen extends BaseActivity {
    Button ok;
    Button cancle;
    PopupWindow pwindo;
    private static int SPLASH_TIME_OUT = 3000;
    public static SplashScreen instance = null;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        instance = this;

        System.out.println(" application app url is : "+ AppConstants.APPURL);

        if(ConnectionDetector.isConnectingToInternet(this))
        {
            FilterActivity.searchTextOfFilterProduct.clear();
            startService(new Intent(this, ProductNameList.class));
        }

//		Toast.makeText(getApplicationContext(),"lat :--- "+AppPreferences.getLatutude(getApplicationContext())+"long :--- "+AppPreferences.getLongitude(getApplicationContext()) , Toast.LENGTH_LONG).show();

        if (new Function().CheckGpsEnableOrNot(getApplicationContext())) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

//                    FusedLocationService fusedLocationService = new FusedLocationService(instance);
                    System.out.println("lat :--- "+ AppPreferences.getLatutude(getApplicationContext())+"long :--- "+AppPreferences.getLongitude(getApplicationContext()));
                    if(AppPreferences.getLatutude(getApplicationContext()).equalsIgnoreCase("0.0") || AppPreferences.getLongitude(getApplicationContext()).equalsIgnoreCase("0.0"))
                    {
                        enableMapLocation();
                    }else
                    {
                        Intent i = new Intent(SplashScreen.this, HomeActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }, SPLASH_TIME_OUT);
        } else {
            showGpsSettingsAlert("GPS Settings...",
                    "GPS is not enabled. Do you want to go to settings menu?",
                    this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(count == 1)
        {
            if(new Function().CheckGpsEnableOrNot(getApplicationContext()))
            {
                if(AppPreferences.getLatutude(getApplicationContext()).equalsIgnoreCase("0.0") || AppPreferences.getLongitude(getApplicationContext()).equalsIgnoreCase("0.0"))
                {
                    enableMapLocation();
                }else
                {
                    Intent i = new Intent(SplashScreen.this,
                            HomeActivity.class);
                    startActivity(i);
                    finish();
                }
            }else
            {
                Toast.makeText(getApplicationContext(), "In order to enter this application you must enable gps", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    @Override
    public void taskCompleted(byte taskType, int taskID, Object data) {
    }

    @Override
    protected void addListeners() {
    }

    @Override
    protected void bindComponents() {
    }

    @Override
    protected Activity getActivity() {
        return null;
    }

//	public static void showHashKey(Context context) {
//		try {
//			PackageInfo info = context.getPackageManager().getPackageInfo(
//					"com.mmfinfotech.craigslist", PackageManager.GET_SIGNATURES);
//			for (Signature signature : info.signatures) {
//				MessageDigest md = MessageDigest.getInstance("SHA");
//				md.update(signature.toByteArray());
//				Log.i("KeyHash:",
//						Base64.encodeToString(md.digest(), Base64.DEFAULT));
//			}
//		} catch (NameNotFoundException e) {
//		} catch (NoSuchAlgorithmException e) {
//		}
//	}

    public void showGpsSettingsAlert(String tittle, String message,
                                     final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(tittle);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                        count = 1;
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(), "In order to enter this application you must enable gps", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
        alertDialog.show();
    }

    private void enableMapLocation() {
        try {
            LayoutInflater inflater = (LayoutInflater) SplashScreen.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup_enable_map_location,
                    (ViewGroup) findViewById(R.id.pop_up_exit_element));
            pwindo = new PopupWindow(layout, LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

            ok = (Button) layout.findViewById(R.id.ok);
            ok.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
//                        Intent i = new Intent(SplashScreen.this, HomeActivity.class);
//                        startActivity(i);
                        finish();
                    pwindo.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
