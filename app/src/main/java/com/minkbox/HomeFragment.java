package com.minkbox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minkbox.adapter.CustomGridAdapter;
import com.minkbox.databace.DataBaseHelper;
import com.minkbox.model.Category;
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


public class HomeFragment extends Fragment {
    private GridView gridView;
    private static CustomGridAdapter gridAdapter;
    int myLastVisiblePos;
    ArrayList<Product> productlist = new ArrayList<Product>();
    DialogManager alert = new DialogManager();
    DataBaseHelper db;
    String resultnull;
    ImageView add_product_button;
    int radius1 = 0;
    int radius2 = 3;
    boolean loadingMore = false;
    String miles = "5";
    LinearLayout miles_header;
    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    boolean connectiondetectFlag = false;
    LinearLayout categoryLayoutText, categoryLayout;
    TextView miles_tv, category_tv;
    ImageView close, close_cat;
    int lastlastitem;
    String flag_limit = "0";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment, null);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);

        categoryLayoutText = (LinearLayout) rootView.findViewById(R.id.cat_header);
        categoryLayout = (LinearLayout) rootView.findViewById(R.id.cat_header1);
        close_cat = (ImageView) rootView.findViewById(R.id.close_cat);

        miles_tv = (TextView) rootView.findViewById(R.id.miles_tv);
        miles_tv.setText(getString(R.string.miles_text, miles));

        close = (ImageView) rootView.findViewById(R.id.close);
        miles_header = (LinearLayout) rootView.findViewById(R.id.miles_header);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                miles_header.setVisibility(View.GONE);
            }
        });

        if (AppPreferences.getFilterDatatype(getActivity().getApplicationContext()).equalsIgnoreCase("menu")) {
            categoryLayout.setVisibility(View.GONE);
            if ((ConnectionDetector.isConnectingToInternet(getActivity()))) {
//                AppPreferences.setFiltercategoryid(getActivity(), "");
                AppPreferences.setSearchtext(getActivity(), "");
                AppPreferences.setCategory(getActivity(), "");
                AppPreferences.setFilterdistance(getActivity(), "5kms");
                AppPreferences.setPosttime(getActivity(), "");
                AppPreferences.setSortby(getActivity(), "");
                AppPreferences.setPricefrom(getActivity(), "");
                AppPreferences.setPriceto(getActivity(), "");
                AppPreferences.setCategoryId(getActivity(), "");
                AppPreferences.setSearchAddress(getActivity(), "");
                AppPreferences.setSearchlatitude(getActivity(), "");
                AppPreferences.setSearchlongitude(getActivity(), "");
                AppPreferences.setNewInYourArea(getActivity(), "");
                FilterActivity.catName.clear();
                FilterActivity.catId.clear();

                int count = categoryLayoutText.getChildCount();
                Log.d("count ll", count + "");
                if (count == 1) {
                    close_cat.setVisibility(View.GONE);
                }
                radius1 = 0;
                radius2 = 3;
                productlist.clear();
                flag_limit = "0";
//                new GetPostByCategiriesIdTask(getActivity()).execute(AppConstants.GETALLPOST);
            }
//
        } else if (AppPreferences.getFilterDatatype(getActivity().getApplicationContext()).equalsIgnoreCase("filter")) {
            AppPreferences.setNewInYourArea(getActivity(), "");
        } else if (AppPreferences.getFilterDatatype(getActivity().getApplicationContext()).equalsIgnoreCase("new area")) {
            AppPreferences.setFiltercategoryid(getActivity(), "");
            AppPreferences.setSearchtext(getActivity(), "");
            AppPreferences.setCategory(getActivity(), "");
            AppPreferences.setFilterdistance(getActivity(), "5kms");
            AppPreferences.setPosttime(getActivity(), "");
            AppPreferences.setSortby(getActivity(), "");
            AppPreferences.setPricefrom(getActivity(), "");
            AppPreferences.setPriceto(getActivity(), "");
            AppPreferences.setCategoryId(getActivity(), "");
            AppPreferences.setSearchAddress(getActivity(), "");
            AppPreferences.setSearchlatitude(getActivity(), "");
            AppPreferences.setSearchlongitude(getActivity(), "");
//            AppPreferences.setNewInYourArea(getActivity(), "");
            FilterActivity.catName.clear();
            FilterActivity.catId.clear();

            int count = categoryLayoutText.getChildCount();
            Log.d("count ll", count + "");
            if (count == 1) {
                close_cat.setVisibility(View.GONE);
            }
            radius1 = 0;
            radius2 = 3;
            productlist.clear();
            flag_limit = "0";
        }

        add_product_button = (ImageView) rootView.findViewById(R.id.add_product);
        add_product_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppPreferences.getId(getActivity().getApplicationContext()).equalsIgnoreCase("")) {
                    Intent i = new Intent(getActivity(), UserRegisterLoginActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(getActivity(), CellAProduct.class);
                    startActivity(i);
                }
            }
        });

        if (!ConnectionDetector.isConnectingToInternet(getActivity())) {
            alert.showAlertDialog(getActivity(), "Alert!", "Please, check your internet connection.", false);
        }

        db = new DataBaseHelper(getActivity());
        gridView = (GridView) rootView.findViewById(R.id.gridView_images);
        gridAdapter = new CustomGridAdapter(getActivity(), new ArrayList<Product>());
        gridView.setAdapter(gridAdapter);
        myLastVisiblePos = gridView.getFirstVisiblePosition();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Product product = gridAdapter.data.get(position);
                Intent i = new Intent(getActivity(),
                        ProductDetail.class);
                i.putExtra("productid", product.getProduct_server_id());
                System.out.println("id--------------" + product.getProduct_server_id());
                startActivity(i);
            }
        });

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int count = categoryLayoutText.getChildCount();
                Log.d("count ll", count + "");
                if (count == 1) {
                    close_cat.setVisibility(View.GONE);
                }

                int currentFirstVisPos = absListView.getFirstVisiblePosition();
                if (currentFirstVisPos > myLastVisiblePos) {
                    //scroll down
                    add_product_button.setVisibility(View.VISIBLE);
                    Animation slide = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
                    add_product_button.startAnimation(slide);
                }
                if (currentFirstVisPos < myLastVisiblePos) {
                    //scroll up
                    Animation slide = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
                    add_product_button.startAnimation(slide);
                }
                myLastVisiblePos = currentFirstVisPos;

                int lastInScreen = firstVisibleItem + visibleItemCount;

                Log.d("onscroll", "FirstVi: " + firstVisibleItem + " visItemcount: " + visibleItemCount + " = " + lastInScreen + " totalitem: " + totalItemCount);

                if ((lastInScreen == totalItemCount) && !(loadingMore)) {

                    System.out.println("----------------------- **************** ------------------------");
                    if ((ConnectionDetector.isConnectingToInternet(getActivity()))) {
                        new GetPostByCategiriesIdTask(getActivity()).execute(AppConstants.GETALLPOST);

                        miles_header.setVisibility(View.GONE);
                    }
                }

            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Refreshing data on server
                int count = categoryLayoutText.getChildCount();
                Log.d("count ll", count + "");
                if (count == 1) {
                    close_cat.setVisibility(View.GONE);
                }
                radius1 = 0;
                radius2 = 3;

                gridAdapter = new CustomGridAdapter(getActivity(), new ArrayList<Product>());
                gridView.setAdapter(gridAdapter);
                if ((ConnectionDetector.isConnectingToInternet(getActivity()))) {
//                    productlist.clear();
                    new GetPostByCategiriesIdTask(getActivity()).execute(AppConstants.GETALLPOST);
                }
            }
        });

        if (!AppPreferences.getNewInYourArea(getActivity()).equalsIgnoreCase("")) {
            categoryLayout.setVisibility(View.VISIBLE);

            final LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setBackgroundResource(R.drawable.gradient);
            linearLayout.setPadding(15, 5, 5, 5);

            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(R.drawable.cancel_btn);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppPreferences.setNewInYourArea(getActivity(), "");
                    categoryLayoutText.removeView(linearLayout);
                    int count = categoryLayoutText.getChildCount();
                    Log.d("count ll", count + "");
                    if (count == 1) {
                        close_cat.setVisibility(View.GONE);
                    }
                    radius1 = 0;
                    radius2 = 3;
                    flag_limit = "0";
                    productlist.clear();
                    new GetPostByCategiriesIdTask(getActivity()).execute(AppConstants.GETALLPOST);
                }
            });

            TextView textView = new TextView(getActivity());
            textView.setText(AppPreferences.getNewInYourArea(getActivity()));

            linearLayout.addView(imageView);
            linearLayout.addView(textView);
            categoryLayoutText.addView(linearLayout);
        }

        if (!AppPreferences.getCategory(getActivity()).equalsIgnoreCase("")) {
            Log.d("CategoryName", AppPreferences.getCategory(getActivity()));
            Log.d("pos img", AppPreferences.getCategoryId(getActivity()));
            categoryLayout.setVisibility(View.VISIBLE);
            String categoryIdList = AppPreferences.getCategoryId(getActivity()).replace(" ", "");
            final String[] listid = categoryIdList.split(",");

            System.out.println("cat id " + FilterActivity.catId + " list id : " + listid.toString());

            String categoryList = AppPreferences.getCategory(getActivity());
            final String[] list = categoryList.split(",");
            int i;
            for (i = 0; i < list.length; i++) {
                final LinearLayout linearLayout = new LinearLayout(getActivity());
                linearLayout.setBackgroundResource(R.drawable.gradient);
                linearLayout.setPadding(15, 5, 5, 5);

                ImageView imageView = new ImageView(getActivity());
                imageView.setBackgroundResource(R.drawable.cancel_btn);

                final int finalI = i;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("pos img", finalI + " catName:" + list[finalI].trim() + " id: " + listid[finalI]);
                        //FilterActivity.catName.remove("Baby & Kids");
                        FilterActivity.catName.remove(list[finalI].trim());
                        FilterActivity.catId.remove(listid[finalI]);
                        AppPreferences.setCategory(getActivity(), FilterActivity.catName.toString().replace("[", "").replace("]", ""));
                        AppPreferences.setCategoryId(getActivity(), FilterActivity.catId.toString().replace("[", "").replace("]", ""));
                        Log.d("pos img", finalI + " catName:" + list[finalI] + FilterActivity.catName.toString());
                        Log.d("pos img", finalI + " catId:" + listid[finalI] + FilterActivity.catId.toString());
                        categoryLayoutText.removeView(linearLayout);
                        int count = categoryLayoutText.getChildCount();
                        Log.d("count ll", count + "");
                        if (count == 1) {
                            close_cat.setVisibility(View.GONE);
                        }
                        radius1 = 0;
                        radius2 = 3;
                        productlist.clear();
                        flag_limit = "0";
                        new GetPostByCategiriesIdTask(getActivity()).execute(AppConstants.GETALLPOST);
                    }
                });

                TextView textView = new TextView(getActivity());
                textView.setText(list[i]);
                linearLayout.addView(imageView);
                linearLayout.addView(textView);
                categoryLayoutText.addView(linearLayout);
            }
        }

        if (!AppPreferences.getFiltercategoryid(getActivity()).equalsIgnoreCase("") && !AppPreferences.getFiltercategoryName(getActivity()).equalsIgnoreCase("")) {
            categoryLayout.setVisibility(View.VISIBLE);

            final LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setBackgroundResource(R.drawable.gradient);
            linearLayout.setPadding(15, 5, 5, 5);

            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(R.drawable.cancel_btn);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppPreferences.setFiltercategoryid(getActivity(), "");
                    AppPreferences.setFiltercategoryName(getActivity(), "");
                    categoryLayoutText.removeView(linearLayout);
                    int count = categoryLayoutText.getChildCount();
                    Log.d("count ll", count + "");
                    if (count == 1) {
                        close_cat.setVisibility(View.GONE);
                    }
                    radius1 = 0;
                    radius2 = 3;
                    productlist.clear();
                    flag_limit = "0";
                    System.out.println(" -------------- home fragment all product  flag limit ----------- " + flag_limit);
                    new GetPostByCategiriesIdTask(getActivity()).execute(AppConstants.GETALLPOST);
                }
            });

            TextView textView = new TextView(getActivity());
            textView.setText(AppPreferences.getFiltercategoryName(getActivity()));

            linearLayout.addView(imageView);
            linearLayout.addView(textView);
            categoryLayoutText.addView(linearLayout);
        }

        if (!AppPreferences.getSearchAddress(getActivity()).equalsIgnoreCase("")) {
            categoryLayout.setVisibility(View.VISIBLE);

            final LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setBackgroundResource(R.drawable.gradient);
            linearLayout.setPadding(15, 5, 5, 5);

            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(R.drawable.cancel_btn);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppPreferences.setSearchAddress(getActivity(), "");
                    AppPreferences.setSearchlatitude(getActivity(), "");
                    AppPreferences.setSearchlongitude(getActivity(), "");
                    categoryLayoutText.removeView(linearLayout);
                    int count = categoryLayoutText.getChildCount();
                    Log.d("count ll", count + "");
                    if (count == 1) {
                        close_cat.setVisibility(View.GONE);
                    }
                    radius1 = 0;
                    radius2 = 3;
                    flag_limit = "0";
                    productlist.clear();
                    new GetPostByCategiriesIdTask(getActivity()).execute(AppConstants.GETALLPOST);
                }
            });

            TextView textView = new TextView(getActivity());
            textView.setText(AppPreferences.getSearchAddress(getActivity()));

            linearLayout.addView(imageView);
            linearLayout.addView(textView);
            categoryLayoutText.addView(linearLayout);
        }

        if (!AppPreferences.getSearchtext(getActivity()).equalsIgnoreCase("")) {
            categoryLayout.setVisibility(View.VISIBLE);

            final LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setBackgroundResource(R.drawable.gradient);
            linearLayout.setPadding(15, 5, 5, 5);

            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(R.drawable.cancel_btn);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppPreferences.setSearchtext(getActivity(), "");
                    categoryLayoutText.removeView(linearLayout);
                    int count = categoryLayoutText.getChildCount();
                    Log.d("count ll", count + "");
                    if (count == 1) {
                        close_cat.setVisibility(View.GONE);
                    }
                    radius1 = 0;
                    radius2 = 3;
                    productlist.clear();
                    flag_limit = "0";
                    new GetPostByCategiriesIdTask(getActivity()).execute(AppConstants.GETALLPOST);
                }
            });

            TextView textView = new TextView(getActivity());
            textView.setText(AppPreferences.getSearchtext(getActivity()));

            linearLayout.addView(imageView);
            linearLayout.addView(textView);
            categoryLayoutText.addView(linearLayout);
        }

        if (!AppPreferences.getFilterdistance(getActivity()).equalsIgnoreCase("5kms")) {
            categoryLayout.setVisibility(View.VISIBLE);
            final LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setBackgroundResource(R.drawable.gradient);
            linearLayout.setPadding(15, 5, 5, 5);

            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(R.drawable.cancel_btn);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppPreferences.setFilterdistance(getActivity(), "5kms");
                    categoryLayoutText.removeView(linearLayout);
                    int count = categoryLayoutText.getChildCount();
                    Log.d("count ll", count + "");
                    if (count == 1) {
                        close_cat.setVisibility(View.GONE);
                    }
                    radius1 = 0;
                    radius2 = 3;
                    flag_limit = "0";
                    productlist.clear();
                    new GetPostByCategiriesIdTask(getActivity()).execute(AppConstants.GETALLPOST);
                }
            });

            TextView textView = new TextView(getActivity());
            if (AppPreferences.getFilterdistance(getActivity()).equalsIgnoreCase("")) {
                textView.setText("10+kms");//AppPreferences.getFilterdistance(getActivity())
            } else {
                textView.setText(AppPreferences.getFilterdistance(getActivity()));
            }

            linearLayout.addView(imageView);
            linearLayout.addView(textView);
            categoryLayoutText.addView(linearLayout);
        }

        if (!AppPreferences.getPosttime(getActivity()).equalsIgnoreCase("")) {
            categoryLayout.setVisibility(View.VISIBLE);
            final LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setBackgroundResource(R.drawable.gradient);
            linearLayout.setPadding(15, 5, 5, 5);

            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(R.drawable.cancel_btn);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppPreferences.setPosttime(getActivity(), "");
                    categoryLayoutText.removeView(linearLayout);
                    int count = categoryLayoutText.getChildCount();
                    Log.d("count ll", count + "");
                    if (count == 1) {
                        close_cat.setVisibility(View.GONE);
                    }
                    radius1 = 0;
                    radius2 = 3;
                    productlist.clear();
                    flag_limit = "0";
                    new GetPostByCategiriesIdTask(getActivity()).execute(AppConstants.GETALLPOST);
                }
            });

            TextView textView = new TextView(getActivity());
            textView.setText(AppPreferences.getPosttime(getActivity()));

            linearLayout.addView(imageView);
            linearLayout.addView(textView);
            categoryLayoutText.addView(linearLayout);
        }

        if (!AppPreferences.getSortby(getActivity()).equalsIgnoreCase("")) {
            categoryLayout.setVisibility(View.VISIBLE);
            final LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setBackgroundResource(R.drawable.gradient);
            linearLayout.setPadding(15, 5, 5, 5);

            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(R.drawable.cancel_btn);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppPreferences.setSortby(getActivity(), "");
                    categoryLayoutText.removeView(linearLayout);
                    int count = categoryLayoutText.getChildCount();
                    Log.d("count ll", count + "");
                    if (count == 1) {
                        close_cat.setVisibility(View.GONE);
                    }
                    radius1 = 0;
                    radius2 = 3;
                    productlist.clear();
                    flag_limit = "0";
                    new GetPostByCategiriesIdTask(getActivity()).execute(AppConstants.GETALLPOST);
                }
            });

            TextView textView = new TextView(getActivity());
            textView.setText(AppPreferences.getSortby(getActivity()));

            linearLayout.addView(imageView);
            linearLayout.addView(textView);
            categoryLayoutText.addView(linearLayout);
        }
        if (!AppPreferences.getPricefrom(getActivity()).equalsIgnoreCase("") && !AppPreferences.getPriceto(getActivity()).equalsIgnoreCase("")) {
            categoryLayout.setVisibility(View.VISIBLE);
            final LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setBackgroundResource(R.drawable.gradient);
            linearLayout.setPadding(15, 5, 5, 5);

            ImageView imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(R.drawable.cancel_btn);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppPreferences.setPricefrom(getActivity(), "");
                    AppPreferences.setPriceto(getActivity(), "");
                    categoryLayoutText.removeView(linearLayout);
                    int count = categoryLayoutText.getChildCount();
                    Log.d("count ll", count + "");
                    if (count == 1) {
                        close_cat.setVisibility(View.GONE);
                    }
                    radius1 = 0;
                    radius2 = 3;
                    productlist.clear();
                    flag_limit = "0";
                    new GetPostByCategiriesIdTask(getActivity()).execute(AppConstants.GETALLPOST);
                }
            });

            TextView textView = new TextView(getActivity());
            textView.setText("Price: Min " + AppPreferences.getPricefrom(getActivity()) + " Max " + AppPreferences.getPriceto(getActivity()));

            linearLayout.addView(imageView);
            linearLayout.addView(textView);
            categoryLayoutText.addView(linearLayout);
        } else {
            if (!AppPreferences.getPricefrom(getActivity()).equalsIgnoreCase("")) {
                categoryLayout.setVisibility(View.VISIBLE);
                final LinearLayout linearLayout = new LinearLayout(getActivity());
                linearLayout.setBackgroundResource(R.drawable.gradient);
                linearLayout.setPadding(15, 5, 5, 5);

                ImageView imageView = new ImageView(getActivity());
                imageView.setBackgroundResource(R.drawable.cancel_btn);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppPreferences.setPricefrom(getActivity(), "");
                        categoryLayoutText.removeView(linearLayout);
                        int count = categoryLayoutText.getChildCount();
                        Log.d("count ll", count + "");
                        if (count == 1) {
                            close_cat.setVisibility(View.GONE);
                        }
                        radius1 = 0;
                        radius2 = 3;
                        productlist.clear();
                        flag_limit = "0";
                        new GetPostByCategiriesIdTask(getActivity()).execute(AppConstants.GETALLPOST);
                    }
                });

                TextView textView = new TextView(getActivity());
                textView.setText("Price: Min " + AppPreferences.getPricefrom(getActivity()));

                linearLayout.addView(imageView);
                linearLayout.addView(textView);
                categoryLayoutText.addView(linearLayout);
            } else if (!AppPreferences.getPriceto(getActivity()).equalsIgnoreCase("")) {
                categoryLayout.setVisibility(View.VISIBLE);
                final LinearLayout linearLayout = new LinearLayout(getActivity());
                linearLayout.setBackgroundResource(R.drawable.gradient);
                linearLayout.setPadding(15, 5, 5, 5);

                ImageView imageView = new ImageView(getActivity());
                imageView.setBackgroundResource(R.drawable.cancel_btn);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppPreferences.setPriceto(getActivity(), "");
                        categoryLayoutText.removeView(linearLayout);
                        int count = categoryLayoutText.getChildCount();
                        Log.d("count ll", count + "");
                        if (count == 1) {
                            close_cat.setVisibility(View.GONE);
                        }
                        radius1 = 0;
                        radius2 = 3;
                        productlist.clear();
                        flag_limit = "0";
                        new GetPostByCategiriesIdTask(getActivity()).execute(AppConstants.GETALLPOST);
                    }
                });

                TextView textView = new TextView(getActivity());
                textView.setText("Price: Max " + AppPreferences.getPriceto(getActivity()));

                linearLayout.addView(imageView);
                linearLayout.addView(textView);
                categoryLayoutText.addView(linearLayout);
            }
        }

        close_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryLayout.setVisibility(View.GONE);
                if ((ConnectionDetector.isConnectingToInternet(getActivity()))) {
                    AppPreferences.setFiltercategoryid(getActivity(), "");
                    AppPreferences.setSearchtext(getActivity(), "");
                    AppPreferences.setCategory(getActivity(), "");
                    AppPreferences.setFilterdistance(getActivity(), "5kms");
                    AppPreferences.setPosttime(getActivity(), "");
                    AppPreferences.setSortby(getActivity(), "");
                    AppPreferences.setPricefrom(getActivity(), "");
                    AppPreferences.setPriceto(getActivity(), "");
                    AppPreferences.setCategoryId(getActivity(), "");
                    AppPreferences.setSearchAddress(getActivity(), "");
                    AppPreferences.setSearchlatitude(getActivity(), "");
                    AppPreferences.setSearchlongitude(getActivity(), "");
                    AppPreferences.setNewInYourArea(getActivity(), "");
                    FilterActivity.catName.clear();
                    FilterActivity.catId.clear();

                    int count = categoryLayoutText.getChildCount();
                    Log.d("count ll", count + "");
                    if (count == 1) {
                        close_cat.setVisibility(View.GONE);
                    }
                    radius1 = 0;
                    radius2 = 3;
                    productlist.clear();
                    flag_limit = "0";
                    new GetPostByCategiriesIdTask(getActivity()).execute(AppConstants.GETALLPOST);
                }
