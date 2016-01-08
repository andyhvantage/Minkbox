package com.minkbox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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
import com.minkbox.utils.AppConstants;
import com.minkbox.utils.AppPreferences;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.Arrays;

public class FacebookVerficationActivity extends AppCompatActivity {
    Toolbar toolbar;
    public static CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    FacebookVerficationActivity facebookVerficationActivityInstance = null;

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            AccessToken accessToken = loginResult.getAccessToken();
                Log.d("fblogin", "aaa");
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
                                System.out.println(" fblogin id : --------- "+socialId+"  email --------- "+emailId+" name :  "+name+" and id is : "+AppPreferences.getId(FacebookVerficationActivity.this));
                                //                               {"id":"472472322934284","email":"mmfinfotech346@gmail.com","name":"Vikas Barve"}
                               /* new Login(getActivity()).execute("facebook", socialId,
                                        name, emailId);
*/
//                                if(emailId.equalsIgnoreCase(AppPreferences.getId(FacebookVerficationActivity.this))){

                                    new FacebookVerificationTask(facebookVerficationActivityInstance).execute();
 //                               }
//                                else
//                                {
//                                    Toast.makeText(facebookVerficationActivityInstance.getApplicationContext(), "Your current Email And Facebook verification Email not matched", Toast.LENGTH_LONG).show();
//                                }
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
            Toast.makeText(getApplicationContext(), "------ Facebook Login Cancel ----------", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(FacebookException e) {
                       Toast.makeText(getApplicationContext(), "------ Facebook Login Error ----------", Toast.LENGTH_LONG).show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(FacebookVerficationActivity.this);
        setContentView(R.layout.activity_facebook_verfication);

        facebookVerficationActivityInstance = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = getString(R.string.app_name);
        title = getString(R.string.title_activity_identity);
        getSupportActionBar().setTitle(title);

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

        LoginButton loginButton = (LoginButton) findViewById(R.id.fbloginbtn1);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends", "email"));
        loginButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        loginButton.registerCallback(callbackManager, callback);
    }

    public void onStop() {
        super.onStop();

        accessTokenTracker.stopTracking();
       profileTracker.stopTracking();
        LoginManager.getInstance().logOut();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            Intent i = new Intent(FacebookVerficationActivity.this, IdentiVerificationActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        System.out.println(" ---------------------------- onActivityResult ---------");

            super.onActivityResult(requestCode, resultCode, data);
            System.out.println("***************** facebook  onActivityResult called **************");
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public class FacebookVerificationTask extends AsyncTask<String, Void, String> {

        Activity activity;
        private String networkFlag = "false";
        private JSONObject jsonObj;
        private String TAG = FacebookVerificationTask.class.getSimpleName();
        private int status = 0;

        public FacebookVerificationTask(Activity activity) {
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
                jsonObj.put("fbvstatus",1);
                jsonObj.put("gplusvstatus",0);
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
                        AppPreferences.setVerifyfb(FacebookVerficationActivity.this, true);
                        Intent intent = new Intent(FacebookVerficationActivity.this,
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
        facebookVerficationActivityInstance = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(FacebookVerficationActivity.this, IdentiVerificationActivity.class);
        startActivity(i);
        finish();
    }

}
