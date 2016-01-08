package com.minkbox.network;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.minkbox.ProductAllDetails;
import com.minkbox.ProductDetail;
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

/**
 * Created by mmf-su-yash on 9/10/2015.
 */
public class ReserveTask extends AsyncTask<String, Void, String> {

    Context con;
    private String networkFlag = "false", lat, lng;
    private JSONObject jsonObj;
    private String TAG = ReserveTask.class.getSimpleName();
    private ProgressDialog mProgressDialog;
    private int status = 0;
    String reserveSts;
    int product_id;
    String reserve_status;
    String resultnull;


    public ReserveTask(Context context, String reserveSt, int productid) {
        this.con = context;
        this.reserveSts = reserveSt;
        this.product_id = productid;
        mProgressDialog = new ProgressDialog(con);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.v(TAG, "reserve task Started");
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
            System.out.println("show all post url ---------------- " + AppConstants.RESERVEPRODUCTAPI);
            HttpPost httppost = new HttpPost(AppConstants.RESERVEPRODUCTAPI);
            jsonObj = new JSONObject();

            jsonObj.put("customer_id", AppPreferences.getCustomerId(con));
            jsonObj.put("product_reserve_status", reserveSts);
            jsonObj.put("product_id", product_id);

            System.out.println("customer_id" + AppPreferences.getCustomerId(con));
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

                                    reserve_status = jsonchildObj.getString("reserve_status");
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

                    if(reserve_status.equalsIgnoreCase("1")){
                        if(ProductAllDetails.productAllDetailsInstance != null)
                        {
                            ProductAllDetails.productAllDetailsInstance.reserve();
                            System.out.println("product_all_detail Reserve task");
                        }
                        if(ProductDetail.productDetailInstance != null)
                        {
                            ProductDetail.productDetailInstance.reserve();
                            System.out.println("product_detail Reserve task");
                        }

                    }
                    if(reserve_status.equalsIgnoreCase("0")){
                        if(ProductAllDetails.productAllDetailsInstance != null) {
                            ProductAllDetails.productAllDetailsInstance.unreserve();
                            System.out.println("product_all_detail unReserve task");
                        }
                        if(ProductDetail.productDetailInstance != null) {
                            ProductDetail.productDetailInstance.unreserve();
                            System.out.println("product_detail unReserve task");
                        }
                }

                } catch (Exception e) {
                }
            } else if (status == 400) {
                mProgressDialog.dismiss();
                //gridAdapter.updateResults(data, title, price);
                Toast.makeText(con, "data not found", Toast.LENGTH_LONG).show();
            } else if (status == 404) {
                Toast.makeText(con, "data not found", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
        }

    }
}
