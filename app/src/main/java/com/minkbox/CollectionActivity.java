package com.minkbox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.minkbox.adapter.CustomCollectionGridAdapter;
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


public class CollectionActivity extends ActionBarActivity {

    Toolbar toolbar;

    private GridView gridView;
    public static CustomCollectionGridAdapter gridAdapter;
    List<Product> collection_product;
    DialogManager alert = new DialogManager();
    TextView blank;
    String resultnull;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = "New Collections";
        getSupportActionBar().setTitle(title);

        collection_product = new ArrayList<Product>();
        gridView = (GridView)findViewById(R.id.gridView);
        blank = (TextView)findViewById(R.id.blank);
        gridAdapter = new CustomCollectionGridAdapter(CollectionActivity.this, collection_product);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Product product = gridAdapter.data.get(position);
                Intent i = new Intent(CollectionActivity.this,
                        ProductDetail.class);
                i.putExtra("productid", product.getProduct_server_id());
                System.out.println("id--------------" + product.getProduct_server_id());
                startActivity(i);
            }
        });

        if(gridAdapter.isEmpty()){
            blank.setVisibility(View.VISIBLE);
        }else{
            blank.setVisibility(View.GONE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if ((ConnectionDetector.isConnectingToInternet(CollectionActivity.this))) {
            new GetCollectionProductTask(CollectionActivity.this).execute();
        } else {
            alert.showAlertDialog(CollectionActivity.this, "Alert!", "Please, check your internet connection.", false);
        }
        gridAdapter.updateResults(collection_product);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_collection, menu);
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

    public class GetCollectionProductTask extends AsyncTask<String, Void, String> {

        Activity activity;
        private String networkFlag = "false", lat, lng;
        String user_total_items ;
        String user_total_sell;
        String user_total_sold ;
        String user_total_reviews;
        private JSONObject jsonObj;
        private String TAG = GetCollectionProductTask.class.getSimpleName();
        private ProgressDialog mProgressDialog;
        private int status = 0;
        ArrayList<Product> productlist = new ArrayList<Product>();

        public GetCollectionProductTask(Activity activity) {
            this.activity = activity;
            mProgressDialog = new ProgressDialog(CollectionActivity.this);
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
                System.out.println(" new collection app url : "+AppConstants.NEWCOLLECTIONAPPURL);
                HttpPost httppost = new HttpPost(AppConstants.NEWCOLLECTIONAPPURL);
                jsonObj = new JSONObject();

                jsonObj.put("customer_id", AppPreferences.getCustomerId(CollectionActivity.this));

                System.out.println("customer_id" + AppPreferences.getCustomerId(CollectionActivity.this));
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

                                        int productid = Integer.parseInt(jsonchildObj.getString("product_id"));
                                        String product_title = jsonchildObj.getString("product_title");
                                        String product_image1 = jsonchildObj.getString("product_image1");
                                        String product_price = jsonchildObj.getString("product_price");
                                        String product_price_currency = jsonchildObj.getString("product_price_currency");
                                        String reserve_status = jsonchildObj.getString("product_reserve_status");
                                        String sold_status = jsonchildObj.getString("product_sold_status");
                                        String like_status = jsonchildObj.getString("like_status");
                                        String product_status = jsonchildObj.getString("product_status");
                                    //    miles = jsonchildObj.getString("miles");
                                     //   flag_limit = String.valueOf(jsonchildObj.getInt("flag_limit"));

                                        Product product=new Product();
                                        product.setProduct_server_id(productid);
                                        product.setProduct_title(product_title);
                                        product.setProduct_image1(product_image1);
                                        product.setProduct_price(product_price);
                                        product.setProduct_price_currency(product_price_currency);
                                        product.setProduct_reserve_status(reserve_status);
                                        product.setProduct_sold_status(sold_status);
                                        product.setProduct_status(product_status);
                                        product.setProduct_like_status(like_status);
                                        productlist.add(product);
                                    }


                                } else {
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

                        }
                    } catch (Exception e) {
                    }
                } else if (status == 400) {
                    mProgressDialog.dismiss();
                    //gridAdapter.updateResults(data, title, price);
                    Toast.makeText(CollectionActivity.this, "data not found", Toast.LENGTH_LONG).show();
                } else if (status == 404) {
                    Toast.makeText(CollectionActivity.this, "Check Username / Password", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
            }

            if(gridAdapter.isEmpty()){
                blank.setVisibility(View.VISIBLE);
            }else{
                blank.setVisibility(View.GONE);
            }

        }
    }
}
