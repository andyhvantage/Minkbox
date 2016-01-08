package com.minkbox;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.minkbox.utils.AppPreferences;

public class IdentiVerificationActivity extends ActionBarActivity {
    Toolbar toolbar;
    LinearLayout l1, l2, l3, l4;
    PopupWindow pwindoemail;
    Dialog dialog;
    ImageView imagegoogle, imagefacebook, imageemail;
    TextView ggoogle, facebook, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identi_verification);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = getString(R.string.app_name);
        title = getString(R.string.title_activity_identity);
        getSupportActionBar().setTitle(title);
        l1 = (LinearLayout) findViewById(R.id.emaillayout);
        l2 = (LinearLayout) findViewById(R.id.phonelayout);
        l3 = (LinearLayout) findViewById(R.id.facebooklayout);
        l4 = (LinearLayout) findViewById(R.id.googlepluslayout);
        imagegoogle = (ImageView)findViewById(R.id.imagegoogle);
        imagefacebook = (ImageView)findViewById(R.id.imagefacebook);
        imageemail = (ImageView)findViewById(R.id.imageemail);
        ggoogle = (TextView)findViewById(R.id.ggoogle);
        facebook = (TextView)findViewById(R.id.facebook);
        email = (TextView)findViewById(R.id.email);

        if (AppPreferences.getVerifyweb(IdentiVerificationActivity.this)) {
            imageemail.setBackgroundResource(R.drawable.email_bitmap);
        }
        if (AppPreferences.getVerifymobile(IdentiVerificationActivity.this)) {
            imagegoogle.setBackgroundResource(R.drawable.gplus_bitmap);
        }
        if (AppPreferences.getVerifyfb(IdentiVerificationActivity.this)) {
            imagefacebook.setBackgroundResource(R.drawable.fb_bitmap);
        }
        if (AppPreferences.getVerifygplus(IdentiVerificationActivity.this)) {
            imagegoogle.setBackgroundResource(R.drawable.gplus_bitmap);
        }

        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AppPreferences.getVerifyweb(IdentiVerificationActivity.this)) {
                    initiatePopupWindowChangeemail();
                }
            }
        });
        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AppPreferences.getVerifymobile(IdentiVerificationActivity.this)) {
                    Intent intent = new Intent(IdentiVerificationActivity.this, PhoneVerificationActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });
        l3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AppPreferences.getVerifyfb(IdentiVerificationActivity.this)){
                    Intent intent = new Intent(IdentiVerificationActivity.this, FacebookVerficationActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        l4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppPreferences.getVerifygplus(IdentiVerificationActivity.this)) {
                    Intent intent = new Intent(IdentiVerificationActivity.this, GoogleplusVerificationActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
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
        return super.onOptionsItemSelected(item);
    }

    private void initiatePopupWindowChangeemail() {
        try {
            dialog = new Dialog(IdentiVerificationActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.email_popup);
            TextView textview_email = (TextView) dialog.findViewById(R.id.text_email);
            textview_email.setText(AppPreferences.getId(IdentiVerificationActivity.this));

            Button btn_yescorrectemail = (Button) dialog.findViewById(R.id.correct_button);
            btn_yescorrectemail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(IdentiVerificationActivity.this, EmailVerificationActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                    finish();
                }
            });
            Button btn_changeemail = (Button) dialog.findViewById(R.id.change_email_button);
            btn_changeemail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(IdentiVerificationActivity.this, EditProfile.class);
                    startActivity(intent);
                    dialog.dismiss();

                }
            });
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
