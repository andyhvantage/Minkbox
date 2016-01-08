package com.minkbox;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.minkbox.adapter.FielterCategoryAdapter;
import com.minkbox.databace.DataBaseHelper;
import com.minkbox.model.Category;
import com.minkbox.model.FielterBean;
import com.minkbox.service.ProductNameList;
import com.minkbox.utils.AppConstants;
import com.minkbox.utils.AppPreferences;
import com.minkbox.utils.ConnectionDetector;

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
import java.util.ArrayList;

public class FilterActivity extends ActionBarActivity {
    Toolbar toolbar;
    LinearLayout linearLayout;
    ListView categorylist;
    ArrayList<Integer> categoriesid = new ArrayList<Integer>();
    ArrayList<Category> al = new ArrayList<Category>();
    FielterCategoryAdapter adapter;
    EditText from_edit, to_edit, lat_ed, lon_ed, area_edit;
    AutoCompleteTextView auto_product;
    RadioButton rd1, rd2, rd3, rd4, rd5, rd6, rd7, rd8, rd9, rd10, rd11, rd12;
    public static ArrayList<String> catId = new ArrayList<String>();
    public static ArrayList<String> catName = new ArrayList<String>();
    public int categoryid = 0;
    DataBaseHelper db;
    String productname, latitutefielter, longitudefielter, distancefielter, listedwithin, pricefrom, priceto, sortby, place;
//    ArrayList<String> searchText = new ArrayList<String>();
    public static FilterActivity filterActivityInstance =  null;
    public static  ArrayList<String> searchTextOfFilterProduct = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        filterActivityInstance =  this;
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = getString(R.string.title_activity_fielter);
        getSupportActionBar().setTitle(title);
        from_edit = (EditText) findViewById(R.id.form_edit);
        to_edit = (EditText) findViewById(R.id.to_edit);
        area_edit = (EditText) findViewById(R.id.searchplace_edit);
        auto_product = (AutoCompleteTextView) findViewById(R.id.searchedit);
        rd1 = (RadioButton) findViewById(R.id.nearby_radio_button);
        rd2 = (RadioButton) findViewById(R.id.area_radio_button);
        rd3 = (RadioButton) findViewById(R.id.city_radio_button);
        rd4 = (RadioButton) findViewById(R.id.further_radio_button);
        rd5 = (RadioButton) findViewById(R.id.hour_radio_button);
        rd6 = (RadioButton) findViewById(R.id.seven_daysradio_button);
        rd7 = (RadioButton) findViewById(R.id.thirty_day_radio_button);
        rd8 = (RadioButton) findViewById(R.id.all_radio_buuton);
        rd9 = (RadioButton) findViewById(R.id.distance_radio_button);
        rd10 = (RadioButton) findViewById(R.id.high_radio_button);
        rd11 = (RadioButton) findViewById(R.id.low_radio_button);
        rd12 = (RadioButton) findViewById(R.id.recently_radio_button);

        from_edit.setText(AppPreferences.getPricefrom(FilterActivity.this));
        to_edit.setText(AppPreferences.getPriceto(FilterActivity.this));
        auto_product.setText(AppPreferences.getSearchtext(FilterActivity.this));

        if (AppPreferences.getFilterdistance(FilterActivity.this).equalsIgnoreCase("NearBy1km")) {
            rd1.setChecked(true);
        //    rd1.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else if (AppPreferences.getFilterdistance(FilterActivity.this).equalsIgnoreCase("5kms")) {
            rd2.setChecked(true);
       //     rd2.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else if (AppPreferences.getFilterdistance(FilterActivity.this).equalsIgnoreCase("10kms")) {
            rd3.setChecked(true);
        //    rd3.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else if (AppPreferences.getFilterdistance(FilterActivity.this).equalsIgnoreCase("")) {
            rd4.setChecked(true);
        //    rd4.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        if (AppPreferences.getPosttime(FilterActivity.this).equalsIgnoreCase("24hour")) {
            rd5.setChecked(true);
       //     rd5.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else if (AppPreferences.getPosttime(FilterActivity.this).equalsIgnoreCase("7day")) {
            rd6.setChecked(true);
       //     rd6.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else if (AppPreferences.getPosttime(FilterActivity.this).equalsIgnoreCase("30days")) {
            rd7.setChecked(true);
       //     rd7.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else if (AppPreferences.getPosttime(FilterActivity.this).equalsIgnoreCase("")) {
            rd8.setChecked(true);
       //     rd8.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        if (AppPreferences.getSortby(FilterActivity.this).equalsIgnoreCase("")) {
            rd9.setChecked(true);
        //    rd9.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else if (AppPreferences.getSortby(FilterActivity.this).equalsIgnoreCase("pricelowtohigh")) {
            rd10.setChecked(true);
        //    rd10.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else if (AppPreferences.getSortby(FilterActivity.this).equalsIgnoreCase("pricehightolow")) {
            rd11.setChecked(true);
        //    rd11.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else if (AppPreferences.getSortby(FilterActivity.this).equalsIgnoreCase("mostrecentpublished")) {
            rd12.setChecked(true);
          //  rd12.setTextColor(getResources().getColor(R.color.colorPrimary));
        }


