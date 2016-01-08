package com.minkbox.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.minkbox.FragmentDrawer;
import com.minkbox.R;
import com.minkbox.UserProfile;
import com.minkbox.network.Servicetask;
import com.minkbox.service.ChatDataBaseService;
import com.minkbox.utils.AppConstants;
import com.minkbox.utils.AppPreferences;
import com.minkbox.utils.Config;
import com.minkbox.utils.ConnectionDetector;
import com.minkbox.utils.Controller;
import com.minkbox.utils.DialogManager;
import com.minkbox.utils.Function;
import com.minkbox.utils.ServiceHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserLoginFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    TextView forgetpwd;
    Dialog dialog;
    EditText edit_name, edit_pass;
    Button btn_login;
    String username, password;
    RelativeLayout parentlayout;
    Button btn_gplussign;
    Button ok;
    Button cancle;
    PopupWindow pwindo;
    private String TAG = "LoginRegistrationActivity";
    RelativeLayout otherView;
    EditText email;
    DialogManager dialogManager = new DialogManager();
    String facebook_id, facebook_email, facebook_name, gplusId, gplusName,
            gplusEmail;
    public static String loginType = "";
    ProgressDialog asynDialog;
    String status = "", result = "", loginId = "";
    Controller aController;
    private static GoogleApiClient mGoogleApiClient;
    private static boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private static boolean mIntentInProgress;
    private static final int RC_SIGN_IN = 0;
    public static CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    public static String chatName="";
    public static String chatEmail="";
    public static String chatImei="";

    public UserLoginFragment() {
        super();
    }

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            AccessToken accessToken = loginResult.getAccessToken();

            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            System.out.println("json----- " + object.toString());

                            try {
                                String socialId = object.getString("id");
                                String emailId = object.getString("email");
                                String name = object.getString("name");
                                System.out.println(" fblogin id : --------- "+socialId+"  email --------- "+emailId+" name :  "+name);
 //                               {"id":"472472322934284","email":"mmfinfotech346@gmail.com","name":"Vikas Barve"}
                                new Login(getActivity()).execute("facebook", socialId,
                                        name, emailId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
//            Toast.makeText(getApplicationContext(), "------ Facebook Login Cancel ----------", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(FacebookException e) {
            //           Toast.makeText(getApplicationContext(), "------ Facebook Login Error ----------", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker= new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {

            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_login_fragment, null);

        LoginButton loginButton = (LoginButton) rootView.findViewById(R.id.fbloginbtn1);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends", "email"));

        loginButton.setBackgroundResource(R.drawable.loginfacebook);
       loginButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.trans);
        loginButton.setHeight((int) getResources().getDimension(R.dimen.login_buttons_height));
        loginButton.registerCallback(callbackManager, callback);

        loginType = "";
        mSignInClicked = false;
        mIntentInProgress = false;
        mGoogleApiClient = null;

        aController = (Controller)getActivity().getApplicationContext();

        if(AppPreferences.getDeviceid(getActivity()).equalsIgnoreCase("")){
            getDeviceId();
        }

        edit_name = (EditText) rootView.findViewById(R.id.name_edit);
        edit_pass = (EditText) rootView.findViewById(R.id.password_edit);

        parentlayout = (RelativeLayout) rootView.findViewById(R.id.linear);

        parentlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Function.hideSoftKeyboard(getActivity());
            }
        });

        forgetpwd = (TextView) rootView.findViewById(R.id.forgotpswdtext);
        String mystring=new String("Forgot Password?");
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        forgetpwd.setText(content);
        forgetpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                dialog.setContentView(R.layout.forget_password_popup);
                email = (EditText) dialog.findViewById(R.id.email);
                Button send = (Button) dialog.findViewById(R.id.ok);
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(email.getText().toString())) {
                            email.setError(getString(R.string.error_field_required));
                        } else if ((email.getText().toString()).contains("+")) {
                            email.setError(getString(R.string.error_field_required));
                        } else {
                            new ForgotPasswordTask(getActivity(), email.getText().toString().trim()).execute();
                        }
                    }
                });
                Button cancleButton = (Button) dialog.findViewById(R.id.cancle);
                cancleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        btn_login = (Button) rootView.findViewById(R.id.loginbtn);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!new Function().CheckGpsEnableOrNot(getActivity())) {
                    showGpsSettingsAlert("GPS Settings...",
                            "GPS is not enabled. Do you want to go to settings menu?",
                            getActivity());
                }

                if (edit_name.length() > 0) {
                    username = edit_name.getText().toString();
                    if (edit_pass.length() > 0) {
                        loginType = "web";
                        password = edit_pass.getText().toString();
                        new Login(getActivity()).execute(loginType,
                                edit_name.getText().toString(), edit_pass
                                        .getText().toString());
                    } else {
                        edit_pass.setError("Enter Password");
                    }
                } else {
                    edit_name.setError("Enter Email Id/Username");
                }
            }
        });



        btn_gplussign = (Button) rootView.findViewById(R.id.logingmail);
        btn_gplussign.setBackgroundResource(R.drawable.logingmail);
        btn_gplussign.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.trans);
        btn_gplussign.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loginType = "googlePlus";
                signInWithGplus();
            }
        });

        otherView = (RelativeLayout) rootView.findViewById(R.id.linear);
        otherView.setVisibility(View.VISIBLE);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        return rootView;
    }

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
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        getActivity().finish();
                    }
                });
        alertDialog.show();
    }

    private void enableMapLocation() {
        try {
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup_enable_map_location,
                    (ViewGroup) getActivity().findViewById(R.id.pop_up_exit_element));
            pwindo = new PopupWindow(layout, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

            ok = (Button) layout.findViewById(R.id.ok);
            ok.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    private void resolveSignInError() {
        System.out.println("mConnectionResult is ---------- "
                + mConnectionResult);
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        getProfileInformation();
        updateUI(true);
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
        // updateUI(false);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), getActivity(),
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            mConnectionResult = result;

            if (mSignInClicked) {
                resolveSignInError();
            }
        }
    }

    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
        LoginManager.getInstance().logOut();

    }

    class Login extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialogManager.showProcessDialog(getActivity(), "Login...");
        }

        Context context;

        public Login(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            ServiceHandler serviceHandler = new ServiceHandler();
            chatEmail = params[1];
            List<NameValuePair> values = new ArrayList<>();
            values.add(new BasicNameValuePair("loginType", params[0]));
            values.add(new BasicNameValuePair("user_lat", AppPreferences.getLatutude(getActivity())));
            values.add(new BasicNameValuePair("user_long", AppPreferences.getLongitude(getActivity())));

            if (params[0].equals("web")) {
                values.add(new BasicNameValuePair("user_email_id", params[1]));
                values.add(new BasicNameValuePair("user_pwd", params[2]));
                values.add(new BasicNameValuePair("user_social_id", ""));
                values.add(new BasicNameValuePair("user_name", ""));

                if(AppPreferences.getDeviceid(getActivity()).equalsIgnoreCase("")){
                    values.add(new BasicNameValuePair("device_id", getDeviceId()));
                }else{
                    values.add(new BasicNameValuePair("device_id", AppPreferences.getDeviceid(getActivity())));
                }

            } else if (params[0].equals("Gplus")) {
                values.add(new BasicNameValuePair("user_social_id", params[1]));
                values.add(new BasicNameValuePair("user_name", params[2]));
                values.add(new BasicNameValuePair("user_pwd", ""));
                values.add(new BasicNameValuePair("user_email_id", params[3]));
                if(AppPreferences.getDeviceid(getActivity()).equalsIgnoreCase("")){
                    values.add(new BasicNameValuePair("device_id", getDeviceId()));
                }else{
                    values.add(new BasicNameValuePair("device_id", AppPreferences.getDeviceid(getActivity())));
                }
            }
            else if (params[0].equals("facebook")) {
                values.add(new BasicNameValuePair("user_social_id", params[1]));
                values.add(new BasicNameValuePair("user_name", params[2]));
                values.add(new BasicNameValuePair("user_pwd", ""));
                values.add(new BasicNameValuePair("user_email_id", params[3]));
                if(AppPreferences.getDeviceid(getActivity()).equalsIgnoreCase("")){
                    values.add(new BasicNameValuePair("device_id", getDeviceId()));
                }else{
                    values.add(new BasicNameValuePair("device_id", AppPreferences.getDeviceid(getActivity())));
                }
            }
            Log.d("login_value", values.toString());
            String login_json = serviceHandler.makeServiceCall(
                    AppConstants.LOGIN, ServiceHandler.POST, values);
            if (login_json != null) {
                Log.d("login_json", login_json.toString());
                try {
                    JSONObject jsonObject = new JSONObject(login_json);
                    status = jsonObject.getString("status");
                    JSONArray resultArray = jsonObject.getJSONArray("result");

                    SharedPreferences preferences = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("Check_login", true);
                    editor.commit();

                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject jsonObject2 = resultArray.getJSONObject(i);
                        loginId = jsonObject2.getString("id");
                        AppPreferences
                                .setId(context, jsonObject2.getString("email"));
                        AppPreferences
                                .setCustomerId(context, Integer.parseInt(jsonObject2.getString("id")));
                        AppPreferences
                                .setUsername(context, jsonObject2.getString("name"));
                        AppPreferences
                                .setUserprofilepic(context, jsonObject2.getString("profile_pic"));

                        AppPreferences
                                .setUserprofilereview(context, jsonObject2.getString("reviews"));
                        AppPreferences
                                .setUserproductsell(context, jsonObject2.getString("selling"));
                        AppPreferences
                                .setUserproductsold(context, jsonObject2.getString("sold"));
                        AppPreferences
                                .setUserproductitems(context, jsonObject2.getString("items"));

                        AppPreferences
                                .setUsercity(context, jsonObject2.getString("customer_city"));
                        AppPreferences
                                .setUserpincode(context, jsonObject2.getString("customer_pincode"));
                        AppPreferences
                                .setUseraddress(context, jsonObject2.getString("customer_address"));

                        AppPreferences
                                .setUserlastname(context, jsonObject2.getString("last_name"));
                        AppPreferences
                                .setUserdob(context, jsonObject2.getString("dob"));

                        if(jsonObject2.getString("emailvstatus").equalsIgnoreCase("1")){
                            AppPreferences.setVerifyweb(context,true);
                        }else{
                            AppPreferences.setVerifyweb(context,false);
                        }

                        AppPreferences
                                .setUserfavoritecategory(context, jsonObject2.getString("categoryId"));
                      //
//                        AppPreferences
//                                .setUserfavoritecategory(context, );
                        AppPreferences
                                .setUsergender(context, jsonObject2.getString("gender"));
                        AppPreferences.setVerifygplus(context, Integer.parseInt(jsonObject2.getString("gplusvstatus")) == 1 ? true : false);
                        AppPreferences.setVerifyfb(context, Integer.parseInt(jsonObject2.getString("fbvstatus")) == 1 ? true : false);

                                Log.d("loginId", loginId);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            edit_name.setText("");
            edit_pass.setText("");

            dialogManager.stopProcessDialog();
            Log.d("data from server", status + "   " + loginId);
            if (status.equals("200") && !loginId.equals("")) {

                Intent intent = new Intent(getActivity(), ChatDataBaseService.class);
                getActivity().startService(intent);

                FragmentDrawer.userName.setText(AppPreferences.getUsername(getActivity()));
                FragmentDrawer.userItems.setText(AppPreferences.getUserproductitems(getActivity())+ " " + "items");

                NotificationPreferanceTask();

                ImageLoader.getInstance().displayImage(AppPreferences.getUserprofilepic(getActivity()),
                        FragmentDrawer.userImage);
                Intent mintent_home = new Intent(getActivity(),
                        UserProfile.class);//CellAProduct
                mintent_home.putExtra("customerid", AppPreferences.getCustomerId(getActivity()));
                startActivity(mintent_home);
                getActivity().finish();
            } else {
                Toast.makeText(getActivity(), "Please try again later",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void NotificationPreferanceTask() {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("customer_id", AppPreferences.getCustomerId(getActivity()));
            AsyncTask<String, Void, String> json = new Servicetask(getActivity(), jsonObj).execute(AppConstants.GETNOTIFICATIONPREFERANCE);
                Log.d("Notification", json.get());
            if (json!=null) {
                JSONObject object = new JSONObject(json.get());
                if(object.getString("status").equalsIgnoreCase("200")){
                    JSONArray jsonArray = object.getJSONArray("result");
                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        boolean values;
                        values = "1".equals(jsonObject.get("chat"));
                        AppPreferences.setNotificationChatMessage(getActivity(), values);

                        values = "1".equals(jsonObject.get("review"));
                        AppPreferences.setReviewReceived(getActivity(), values);

                        values = "1".equals(jsonObject.get("favorites"));
                        AppPreferences.setNotificationFavourites(getActivity(), values);



                        Log.d("Notification1", AppPreferences.getNotificationChatMessage(getActivity()) + "");
                        Log.d("Notification2", AppPreferences.getReviewReceived(getActivity()) + "");
                        Log.d("Notification3", AppPreferences.getNotificationFavourites(getActivity()) + "");
                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
    }

    public static void myOnActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("login type ---------------" + loginType);
        if (loginType.equalsIgnoreCase("googlePlus")) {
            if (requestCode == RC_SIGN_IN) {
                if (resultCode != Activity.RESULT_OK) {
                    mSignInClicked = false;
                }

                mIntentInProgress = false;

                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
            }
            loginType = "";
        }

    }

    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            updateUI(false);
        }
    }

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
//            btn_gplussign.setVisibility(View.GONE);
//            btn_gplusout.setVisibility(View.VISIBLE);
        } else {
//            btn_gplussign.setVisibility(View.VISIBLE);
//            btn_gplusout.setVisibility(View.GONE);
        }
    }

    private void getProfileInformation() {
        try {
            System.out.println("people api is : --- " + Plus.PeopleApi
                    + " mGoogleApiClient : --- " + mGoogleApiClient);
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String id = currentPerson.getId();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                gplusId = currentPerson.getId();
                gplusName = currentPerson.getDisplayName();
                gplusEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
//                type = "Gplus";
                loginType = "Gplus";
                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl + " person id : " + id);

                signOutFromGplus();

                if (ConnectionDetector.isConnectingToInternet(getActivity())) {
                    new Login(getActivity()).execute(loginType, gplusId,
                            gplusName, gplusEmail);
                }
            } else {
                Toast.makeText(getActivity(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ForgotPasswordTask extends AsyncTask<String, Void, Integer> {
        private String email;
        private Activity activity;
        private JSONObject jsonObj;
        private int status;
        private String message;
        private String TAG = ForgotPasswordTask.class.getSimpleName();

        public ForgotPasswordTask(Activity activity, String email) {
            this.email = email;
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Integer doInBackground(String... params) {
            try {
                HttpParams httpParameters = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParameters, AppConstants.NETWORK_TIMEOUT_CONSTANT);
                HttpConnectionParams.setConnectionTimeout(httpParameters, AppConstants.NETWORK_CONNECTION_TIMEOUT_CONSTANT);
                HttpConnectionParams.setSoTimeout(httpParameters, AppConstants.NETWORK_SOCKET_TIMEOUT_CONSTANT);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                System.out.println(" forget password url is : "+AppConstants.FORGETPASSWORD);
                HttpPost httppost = new HttpPost(AppConstants.FORGETPASSWORD);

                JSONArray jsonArray = new JSONArray();

                StringEntity se = null;
                jsonObj = new JSONObject();

                jsonObj.put("email", email);

                jsonArray.put(jsonObj);

                try {
                    se = new StringEntity(jsonArray.toString(), "UTF-8");
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
                if (jsonString != null) {
                    System.out.println("jsonstring-------------------------------" + jsonString);
                    JSONObject jsonObj = new JSONObject(jsonString);
                    if (jsonObj.getString("status").equalsIgnoreCase("200")) {
                        System.out
                                .println("--------- message 200 got ----------");
                        status = 200;

                    } else if (jsonObj.getString("status").equalsIgnoreCase("400")) {
                        Log.v(TAG, "Status error");
                        status = 404;
                    } else if (jsonObj.getString("status").equalsIgnoreCase("500")) {
                        Log.v(TAG, "No Data Recieved in Request");
                        status = 500;
                    } else {
                        Log.v(TAG, "200 not recieved");
                    }
                }

            } catch (ConnectTimeoutException e) {
            } catch (SocketTimeoutException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
            return status;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            try {
                System.out.println("status--" + status);
                if (status == 200) {
                    System.out.println("200 dialog if");
                    Toast.makeText(activity.getApplicationContext(), "The new password has been sent on your Email Successfully.", Toast.LENGTH_SHORT).show();
                    ResetFields();
                } else if (status == 404) {
                    System.out.println("400 dialog if");
                    Toast.makeText(activity.getApplicationContext(), "Email does not exist.", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("500 dialog if");
                    Toast.makeText(activity.getApplicationContext(), "Your request can not be completed at this moment. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void ResetFields() {
        email.setText("");
        email.setHint("Type Your Email-id");
        dialog.dismiss();
    }

      @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("LogIn");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loginType = "";
    }

    public String getDeviceId(){

        GCMRegistrar.checkDevice(getActivity().getApplicationContext());
        GCMRegistrar.checkManifest(getActivity().getApplicationContext());
        final String regId = GCMRegistrar.getRegistrationId(getActivity().getApplicationContext());

        // Check if regid already presents
        if (regId.equals("")) {
            Log.i("GCM K", "--- Regid = ''" + regId);
            GCMRegistrar.register(getActivity().getApplicationContext(), Config.GOOGLE_SENDER_ID);
            AppPreferences.setDeviceid(getActivity(), regId);
            return regId;
        } else {

            if (GCMRegistrar.isRegisteredOnServer(getActivity())) {

                Log.i("GCM K2", "--- Regid = ''" + regId);
            } else {
                Log.i("GCM K3", "--- Regid = ''" + regId);
            }
            AppPreferences.setDeviceid(getActivity(),regId);
            return regId;
        }


    }

}
