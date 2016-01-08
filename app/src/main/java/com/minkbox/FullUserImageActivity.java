package com.minkbox;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import photoview.PhotoViewAttacher;

public class FullUserImageActivity extends ActionBarActivity {

    static final String PHOTO_TAP_TOAST_STRING = "Photo Tap! X: %.2f %% Y:%.2f %% ID: %d";
    static final String SCALE_TOAST_STRING = "Scaled to: %.2ff";
    private TextView mCurrMatrixTv;
    String customer_profile_pic;
    private PhotoViewAttacher mAttacher;
   // Toolbar toolbar;
    private Toast mCurrentToast;
    private Matrix mCurrentDisplayMatrix = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_user_image);

//        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout
//        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        String title = "Image gallery";
//        getSupportActionBar().setTitle(title);


        Bundle extras = getIntent().getExtras();
        customer_profile_pic = extras.getString("profile_pic");

        ImageView mImageView = (ImageView) findViewById(R.id.iv_photo);
        mCurrMatrixTv = (TextView) findViewById(R.id.tv_current_matrix);

        ImageLoader.getInstance().displayImage(customer_profile_pic,
                mImageView);

        mAttacher = new PhotoViewAttacher(mImageView);

        mAttacher.setOnMatrixChangeListener((PhotoViewAttacher.OnMatrixChangedListener) new MatrixChangeListener());
        mAttacher.setOnPhotoTapListener(new PhotoTapListener());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mAttacher.cleanup();
    }



    private class PhotoTapListener implements PhotoViewAttacher.OnPhotoTapListener {

        @Override
        public void onPhotoTap(View view, float x, float y) {
            float xPercentage = x * 100f;
            float yPercentage = y * 100f;

            showToast(String.format(PHOTO_TAP_TOAST_STRING, xPercentage, yPercentage, view == null ? 0 : view.getId()));
        }
    }

    private void showToast(CharSequence text) {
        if (null != mCurrentToast) {
            mCurrentToast.cancel();
        }

        mCurrentToast = Toast.makeText(FullUserImageActivity.this, text, Toast.LENGTH_SHORT);
//        mCurrentToast.show();
    }

    private class MatrixChangeListener implements PhotoViewAttacher.OnMatrixChangedListener {

        @Override
        public void onMatrixChanged(RectF rect) {
            mCurrMatrixTv.setText(rect.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_email_verification, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
