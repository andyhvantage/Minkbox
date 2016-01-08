package com.minkbox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.minkbox.adapter.ViewPagerRegisterLoginAdapter;
import com.minkbox.classes.SlidingTabLayout;
import com.minkbox.fragment.UserLoginFragment;

import java.util.ArrayList;


public class UserRegisterLoginActivity extends ActionBarActivity {
    private Toolbar toolbar;
    private ArrayList<String> gallerydata;
    public static UserRegisterLoginActivity activityinstance;
    ViewPager pager;
    ViewPagerRegisterLoginAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"LOG IN", "SIGN UP"};
    int Numboftabs = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_register_login);

        findViewById(R.id.user_register_login_layout_l1).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard(UserRegisterLoginActivity.this);
                return false;
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        adapter = new ViewPagerRegisterLoginAdapter(getSupportFragmentManager(), Titles,
                Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true,
        // This makes the tabs Space Evenly in
        // Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_register_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.termcondition) {
            Intent i = new Intent(UserRegisterLoginActivity.this, TermConditionActivity.class);
            startActivity(i);
            return true;
        }

        if (id == android.R.id.home) {
//            Intent i = new Intent(UserRegisterLoginActivity.this, HomeActivity.class);
//            startActivity(i);
            finish();
            return true;
        }

        if (id == R.id.privacy) {
            Intent i = new Intent(UserRegisterLoginActivity.this, PrivacyPolicyActivity.class);
            startActivity(i);
//            finish();
            return true;
        }
        if (id == R.id.safety) {
            Intent i = new Intent(UserRegisterLoginActivity.this, SafetyActivity.class);
            startActivity(i);
//            finish();
            return true;
        }
        if (id == R.id.rules) {
            Intent i = new Intent(UserRegisterLoginActivity.this, RulesActivity.class);
            startActivity(i);
//            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        System.out.println(" ---------------------------- onActivityResult ---------");

        if (UserLoginFragment.loginType.equalsIgnoreCase("Gplus"));
        {
            UserLoginFragment.myOnActivityResult(requestCode, resultCode, data);
        }
         if(!UserLoginFragment.loginType.equalsIgnoreCase("Gplus"))
        {
            super.onActivityResult(requestCode, resultCode, data);
            System.out.println("***************** facebook  onActivityResult called **************");
            UserLoginFragment.callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
