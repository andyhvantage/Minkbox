package com.minkbox;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minkbox.databace.ChatDataBaseHelper;
import com.minkbox.lazyload.ImageLoader;
import com.minkbox.adapter.ViewOtherUserProfileAdapter;
import com.minkbox.adapter.ViewPagerUserProfileAdapter;
import com.minkbox.classes.SlidingTabLayout;
import com.minkbox.service.ChatDataBaseService;
import com.minkbox.utils.AppConstants;
import com.minkbox.utils.AppPreferences;
import com.minkbox.utils.ConnectionDetector;
import com.minkbox.utils.DialogManager;
import com.minkbox.utils.TextDrawable;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;


public class UserProfile extends ActionBarActivity {
    Toolbar toolbar;
    public UserProfile userProfileInstance = null;
    public static UserProfile activityinstanceuserprofile;
    ViewPager pager;
    ViewPagerUserProfileAdapter adapter;
    ViewOtherUserProfileAdapter other_user_adapter;
    SlidingTabLayout tabs;
    TextView userName;
    TextView userCity;
    TextView userProfileReviews;
    TextView userSolvedItems;
    TextView userSellItems;
    ImageView userImage;
    public int customerid;
    String resultnull;
    Bitmap profileicon;
    DialogManager alert = new DialogManager();
    public ImageLoader imageLoader;

    CharSequence Titles[] = {"Sell", "Sold", "Like"};
    CharSequence Titles_other_user[] = {"Sell", "Sold"};
    int[] mResources = {
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
    };
    int Numboftabs = 3;
    int Numboftabs_other_user = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        Bundle extras = getIntent().getExtras();
        customerid = extras.getInt("customerid");
        userProfileInstance = this;


        imageLoader= new ImageLoader(getApplicationContext());

        ChatDataBaseHelper.init(UserProfile.this);

        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title =getString(R.string.app_name);
        title = "User Profile";
        getSupportActionBar().setTitle(title);
        userName = (TextView) findViewById(R.id.user_name);
        userCity = (TextView) findViewById(R.id.user_city);
        userProfileReviews = (TextView) findViewById(R.id.profile_reviews);
        userSolvedItems = (TextView) findViewById(R.id.sold_items);
        userSellItems = (TextView) findViewById(R.id.sell_product);
        ImageView userImage = (ImageView) findViewById(R.id.user_profile_image);
        ImageView userImage1 = (ImageView) findViewById(R.id.user_profile_image1);
        LinearLayout ll = (LinearLayout)findViewById(R.id.ll);