//
            }
        });

        return rootView;
    }

    public class GetPostByCategiriesIdTask extends AsyncTask<String, Void, String> {

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
        private String TAG = GetPostByCategiriesIdTask.class.getSimpleName();
        // private ProgressDialog mProgressDialog;
        private int status = 0;


        public GetPostByCategiriesIdTask(Activity activity) {
            this.activity = activity;
            try {
                if (Float.valueOf(miles) > 5) {
                    miles_tv.setText(getString(R.string.miles_text, miles));

                } else {
                    miles_tv.setText(getString(R.string.miles_text, "5"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(TAG, "Sign In task Started");
            if (!mSwipeRefreshLayout.isRefreshing()) {
                //  mProgressDialog.show();
            }
        }

        @SuppressLint("NewApi")
        protected String doInBackground(String... params) {
            loadingMore = true;
            try {
                HttpParams httpParameters = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParameters,
                        AppConstants.NETWORK_TIMEOUT_CONSTANT);
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        AppConstants.NETWORK_CONNECTION_TIMEOUT_CONSTANT);
                HttpConnectionParams.setSoTimeout(httpParameters,
                        AppConstants.NETWORK_SOCKET_TIMEOUT_CONSTANT);

                HttpClient httpclient = new DefaultHttpClient();
                System.out.println("show all post url ---------------- " + AppConstants.GETALLPOST);
                HttpPost httppost = new HttpPost(params[0]);
                jsonObj = new JSONObject();
                jsonObj.put("customer_id", AppPreferences.getCustomerId(getActivity()));
                jsonObj.put("lat", AppPreferences.getLatutude(getActivity()));//  "23.2508769");// AppPreferences.getLatutude(getActivity()));//"22.9600"
                jsonObj.put("lng", AppPreferences.getLongitude(getActivity()));//  "77.4015313"); //AppPreferences.getLongitude(getActivity()));//"76.0600"

                //    jsonObj.put("lat", "18.5203");//  "23.2508769");// AppPreferences.getLatutude(getActivity()));//"22.9600"
                // jsonObj.put("lng", "73.8567");

                jsonObj.put("categoryId", AppPreferences.getFiltercategoryid(getActivity()));
                jsonObj.put("radius1", String.valueOf(radius1));
                jsonObj.put("radius2", String.valueOf(radius2));
                jsonObj.put("search_text", AppPreferences.getSearchtext(getActivity()));
                jsonObj.put("search_latitude", AppPreferences.getSearchlatitude(getActivity()));
                jsonObj.put("search_longitude", AppPreferences.getSearchlongitude(getActivity()));
                jsonObj.put("search_categoryId", AppPreferences.getCategoryId(getActivity()));
                jsonObj.put("search_distance", "");//flag_limit.equalsIgnoreCase("0") ? AppPreferences.getFilterdistance(getActivity()) : "");
                jsonObj.put("search_post_time", AppPreferences.getPosttime(getActivity()));
                jsonObj.put("search_price_from", AppPreferences.getPricefrom(getActivity()));
                jsonObj.put("search_price_to", AppPreferences.getPriceto(getActivity()));
                jsonObj.put("search_sort_by", AppPreferences.getSortby(getActivity()));
                jsonObj.put("flag_limit", flag_limit);

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
                            JSONArray categoryArray = jsonObj.getJSONArray("category");
                            JSONArray jsonChildArray = jsonObj.getJSONArray("result");//result

                            if (jsonObj.getString("status").equalsIgnoreCase("200")) {
                                status = 200;

                                Log.v(TAG, "after jsonChildArray" + jsonChildArray + " result array length is : " + jsonChildArray.length());

                                if (categoryArray != null && categoryArray.length() > 0) {

                                    for (int j = 0; j < categoryArray.length(); j++) {
                                        JSONObject childobj = categoryArray.getJSONObject(j);
                                        String categoryId = childobj.getString("id");
                                        String categoryName = childobj.getString("category_name");
                                        Log.d("categoryName1: ", categoryName);

                                        String categoryName1 = categoryName.replace(",", "");

                                        Log.d("categoryName2: ", categoryName1);

                                        Category category = new Category();
                                        category.setServer_categoryid(Integer.parseInt(categoryId));
                                        category.setCategory_name(categoryName1);
                                        db.insertOrUpdateCategories(category);

                                        Log.d("database", "insert " + childobj.getString("id"));
                                    }
                                }


                                if (jsonChildArray != null && jsonChildArray.length() > 0) {
                                    for (int i = 0; i < jsonChildArray.length(); i++) {

                                        //start++;

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
                                        miles = jsonchildObj.getString("miles");
                                        flag_limit = String.valueOf(jsonchildObj.getInt("flag_limit"));

                                        Product product = new Product();
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
                                        networkFlag = "true";
                                    }


                                    loadingMore = false;
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
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            } else {
                // mProgressDialog.dismiss();
            }

            try {
                if (status == 200) {
                    try {

                        miles_header.setVisibility(View.VISIBLE);
                        try {
                            if (Float.valueOf(miles) > 5) {
                                miles_tv.setText(getString(R.string.miles_text, miles));

                            } else {
                                miles_tv.setText(getString(R.string.miles_text, "5"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //      miles_tv.setText(getString(R.string.miles_text, miles));
                        radius1 = radius2;
                        radius2 = radius1 + 3;

                        //mProgressDialog.dismiss();

                        gridAdapter.updateResults(productlist);
                        // update(productlist);

                        AlphaAnimation alphaAnim = new AlphaAnimation(1.0f, 0.0f);
                        alphaAnim.setDuration(5000);
                        alphaAnim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            public void onAnimationEnd(Animation animation) {
                                // make invisible when animation completes, you could also remove the view from the layout
                                miles_header.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        miles_header.startAnimation(alphaAnim);
                    } catch (Exception e) {
                    }
                } else if (status == 400) {
                    miles_header.setVisibility(View.VISIBLE);
                    // mProgressDialog.dismiss();
                    gridAdapter.updateResults(productlist);
                    Toast.makeText(getActivity(), "data not found", Toast.LENGTH_LONG).show();
                } else if (status == 500) {
                    miles_header.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "Check Username / Password", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag_limit = "0";
    }
}

