package com.minkbox;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

import com.minkbox.utils.AppConstants;


public class RulesActivity extends ActionBarActivity {

    private WebView rules_detail;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = "Rules";
        getSupportActionBar().setTitle(title);

        rules_detail=(WebView)findViewById(R.id.wV_rules);
        rules_detail.getSettings().setJavaScriptEnabled(true);
        rules_detail.setWebViewClient(new MyWebClient(this));
        rules_detail.loadUrl(AppConstants.RULES);
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
