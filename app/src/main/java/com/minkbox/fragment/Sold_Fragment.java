package com.minkbox.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.minkbox.ProductAllDetails;
import com.minkbox.R;
import com.minkbox.adapter.CustomProductGridAdapter;
import com.minkbox.databace.DataBaseHelper;
import com.minkbox.model.Product;
import com.minkbox.utils.AppConstants;
import com.minkbox.utils.AppPreferences;
import com.minkbox.utils.ConnectionDetector;
import com.minkbox.utils.DialogManager;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MMFA-YOGESH on 9/4/2015.
 */
public class Sold_Fragment extends Fragment {

    private GridView gridView;
    private static CustomProductGridAdapter gridAdapter;
    List<Product> sold_product;
    DialogManager alert = new DialogManager();
    DataBaseHelper db;
    String resultnull;
    TextView blank;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sold, container, false);

        db = new DataBaseHelper(getActivity());
        sold_product = new ArrayList<Product>();
        gridView = (GridView) rootView.findViewById(R.id.gridView_sold);
        blank = (TextView) rootView.findViewById(R.id.blank);
        gridAdapter = new CustomProductGridAdapter(getActivity(), sold_product);
        gridView.setAdapter(gridAdapter);

        if (gridAdapter.isEmpty()) {
            blank.setVisibility(View.VISIBLE);
        } else {
            blank.setVisibility(View.GONE);
        }

        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Product product = gridAdapter.data.get(position);
                Intent i = new Intent(getActivity(),
                        ProductAllDetails.class);
                i.putExtra("productid", product.getProduct_server_id());

                System.out.println("id--------------" + product.getProduct_server_id());
                startActivity(i);
            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((ConnectionDetector.isConnectingToInternet(getActivity()))) {
            new GetSellProductTask(getActivity()).execute();
        } else {
            alert.showAlertDialog(getActivity(), "Alert!", "Please, check your internet connection.", false);
        }
    }

    public class GetSellProductTask extends AsyncTask<String, Void, String> {

        Activity activity;
        private String networkFlag = "false", lat, lng;
        boolean isSubmitFragment = false;
        String paymentId;
        boolean walletPaymentFlag = false;
        private JSONArray contacts;
        private String TAG_CONTACTS = "result";
        private String TAG_COUNTRY = "code";
        private String TAG_CODE = "country";
        private String TAG_STD = "std";
        private JSONObject jsonObj;
        private String TAG = GetSellProductTask.class.getSimpleName();
        private ProgressDialog mProgressDialog;
        private int status = 0;
        ArrayList<String> data;
        ArrayList<String> title;
        ArrayList<String> price;
        ArrayList<Integer> productid;
        ArrayList<Product> productlist = new ArrayList<Product>();

        public GetSellProductTask(Activity activity) {
            this.activity = activity;
            mProgressDialog = new ProgressDialog(getActivity());
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
                System.out.println("show all post url ---------------- " + AppConstants.SOLDPRODUCT);
                HttpPost httppost = new HttpPost(AppConstants.SOLDPRODUCT);
                jsonObj = new JSONObject();

                jsonObj.put("customer_id", AppPreferences.getCustomerId(getActivity()));

                System.out.println("customer_id" + AppPreferences.getCustomerId(getActivity()));
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

                                        int productid = Integer.parseInt(jsonchildObj.getString("productid"));
                                        String product_title = jsonchildObj.getString("title");
                                        int product_category_id = Integer.parseInt(jsonchildObj.getString("category"));
                                        String product_category_name = jsonchildObj.getString("category_name");
                                        String product_description = jsonchildObj.getString("description");
                                        String product_image1 = jsonchildObj.getString("image1");
                                        String product_image2 = jsonchildObj.getString("image2");
                                        String product_image3 = jsonchildObj.getString("image3");
                                        String product_image4 = jsonchildObj.getString("image4");
                                        String product_price = jsonchildObj.getString("price");
                                        String product_price_currency = jsonchildObj.getString("currency");
                                        String product_latitude = jsonchildObj.getString("latitude");
                                        String product_longitude = jsonchildObj.getString("longitude");
                                        String product_city = jsonchildObj.getString("product_city");
                                        String product_map_screenshot = jsonchildObj.getString("map_screenshot");
                                        String product_total_reviews = jsonchildObj.getString("total_reviews");
                                        String product_total_likes = jsonchildObj.getString("total_likes");
                                        String product_address = jsonchildObj.getString("product_address");
                                        String product_pincode = jsonchildObj.getString("product_pincode");

                                        Product product = new Product();
                                        product.setProduct_server_id(productid);
                                        product.setProduct_title(product_title);
                                        product.setProduct_category_id(product_category_id);
                                        product.setProduct_category_name(product_category_name);
                                        product.setProduct_description(product_description);
                                        product.setProduct_image1(product_image1);
                                        product.setProduct_image2(product_image2);
                                        product.setProduct_image3(product_image3);
                                        product.setProduct_image4(product_image4);
                                        product.setProduct_price(product_price);
                                        product.setProduct_price_currency(product_price_currency);
                                        product.setProduct_latitude(product_latitude);
                                        product.setProduct_longitude(product_longitude);
                                        product.setProduct_map_screenshot(product_map_screenshot);
                                        product.setProduct_total_reviews(product_total_reviews);
                                        product.setProduct_total_likes(product_total_likes);
                                        product.setProduct_address(product_address);
                                        product.setProduct_pincode(product_pincode);
                                        product.setProduct_city(product_city);
                                        productlist.add(product);
                                        product.setProduct_status("sold");
                                        product.setProduct_sold_status("1");
                                        product.setProduct_reserve_status("0");
                                        db.insertOrUpdateProduct(product);

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
                        gridAdapter.updateResults(productlist);


                        if (resultnull != null && resultnull.equalsIgnoreCase("")) {
                            gridView.setVisibility(View.GONE);
                            blank.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                    }
                } else if (status == 400) {
                    mProgressDialog.dismiss();
                    //gridAdapter.updateResults(data, title, price);
                    Toast.makeText(getActivity(), "data not found", Toast.LENGTH_LONG).show();
                } else if (status == 404) {
                    Toast.makeText(getActivity(), "Check Username / Password", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
            }

            if (gridAdapter.isEmpty()) {
                blank.setVisibility(View.VISIBLE);
            } else {
                blank.setVisibility(View.GONE);
            }
        }
    }
}