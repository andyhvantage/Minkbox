package com.minkbox;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

import com.minkbox.utils.AppConstants;


public class SafetyActivity extends ActionBarActivity {

    private WebView safety_detail;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = "Safety";
        getSupportActionBar().setTitle(title);

        safety_detail=(WebView)findViewById(R.id.wV_safety);
        safety_detail.getSettings().setJavaScriptEnabled(true);
        safety_detail.setWebViewClient(new MyWebClient(this));
        safety_detail.loadUrl(AppConstants.SAFETY);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
