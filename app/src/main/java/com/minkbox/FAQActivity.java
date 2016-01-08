package com.minkbox;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

import com.minkbox.utils.AppConstants;


public class FAQActivity extends ActionBarActivity {
    private WebView faq_detail;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = "FAQ";
        getSupportActionBar().setTitle(title);

        faq_detail = (WebView) findViewById(R.id.wV_faq);
        faq_detail.getSettings().setJavaScriptEnabled(true);
        faq_detail.setWebViewClient(new MyWebClient(this));
        faq_detail.loadUrl(AppConstants.FAQ);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