        if(customerid==AppPreferences.getCustomerId(UserProfile.this)) {
            System.out.print("my customer_id--------------------------------"+AppPreferences.getCustomerId(UserProfile.this));
            adapter = new ViewPagerUserProfileAdapter(UserProfile.this, getSupportFragmentManager(), Titles,
                    Numboftabs);
            pager = (ViewPager) findViewById(R.id.pager);
            pager.setAdapter(adapter);

            tabs = (SlidingTabLayout) findViewById(R.id.tabs);
            tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true,
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.tabsScrollColor);
                }
            });
            tabs.setCustomTabView(R.layout.custom_tabview, 0);
            tabs.setViewPager(pager);


            userName.setText(AppPreferences.getUsername(getApplicationContext()));
            userCity.setText(AppPreferences.getUsercity(getApplicationContext()) + " " + AppPreferences.getUserpincode(getApplicationContext()));
            userProfileReviews.setText(AppPreferences.getUserprofilereview(getApplicationContext()));
            userSolvedItems.setText(AppPreferences.getUserproductsold(getApplicationContext()));
            userSellItems.setText(AppPreferences.getUserproductsell(getApplicationContext()));


            if (AppPreferences.getUserprofilepic(getApplicationContext()).equalsIgnoreCase("")) {
                if(AppPreferences.getUsername(getApplicationContext()).equalsIgnoreCase("")){
                    profileicon = BitmapFactory.decodeResource(getResources(), R.drawable.def_user_img);
                    userImage.setImageBitmap(profileicon);
                }else {
                    userImage.setVisibility(View.GONE);
                    userImage1.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.RIGHT_OF, R.id.user_profile_image1);
                    params.setMarginStart(5);
                    ll.setLayoutParams(params);

                    String name = AppPreferences.getUsername(getApplicationContext());
                    char firstLetter = name.charAt(0);
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRound(String.valueOf(firstLetter), Color.parseColor("#6faf20"));

                    userImage1.setImageDrawable(drawable);
                }
            } else {
                userImage1.setVisibility(View.GONE);
                userImage.setVisibility(View.VISIBLE);
                imageLoader.DisplayImage(AppPreferences.getUserprofilepic(getApplicationContext()), userImage);
            }

            userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(AppPreferences.getUserprofilepic(getApplicationContext()).equalsIgnoreCase("")){
                        Toast.makeText(UserProfile.this,"No Image Available",Toast.LENGTH_LONG);
                    }else {
                        Intent i = new Intent(UserProfile.this, FullUserImageActivity.class);
                        i.putExtra("profile_pic", AppPreferences.getUserprofilepic(getApplicationContext()));
                        startActivity(i);
                    }
                }
            });


        }else{

            System.out.print("owner id--------------------------------" + customerid);
            other_user_adapter = new ViewOtherUserProfileAdapter(UserProfile.this, getSupportFragmentManager(), Titles_other_user,
                    Numboftabs_other_user, customerid);
            pager = (ViewPager) findViewById(R.id.pager);
            pager.setAdapter(other_user_adapter);

            tabs = (SlidingTabLayout) findViewById(R.id.tabs);
            tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true,
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.tabsScrollColor);
                }
            });

            tabs.setCustomTabView(R.layout.custom_tabview, 0);
            tabs.setViewPager(pager);
            if ((ConnectionDetector.isConnectingToInternet(UserProfile.this))) {
                new OtherUserProfileTask(UserProfile.this, customerid).execute();
            } else {
                alert.showAlertDialog(UserProfile.this, "Alert!", "Please, check your internet connection.", false);
            }

        }

    }

    public void showAppQuitAlertDialog(String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alert!");
        builder.setIcon(R.drawable.ic_launcher);
        builder.setMessage(message);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("home activity instance is ------------- " + UserProfile.this);
                if (UserProfile.this != null) {
                    if ((ConnectionDetector
                            .isConnectingToInternet(UserProfile.this))) {
                        new LogoutTask(UserProfile.this).execute();
                    } else {
                        Toast.makeText(UserProfile.this, "Please, check your internet connection.", Toast.LENGTH_LONG).show();
                    }
                }
                dialog.dismiss();
                Runtime.getRuntime().gc();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Runtime.getRuntime().gc();
            }
        });
        AlertDialog msg = builder.create();
        msg.show();
    }

    ///////////////logoutasync////////////////////////////
    public class LogoutTask extends AsyncTask<String, Void, String> {

        Context context;
        String networkFlag = "false";
        private JSONObject jsonObj;
        private String TAG = LogoutTask.class.getSimpleName();
        private int status = 0;
        private ProgressDialog mProgressDialog;
        public ArrayList<HashMap<String, String>> data;

        public LogoutTask(Context context) {
            this.context = context;

            mProgressDialog = new ProgressDialog(UserProfile.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("nik", "registration pre method");
            mProgressDialog.show();
        }

        protected String doInBackground(String... params) {
            try {
                HttpParams httpParameters = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParameters, AppConstants.NETWORK_TIMEOUT_CONSTANT);
                HttpConnectionParams.setConnectionTimeout(httpParameters, AppConstants.NETWORK_CONNECTION_TIMEOUT_CONSTANT);
                HttpConnectionParams.setSoTimeout(httpParameters, AppConstants.NETWORK_SOCKET_TIMEOUT_CONSTANT);

                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(AppConstants.LOGOUTAPPURL);
                JSONArray jsonArray = new JSONArray();
                StringEntity se = null;
                jsonObj = new JSONObject();
                jsonObj.put("email", AppPreferences.getId(userProfileInstance.getApplicationContext()));
                jsonArray.put(jsonObj);

                try {
                    se = new StringEntity(jsonArray.toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                System.out.println("json : " + jsonArray.toString());
                Log.v("json sent : ", jsonArray.toString());
                httppost.setEntity(se);

                try {
                    se = new StringEntity(jsonArray.toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                System.out.println("JSON sent is : " + jsonArray.toString());
                httppost.setEntity(se);

                HttpResponse response = null;
                response = httpclient.execute(httppost);
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String jsonString = "";
                try {
                    jsonString = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("jsonString response is : " + jsonString);
                if (jsonString != null && jsonString.length() > 0) {
                    if (jsonString.contains("status")) {
                        JSONTokener tokener = new JSONTokener(jsonString);

                        JSONObject finalResult = new JSONObject(tokener);//jsonString
                        System.out.println("JSON response is : " + finalResult);

                        if (finalResult.getString("status").equalsIgnoreCase("200")) {
                            System.out.println("--------- message 200 got ----------");
                            status = 200;
                            return jsonString;
                        } else if (finalResult.getString("status").equalsIgnoreCase("400")) {
                            Log.v(TAG, "Status error");
                            status = 400;
                            return "";
                        } else if (finalResult.getString("status").equalsIgnoreCase("500")) {
                            Log.v(TAG, "No Data Recieved in Request");
                            status = 500;
                            return "";
                        } else {
                            Log.v(TAG, "200 not recieved");
                            return "";
                        }
                    }
                }

            } catch (ConnectTimeoutException e) {
                networkFlag = "false";
            } catch (SocketTimeoutException e) {
                networkFlag = "false";
            } catch (Exception e) {
                e.printStackTrace();
            }
            return networkFlag;
        }

        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                mProgressDialog.dismiss();
                System.out.println("status--" + status);

                if (status == 200) {
                    AppPreferences.setVerifygplus(UserProfile.this, false);
                    AppPreferences.setVerifyfb(UserProfile.this, false);

                    System.out.println("200 dialog if");
                    AppPreferences.setId(context, "");
                    AppPreferences.setUsername(context, "");
                    AppPreferences.setCustomerId(context, 0);
                    AppPreferences.setUserproductitems(context, "");
                    AppPreferences.setUserprofilepic(context, "");
                    Bitmap profileicon1 = BitmapFactory.decodeResource(getResources(), R.drawable.def_user_img);
                    FragmentDrawer.userImage.setImageBitmap(profileicon1);
                    FragmentDrawer.userName.setText("Log in or register");
                    FragmentDrawer.userItems.setText("at Minkbox");
                    profileicon = BitmapFactory.decodeResource(getResources(), R.drawable.def_user_img);
                    FragmentDrawer.userImage.setImageBitmap(profileicon);
                    AppPreferences.setUserfavoritecategory(context, "");
                    System.out.println("fav category11-------------" + AppPreferences.getUserfavoritecategory(context));
                    AppPreferences.setVerifyweb(getApplicationContext(), false);
                    boolean status = ChatDataBaseHelper.deleteAllChat();
                    System.out.println("chat delete status" + status);
                    Intent i = new Intent(userProfileInstance, HomeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    userProfileInstance.finish();
                    System.out.println("loginCustomerId-------------------------" + AppPreferences.getId(context));

                } else if (status == 400) {
                    System.out.println("400 dialog if");
                } else {
                    System.out.println("500 dialog if");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        MenuItem edit = menu.findItem(R.id.action_edit);
        MenuItem logout = menu.findItem(R.id.action_logout);
        MenuItem share = menu.findItem(R.id.action_share);

        if (customerid==AppPreferences.getCustomerId(UserProfile.this)) {
            edit.setVisible(true);
            share.setVisible(true);
            logout.setVisible(true);
        }
        else{
            edit.setVisible(false);
            share.setVisible(true);
            logout.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Install this app :- ");
            shareIntent.putExtra(Intent.EXTRA_TEXT, AppConstants.APPSHAREURL);
            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.app_name)));
            return true;
        }

        if (id == R.id.action_edit) {
            Intent i = new Intent(UserProfile.this, EditProfile.class);
            startActivity(i);
        }
        if (id == R.id.action_logout) {
            showAppQuitAlertDialog("Do you want to logout from the app ?", UserProfile.this);
        }

        if (id == R.id.action_report_user) {
            if (AppPreferences.getId(UserProfile.this).equalsIgnoreCase("")) {
                Intent i = new Intent(UserProfile.this, UserRegisterLoginActivity.class);
                startActivity(i);
            }else {
                Intent i = new Intent(UserProfile.this, ReportUser.class);
                i.putExtra("customer_id", customerid);
                startActivity(i);
            }
        }

        if (id == android.R.id.home) {
//            Intent i = new Intent(UserProfile.this, HomeActivity.class);
//            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userProfileInstance = null;
    }

    public class OtherUserProfileTask extends AsyncTask<String, Void, String> {

        Context context;
        private String networkFlag = "false", lat, lng;
        private JSONObject jsonObj;
        private String TAG = OtherUserProfileTask.class.getSimpleName();
        private ProgressDialog mProgressDialog;
        private int status = 0;
        int productid;
        int user_id ;
        String user_name ;
        String user_reviews ;
        String user_profile_pic ;
        String user_city ;
        String user_pincode ;
        String user_sell_product;
        String user_sold_product;

        public OtherUserProfileTask(Context con, int productId) {
            this.context = con;
            this.productid = productId;
            mProgressDialog = new ProgressDialog(con);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "Sign In task Started");
            mProgressDialog.show();
        }

        @SuppressLint("NewApi")
        protected String doInBackground(String... params) {
            try {
                HttpParams httpParameters = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParameters,
                        AppConstants.NETWORK_TIMEOUT_CONSTANT);
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        AppConstants.NETWORK_CONNECTION_TIMEOUT_CONSTANT);
                HttpConnectionParams.setSoTimeout(httpParameters,
                        AppConstants.NETWORK_SOCKET_TIMEOUT_CONSTANT);

                HttpClient httpclient = new DefaultHttpClient();
                System.out.println("show all post url ---------------- " + AppConstants.OTHERUSERPROFILE);
                HttpPost httppost = new HttpPost(AppConstants.OTHERUSERPROFILE);
                jsonObj = new JSONObject();

                jsonObj.put("customer_id", AppPreferences.getCustomerId(UserProfile.this));
                jsonObj.put("view_customer_id", customerid);

                System.out.println("owner id" + customerid);
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

                StringEntity se = null;
                try {
                    se = new StringEntity(jsonArray.toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.v("json : ", jsonArray.toString(2));
                System.out.println("Sent JSON is : " + jsonArray.toString());
                httppost.setEntity(se);
                HttpResponse response = null;
                response = httpclient.execute(httppost);
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String jsonString = "";
                try {
                    jsonString = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (jsonString != null) {
                    System.out.println("json response string is -------------    " + jsonString);
                    if (jsonString.contains("result")) {

                        try {
                            JSONObject jsonObj = new JSONObject(jsonString);
                            JSONArray jsonChildArray = jsonObj.getJSONArray("result");//result

                            if (jsonObj.getString("status").equalsIgnoreCase("200")) {
                                status = 200;

                                Log.v(TAG, "after jsonChildArray" + jsonChildArray + " result array length is : " + jsonChildArray.length());
                                if (jsonChildArray != null && jsonChildArray.length() > 0) {
                                    for (int i = 0; i < jsonChildArray.length(); i++) {
                                        JSONObject jsonchildObj = jsonChildArray.getJSONObject(i);

                                        user_id = Integer.parseInt(jsonchildObj.getString("user_id"));
                                        user_name = jsonchildObj.getString("user_name");
                                        user_reviews = jsonchildObj.getString("user_reviews");
                                        user_profile_pic = jsonchildObj.getString("user_profile_pic");
                                        user_city = jsonchildObj.getString("user_city");
                                        user_pincode = jsonchildObj.getString("user_pincode");
                                        user_sell_product = jsonchildObj.getString("user_sell_product");
                                        user_sold_product = jsonchildObj.getString("user_sold_product");

                                        networkFlag = "true";
                                    }


                                } else {
                                    resultnull = "";
                                }

                            } else if (jsonObj.getString("status").equalsIgnoreCase("404")) {
                                Log.v(TAG, "Status error");
                                status = 404;
                            } else if (jsonObj.getString("status").equalsIgnoreCase("500")) {
                                status = 500;
                                Log.v(TAG, "No Data Recieved in Request");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

            } catch (ConnectTimeoutException e) {
                System.out.println("Time out");
                networkFlag = "false";
                status = 600;

            } catch (SocketTimeoutException e) {
                networkFlag = "false";
            } catch (Exception e) {
                e.printStackTrace();
            }
            return networkFlag;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            try {
                if (status == 200) {
                    try {
                        mProgressDialog.dismiss();
                        System.out.println("owner data==========================="+user_name);
                        userName.setText(user_name);
                        userCity.setText(user_city+" "+user_pincode);
                        userProfileReviews.setText(user_reviews);
                        userSolvedItems.setText(user_sold_product);
                        userSellItems.setText(user_sell_product);

                        if (user_profile_pic.equalsIgnoreCase("")) {
                            System.out.println("user profile blank---------------------");
                            userImage = (ImageView)findViewById(R.id.user_profile_image);
                            Bitmap profileicon = BitmapFactory.decodeResource(getResources(), R.drawable.def_user_img);
                            userImage.setImageBitmap(profileicon);
                        } else {
                            userImage = (ImageView)findViewById(R.id.user_profile_image);
//                            ImageLoader.getInstance().displayImage(user_profile_pic,
//                                    userImage);
                            imageLoader.DisplayImage(user_profile_pic, userImage);
                        }

                        userImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (user_profile_pic.equalsIgnoreCase("")) {
                                    Toast.makeText(UserProfile.this,"No Image Available",Toast.LENGTH_LONG);
                                }else {
                                    Intent i = new Intent(UserProfile.this, FullUserImageActivity.class);
                                    i.putExtra("profile_pic", user_profile_pic);
                                    startActivity(i);
                                }
                            }
                        });


                    } catch (Exception e) {
                    }
                } else if (status == 400) {
                    mProgressDialog.dismiss();
                    //gridAdapter.updateResults(data, title, price);
                    Toast.makeText(UserProfile.this, "data not found", Toast.LENGTH_LONG).show();
                } else if (status == 404) {
                    Toast.makeText(UserProfile.this, "Check Username / Password", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
            }

        }
    }

    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent i = new Intent(UserProfile.this, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}