package com.minkbox;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
//import com.minkbox.network.MakeAnOfferTask;
import com.minkbox.network.ReserveTask;
//import com.minkbox.network.WantItTask;
import com.minkbox.utils.AppConstants;
import com.minkbox.utils.AppPreferences;
import com.minkbox.utils.ConnectionDetector;
import com.minkbox.utils.DialogManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class ProductAllDetails extends Activity {

    String product_status;
    TextView product_cost;
    TextView product_title;
    TextView product_description;
    TextView product_total_review;
    TextView product_total_like;
    RelativeLayout owner_profile, locationUpdate;
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
    RelativeLayout other_profile_layout;
    String reserveStatus;
    String like_status;
    int product_id;
    DataBaseHelper db;
    Toolbar toolbar;
    Bitmap mapicon;
    Product product;
    Bitmap userProfileicon;
    public static ProductAllDetails productAllDetailsInstance = null;
    DialogManager alert = new DialogManager();
    ArrayList<String> imagelist = new ArrayList<String>();
    private static int page = 0;
    private Timer timer;
    private CirclePageIndicator mIndicator;
    private ImageAdapterGallery adapter;
    private ViewPager viewPager;
    RelativeLayout chat;
    RelativeLayout makeOffer;
    RelativeLayout wantComment;
    LinearLayout offerRatelin;
    TextView titleOffer;
    TextView titleWant;
    Dialog dialog;
    EditText offerRate;
    EditText offerComment;
    TextView offerRateCurrency;
    String fileName = "female.png";
    ImageView btn,btngplus,btntwit,btnwtsup;
    String externalStorageDirectory = Environment.getExternalStorageDirectory().toString();
    String myDir = externalStorageDirectory + "/saved_images/"; // the
    Uri uri = Uri.parse("file:/storage/emulated/0/download/female.png" + myDir + fileName);
    public static ImageView likeButton, unLikeButton;
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
        productAllDetailsInstance = this;
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
        //map_snap_shot = (ImageView) findViewById(R.id.map);
        user_profile = (ImageView) findViewById(R.id.user_profile);

        product_cost = (TextView) findViewById(R.id.product_cost);
        product_title = (TextView) findViewById(R.id.product_name);
        product_description = (TextView) findViewById(R.id.description);
        product_total_review = (TextView) findViewById(R.id.total_views);
        product_total_like = (TextView) findViewById(R.id.total_rates);

        chat=(RelativeLayout)findViewById(R.id.chat);
        makeOffer=(RelativeLayout)findViewById(R.id.offer);
        wantComment=(RelativeLayout)findViewById(R.id.want);
        owner_profile = (RelativeLayout) findViewById(R.id.other_user_profile_rel);
        locationUpdate = (RelativeLayout) findViewById(R.id.map_lay);
        db = new DataBaseHelper(getApplicationContext());

        product = db.getProductById(product_id);

        owner_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProductAllDetails.this, UserProfile.class);
                i.putExtra("customerid", product.getProduct_owner_id());
                System.out.print("owner id------------------------------" + product.getProduct_owner_id());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });


        if(!product.getProduct_image1().equalsIgnoreCase("")){
            imagelist.add(product.getProduct_image1());
        }
        if(!product.getProduct_image2().equalsIgnoreCase("")){
            imagelist.add(product.getProduct_image2());
        }
        if(!product.getProduct_image3().equalsIgnoreCase("")){
            imagelist.add(product.getProduct_image3());
        }
        if(!product.getProduct_image4().equalsIgnoreCase("")){
            imagelist.add(product.getProduct_image4());
        }

        viewPager = (ViewPager) findViewById(R.id.pager);
        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        adapter = new ImageAdapterGallery(productAllDetailsInstance,imagelist);

        if(imagelist.size()==1){
            mIndicator.setVisibility(View.GONE);
        }

        viewPager.setAdapter(adapter);
        mIndicator.setViewPager(viewPager);
        pageSwitcher(10);

        product_cost.setText(product.getProduct_price_currency() + " " + product.getProduct_price());
        product_title.setText(product.getProduct_title());
        product_description.setText(product.getProduct_description());

        System.out.println(" ************************  product total review : " + product.getProduct_total_reviews() + " product total likes : " + product.getProduct_total_likes());

        product_total_review.setText(product.getProduct_total_reviews());
        product_total_like.setText(product.getProduct_total_likes());
        marker_place.setText(product.getProduct_city() + " " + product.getProduct_pincode());

        latlong = new LatLng(Double.parseDouble(product.getProduct_latitude()),Double.parseDouble(product.getProduct_longitude()));
        initilizeMap();

        locationUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(product.getProduct_status().equalsIgnoreCase("sell")){
                    Intent i = new Intent(ProductAllDetails.this, MyProductLocationUpdate.class);
                    i.putExtra("productid", product_id);
                    // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
                if(product.getProduct_status().equalsIgnoreCase("sold")){
                    Intent i = new Intent(ProductAllDetails.this, MyLocationmapUpdate.class);
                    i.putExtra("productid", product_id);
                    //   i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
                if(product.getProduct_status().equalsIgnoreCase("like")){
                    Intent i = new Intent(ProductAllDetails.this, LocationMap.class);
                    i.putExtra("productid", product_id);
                    //    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            }
        });

        /*if (product.getProduct_map_screenshot().equalsIgnoreCase("")) {
            map_snap_shot.setImageResource(R.drawable.defaultmap);
            System.out.print("ap blank------------------");
        } else {

            ImageLoader.getInstance().displayImage(product.getProduct_map_screenshot(),
                    map_snap_shot);
            System.out.print("ap blank not------------------");
        }

        map_snap_shot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(product.getProduct_status().equalsIgnoreCase("sell")){
                    Intent i = new Intent(ProductAllDetails.this, MyProductLocationUpdate.class);
                    i.putExtra("productid", product_id);
                    // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
                if(product.getProduct_status().equalsIgnoreCase("sold")){
                    Intent i = new Intent(ProductAllDetails.this, MyLocationmapUpdate.class);
                    i.putExtra("productid", product_id);
                    //   i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
                if(product.getProduct_status().equalsIgnoreCase("like")){
                    Intent i = new Intent(ProductAllDetails.this, LocationMap.class);
                    i.putExtra("productid", product_id);
                    //    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            }
        });*/

        if (product.getProduct_status().equalsIgnoreCase("sell")) {
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
        if (product.getProduct_status().equalsIgnoreCase("sold")) {
            soldText.setVisibility(View.VISIBLE);
            own_product.setVisibility(View.GONE);
            other_product.setVisibility(View.GONE);
            other_profile_layout.setVisibility(View.GONE);
        }
        if (product.getProduct_status().equalsIgnoreCase("like")) {

            user_name.setText(product.getProduct_owner_name());
            user_profile_reviews.setText(product.getProduct_owner_profile_reviews());

            if (product.getProduct_owner_profile_pic().equalsIgnoreCase("")) {
                userProfileicon = BitmapFactory.decodeResource(getResources(), R.drawable.def_user_img);
                user_profile.setImageBitmap(userProfileicon);
            } else {
                ImageLoader.getInstance().displayImage(product.getProduct_owner_profile_pic(),
                        user_profile);
            }


            if ("0".equalsIgnoreCase(product.getProduct_sold_status())) {
                soldText.setVisibility(View.GONE);
                own_product.setVisibility(View.GONE);
                other_product.setVisibility(View.VISIBLE);
                other_profile_layout.setVisibility(View.VISIBLE);

                if ("0".equalsIgnoreCase(product.getProduct_reserve_status())) {
                    reserveText.setVisibility(View.GONE);
                } else {
                    reserveText.setVisibility(View.VISIBLE);
                }
            } else {
                soldText.setVisibility(View.VISIBLE);
                own_product.setVisibility(View.GONE);
                other_product.setVisibility(View.GONE);
                other_profile_layout.setVisibility(View.VISIBLE);
            }
        }

        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reserveStatus = "1";

                if ((ConnectionDetector.isConnectingToInternet(ProductAllDetails.this))) {
                    new ReserveTask(ProductAllDetails.this, reserveStatus, product_id).execute();
                } else {
                    alert.showAlertDialog(ProductAllDetails.this, "Alert!", "Please, check your internet connection.", false);
                }
            }
        });
        unReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reserveStatus = "0";

                if ((ConnectionDetector.isConnectingToInternet(ProductAllDetails.this))) {
                    new ReserveTask(ProductAllDetails.this, reserveStatus, product_id).execute();
                } else {
                    alert.showAlertDialog(ProductAllDetails.this, "Alert!", "Please, check your internet connection.", false);
                }
            }
        });

        sold_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductAllDetails.this, SoldApiActivity.class);
                i.putExtra("productid", product_id);
                //   i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });


        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!AppPreferences.getId(ProductAllDetails.this).equalsIgnoreCase("")) {

                    if (!AppPreferences.getDeviceid(ProductAllDetails.this).equalsIgnoreCase("")) {
                        Intent i = new Intent(ProductAllDetails.this, ChatActivity.class);
                        i.setAction("productDetail");
                        i.putExtra("productId", product.getProduct_server_id());
                        i.putExtra("productOwnerId", product.getProduct_owner_id());
                        i.putExtra("productName", product.getProduct_title());
                        i.putExtra("productPrice", product.getProduct_price());
                        i.putExtra("productOwnerName", product.getProduct_owner_name());
//                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    } else {

                    }
                }else{
                    Toast.makeText(ProductAllDetails.this, "Sorry, chat is unavailable right now. Please try again later", Toast.LENGTH_LONG).show();
                }
            }
        });
        makeOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!AppPreferences.getId(ProductAllDetails.this).equalsIgnoreCase("")) {

                    if (!AppPreferences.getDeviceid(ProductAllDetails.this).equalsIgnoreCase("")) {
                        dialog = new Dialog(ProductAllDetails.this);
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
                                    if(!AppPreferences.getId(ProductAllDetails.this).equalsIgnoreCase("")) {

                                        if (!AppPreferences.getDeviceid(ProductAllDetails.this).equalsIgnoreCase("")) {
                                            Intent i = new Intent(ProductAllDetails.this, ChatActivity.class);
                                            i.setAction("productDetail_MakeAnOffer");
                                            i.putExtra("productId", product.getProduct_server_id());
                                            i.putExtra("productOwnerId", product.getProduct_owner_id());
                                            i.putExtra("productName", product.getProduct_title());
                                            i.putExtra("productPrice", product.getProduct_price());
                                            i.putExtra("productOwnerName", product.getProduct_owner_name());
                                            i.putExtra("offerRate", "Rs. "+offerRate.getText().toString()+" New Offer");
                                            i.putExtra("offerComment", offerComment.getText().toString());
                                            i.putExtra("ProductImage", product.getProduct_image1());
                                            i.putExtra("ProfileImage", product.getProduct_owner_profile_pic());
                                            startActivity(i);
                                        } else {

                                        }
                                    }else{
                                        Toast.makeText(ProductAllDetails.this,"Sorry, chat is unavailable right now. Please try again later", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(ProductAllDetails.this, "Sorry, chat is unavailable right now. Please try again later", Toast.LENGTH_LONG).show();
                }
            }
        });

        wantComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AppPreferences.getId(ProductAllDetails.this).equalsIgnoreCase("")) {

                    if (!AppPreferences.getDeviceid(ProductAllDetails.this).equalsIgnoreCase("")) {
                        dialog = new Dialog(ProductAllDetails.this);
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
                                    if(!AppPreferences.getId(ProductAllDetails.this).equalsIgnoreCase("")) {

                                        if (!AppPreferences.getDeviceid(ProductAllDetails.this).equalsIgnoreCase("")) {
                                            Intent i = new Intent(ProductAllDetails.this, ChatActivity.class);
                                            i.setAction("productDetail_MakeAnOffer");
                                            i.putExtra("productId", product.getProduct_server_id());
                                            i.putExtra("productOwnerId", product.getProduct_owner_id());
                                            i.putExtra("productName", product.getProduct_title());
                                            i.putExtra("productPrice", product.getProduct_price());
                                            i.putExtra("productOwnerName", product.getProduct_owner_name());
                                            i.putExtra("offerRate", "I want it");
                                            i.putExtra("offerComment", offerComment.getText().toString());
                                            i.putExtra("ProductImage", product.getProduct_image1());
                                            i.putExtra("ProfileImage", product.getProduct_owner_profile_pic());
                                            startActivity(i);
                                        } else {

                                        }
                                    }else{
                                        Toast.makeText(ProductAllDetails.this,"Sorry, chat is unavailable right now. Please try again later", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(ProductAllDetails.this, "Sorry, chat is unavailable right now. Please try again later", Toast.LENGTH_LONG).show();
                }

            }
        });

        btn = (ImageView) findViewById(R.id.button_share);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, AppConstants.APPSHAREURL);
                boolean facebookAppFound = false;
                List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
                for (ResolveInfo info : matches) {
                    if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook")) {
                        intent.setPackage(info.activityInfo.packageName);
                        facebookAppFound = true;
                        break;
                    }
                }
                if (!facebookAppFound) {
                    String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + AppConstants.APPSHAREURL;
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
                }
                startActivity(intent);
            }
        });
        btngplus=(ImageView)findViewById(R.id.button_share2);
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
                whatsappIntent.setType("text/plain");
                whatsappIntent.setPackage("com.whatsapp");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
                try {

                    productAllDetailsInstance.startActivity(whatsappIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(),"Whatsup is not Installed",Toast.LENGTH_LONG).show();
                }
            }
        });

        likeButton = (ImageView)findViewById(R.id.button_like_image);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((ConnectionDetector.isConnectingToInternet(ProductAllDetails.this))) {
                    new LikeTask(ProductAllDetails.this, "1", product_id).execute();
                } else {
                    alert.showAlertDialog(ProductAllDetails.this, "Alert!", "Please, check your internet connection.", false);
                }
            }
        });

        unLikeButton = (ImageView)findViewById(R.id.button_unlike_image);
        unLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppPreferences.getId(ProductAllDetails.this).equalsIgnoreCase("")) {
                    Intent i = new Intent(ProductAllDetails.this, UserRegisterLoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }else {
                    if ((ConnectionDetector.isConnectingToInternet(ProductAllDetails.this))) {
                        new LikeTask(ProductAllDetails.this, "0", product_id).execute();
                    } else {
                        alert.showAlertDialog(ProductAllDetails.this, "Alert!", "Please, check your internet connection.", false);
                    }
                }
            }
        });

        productZoomFloatButton = (FloatingActionButton) findViewById(R.id.product_zoom_float_button);
        reportProductFloatButton = (FloatingActionButton) findViewById(R.id.report_this_product_float_button);

        if(AppPreferences.getCustomerId(getApplicationContext()) == 0)
        {
            ProductAllDetails.likeButton.setVisibility(View.GONE);
            ProductAllDetails.unLikeButton.setVisibility(View.GONE);
        }else if (product.getProduct_status().equalsIgnoreCase("sell"))
        {
            ProductAllDetails.likeButton.setVisibility(View.GONE);
            ProductAllDetails.unLikeButton.setVisibility(View.GONE);
            productZoomFloatButton.setTitle("Edit");
            reportProductFloatButton.setTitle("Delete Product");
        }else if (product.getProduct_status().equalsIgnoreCase("sold"))
        {
            ProductAllDetails.likeButton.setVisibility(View.GONE);
            ProductAllDetails.unLikeButton.setVisibility(View.GONE);
            productZoomFloatButton.setTitle("Edit");
            reportProductFloatButton.setTitle("Delete Product");
            productZoomFloatButton.setVisibility(View.GONE);
        }if (product.getProduct_status().equalsIgnoreCase("like")) {

            ProductAllDetails.likeButton.setVisibility(View.GONE);
            ProductAllDetails.unLikeButton.setVisibility(View.VISIBLE);
            productZoomFloatButton.setVisibility(View.GONE);
            reportProductFloatButton.setTitle("Report this product");
        }
            else
        {
            ProductAllDetails.unLikeButton.setVisibility(View.GONE);
        }

        productZoomFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProductAllDetails.this, UpdateProduct.class);
                i.putExtra("productid", product_id);
                startActivity(i);
            }
        });


        final FloatingActionButton shareFloatButton = (FloatingActionButton) findViewById(R.id.share_float_button);
        shareFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Install this app :- ");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, AppConstants.APPSHAREURL);
                startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.app_name)));
            }
        });

        reportProductFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((ConnectionDetector.isConnectingToInternet(ProductAllDetails.this))) {
                    new DeleteTask(ProductAllDetails.this, product_id).execute();
                } else {
                    alert.showAlertDialog(ProductAllDetails.this, "Alert!", "Please, check your internet connection.", false);
                }
            }
        });

    }
    private void initilizeMap() {
        try {

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
            Intent i = new Intent(ProductAllDetails.this, UpdateProduct.class);
            i.putExtra("productid", product_id);
            System.out.println("product_id in all detail*******************" + product_id);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        if (id == R.id.action_like) {
            if (AppPreferences.getId(ProductAllDetails.this).equalsIgnoreCase("")) {
                Intent i = new Intent(ProductAllDetails.this, UserRegisterLoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }else {
                if ((ConnectionDetector.isConnectingToInternet(ProductAllDetails.this))) {
                    new LikeTask(ProductAllDetails.this, "1", product_id).execute();
                } else {
                    alert.showAlertDialog(ProductAllDetails.this, "Alert!", "Please, check your internet connection.", false);
                }
            }
        }
        if (id == R.id.action_dislike) {

            if (AppPreferences.getId(ProductAllDetails.this).equalsIgnoreCase("")) {
                Intent i = new Intent(ProductAllDetails.this, UserRegisterLoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }else {
                if ((ConnectionDetector.isConnectingToInternet(ProductAllDetails.this))) {
                    new LikeTask(ProductAllDetails.this, "0", product_id).execute();
                } else {
                    alert.showAlertDialog(ProductAllDetails.this, "Alert!", "Please, check your internet connection.", false);
                }
            }
        }
        if (id == R.id.action_Delete) {
            if ((ConnectionDetector.isConnectingToInternet(ProductAllDetails.this))) {
                new DeleteTask(ProductAllDetails.this, product_id).execute();
            } else {
                alert.showAlertDialog(ProductAllDetails.this, "Alert!", "Please, check your internet connection.", false);
            }
        }
        if (id == R.id.action_report) {
            if (AppPreferences.getId(ProductAllDetails.this).equalsIgnoreCase("")) {
                Intent i = new Intent(ProductAllDetails.this, UserRegisterLoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }else {
                Intent i = new Intent(ProductAllDetails.this, ReportThisProduct.class);
                i.putExtra("productid", product_id);
                // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        }
        if (id == R.id.action_share) {
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Install this app :- ");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, AppConstants.APPSHAREURL);
            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.app_name)));
        }
        return super.onOptionsItemSelected(item);
    }


    public void reserve() {
        reserve.setVisibility(View.GONE);
        unReserve.setVisibility(View.VISIBLE);
        reserveText.setVisibility(View.VISIBLE);
    }

    public void unreserve() {
        reserve.setVisibility(View.VISIBLE);
        unReserve.setVisibility(View.GONE);
        reserveText.setVisibility(View.GONE);

    }

    public void sold() {

        reserveText.setVisibility(View.GONE);
        soldText.setVisibility(View.VISIBLE);
        own_product.setVisibility(View.GONE);
        other_product.setVisibility(View.GONE);
        other_profile_layout.setVisibility(View.GONE);
    }


    public void pageSwitcher(int seconds) {
        timer = new Timer(); // At this line a new Thread will be created
        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 200); // delay
        // in
        // milliseconds
    }

    // this is an inner class...
    class RemindTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (page > 4) {
                        page = 0;
                    } else {
                        viewPager.setCurrentItem(page++, true);
                    }
                }
            });
        }
    }

    public void likeMethod(String totalLike) {
        product_total_like.setText(totalLike);
        System.out.println("like 0----------------***--------------------");
        Intent i = new Intent(ProductAllDetails.this, UserProfile.class);
        i.putExtra("customerid", AppPreferences.getCustomerId(ProductAllDetails.this));
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent i = new Intent(ProductAllDetails.this, UserProfile.class);
        i.putExtra("customerid", AppPreferences.getCustomerId(ProductAllDetails.this));
        startActivity(i);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        productAllDetailsInstance = null;
    }
}
