package com.minkbox;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

import com.minkbox.utils.AppConstants;


public class TermConditionActivity extends ActionBarActivity {

    private WebView term_condition_detail;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_condition);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = "Terms & Conditions";
        getSupportActionBar().setTitle(title);


        term_condition_detail=(WebView)findViewById(R.id.wV_term_conidition);
        term_condition_detail.getSettings().setJavaScriptEnabled(true);
        term_condition_detail.setWebViewClient(new MyWebClient(this));
        term_condition_detail.loadUrl(AppConstants.TERM_CONDITION);
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

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
    }

}
