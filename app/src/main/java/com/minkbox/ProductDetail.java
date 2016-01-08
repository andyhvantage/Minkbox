package com.minkbox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.manuelpeinado.fadingactionbar.FadingActionBarHelper;
import com.minkbox.adapter.CirclePageIndicator;
import com.minkbox.adapter.ImageAdapterGallery;
import com.minkbox.databace.DataBaseHelper;
import com.minkbox.model.Product;
import com.minkbox.network.DeleteTask;
import com.minkbox.network.LikeTask;
import com.minkbox.network.ReserveTask;
import com.minkbox.utils.AppConstants;
import com.minkbox.utils.AppPreferences;
import com.minkbox.utils.ConnectionDetector;
import com.minkbox.utils.DialogManager;
import com.nostra13.universalimageloader.core.ImageLoader;

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
import java.util.Timer;
import java.util.TimerTask;

public class ProductDetail extends Activity{

    TextView product_cost;
    TextView product_title_text;
    TextView product_description_text;
    TextView product_total_review;
    public static TextView product_total_like;
    TextView marker_place;
    //ImageView map_snap_shot;
    TextView user_name;
    TextView user_profile_reviews;
    ImageView user_profile;
    Button reserve;
    Button unReserve;
    Button sold_button;
    ImageView soldText;
    ImageView reserveText;
    LinearLayout own_product;
    LinearLayout other_product;
    RelativeLayout other_profile_layout, locationUpdate;
    String reserveStatus;
    Toolbar toolbar;
    DataBaseHelper db;
    Bitmap mapicon;
    Bitmap userProfileicon;
    public static ProductDetail productDetailInstance = null;
    DialogManager alert = new DialogManager();
    String resultnull;
    int product_id;
    RelativeLayout owner_profile;
    boolean ownProductFlag = false;
    ArrayList<String> imagelist = new ArrayList<String>();
    private static int page = 0;
    private Timer timer;
    private CirclePageIndicator mIndicator;
    EditText offerRate;
    EditText offerComment;
    TextView offerRateCurrency;
    private ImageAdapterGallery adapter;
    private ViewPager viewPager;
    RelativeLayout chat;
    RelativeLayout makeOffer;
    RelativeLayout wantComment;
    LinearLayout offerRatelin;
    TextView titleOffer;
    TextView titleWant;
    Dialog dialog;
    public static ImageView likeButton, unLikeButton;
    String fileName = "female.png";
    ImageView btn,btngplus,btntwit,btnwtsup;
    String externalStorageDirectory = Environment.getExternalStorageDirectory().toString();
    String myDir = externalStorageDirectory + "/saved_images/"; // the
    Uri uri = Uri.parse("android.resource://com.minkbox/drawable/ic_launcher");//"file:/storage/emulated/0/download/female.png" + myDir + fileName);
    FloatingActionButton productZoomFloatButton, reportProductFloatButton;

    MapFragment mapFragment;
    GoogleMap googleMap;
    LatLng latlong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FadingActionBarHelper helper = new FadingActionBarHelper()
                .actionBarBackground(R.drawable.ab_background).lightActionBar(false)
                .headerLayout(R.layout.header)
                .contentLayout(R.layout.activity_product_all_details);
        setContentView(helper.createView(this));
        System.out.println("helper : ---------- " + helper);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayUseLogoEnabled(false);
        helper.initActionBar(this);

        Bundle extras = getIntent().getExtras();
        product_id = extras.getInt("productid");

        db = new DataBaseHelper(getApplicationContext());

        if ((ConnectionDetector.isConnectingToInternet(ProductDetail.this))) {
            new GetHomeProductDetailsTask(ProductDetail.this, product_id).execute(AppConstants.GETALLPOST);
        } else {
            alert.showAlertDialog(ProductDetail.this, "Alert!", "Please, check your internet connection.", false);
        }
        productDetailInstance = this;

