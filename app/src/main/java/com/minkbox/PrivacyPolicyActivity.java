package com.minkbox;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

import com.minkbox.utils.AppConstants;


public class PrivacyPolicyActivity extends ActionBarActivity {

    private WebView privacy_policy_detail;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = "Privacy & Policy";
        getSupportActionBar().setTitle(title);

        privacy_policy_detail=(WebView)findViewById(R.id.wV_privacy_policy);
        privacy_policy_detail.getSettings().setJavaScriptEnabled(true);
        privacy_policy_detail.setWebViewClient(new MyWebClient(this));
        privacy_policy_detail.loadUrl(AppConstants.PRIVACY_POLICY);
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
