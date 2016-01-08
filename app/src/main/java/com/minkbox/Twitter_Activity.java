package com.minkbox;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

import com.minkbox.utils.AppConstants;


public class Twitter_Activity extends ActionBarActivity {
    Toolbar toolbar;
    WebView twitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = getResources().getString(R.string.appname_on_twitter_text);//"MinkBox on Twitter";
        getSupportActionBar().setTitle(title);
        twitter = (WebView) findViewById(R.id.webview);
        twitter.getSettings().setJavaScriptEnabled(true);
        twitter.setWebViewClient(new MyWebClient(this));
        twitter.loadUrl(AppConstants.TWITTERFOLLOWAPPURL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
