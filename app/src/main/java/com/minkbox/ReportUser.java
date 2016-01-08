package com.minkbox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.minkbox.network.ReportUserTask;
import com.minkbox.utils.ConnectionDetector;
import com.minkbox.utils.DialogManager;

public class ReportUser extends AppCompatActivity {
    DialogManager alert = new DialogManager();
    int customer_id;
    String bug_status="";
    ImageView report_bug1_unselect;
    ImageView report_bug2_unselect;
    ImageView report_bug3_unselect;
    ImageView report_bug4_unselect;
    ImageView report_bug5_unselect;
    ImageView report_bug6_unselect;
    ImageView report_bug1_select;
    ImageView report_bug2_select;
    ImageView report_bug3_select;
    ImageView report_bug4_select;
    ImageView report_bug5_select;
    ImageView report_bug6_select;

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_user);
        Bundle extras = getIntent().getExtras();
        customer_id = extras.getInt("customer_id");
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = getString(R.string.app_name);
        title = getString(R.string.title_activity_home);
        getSupportActionBar().setTitle(title);
        report_bug1_unselect = (ImageView) findViewById(R.id.bugs1);
        report_bug2_unselect = (ImageView) findViewById(R.id.bugs2);
        report_bug3_unselect = (ImageView) findViewById(R.id.bugs3);
        report_bug4_unselect = (ImageView) findViewById(R.id.bugs4);
        report_bug5_unselect = (ImageView) findViewById(R.id.bugs5);
        report_bug6_unselect = (ImageView) findViewById(R.id.bugs6);

        report_bug1_select = (ImageView) findViewById(R.id.bugs1_selected);
        report_bug2_select = (ImageView) findViewById(R.id.bugs2_selected);
        report_bug3_select = (ImageView) findViewById(R.id.bugs3_selected);
        report_bug4_select = (ImageView) findViewById(R.id.bugs4_selected);
        report_bug5_select = (ImageView) findViewById(R.id.bugs5_selected);
        report_bug6_select = (ImageView) findViewById(R.id.bugs6_selected);



        report_bug1_unselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bug_status = "suspicious_behavior";
                report_bug1_select.setVisibility(View.VISIBLE);
                report_bug1_unselect.setVisibility(View.GONE);

                report_bug2_unselect.setVisibility(View.VISIBLE);
                report_bug3_unselect.setVisibility(View.VISIBLE);
                report_bug4_unselect.setVisibility(View.VISIBLE);
                report_bug5_unselect.setVisibility(View.VISIBLE);
                report_bug6_unselect.setVisibility(View.VISIBLE);


                report_bug2_select.setVisibility(View.GONE);
                report_bug3_select.setVisibility(View.GONE);
                report_bug4_select.setVisibility(View.GONE);
                report_bug5_select.setVisibility(View.GONE);
                report_bug6_select.setVisibility(View.GONE);

            }
        });

        report_bug2_unselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bug_status = "scam";
                report_bug2_select.setVisibility(View.VISIBLE);
                report_bug2_unselect.setVisibility(View.GONE);

                report_bug1_unselect.setVisibility(View.VISIBLE);
                report_bug3_unselect.setVisibility(View.VISIBLE);
                report_bug4_unselect.setVisibility(View.VISIBLE);
                report_bug5_unselect.setVisibility(View.VISIBLE);
                report_bug6_unselect.setVisibility(View.VISIBLE);

                report_bug1_select.setVisibility(View.GONE);
                report_bug3_select.setVisibility(View.GONE);
                report_bug4_select.setVisibility(View.GONE);
                report_bug5_select.setVisibility(View.GONE);
                report_bug6_select.setVisibility(View.GONE);

            }
        });

        report_bug3_unselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bug_status = "No-show";
                report_bug3_select.setVisibility(View.VISIBLE);
                report_bug3_unselect.setVisibility(View.GONE);

                report_bug1_unselect.setVisibility(View.VISIBLE);
                report_bug2_unselect.setVisibility(View.VISIBLE);
                report_bug4_unselect.setVisibility(View.VISIBLE);
                report_bug5_unselect.setVisibility(View.VISIBLE);
                report_bug6_unselect.setVisibility(View.VISIBLE);

                report_bug1_select.setVisibility(View.GONE);
                report_bug2_select.setVisibility(View.GONE);
                report_bug4_select.setVisibility(View.GONE);
                report_bug5_select.setVisibility(View.GONE);
                report_bug6_select.setVisibility(View.GONE);

            }
        });

        report_bug4_unselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bug_status = "Bad behavior or abuse";
                report_bug4_select.setVisibility(View.VISIBLE);
                report_bug4_unselect.setVisibility(View.GONE);

                report_bug1_unselect.setVisibility(View.VISIBLE);
                report_bug2_unselect.setVisibility(View.VISIBLE);
                report_bug3_unselect.setVisibility(View.VISIBLE);
                report_bug5_unselect.setVisibility(View.VISIBLE);
                report_bug6_unselect.setVisibility(View.VISIBLE);

                report_bug1_select.setVisibility(View.GONE);
                report_bug2_select.setVisibility(View.GONE);
                report_bug3_select.setVisibility(View.GONE);
                report_bug5_select.setVisibility(View.GONE);
                report_bug6_select.setVisibility(View.GONE);

            }
        });

        report_bug5_unselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bug_status = "Defective or wrong item";
                report_bug5_select.setVisibility(View.VISIBLE);
                report_bug5_unselect.setVisibility(View.GONE);

                report_bug1_unselect.setVisibility(View.VISIBLE);
                report_bug2_unselect.setVisibility(View.VISIBLE);
                report_bug3_unselect.setVisibility(View.VISIBLE);
                report_bug4_unselect.setVisibility(View.VISIBLE);
                report_bug6_unselect.setVisibility(View.VISIBLE);

                report_bug1_select.setVisibility(View.GONE);
                report_bug2_select.setVisibility(View.GONE);
                report_bug3_select.setVisibility(View.GONE);
                report_bug4_select.setVisibility(View.GONE);
                report_bug6_select.setVisibility(View.GONE);

            }
        });

        report_bug6_unselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bug_status = "Other concern";
                report_bug6_select.setVisibility(View.VISIBLE);
                report_bug6_unselect.setVisibility(View.GONE);

                report_bug1_unselect.setVisibility(View.VISIBLE);
                report_bug2_unselect.setVisibility(View.VISIBLE);
                report_bug3_unselect.setVisibility(View.VISIBLE);
                report_bug4_unselect.setVisibility(View.VISIBLE);
                report_bug5_unselect.setVisibility(View.VISIBLE);

                report_bug1_select.setVisibility(View.GONE);
                report_bug2_select.setVisibility(View.GONE);
                report_bug3_select.setVisibility(View.GONE);
                report_bug4_select.setVisibility(View.GONE);
                report_bug5_select.setVisibility(View.GONE);

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report_this_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            finish();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_report_product) {
            if(bug_status==""){
                Toast.makeText(ReportUser.this, "Plaese select one bug report.", Toast.LENGTH_LONG);
            }else {
                if ((ConnectionDetector.isConnectingToInternet(ReportUser.this))) {
                    new ReportUserTask(ReportUser.this, customer_id, bug_status).execute();
                } else {
                    alert.showAlertDialog(ReportUser.this, "Alert!", "Please, check your internet connection.", false);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