        sold_button = (Button) findViewById(R.id.sold_button);
        reserve = (Button) findViewById(R.id.reservsed_button);
        unReserve = (Button) findViewById(R.id.unreservsed_button);
        soldText = (ImageView) findViewById(R.id.soldtext);
        reserveText = (ImageView) findViewById(R.id.reservetext);
        own_product = (LinearLayout) findViewById(R.id.own_product_lin);
        other_product = (LinearLayout) findViewById(R.id.other_product_lin);
        other_profile_layout = (RelativeLayout) findViewById(R.id.other_user_profile_rel);
        marker_place = (TextView) findViewById(R.id.marker_place);
        user_name = (TextView) findViewById(R.id.user_name);
        user_profile_reviews = (TextView) findViewById(R.id.reviews);
        owner_profile = (RelativeLayout) findViewById(R.id.other_user_profile_rel);
        locationUpdate = (RelativeLayout) findViewById(R.id.map_lay);
        //map_snap_shot = (ImageView) findViewById(R.id.map);
        user_profile = (ImageView) findViewById(R.id.user_profile);
        product_cost = (TextView) findViewById(R.id.product_cost);
        product_title_text = (TextView) findViewById(R.id.product_name);
        product_description_text = (TextView) findViewById(R.id.description);
        product_total_review = (TextView) findViewById(R.id.total_views);
        product_total_like = (TextView) findViewById(R.id.total_rates);
        chat = (RelativeLayout) findViewById(R.id.chat);
        makeOffer = (RelativeLayout) findViewById(R.id.offer);
        wantComment = (RelativeLayout) findViewById(R.id.want);


        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reserveStatus = "1";

