package com.minkbox.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.minkbox.databace.ChatDataBaseHelper;
import com.minkbox.model.ChatBean;
import com.minkbox.network.NetworkUtil;
import com.minkbox.utils.AppConstants;
import com.minkbox.utils.Config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by MMFA-MANISH on 10/10/2015.
 */
public class ChatServices extends BroadcastReceiver {

    Context context;
    String serverURL = AppConstants.SENDCHAT;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        this.context = context;
        String status = NetworkUtil.getConnectivityStatusString(context);

        ChatDataBaseHelper.init(context);

      //  Toast.makeText(context, status, Toast.LENGTH_LONG).show();

        if(status.equalsIgnoreCase("Wifi enabled") || status.equalsIgnoreCase("Mobile data enabled")) {
            List<ChatBean> SpinnerUserData = ChatDataBaseHelper.getAllUserData();

            //for (ChatBean spinnerdt : SpinnerUserData) {
            for (int i = 0; i < SpinnerUserData.size(); i++) {

                final String senderId = SpinnerUserData.get(i).getSenderId();
                final String message = SpinnerUserData.get(i).getMessage();
                final String userName = SpinnerUserData.get(i).getUserName();
                final String productId = SpinnerUserData.get(i).getProductId();
                final String reciverId = SpinnerUserData.get(i).getReciverId();
                final String db_Id = String.valueOf(SpinnerUserData.get(i).getId());

                new LongOperation().execute(serverURL, senderId, message, userName, productId, reciverId, db_Id);
            }
        }
    }


    public class LongOperation  extends AsyncTask<String, Void, String> {

        // Required initialization

        //private final HttpClient Client = new DefaultHttpClient();
        // private Controller aController = null;
        private String Error = null;
        String data  = "";
        int sizeData = 0;
        String db_Id="";


        protected void onPreExecute() {

        }

        // Call after onPreExecute method
        protected String doInBackground(String... params) {

            /************ Make Post Call To Web Server ***********/
            BufferedReader reader=null;
            String Content = "";
            db_Id = params[6];
            // Send data
            try{

                // Defined URL  where to send data
                URL url = new URL(params[0]);
               /* new LongOperation().execute(serverURL, senderId
                        , message, userName , productId, productOwnerId);*/
                // Set Request parameter
                if(!params[1].equals("")) //iemi
                    data +="&" + URLEncoder.encode("senderId", "UTF-8") + "="+params[1].toString();
                if(!params[2].equals(""))  // message
                    data +="&" + URLEncoder.encode("message", "UTF-8") + "="+params[2].toString();
                if(!params[3].equals(""))  // username
                    data +="&" + URLEncoder.encode("userName", "UTF-8") + "="+params[3].toString();
                if(!params[4].equals("")) // pro id
                    data +="&" + URLEncoder.encode("productId", "UTF-8") + "="+params[4].toString();
                if(!params[5].equals(""))  // pro owner id
                    data +="&" + URLEncoder.encode("productOwnerId", "UTF-8") + "="+params[5].toString();

                Log.d("json sent", data);

                // Send POST data request

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( data );
                wr.flush();

                // Get the server response

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while((line = reader.readLine()) != null)
                {
                    // Append server response in string
                    sb.append(line + " ");
                }

                // Append Server Response To Content String
                Content = sb.toString();
            }
            catch(Exception ex)
            {
                Error = ex.getMessage();
            }
            finally
            {
                try
                {

                    reader.close();
                }

                catch(Exception ex) {}
            }

            /*****************************************************/
            return Content;
        }

        protected void onPostExecute(String Result) {

            if (Error != null) {
                Toast.makeText(context, "Error: " + Error, Toast.LENGTH_LONG).show();

            } else {

                // Show Response Json On Screen (activity)
//                Toast.makeText(getBaseContext(), "Message sent." + Result, Toast.LENGTH_LONG).show();

                ChatDataBaseHelper.updateUserData(db_Id, "true");

            }
        }

    }
}