        if(rd1.isChecked()){
            rd1.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        if(rd2.isChecked()){
            rd2.setTextColor(getResources().getColor(R.color.colorPrimary));
        }if(rd3.isChecked()){
            rd3.setTextColor(getResources().getColor(R.color.colorPrimary));
        }if(rd4.isChecked()){
            rd4.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        rd1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(rd1.isChecked()){
                    rd1.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else{
                    rd1.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

        rd2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(rd2.isChecked()){
                    rd2.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else{
                    rd2.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });
        rd3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(rd3.isChecked()){
                    rd3.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else{
                    rd3.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });
        rd4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(rd4.isChecked()){
                    rd4.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else{
                    rd4.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });


        if(rd5.isChecked()){
            rd5.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        if(rd6.isChecked()){
            rd6.setTextColor(getResources().getColor(R.color.colorPrimary));
        }if(rd7.isChecked()){
            rd7.setTextColor(getResources().getColor(R.color.colorPrimary));
        }if(rd8.isChecked()){
            rd8.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        rd5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(rd5.isChecked()){
                    rd5.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else{
                    rd5.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

        rd6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(rd6.isChecked()){
                    rd6.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else{
                    rd6.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });
        rd7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(rd7.isChecked()){
                    rd7.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else{
                    rd7.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });
        rd8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(rd8.isChecked()){
                    rd8.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else{
                    rd8.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });


        if(rd9.isChecked()){
            rd9.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        if(rd10.isChecked()){
            rd10.setTextColor(getResources().getColor(R.color.colorPrimary));
        }if(rd11.isChecked()){
            rd11.setTextColor(getResources().getColor(R.color.colorPrimary));
        }if(rd12.isChecked()){
            rd12.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        rd9.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(rd9.isChecked()){
                    rd9.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else{
                    rd9.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

        rd10.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(rd10.isChecked()){
                    rd10.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else{
                    rd10.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });
        rd11.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(rd11.isChecked()){
                    rd11.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else{
                    rd11.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });
        rd12.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(rd12.isChecked()){
                    rd12.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else{
                    rd12.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

        if(searchTextOfFilterProduct.size() == 0)
        {
            if(ConnectionDetector.isConnectingToInternet(this))
            {
                startService(new Intent(this, ProductNameList.class));
            }
        }

        ArrayAdapter<String> auto_product_adapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item,searchTextOfFilterProduct);
        auto_product.setThreshold(1);
        auto_product.setAdapter(auto_product_adapter);
        auto_product.setTextColor(Color.BLACK);


        linearLayout = (LinearLayout) findViewById(R.id.serchplacelayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterActivity.this, LocationFielterActivity.class);
                startActivity(intent);

            }
        });
        categorylist = (ListView) findViewById(R.id.categorylist);

        String cat = AppPreferences.getCategoryId(FilterActivity.this).replace(" ", "");
        String[] catId = cat.split(",");
        ArrayList<Integer> catIdList = new ArrayList<Integer>();
        for (int i=0; i<catId.length; i++) {
            int catid;
            if (catId[i].equalsIgnoreCase("")) {
                catid = 0;
            }else {
                catid = Integer.parseInt(catId[i]);
            }
            catIdList.add(catid);
        }


        db = new DataBaseHelper(FilterActivity.this);
        ArrayList<Category> lists = db.getCategoryList();
        for (int i = 0; i < lists.size(); i++) {
            Category category = new Category();
            category.setCategory_name(lists.get(i).getCategory_name());
            category.setCategory_id(lists.get(i).getCategory_id());
            if(catIdList.contains(lists.get(i).getCategory_id())){
                Log.d("true","true");
                category.setChkBox(true);
            }else {
                Log.d("false","false");
                category.setChkBox(false);
            }
            al.add(category);
            categoriesid.add(lists.get(i).getCategory_id());
        }

        adapter = new FielterCategoryAdapter(FilterActivity.this,
                R.layout.custom_category, al);
        categorylist.setAdapter(adapter);



    }

        public void setProduct() throws JSONException {

        ArrayAdapter<String> auto_product_adapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item,searchTextOfFilterProduct);
        auto_product.setThreshold(1);
        auto_product.setAdapter(auto_product_adapter);
        auto_product.setTextColor(Color.BLACK);
      }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.nearby_radio_button:
                if (checked)
                    break;
            case R.id.area_radio_button:
                if (checked)
                    break;
            case R.id.city_radio_button:
                if (checked)
                    break;
            case R.id.further_radio_button:
                if (checked)
                    break;
            case R.id.hour_radio_button:
                if (checked)
                    break;
            case R.id.seven_daysradio_button:
                if (checked)
                    break;
            case R.id.thirty_day_radio_button:
                if (checked)
                    break;
            case R.id.all_radio_buuton:
                if (checked)
                    break;
            case R.id.distance_radio_button:
                if (checked)
                    break;
            case R.id.high_radio_button:
                if (checked)
                    break;
            case R.id.low_radio_button:
                if (checked)
                    break;
            case R.id.recently_radio_button:
                if (checked)
                    break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent intent = new Intent(FilterActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        if (id == R.id.action_defaultfielter) {
            AppPreferences.setFiltercategoryid(FilterActivity.this, "");
            AppPreferences.setSearchtext(FilterActivity.this, "");
            AppPreferences.setCategory(FilterActivity.this, "");
            AppPreferences.setFilterdistance(FilterActivity.this, "");
            AppPreferences.setPosttime(FilterActivity.this, "");
            AppPreferences.setSortby(FilterActivity.this, "");
            AppPreferences.setPricefrom(FilterActivity.this, "");
            AppPreferences.setPriceto(FilterActivity.this, "");
            AppPreferences.setCategoryId(FilterActivity.this, "");

            auto_product.setText("");
            rd4.setChecked(true);
            rd4.setTextColor(getResources().getColor(R.color.colorPrimary));
            rd8.setChecked(true);
            rd8.setTextColor(getResources().getColor(R.color.colorPrimary));
            from_edit.setText("");
            to_edit.setText("");
            rd9.setChecked(true);
            rd9.setTextColor(getResources().getColor(R.color.colorPrimary));
            catId.clear();
            catName.clear();
            al.clear();

            ArrayList<Category> lists = db.getCategoryList();
            for (int i = 0; i < lists.size(); i++) {
                Category category = new Category();
                category.setCategory_name(lists.get(i).getCategory_name());
                category.setCategory_id(lists.get(i).getCategory_id());
                category.setChkBox(false);
                al.add(category);
                categoriesid.add(lists.get(i).getCategory_id());
            }
            adapter.notifyDataSetChanged();
            return true;
        }

        if (id == R.id.action_right) {
            AppPreferences.setFilterDatatype(getApplicationContext(), "filter");

            Log.d("catId", catId.toString() + "");
            productname = auto_product.getText().toString().trim();
            place = area_edit.getText().toString().trim();
            pricefrom = from_edit.getText().toString().trim();
            priceto = to_edit.getText().toString().trim();

            if (rd1.isChecked()) {
                distancefielter = "NearBy1km";
                AppPreferences.setFilterdistance(FilterActivity.this,distancefielter);

            } else if (rd2.isChecked()) {
                distancefielter = "5kms";
                AppPreferences.setFilterdistance(FilterActivity.this,distancefielter);

            } else if (rd3.isChecked()) {
                distancefielter = "10kms";
                AppPreferences.setFilterdistance(FilterActivity.this,distancefielter);


            } else if (rd4.isChecked()) {
                distancefielter = "10+kms";
                AppPreferences.setFilterdistance(FilterActivity.this,"");

            }

            if (rd5.isChecked()) {
                listedwithin = "24hour";
                AppPreferences.setPosttime(FilterActivity.this, listedwithin);

            } else if (rd6.isChecked()) {
                listedwithin = "7day";
                AppPreferences.setPosttime(FilterActivity.this,listedwithin);

            } else if (rd7.isChecked()) {
                listedwithin = "30days";
                AppPreferences.setPosttime(FilterActivity.this,listedwithin);

            } else if (rd8.isChecked()) {
                listedwithin = "alllisting";

            }

            if (rd9.isChecked()) {
                sortby = "distance";

            } else if (rd10.isChecked()) {
                sortby = "pricelowtohigh";
                AppPreferences.setSortby(FilterActivity.this, sortby);

            } else if (rd11.isChecked()) {
                sortby = "pricehightolow";
                AppPreferences.setSortby(FilterActivity.this, sortby);

            } else if (rd12.isChecked()) {
                sortby = "mostrecentpublished";
                AppPreferences.setSortby(FilterActivity.this, sortby);

            }

            AppPreferences.setCategory(FilterActivity.this, catName.toString().replace("[", "").replace("]", ""));
            AppPreferences.setCategoryId(FilterActivity.this, catId.toString().replace("[", "").replace("]", ""));
            AppPreferences.setSearchtext(FilterActivity.this, auto_product.getText().toString().trim());
            AppPreferences.setPricefrom(FilterActivity.this, pricefrom);
            AppPreferences.setPriceto(FilterActivity.this, priceto);

//    if(!pricefrom.equalsIgnoreCase("") && !priceto.equalsIgnoreCase("") && ( Double.parseDouble(pricefrom) > Double.parseDouble(priceto)))
//    {
//    }
            Intent intent = new Intent(FilterActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class FielterAsync extends AsyncTask<Void, Void, String> {
        private ProgressDialog mProgressDialog;
        FielterBean fielterBean;
        private JSONObject jsonObj;
        ArrayList<Integer> registrationId;
        private int status = 0;

        public FielterAsync(FielterBean fielterBean) {
            this.fielterBean = fielterBean;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(FilterActivity.this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
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
                System.out.println("fielter value is : ------------ "
                        + AppConstants.FIELTER);
                HttpPost httppost = new HttpPost(AppConstants.FIELTER);
                jsonObj = new JSONObject();
                jsonObj.put("search_text", fielterBean.getProname());
                jsonObj.put("latitude", AppPreferences.getLatutude(getApplicationContext()));
                jsonObj.put("longitude", AppPreferences.getLongitude(getApplicationContext()));
                jsonObj.put("place", fielterBean.getPlace());
                jsonObj.put("categoryid", catId.toString().replace("[","").replace("]",""));
               // jsonObj.put("categoryid", catId);
                jsonObj.put("distance", fielterBean.getDistance());
                jsonObj.put("postTime", fielterBean.getListedwithin());
                jsonObj.put("priceFrom", fielterBean.getPricefrom());
                jsonObj.put("priceTo", fielterBean.getPriceto());
                jsonObj.put("sortby", fielterBean.getSortby());
                jsonObj.put("customerid",
                        AppPreferences.getCustomerId(getApplicationContext()));

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

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
                String jsonString = "";
                try {
                    jsonString = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("JSONString response is : " + jsonString);
                if (jsonString != null) {
                    if (jsonString.contains("result")) {
                        JSONObject jsonObj = new JSONObject(jsonString);
                        jsonString = jsonObj.getString("result");
                        // JSONArray jsonChildArray = jsonObj
                        // .getJSONArray("result");
                        if (jsonObj.getString("status").equalsIgnoreCase("200")) {
                            System.out
                                    .println("--------- message 200 got ----------");
                            status = 200;
                            return jsonString;
                        } else if (jsonObj.getString("status")
                                .equalsIgnoreCase("404")) {
                            status = 404;
                            return jsonString;
                        } else if (jsonObj.getString("status")
                                .equalsIgnoreCase("500")) {
                            status = 500;
                            return jsonString;
                        }
                    }
                }

            } catch (ConnectTimeoutException e) {
                System.out.println("Time out");
                status = 600;
            } catch (SocketTimeoutException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            Log.d("status", status + "");
            if (status == 200) {


                Toast.makeText(FilterActivity.this,
                        "Filter Is Apply Successfully...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(FilterActivity.this,
                        HomeActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(FilterActivity.this,
                        "Plz Try Again Later", Toast.LENGTH_LONG).show();

            }
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        if(!AppPreferences.getSearchAddress(FilterActivity.this).equalsIgnoreCase("")){
            area_edit.setText(AppPreferences.getSearchAddress(FilterActivity.this));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        filterActivityInstance =  null;
    }
}
