package com.minkbox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.minkbox.utils.AppPreferences;
import com.minkbox.utils.Function;

public class PhoneVerificationActivity extends AppCompatActivity {
    Toolbar toolbar;
    Button send;
    EditText number_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = getString(R.string.app_name);
        title = getString(R.string.title_activity_identity);
        getSupportActionBar().setTitle(title);

        send = (Button)findViewById(R.id.send_button);
        number_edit = (EditText)findViewById(R.id.number_edit);

        findViewById(android.R.id.content).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Function.hideSoftKeyboard(PhoneVerificationActivity.this);
                return false;
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String verifyCode = Function.generateUniqueId();
                Log.d("verification Code", verifyCode);

                AppPreferences.setRandomNo(PhoneVerificationActivity.this, verifyCode);


                if (TextUtils.isEmpty(number_edit.getText().toString())) {
                    number_edit.setError("");
                } else {
                    new Function().sendSMSMessage(PhoneVerificationActivity.this, number_edit.getText().toString().trim());
                    Intent intent = new Intent(PhoneVerificationActivity.this, MobileNumVerificationActivity.class);
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

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }
       

        return super.onOptionsItemSelected(item);
    }
}
