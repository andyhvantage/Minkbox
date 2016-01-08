package com.minkbox;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.minkbox.adapter.FavorityCategoryAdapter;
import com.minkbox.databace.DataBaseHelper;
import com.minkbox.model.Category;
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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class FavouriteCategoryActivity extends AppCompatActivity {
    Toolbar toolbar;
    ListView categorylist;
    ArrayList<Integer> categoriesid = new ArrayList<Integer>();
    ArrayList<Category> al = new ArrayList<Category>();
    public static ArrayList<String> catId =new ArrayList<String>();
    public static ArrayList<String> catName = new ArrayList<String>();
    public int categoryid = 0;
    DataBaseHelper db;
    CheckBox checkBox;
    String categorycheck;
    FavorityCategoryAdapter adapter;
    public String categoryName = null;

      public  static boolean firstTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_category);

               firstTime = true;
        System.out.println("fav category-------------" + AppPreferences.getUserfavoritecategory(FavouriteCategoryActivity.this));

        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = getString(R.string.app_name);
        title = getString(R.string.title_activity_favorite);
        getSupportActionBar().setTitle(title);


        String cat = AppPreferences.getUserfavoritecategory(FavouriteCategoryActivity.this).replace(" ", "");
        String[] catId_str_array = cat.split(",");
        categorylist = (ListView) findViewById(R.id.categorylist);
        ArrayList<Integer> catIdList = new ArrayList<Integer>();
        for (int i=0; i<catId_str_array.length; i++) {
            int catid;
            if (catId_str_array[i].equalsIgnoreCase("")) {
                catid = 0;
            }else {
                catid = Integer.parseInt(catId_str_array[i]);
                System.out.println("category--------"+catid);
            }
            catIdList.add(catid);
        }


        db = new DataBaseHelper(FavouriteCategoryActivity.this);
        ArrayList<Category> lists = db.getCategoryList();
        for (int i = 0; i < lists.size(); i++) {
            Category category = new Category();
            category.setCategory_name(lists.get(i).getCategory_name());
            category.setCategory_id(lists.get(i).getCategory_id());
            if(catIdList.contains(lists.get(i).getCategory_id())){
                Log.d("true","true");
                category.setChkBox(true);
                catId.add(String.valueOf(lists.get(i).getCategory_id()));
            }else {
                Log.d("false","false");
                category.setChkBox(false);
            }
            al.add(category);
            categoriesid.add(lists.get(i).getCategory_id());
        }

        adapter = new FavorityCategoryAdapter(FavouriteCategoryActivity.this,
                R.layout.custom_category, al);
        categorylist.setAdapter(adapter);




   }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favourite_category, menu);
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

        if (id == R.id.action_right) {

            Log.d("catId", catId.toString() + "");

            System.out.println("cat id1-------------" + AppPreferences.getUserfavoritecategory(FavouriteCategoryActivity.this));
            System.out.println("cat id2-------------" + catId.toString().replace("[", "").replace("]", ""));

            AppPreferences.setUserfavoritecategory(FavouriteCategoryActivity.this, catId.toString().replace("[", "").replace("]", ""));
         //   AppPreferences.setUserfavoritecategoryname(FavouriteCategoryActivity.this,catName.toString().replace("[", "").replace("]", ""));
            System.out.println("prefrence id-------------" + AppPreferences.getUserfavoritecategory(FavouriteCategoryActivity.this));
            System.out.println("selected id-------------" + catId.toString().replace("[", "").replace("]", ""));

            new FavoritecategoryAsync(FavouriteCategoryActivity.this).execute();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class FavoritecategoryAsync extends AsyncTask<Void, Void, String> {
        private ProgressDialog mProgressDialog;
        private JSONObject jsonObj;
        ArrayList<Integer> registrationId;
        private int status = 0;
        Context context;
        String cateId;

        public FavoritecategoryAsync(Context con) {
            this.context = con;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(FavouriteCategoryActivity.this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                System.out.println("************ favorite category json*****************");
                HttpParams httpParameters = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParameters,
                        AppConstants.NETWORK_TIMEOUT_CONSTANT);
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        AppConstants.NETWORK_CONNECTION_TIMEOUT_CONSTANT);
                HttpConnectionParams.setSoTimeout(httpParameters,
                        AppConstants.NETWORK_SOCKET_TIMEOUT_CONSTANT);

                HttpClient httpclient = new DefaultHttpClient();
                System.out.println("email value : ------------ "
                        + AppConstants.FAVORITEGETEGORY);
                HttpPost httppost = new HttpPost(AppConstants.FAVORITEGETEGORY);
                jsonObj = new JSONObject();
//                jsonObj.put("categoryid", catId);

                jsonObj.put("categoryId", AppPreferences.getUserfavoritecategory(FavouriteCategoryActivity.this));
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


                            cateId = jsonObj.getString("categoryId");


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
//
//            System.out.println("cat id list is :-------------- " + catId.toString());
//            AppPreferences.setUserfavoritecategory(getApplicationContext(), catId.toString().replace("[","").replace("]",""));
//            if (catId != null && catId.size() > 0) {
//                catId.clear();
//            }
            Log.d("status", status + "");
            if (status == 200) {
                AppPreferences.setUserfavoritecategory(FavouriteCategoryActivity.this, cateId);
                Toast.makeText(FavouriteCategoryActivity.this,
                        "Favorite Category Added Successfully...", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(FavouriteCategoryActivity.this,
//                        EditProfile.class);
//              //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
                finish();
            } else {
                Toast.makeText(FavouriteCategoryActivity.this,
                        "Error Occured Failed..", Toast.LENGTH_LONG).show();

            }
        }

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        catId.clear();
    }
}
