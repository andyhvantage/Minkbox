package com.minkbox.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.minkbox.utils.AppConstants;

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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;

/**
 * Created by mmfresearch on 9/26/2015.
 */
public class Servicetask extends AsyncTask<String, Void, String> {

    Context context;
    JSONObject jsonObject;
    String jsonResponse = "";
    int status;
    ProgressDialog dialog;

    public Servicetask(Activity activity, JSONObject jsonObject) {//Context context
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
            StringEntity se = null;
            try {
                se = new StringEntity(jsonArray.toString(), "UTF-8");

              //  se.setContentEncoding(new BasicHeader(
               //         HTTP.CONTENT_ENCODING, "UTF-8"));
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
    }
}
