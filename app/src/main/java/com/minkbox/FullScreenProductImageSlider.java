package com.minkbox;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.minkbox.adapter.CirclePageIndicator;
import com.minkbox.databace.DataBaseHelper;
import com.minkbox.model.Product;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import photoview.PhotoView;

public class FullScreenProductImageSlider extends Activity {

    private static final String ISLOCKED_ARG = "isLocked";

    private ViewPager mViewPager;
    private MenuItem menuLockItem;
    Product product;
    int product_id;
   private CirclePageIndicator mIndicator;
    public static FullScreenProductImageSlider Instance = null;
    SamplePagerAdapter adapter;

    ArrayList<String> imagelist = new ArrayList<String>();
    DataBaseHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_product_image_slider);
        Bundle extras = getIntent().getExtras();
        product_id = extras.getInt("productid");
        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);

        setContentView(mViewPager);
        Instance=this;
        db = new DataBaseHelper(this);
        Product product = db.getProductById(product_id);

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

        adapter = new SamplePagerAdapter(Instance,imagelist);
        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mViewPager.setAdapter(adapter);
        mIndicator.setViewPager(mViewPager);

        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean(ISLOCKED_ARG, false);
            ((HackyViewPager) mViewPager).setLocked(isLocked);
        }
    }

    static class SamplePagerAdapter extends PagerAdapter {
        Context context;
        ArrayList<String> imageslist;

        public SamplePagerAdapter(Context context,ArrayList<String> img_list)
        {
            this.context=context;
            this.imageslist = img_list;
        }
        @Override
        public int getCount() {
            return imageslist.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());

            ImageLoader.getInstance().displayImage(imageslist.get(position).toString(),
                    photoView);
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_full_screen_product_image_slider, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuLockItem = menu.findItem(R.id.menu_lock);
        toggleLockBtnTitle();
        menuLockItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                toggleViewPagerScrolling();
                toggleLockBtnTitle();
                return true;
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    private void toggleViewPagerScrolling() {
        if (isViewPagerActive()) {
            ((HackyViewPager) mViewPager).toggleLock();
        }
    }

    private void toggleLockBtnTitle() {
        boolean isLocked = false;
        if (isViewPagerActive()) {
            isLocked = ((HackyViewPager) mViewPager).isLocked();
        }
        String title = (isLocked) ? getString(R.string.menu_unlock) : getString(R.string.menu_lock);
        if (menuLockItem != null) {
            menuLockItem.setTitle(title);
        }
    }

    private boolean isViewPagerActive() {
        return (mViewPager != null && mViewPager instanceof HackyViewPager);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (isViewPagerActive()) {
            outState.putBoolean(ISLOCKED_ARG, ((HackyViewPager) mViewPager).isLocked());
        }
        super.onSaveInstanceState(outState);
    }

}