                if ((ConnectionDetector.isConnectingToInternet(ProductDetail.this))) {
                    new ReserveTask(ProductDetail.this, reserveStatus, product_id).execute();
                } else {
                    alert.showAlertDialog(ProductDetail.this, "Alert!", "Please, check your internet connection.", false);
                }
            }
        });
        unReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reserveStatus = "0";

                if ((ConnectionDetector.isConnectingToInternet(ProductDetail.this))) {
                    new ReserveTask(ProductDetail.this, reserveStatus, product_id).execute();
                } else {
                    alert.showAlertDialog(ProductDetail.this, "Alert!", "Please, check your internet connection.", false);
                }
            }
        });

        sold_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductDetail.this, SoldApiActivity.class);
                i.putExtra("productid", product_id);
                //  i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        productZoomFloatButton = (FloatingActionButton) findViewById(R.id.product_zoom_float_button);

        final FloatingActionButton shareFloatButton = (FloatingActionButton) findViewById(R.id.share_float_button);
        shareFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Install this app :- ");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, AppConstants.APPSTOREURL);
                startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.app_name)));
            }
        });

        reportProductFloatButton = (FloatingActionButton) findViewById(R.id.report_this_product_float_button);

        likeButton = (ImageView)findViewById(R.id.button_like_image);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((ConnectionDetector.isConnectingToInternet(ProductDetail.this))) {
                new LikeTask(ProductDetail.this, "1", product_id).execute();
                } else {
                    alert.showAlertDialog(ProductDetail.this, "Alert!", "Please, check your internet connection.", false);
                }
            }
        });

        unLikeButton = (ImageView)findViewById(R.id.button_unlike_image);
        unLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((ConnectionDetector.isConnectingToInternet(ProductDetail.this))) {
                    new LikeTask(ProductDetail.this, "0", product_id).execute();
                } else {
                    alert.showAlertDialog(ProductDetail.this, "Alert!", "Please, check your internet connection.", false);
                }
            }
        });

        btn = (ImageView) findViewById(R.id.button_share);
        btn.getAdjustViewBounds();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String urlToShare = "APPSHAREURL";
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, AppConstants.APPSHAREURL);
                // See if official Facebook app is found
                boolean facebookAppFound = false;
                List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
                for (ResolveInfo info : matches) {
                    if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook")) {
                        intent.setPackage(info.activityInfo.packageName);
                        facebookAppFound = true;
                        break;
                    }
                }
                //If facebook app not found, load sharer.php in a browser
                if (!facebookAppFound) {
                    String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + AppConstants.APPSHAREURL;
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
                }
                startActivity(intent);
            }
        });
        btngplus=(ImageView)findViewById(R.id.button_share2);
        btngplus.getAdjustViewBounds();
        btngplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                String[] recipients={AppPreferences.getId(getApplicationContext())};
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT,"5kmx App");
                intent.putExtra(Intent.EXTRA_TEXT,"Found this app\n"+ AppConstants.APPSHAREURL);
                intent.setType("text/html");
                startActivity(Intent.createChooser(intent, "Send mail"));
            }
        });
        btntwit=(ImageView)findViewById(R.id.button_share3);
        btntwit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweetUrl = "https://twitter.com/intent/tweet?text= "+ AppConstants.APPSHARETEXT +" &url="
                        + AppConstants.APPSHAREURL;
                Uri uri = Uri.parse(tweetUrl);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        btnwtsup=(ImageView)findViewById(R.id.button_share4);
        btnwtsup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType("image/png");
                whatsappIntent.setPackage("com.whatsapp");
                Uri uri = Uri.parse("android.resource://com.minkbox/" + R.drawable.ic_launcher);
                whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.thousand.legs&hl=en");
                try {
                    productDetailInstance.startActivity(whatsappIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), "Whatsup not installed on your device", Toast.LENGTH_LONG).show();
                }
            }
        });

        if(AppPreferences.getCustomerId(getApplicationContext()) == 0)
        {
            ProductDetail.likeButton.setVisibility(View.GONE);
            ProductDetail.unLikeButton.setVisibility(View.GONE);
        }else
        {
            ProductDetail.unLikeButton.setVisibility(View.GONE);
        }
    }


    public class GetHomeProductDetailsTask extends AsyncTask<String, Void, String> {

        Context context;
        private String networkFlag = "false", lat, lng;
        private JSONObject jsonObj;
        private String TAG = GetHomeProductDetailsTask.class.getSimpleName();
        private ProgressDialog mProgressDialog;
        private int status = 0;
        int productid;
        Product product;
        String product_total_reviews;
        String product_total_likes;

        ArrayList<Product> productlist = new ArrayList<Product>();

        public GetHomeProductDetailsTask(Context con, int productId) {
            this.context = con;
            this.productid = productId;
            mProgressDialog = new ProgressDialog(con);
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
                System.out.println("show all post url ---------------- " + AppConstants.PRODUCTALLDETAIL);
                HttpPost httppost = new HttpPost(AppConstants.PRODUCTALLDETAIL);
                jsonObj = new JSONObject();

                jsonObj.put("customer_id", AppPreferences.getCustomerId(ProductDetail.this));
                jsonObj.put("product_id", productid);

                System.out.println("customer_id" + AppPreferences.getCustomerId(ProductDetail.this));
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
                                        int product_category_id = Integer.parseInt(jsonchildObj.getString("category_id"));
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
                                        product_total_reviews = jsonchildObj.getString("total_reviews");
                                        product_total_likes = jsonchildObj.getString("total_likes");
                                        String product_address = jsonchildObj.getString("product_address");
                                        String product_pincode = jsonchildObj.getString("product_pincode");

                                        String reserve_status = jsonchildObj.getString("product_reserve_status");
                                        String sold_status = jsonchildObj.getString("product_sold_status");
                                        String like_status = jsonchildObj.getString("product_like_status");
                                        String product_status = jsonchildObj.getString("product_status");
                                        int owner_id = Integer.parseInt(jsonchildObj.getString("product_owner_id"));
                                        String owner_name = jsonchildObj.getString("product_owner_name");
                                        String owner_profile_pic = jsonchildObj.getString("product_owner_profile_pic");
                                        String owner_profile_review = jsonchildObj.getString("product_owner_profile_reviews");
                                        System.out.print("owner review------------------------------" + owner_profile_review);

                                        product = new Product();
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
                                        product.setProduct_city(product_city);
                                        product.setProduct_total_reviews(product_total_reviews);
                                        product.setProduct_total_likes(product_total_likes);
                                        product.setProduct_address(product_address);
                                        product.setProduct_pincode(product_pincode);
                                        product.setProduct_reserve_status(reserve_status);

                                        product.setProduct_sold_status(sold_status);
                                        product.setProduct_like_status(like_status);
                                        product.setProduct_status(product_status);
                                        product.setProduct_owner_id(owner_id);
                                        product.setProduct_owner_name(owner_name);
                                        product.setProduct_owner_profile_pic(owner_profile_pic);
                                        product.setProduct_owner_profile_reviews(owner_profile_review);
                                        System.out.print("reserve---------------------" + reserve_status);
                                        productlist.add(product);
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
                        product_total_like.setText(product_total_likes);
                        product_total_review.setText(product_total_reviews);

                        product_cost.setText(product.getProduct_price_currency()+" "+product.getProduct_price());
                        product_title_text.setText(product.getProduct_title());
                        product_description_text.setText(product.getProduct_description());
                        product_total_review.setText(product.getProduct_total_reviews());
                        product_total_like.setText(product.getProduct_total_likes());
                        marker_place.setText(product.getProduct_city() + " " + product.getProduct_pincode());

                        latlong = new LatLng(Double.parseDouble(product.getProduct_latitude()), Double.parseDouble(product.getProduct_longitude()));
                        initilizeMap();

                        locationUpdate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (product.getProduct_status().equalsIgnoreCase("own")) {
                                    Intent i = new Intent(ProductDetail.this, MyProductLocationUpdate.class);
                                    i.putExtra("productid", product_id);
                                    startActivity(i);
                                }
                                if (product.getProduct_status().equalsIgnoreCase("another")) {
                                    Intent i = new Intent(ProductDetail.this, LocationMap.class);
                                    i.putExtra("productid", product_id);
                                    startActivity(i);
                                }
                            }
                        });

                        if (product.getProduct_status().equalsIgnoreCase("own")) {
                            ownProductFlag = true;

                            if (product.getProduct_sold_status().equalsIgnoreCase("1")) {
                                soldText.setVisibility(View.VISIBLE);
                                own_product.setVisibility(View.GONE);
                                other_product.setVisibility(View.GONE);
                                other_profile_layout.setVisibility(View.GONE);
                                reserveText.setVisibility(View.GONE);

                            } else {
                                own_product.setVisibility(View.VISIBLE);
                                other_product.setVisibility(View.GONE);
                                other_profile_layout.setVisibility(View.GONE);
                                soldText.setVisibility(View.GONE);
                                if ("0".equalsIgnoreCase(product.getProduct_reserve_status())) {
                                    reserveText.setVisibility(View.GONE);
                                    reserve.setVisibility(View.VISIBLE);
                                    unReserve.setVisibility(View.GONE);
                                } else {
                                    reserveText.setVisibility(View.VISIBLE);
                                    reserve.setVisibility(View.GONE);
                                    unReserve.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        if(AppPreferences.getCustomerId(getApplicationContext()) != 0)
                        {
                            if ("1".equalsIgnoreCase(product.getProduct_like_status())) {
                                unLikeButton.setVisibility(View.VISIBLE);
                                likeButton.setVisibility(View.GONE);
                            } else {
                                likeButton.setVisibility(View.VISIBLE);
                                unLikeButton.setVisibility(View.GONE);
                            }
                        }else
                        {
                            unLikeButton.setVisibility(View.GONE);
                            likeButton.setVisibility(View.GONE);
                        }


                        if (product.getProduct_status().equalsIgnoreCase("another")) {
                            ownProductFlag = false;

                            other_profile_layout.setVisibility(View.VISIBLE);
                            user_name.setText(product.getProduct_owner_name());
                            user_profile_reviews.setText(product.getProduct_owner_profile_reviews());

                            if (product.getProduct_owner_profile_pic().equalsIgnoreCase("")) {
                                userProfileicon = BitmapFactory.decodeResource(getResources(), R.drawable.def_user_img);
                                user_profile.setImageBitmap(userProfileicon);
                            } else {
                                ImageLoader.getInstance().displayImage(product.getProduct_owner_profile_pic(),
                                        user_profile);
                            }

                            if (product.getProduct_sold_status().equalsIgnoreCase("1")) {

                                soldText.setVisibility(View.VISIBLE);
                                own_product.setVisibility(View.GONE);
                                other_product.setVisibility(View.GONE);
                                reserveText.setVisibility(View.GONE);

                            } else {
                                own_product.setVisibility(View.GONE);
                                other_product.setVisibility(View.VISIBLE);
                                soldText.setVisibility(View.GONE);
                                if ("0".equalsIgnoreCase(product.getProduct_reserve_status())) {
                                    reserveText.setVisibility(View.GONE);
                                } else {
                                    reserveText.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        if(ownProductFlag)
                        {
                            productZoomFloatButton.setTitle("Edit");
                            reportProductFloatButton.setTitle("Delete Product");
                        }
                        productZoomFloatButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(ownProductFlag)
                                {
                                    Intent i = new Intent(ProductDetail.this, UpdateProduct.class);
                                    i.putExtra("productid", product_id);
                                    startActivity(i);
                                }else
                                {
                                    Intent i = new Intent(ProductDetail.this, FullScreenProductImageSlider.class);
                                    i.putExtra("productid", product_id);
                                    startActivity(i);
                                }
                            }
                        });

                        reportProductFloatButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(ownProductFlag) {
                                    if ((ConnectionDetector.isConnectingToInternet(ProductDetail.this))) {
                                        new DeleteTask(ProductDetail.this, product_id).execute();
                                    } else {
                                        alert.showAlertDialog(ProductDetail.this, "Alert!", "Please, check your internet connection.", false);
                                    }
                                }else{
                                    if (AppPreferences.getId(ProductDetail.this).equalsIgnoreCase("")) {
                                        Intent i = new Intent(ProductDetail.this, UserRegisterLoginActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                    } else {
                                        Intent i = new Intent(ProductDetail.this, ReportThisProduct.class);
                                        i.putExtra("productid", product_id);
                                        startActivity(i);
                                    }
                                }
                            }
                        });

                        owner_profile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(ProductDetail.this, UserProfile.class);
                                i.putExtra("customerid", product.getProduct_owner_id());
                                System.out.print("owner id------------------------------" + product.getProduct_owner_id());
                                startActivity(i);
                            }
                        });

                        if(!product.getProduct_image1().equalsIgnoreCase("")){
                            imagelist.add(product.getProduct_image1());
//                            shareImageLink = product.getProduct_image1();
                        }
                        if(!product.getProduct_image2().equalsIgnoreCase("")){
                            imagelist.add(product.getProduct_image2());
//                            if(shareImageLink.equalsIgnoreCase(""))
//                            {
//                                shareImageLink = product.getProduct_image2();
//                            }
                        }
                        if(!product.getProduct_image3().equalsIgnoreCase("")){
                            imagelist.add(product.getProduct_image3());
                        }
                        if(!product.getProduct_image4().equalsIgnoreCase("")){
                            imagelist.add(product.getProduct_image4());
                        }
                        viewPager = (ViewPager) findViewById(R.id.pager);
                        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
                        adapter = new ImageAdapterGallery(productDetailInstance, imagelist);

                        if(imagelist.size()==1){
                            mIndicator.setVisibility(View.GONE);
                        }

                        viewPager.setAdapter(adapter);
                        mIndicator.setViewPager(viewPager);
                        pageSwitcher(10);

                        chat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(!AppPreferences.getId(ProductDetail.this).equalsIgnoreCase("")) {
                                    if (!AppPreferences.getDeviceid(ProductDetail.this).equalsIgnoreCase("")) {
                                        Intent i = new Intent(ProductDetail.this, ChatActivity.class);
                                        i.setAction("productDetail");
                                        i.putExtra("productId", productlist.get(0).getProduct_server_id());
                                        i.putExtra("productOwnerId", productlist.get(0).getProduct_owner_id());
                                        //i.putExtra("productRecieverName", productlist.get(0).getProduct_);
                                        i.putExtra("productName", productlist.get(0).getProduct_title());
                                        i.putExtra("productPrice", productlist.get(0).getProduct_price());
                                        i.putExtra("productOwnerName", productlist.get(0).getProduct_owner_name());
                                        i.putExtra("ProductImage", productlist.get(0).getProduct_image1());
                                        i.putExtra("ProfileImage", productlist.get(0).getProduct_owner_profile_pic());
                                        startActivity(i);
                                    } else {

                                    }
                                }else{
                                    Toast.makeText(ProductDetail.this,"Sorry, chat is unavailable right now. Please try again later", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        makeOffer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (!AppPreferences.getId(ProductDetail.this).equalsIgnoreCase("")) {

                                    if (!AppPreferences.getDeviceid(ProductDetail.this).equalsIgnoreCase("")) {
                                        dialog = new Dialog(ProductDetail.this);
                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                                        dialog.setContentView(R.layout.make_an_offer);
                                        offerRate = (EditText) dialog.findViewById(R.id.offer_rate);
                                        offerComment = (EditText) dialog.findViewById(R.id.make_offer_comment);
                                        offerRateCurrency = (TextView) dialog.findViewById(R.id.currnecy);
                                        offerRateCurrency.setText(product.getProduct_price_currency());
                                        titleOffer = (TextView) dialog.findViewById(R.id.title);
                                        titleWant = (TextView) dialog.findViewById(R.id.title_want);
                                        offerRatelin = (LinearLayout) dialog.findViewById(R.id.offer_rate_lin);

                                        titleOffer.setVisibility(View.VISIBLE);
                                        titleWant.setVisibility(View.GONE);
                                        offerRatelin.setVisibility(View.VISIBLE);
                                        Button send = (Button) dialog.findViewById(R.id.ok);
                                        send.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (TextUtils.isEmpty(offerRate.getText().toString())) {
                                                    offerRate.setError(getString(R.string.error_field_required));
                                                } else if (TextUtils.isEmpty(offerComment.getText().toString())) {
                                                    offerComment.setError(getString(R.string.error_field_required));
                                                } else {
                                                    if(!AppPreferences.getId(ProductDetail.this).equalsIgnoreCase("")) {

                                                        if (!AppPreferences.getDeviceid(ProductDetail.this).equalsIgnoreCase("")) {
                                                            Intent i = new Intent(ProductDetail.this, ChatActivity.class);
                                                            i.setAction("productDetail_MakeAnOffer");
                                                            i.putExtra("productId", productlist.get(0).getProduct_server_id());
                                                            i.putExtra("productOwnerId", productlist.get(0).getProduct_owner_id());
                                                            i.putExtra("productName", productlist.get(0).getProduct_title());
                                                            i.putExtra("productPrice", productlist.get(0).getProduct_price());
                                                            i.putExtra("productOwnerName", productlist.get(0).getProduct_owner_name());
                                                            i.putExtra("offerRate", "Rs. "+offerRate.getText().toString()+" New Offer");
                                                            i.putExtra("offerComment", offerComment.getText().toString());
                                                            i.putExtra("ProductImage", productlist.get(0).getProduct_image1());
                                                            i.putExtra("ProfileImage", productlist.get(0).getProduct_owner_profile_pic());
                                                            startActivity(i);
                                                        } else {

                                                        }
                                                    }else{
                                                        Toast.makeText(ProductDetail.this,"Sorry, chat is unavailable right now. Please try again later", Toast.LENGTH_LONG).show();
                                                    }                                            dialog.dismiss();
                                                }
                                            }
                                        });
                                        Button cancleButton = (Button) dialog.findViewById(R.id.cancle);
                                        cancleButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                dialog.dismiss();
                                            }
                                        });
                                        dialog.show();
                                    }
                                } else {
                                    Toast.makeText(ProductDetail.this, "Sorry, chat is unavailable right now. Please try again later", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        wantComment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (!AppPreferences.getId(ProductDetail.this).equalsIgnoreCase("")) {

                                    if (!AppPreferences.getDeviceid(ProductDetail.this).equalsIgnoreCase("")) {
                                        dialog = new Dialog(ProductDetail.this);
                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                                        dialog.setContentView(R.layout.make_an_offer);
                                        offerRate = (EditText) dialog.findViewById(R.id.offer_rate);
                                        offerComment = (EditText) dialog.findViewById(R.id.make_offer_comment);

                                        titleOffer = (TextView) dialog.findViewById(R.id.title);
                                        titleWant = (TextView) dialog.findViewById(R.id.title_want);
                                        offerRatelin = (LinearLayout) dialog.findViewById(R.id.offer_rate_lin);

                                        titleOffer.setVisibility(View.GONE);
                                        titleWant.setVisibility(View.VISIBLE);
                                        offerRatelin.setVisibility(View.GONE);
                                        Button send = (Button) dialog.findViewById(R.id.ok);
                                        send.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (TextUtils.isEmpty(offerComment.getText().toString())) {
                                                    offerComment.setError(getString(R.string.error_field_required));
                                                } else {
                                                    if(!AppPreferences.getId(ProductDetail.this).equalsIgnoreCase("")) {

                                                        if (!AppPreferences.getDeviceid(ProductDetail.this).equalsIgnoreCase("")) {
                                                            Intent i = new Intent(ProductDetail.this, ChatActivity.class);
                                                            i.setAction("productDetail_MakeAnOffer");
                                                            i.putExtra("productId", productlist.get(0).getProduct_server_id());
                                                            i.putExtra("productOwnerId", productlist.get(0).getProduct_owner_id());
                                                            i.putExtra("productName", productlist.get(0).getProduct_title());
                                                            i.putExtra("productPrice", productlist.get(0).getProduct_price());
                                                            i.putExtra("productOwnerName", productlist.get(0).getProduct_owner_name());
                                                            i.putExtra("offerRate", "I want it");
                                                            i.putExtra("offerComment", offerComment.getText().toString());
                                                            i.putExtra("ProductImage", productlist.get(0).getProduct_image1());
                                                            i.putExtra("ProfileImage", productlist.get(0).getProduct_owner_profile_pic());
                                                            startActivity(i);
                                                        } else {

                                                        }
                                                    }else{
                                                        Toast.makeText(ProductDetail.this,"Sorry, chat is unavailable right now. Please try again later", Toast.LENGTH_LONG).show();
                                                    }                                            dialog.dismiss();
                                                }
                                            }
                                        });
                                        Button cancleButton = (Button) dialog.findViewById(R.id.cancle);
                                        cancleButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                dialog.dismiss();
                                            }
                                        });
                                        dialog.show();
                                    }
                                } else {
                                    Toast.makeText(ProductDetail.this, "Sorry, chat is unavailable right now. Please try again later", Toast.LENGTH_LONG).show();
                                }

                            }
                        });

                        if (resultnull != null && resultnull.equalsIgnoreCase("")) {
                            Toast.makeText(ProductDetail.this, "No data found", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                    }
                } else if (status == 400) {
                    mProgressDialog.dismiss();
                    Toast.makeText(ProductDetail.this, "data not found", Toast.LENGTH_LONG).show();
                } else if (status == 404) {
                    Toast.makeText(ProductDetail.this, "Check Username / Password", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
            }
            if(AppPreferences.getCustomerId(getApplicationContext()) == 0)
            {
                System.out.println("customer id is : --------------- " + AppPreferences.getCustomerId(getApplicationContext()));
                ProductDetail.likeButton.setVisibility(View.GONE);
                ProductDetail.unLikeButton.setVisibility(View.GONE);
            }
        }
    }

    private void initilizeMap() {
        try {
            Log.v("map", "---     initialize Map called    ---" + googleMap);
            if (googleMap == null) {
                mapFragment = ((MapFragment) getFragmentManager()
                        .findFragmentById(R.id.map));
                googleMap = mapFragment.getMap();
                this.googleMap.getUiSettings();



                googleMap.animateCamera(CameraUpdateFactory
                        .newLatLngZoom(latlong, 15.0f));

                googleMap
                        .addMarker(new MarkerOptions()
                                .position(latlong)
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.address_marker)));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_product_all_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }

        if (id == R.id.action_edit) {
            Intent i = new Intent(ProductDetail.this, UpdateProduct.class);
            i.putExtra("productid", product_id);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

        if (id == R.id.action_Delete) {
            if ((ConnectionDetector.isConnectingToInternet(ProductDetail.this))) {
                new DeleteTask(ProductDetail.this, product_id).execute();
            } else {
                alert.showAlertDialog(ProductDetail.this, "Alert!", "Please, check your internet connection.", false);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void reserve() {
        reserve.setVisibility(View.GONE);
        unReserve.setVisibility(View.VISIBLE);
        reserveText.setVisibility(View.VISIBLE);
        System.out.print("reserve product------------");
    }

    public void unreserve() {
        reserve.setVisibility(View.VISIBLE);
        unReserve.setVisibility(View.GONE);
        reserveText.setVisibility(View.GONE);
        System.out.print("unreserve product------------");
    }

    public void sold() {

        reserveText.setVisibility(View.GONE);
        soldText.setVisibility(View.VISIBLE);
        own_product.setVisibility(View.GONE);
        other_product.setVisibility(View.GONE);
        other_profile_layout.setVisibility(View.GONE);
    }

    public void like() {
                                        Toast.makeText(getApplicationContext(), "image click----------------", Toast.LENGTH_LONG).show();
//        like.setVisible(false);
//        unlike.setVisible(true);
        unLikeButton.setVisibility(View.VISIBLE);
        likeButton.setVisibility(View.GONE);
    }

    public void unlike() {
//        Toast.makeText(getApplicationContext(), "image click----------------", Toast.LENGTH_LONG).show();
        likeButton.setVisibility(View.VISIBLE);
        unLikeButton.setVisibility(View.GONE);
    }

    public void pageSwitcher(int seconds) {
        timer = new Timer(); // At this line a new Thread will be created
        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 200); // delay
    }

    // this is an inner class...
    class RemindTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                    if(page <= adapter.getCount()-1)
                    {
                        viewPager.setCurrentItem(page, true);
                        page++;
                    }else if(page >= adapter.getCount()-1){
                        page = 0;
                        viewPager.setCurrentItem(page, true);
                    }
                }
            });
        }
    }

    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        productDetailInstance = null;
//        shareImageLink = "";
    }
}
