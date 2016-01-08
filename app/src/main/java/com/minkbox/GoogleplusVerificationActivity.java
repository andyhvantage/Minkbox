package com.minkbox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.minkbox.utils.AppConstants;
import com.minkbox.utils.AppPreferences;
import com.minkbox.utils.ServiceHandler;

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
import java.util.List;

public class GoogleplusVerificationActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    Toolbar toolbar;
    Button btn_gplussign, btn_gplusout;
    String gplusId, gplusName,
            gplusEmail;
    RelativeLayout otherView;
    String type;
    String loginType = "";
    private GoogleApiClient mGoogleApiClient;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private boolean mIntentInProgress;
    private static final int RC_SIGN_IN = 0;
    private String TAG = "LoginRegistrationActivity";
    ProgressDialog asynDialog;
    String status = "", result = "", loginId = "";
    public GoogleplusVerificationActivity googleplusVerificationActivityInstance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googleplus_verification);

        googleplusVerificationActivityInstance = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = getString(R.string.app_name);
        title = getString(R.string.title_activity_identity);
        getSupportActionBar().setTitle(title);
        btn_gplussign = (Button) findViewById(R.id.profile_verify_button_login);// SignInButton
        btn_gplussign.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loginType = "googlePlus";
                signInWithGplus();
            }
        });

        btn_gplusout = (Button) findViewById(R.id.profile_verify_button_logout);
        btn_gplusout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                signOutFromGplus();
            }
        });
        otherView = (RelativeLayout) findViewById(R.id.relativegplus);
        otherView.setVisibility(View.VISIBLE);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
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
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
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

    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
        // updateUI(false);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
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

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    // ////////////g+login/////////////////////////
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
            btn_gplussign.setVisibility(View.GONE);
            btn_gplusout.setVisibility(View.GONE);
        } else {
            btn_gplussign.setVisibility(View.VISIBLE);
            btn_gplusout.setVisibility(View.GONE);
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
                type = "Gplus";

                signOutFromGplus();
//                if(gplusEmail.equalsIgnoreCase(AppPreferences.getId(GoogleplusVerificationActivity.this))){
                    new GplusVerificationTask(googleplusVerificationActivityInstance).execute();
//                }

                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl + " person id : " + id);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_googleplus_verification, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            Intent i = new Intent(GoogleplusVerificationActivity.this, IdentiVerificationActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class GplusVerificationTask extends AsyncTask<String, Void, String> {

        Activity activity;
        private String networkFlag = "false";
        private JSONObject jsonObj;
        private String TAG = GplusVerificationTask.class.getSimpleName();
        private int status = 0;

        public GplusVerificationTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "fb verification  task Started");
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
                System.out.println("show all fb, gplus url ---------------- " + AppConstants.FACEBOOKGPLUSVERIFICATIONAPPURL);
                HttpPost httppost = new HttpPost(AppConstants.FACEBOOKGPLUSVERIFICATIONAPPURL);
                jsonObj = new JSONObject();
                jsonObj.put("emailvstatus", 0);
                jsonObj.put("fbvstatus", 0);
                jsonObj.put("gplusvstatus", 1);
                jsonObj.put("customer_id", AppPreferences.getCustomerId(activity.getApplicationContext()));

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
                            jsonObj = new JSONObject(jsonString);
                            if (jsonObj.getString("status").equalsIgnoreCase("200")) {
                                status = 200;
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

            try {
                if (status == 200) {
                    try {
                        AppPreferences.setVerifygplus(activity.getApplicationContext(), true);
                        Intent intent = new Intent(activity,
                                ThankuEmailActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(activity, "Your identity verified successfully", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                    }
                } else if (status == 400) {
                    Toast.makeText(activity, "Your request can not be completed at this moment", Toast.LENGTH_LONG).show();
                } else if (status == 500) {
                    Toast.makeText(activity, "Your request can not be completed at this moment", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        googleplusVerificationActivityInstance = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(GoogleplusVerificationActivity.this, IdentiVerificationActivity.class);
        startActivity(i);
        finish();
    }
}
