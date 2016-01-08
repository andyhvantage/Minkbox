package com.minkbox.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.minkbox.databace.ChatDataBaseHelper;
import com.minkbox.model.ChatBean;
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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

//This class used to get the total credit of the user logged in app
public class ChatDataBaseService extends IntentService {

    String jsonResponse = "";
    int status;


    public ChatDataBaseService(){
		super("service");
        ChatDataBaseHelper.init(ChatDataBaseService.this);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		JSONObject jsonObj = new JSONObject();
		try {
                System.out.println("************fielter value code1*****************");
                HttpParams httpParameters = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParameters,
                        AppConstants.NETWORK_TIMEOUT_CONSTANT);
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        AppConstants.NETWORK_CONNECTION_TIMEOUT_CONSTANT);
                HttpConnectionParams.setSoTimeout(httpParameters,
                        AppConstants.NETWORK_SOCKET_TIMEOUT_CONSTANT);

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(AppConstants.CHATDATA);
            jsonObj.put("customer_id", AppPreferences.getCustomerId(ChatDataBaseService.this));

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObj);

            StringEntity se = null;
            try {
                se = new StringEntity(jsonArray.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.v("json : ", jsonArray.toString(2));
            System.out.println("Sent JSON is 1: " + jsonArray.toString());
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
                System.out.println("JSONString response is 1: " + jsonResponse);

            JSONObject jsonObject = new JSONObject(jsonResponse);
            if(jsonObject.getString("status").equalsIgnoreCase("200")) {
                JSONArray jsonArray1 = jsonObject.getJSONArray("result");
                for (int i = 0; i < jsonArray1.length(); i++) {
                    JSONObject object = jsonArray1.getJSONObject(i);
                    ChatBean chatBean = new ChatBean();
                    chatBean.setCustomer_id(String.valueOf(AppPreferences.getCustomerId(ChatDataBaseService.this)));
                    chatBean.setProductId(object.getString("product_Id"));

                    chatBean.setMessage(object.getString("message"));
                    chatBean.setSuccess("true");


                    chatBean.setProductName(object.getString("productName"));

                    if(object.getString("sender_id").equalsIgnoreCase(String.valueOf(AppPreferences.getCustomerId(ChatDataBaseService.this)))){
                        chatBean.setUserName(object.getString("reciver_name"));
                        chatBean.setProfileImage(object.getString("profileImageReciver"));
                        chatBean.setSendStatus("true");
                        chatBean.setSenderId(object.getString("sender_id"));
                        chatBean.setReciverId(object.getString("reciverId"));
                        Log.d("chat", "true"+ object.getString("sender_name") );
                    }else{
                        chatBean.setUserName(object.getString("sender_name"));
                        chatBean.setProfileImage(object.getString("profileImageSender"));
                        chatBean.setSendStatus("false");
                        chatBean.setSenderId(object.getString("reciverId"));
                        chatBean.setReciverId(object.getString("sender_id"));
                        Log.d("chat", "false"+ object.getString("reciver_name"));
                    }


                    chatBean.setProductPrice(object.getString("productPrice"));
                    chatBean.setProductOwnerId(object.getString("productOwnerName"));

                   // chatBean.setProfileImage(object.getString("profileImageSender"));

                    chatBean.setProductImage(object.getString("productImage"));



                    chatBean.setDateTime(object.getString("date"));
                    chatBean.setTime(getTime(object.getString("time")));
                    //chatBean.setProfileImage(object.getString("productOwnerprofile_pic"));


                    long Id = ChatDataBaseHelper.addUserData(chatBean);

                }
            }


        } catch (ConnectTimeoutException e) {
                System.out.println("Time out");
                status = 600;
            } catch (SocketTimeoutException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }

	}

    private String getTime(String timeStampStr){

        try{
            DateFormat sdf = new SimpleDateFormat("HH:mm");
            Time netDate = (new Time(Long.parseLong(timeStampStr)));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }
}
