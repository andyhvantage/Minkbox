package com.minkbox.service;

import android.app.IntentService;
import android.content.Intent;

import com.minkbox.FilterActivity;
import com.minkbox.utils.AppConstants;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;

//This class used to get the total credit of the user logged in app
public class ProductNameList extends IntentService {

//    JSONObject jsonObject;
    String jsonResponse = "";
    int status;

    public ProductNameList(){
		super("service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

        System.out.println("--------------------  proudct name list method called =------------------");

		JSONObject jsonObj = new JSONObject();
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
                HttpPost httppost = new HttpPost(AppConstants.GETALLPRODUCT);
                //jsonObj = new JSONObject();

//                JSONArray jsonArray = new JSONArray();
//                jsonArray.put(jsonObject);

//                Log.d("json Data", jsonArray.toString());
//
//                httppost.setHeader("Accept", "application/json");
//                httppost.setHeader("Content-type", "application/json");
//                StringEntity se = null;
//                try {
//                    se = new StringEntity(jsonArray.toString());
//
//                    se.setContentEncoding(new BasicHeader(
//                            HTTP.CONTENT_ENCODING, "UTF-8"));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                Log.v("json : ", jsonArray.toString(2));
//                System.out.println("Sent JSON is : " + jsonArray.toString());
//                httppost.setEntity(se);
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

            FilterActivity.searchTextOfFilterProduct.clear();
            FilterActivity.searchTextOfFilterProduct.add("");
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i);
                FilterActivity.searchTextOfFilterProduct.add(object.getString("product_name"));
            }

            System.out.println("--------------------  proudct name list method called =------------------"+FilterActivity.searchTextOfFilterProduct.size());

            if(FilterActivity.filterActivityInstance != null)
            {
                FilterActivity.filterActivityInstance.setProduct();
            }
        } catch (ConnectTimeoutException e) {
                System.out.println("Time out");
                status = 600;
            } catch (SocketTimeoutException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }

/*
            jsonObj.put("customer_id", AppPreferences.getCustomerId(getApplicationContext()));
            AsyncTask<String, Void, String> jsonResponse =  new Servicetask(FilterActivity.filterActivityInstance,jsonObj).execute(AppConstants.GETALLPRODUCT);
            FilterActivity.filterActivityInstance.getProduct(jsonResponse.get());
*/

	}

}
