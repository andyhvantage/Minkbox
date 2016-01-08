package com.minkbox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.minkbox.utils.AppConstants;
import com.minkbox.utils.AppPreferences;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;

public class UserNotification extends AppCompatActivity {
    Toolbar toolbar;
    Button applyButton;
    CheckBox chat_msg_box,review_msg_box,favorites_msg_box, miscellaneous_msg_box;
    AsyncTask<String, Void, String> json;
    UserNotification userNotificationInstance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_notification);
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = getString(R.string.title_activity_notification);
        getSupportActionBar().setTitle(title);
        userNotificationInstance = this;

        chat_msg_box = (CheckBox)findViewById(R.id.chat_msg_box);
        review_msg_box = (CheckBox)findViewById(R.id.review_msg_box);
        favorites_msg_box = (CheckBox)findViewById(R.id.favorites_msg_box);
        miscellaneous_msg_box = (CheckBox)findViewById(R.id.miscellan_msg_box);

        chat_msg_box.setChecked(AppPreferences.getNotificationChatMessage(UserNotification.this));
        review_msg_box.setChecked(AppPreferences.getReviewReceived(UserNotification.this));

        applyButton = (Button)findViewById(R.id.apply_button);
        applyButton.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View view) {

//                final ProgressDialog myProgressDialog = new ProgressDialog(UserNotification.this);
//                myProgressDialog.setTitle("Wait...");
//                myProgressDialog.setMessage("Sending data...");
//                myProgressDialog.show();

                Handler handler=new Handler();
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            String chat = "0";
                            String review = "0";
                            if(chat_msg_box.isChecked()){
                                chat = "1";
                            }
                            if(review_msg_box.isChecked()){
                                review = "1";
                            }


                            JSONObject jsonObj = new JSONObject();
                            jsonObj.put("customer_id", String.valueOf(AppPreferences.getCustomerId(UserNotification.this)));
                            jsonObj.put("chat", chat);
                            jsonObj.put("review", review);


//                            json = new Servicetask(userNotificationInstance, jsonObj).execute(AppConstants.SETNOTIFICATIONPREFERANCE);
                                    new Preferencetask(userNotificationInstance, jsonObj).execute(AppConstants.SETNOTIFICATIONPREFERANCE);

//                            if (json!=null) {
//                                    JSONObject object = new JSONObject(json.get());
//                                if(object.getString("status").equalsIgnoreCase("200")){
//                                    AppPreferences.setNotificationChatMessage(UserNotification.this, chat_msg_box.isChecked());
//                                    AppPreferences.setReviewReceived(UserNotification.this, review_msg_box.isChecked());
//                                    AppPreferences.setNotificationFavourites(UserNotification.this, favorites_msg_box.isChecked());
//                                    AppPreferences.setNotificationMiscellaneous(UserNotification.this, miscellaneous_msg_box.isChecked());
//                                    Toast.makeText(getApplicationContext(), "Your detail successfully updated", Toast.LENGTH_LONG).show();
//                                }
//                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {

                        }
//                        myProgressDialog.dismiss();
                    }
                });

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_notification, menu);
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
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userNotificationInstance = null;
    }

    public class Preferencetask extends AsyncTask<String, Void, String> {

        Context context;
        JSONObject jsonObject;
        String jsonResponse = "";
        int status;
        ProgressDialog dialog;

        public Preferencetask(Activity activity, JSONObject jsonObject) {//Context context
            this.context = context;
            this.jsonObject = jsonObject;
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                System.out.println("************fielter value code*****************");
                HttpParams httpParameters = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParameters,
                        AppConstants.NETWORK_TIMEOUT_CONSTANT);
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        AppConstants.NETWORK_CONNECTION_TIMEOUT_CONSTANT);
                HttpConnectionParams.setSoTimeout(httpParameters,
                        AppConstants.NETWORK_SOCKET_TIMEOUT_CONSTANT);

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(params[0]);
                //jsonObj = new JSONObject();

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObject);

                Log.d("json Data", jsonArray.toString());

                httppost.setHeader("Accept", "application/json");
                httppost.setHeader("Content-type", "application/json");
                StringEntity se = null;
                try {
                    se = new StringEntity(jsonArray.toString());

                    se.setContentEncoding(new BasicHeader(
                            HTTP.CONTENT_ENCODING, "UTF-8"));
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
                    reader = new BufferedReader(new InputStreamReader(response
                            .getEntity().getContent(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    jsonResponse = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("JSONString response is : " + jsonResponse);
            } catch (ConnectTimeoutException e) {
                System.out.println("Time out");
                status = 600;
            } catch (SocketTimeoutException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonResponse;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            if (s!=null) {
                try {
                    JSONObject object = new JSONObject(s);
                    if(object.getString("status").equalsIgnoreCase("200")){
                        AppPreferences.setNotificationChatMessage(UserNotification.this, chat_msg_box.isChecked());
                        AppPreferences.setReviewReceived(UserNotification.this, review_msg_box.isChecked());
                        AppPreferences.setNotificationFavourites(UserNotification.this, favorites_msg_box.isChecked());
                        AppPreferences.setNotificationMiscellaneous(UserNotification.this, miscellaneous_msg_box.isChecked());
                        Toast.makeText(getApplicationContext(), "Your detail successfully updated", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
