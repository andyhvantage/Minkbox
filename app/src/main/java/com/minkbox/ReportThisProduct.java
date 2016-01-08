package com.minkbox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.minkbox.network.ReportTask;
import com.minkbox.utils.ConnectionDetector;
import com.minkbox.utils.DialogManager;

public class ReportThisProduct extends AppCompatActivity {
    DialogManager alert = new DialogManager();
    int product_id;
    String bug_status="";
    ImageView report_bug1_unselect;
    ImageView report_bug2_unselect;
    ImageView report_bug3_unselect;
    ImageView report_bug4_unselect;
    ImageView report_bug5_unselect;
    ImageView report_bug6_unselect;
    ImageView report_bug7_unselect;
    ImageView report_bug8_unselect;
    ImageView report_bug9_unselect;
    ImageView report_bug1_select;
    ImageView report_bug2_select;
    ImageView report_bug3_select;
    ImageView report_bug4_select;
    ImageView report_bug5_select;
    ImageView report_bug6_select;
    ImageView report_bug7_select;
    ImageView report_bug8_select;
    ImageView report_bug9_select;
    Toolbar toolbar;
    public static ReportThisProduct instance = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_this_product);
        Bundle extras = getIntent().getExtras();
        product_id = extras.getInt("productid");
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title  = "Report this product";
        getSupportActionBar().setTitle(title);
        instance=this;
        report_bug1_unselect = (ImageView) findViewById(R.id.bugs1);
        report_bug2_unselect = (ImageView) findViewById(R.id.bugs2);
        report_bug3_unselect = (ImageView) findViewById(R.id.bugs3);
        report_bug4_unselect = (ImageView) findViewById(R.id.bugs4);
        report_bug5_unselect = (ImageView) findViewById(R.id.bugs5);
        report_bug6_unselect = (ImageView) findViewById(R.id.bugs6);
        report_bug7_unselect = (ImageView) findViewById(R.id.bugs7);
        report_bug8_unselect = (ImageView) findViewById(R.id.bugs8);
        report_bug9_unselect = (ImageView) findViewById(R.id.bugs9);

        report_bug1_select = (ImageView) findViewById(R.id.bugs1_selected);
        report_bug2_select = (ImageView) findViewById(R.id.bugs2_selected);
        report_bug3_select = (ImageView) findViewById(R.id.bugs3_selected);
        report_bug4_select = (ImageView) findViewById(R.id.bugs4_selected);
        report_bug5_select = (ImageView) findViewById(R.id.bugs5_selected);
        report_bug6_select = (ImageView) findViewById(R.id.bugs6_selected);
        report_bug7_select = (ImageView) findViewById(R.id.bugs7_selected);
        report_bug8_select = (ImageView) findViewById(R.id.bugs8_selected);
        report_bug9_select = (ImageView) findViewById(R.id.bugs9_selected);

        report_bug1_unselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bug_status = "wrong_ategory";
                report_bug1_select.setVisibility(View.VISIBLE);
                report_bug1_unselect.setVisibility(View.GONE);

                report_bug2_unselect.setVisibility(View.VISIBLE);
                report_bug3_unselect.setVisibility(View.VISIBLE);
                report_bug4_unselect.setVisibility(View.VISIBLE);
                report_bug5_unselect.setVisibility(View.VISIBLE);
                report_bug6_unselect.setVisibility(View.VISIBLE);
                report_bug7_unselect.setVisibility(View.VISIBLE);
                report_bug8_unselect.setVisibility(View.VISIBLE);
                report_bug9_unselect.setVisibility(View.VISIBLE);

                report_bug2_select.setVisibility(View.GONE);
                report_bug3_select.setVisibility(View.GONE);
                report_bug4_select.setVisibility(View.GONE);
                report_bug5_select.setVisibility(View.GONE);
                report_bug6_select.setVisibility(View.GONE);
                report_bug7_select.setVisibility(View.GONE);
                report_bug8_select.setVisibility(View.GONE);
                report_bug9_select.setVisibility(View.GONE);
            }
        });

        report_bug2_unselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bug_status = "people_or_animals";
                report_bug2_select.setVisibility(View.VISIBLE);
                report_bug2_unselect.setVisibility(View.GONE);

                report_bug1_unselect.setVisibility(View.VISIBLE);
                report_bug3_unselect.setVisibility(View.VISIBLE);
                report_bug4_unselect.setVisibility(View.VISIBLE);
                report_bug5_unselect.setVisibility(View.VISIBLE);
                report_bug6_unselect.setVisibility(View.VISIBLE);
                report_bug7_unselect.setVisibility(View.VISIBLE);
                report_bug8_unselect.setVisibility(View.VISIBLE);
                report_bug9_unselect.setVisibility(View.VISIBLE);

                report_bug1_select.setVisibility(View.GONE);
                report_bug3_select.setVisibility(View.GONE);
                report_bug4_select.setVisibility(View.GONE);
                report_bug5_select.setVisibility(View.GONE);
                report_bug6_select.setVisibility(View.GONE);
                report_bug7_select.setVisibility(View.GONE);
                report_bug8_select.setVisibility(View.GONE);
                report_bug9_select.setVisibility(View.GONE);
            }
        });

        report_bug3_unselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bug_status = "joke";
                report_bug3_select.setVisibility(View.VISIBLE);
                report_bug3_unselect.setVisibility(View.GONE);

                report_bug1_unselect.setVisibility(View.VISIBLE);
                report_bug2_unselect.setVisibility(View.VISIBLE);
                report_bug4_unselect.setVisibility(View.VISIBLE);
                report_bug5_unselect.setVisibility(View.VISIBLE);
                report_bug6_unselect.setVisibility(View.VISIBLE);
                report_bug7_unselect.setVisibility(View.VISIBLE);
                report_bug8_unselect.setVisibility(View.VISIBLE);
                report_bug9_unselect.setVisibility(View.VISIBLE);

                report_bug1_select.setVisibility(View.GONE);
                report_bug2_select.setVisibility(View.GONE);
                report_bug4_select.setVisibility(View.GONE);
                report_bug5_select.setVisibility(View.GONE);
                report_bug6_select.setVisibility(View.GONE);
                report_bug7_select.setVisibility(View.GONE);
                report_bug8_select.setVisibility(View.GONE);
                report_bug9_select.setVisibility(View.GONE);
            }
        });

        report_bug4_unselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bug_status = "fake_item";
                report_bug4_select.setVisibility(View.VISIBLE);
                report_bug4_unselect.setVisibility(View.GONE);

                report_bug1_unselect.setVisibility(View.VISIBLE);
                report_bug2_unselect.setVisibility(View.VISIBLE);
                report_bug3_unselect.setVisibility(View.VISIBLE);
                report_bug5_unselect.setVisibility(View.VISIBLE);
                report_bug6_unselect.setVisibility(View.VISIBLE);
                report_bug7_unselect.setVisibility(View.VISIBLE);
                report_bug8_unselect.setVisibility(View.VISIBLE);
                report_bug9_unselect.setVisibility(View.VISIBLE);
                report_bug1_select.setVisibility(View.GONE);
                report_bug2_select.setVisibility(View.GONE);
                report_bug3_select.setVisibility(View.GONE);
                report_bug5_select.setVisibility(View.GONE);
                report_bug6_select.setVisibility(View.GONE);
                report_bug7_select.setVisibility(View.GONE);
                report_bug8_select.setVisibility(View.GONE);
                report_bug9_select.setVisibility(View.GONE);
            }
        });

        report_bug5_unselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bug_status = "explicit_content";
                report_bug5_select.setVisibility(View.VISIBLE);
                report_bug5_unselect.setVisibility(View.GONE);

                report_bug1_unselect.setVisibility(View.VISIBLE);
                report_bug2_unselect.setVisibility(View.VISIBLE);
                report_bug3_unselect.setVisibility(View.VISIBLE);
                report_bug4_unselect.setVisibility(View.VISIBLE);
                report_bug6_unselect.setVisibility(View.VISIBLE);
                report_bug7_unselect.setVisibility(View.VISIBLE);
                report_bug8_unselect.setVisibility(View.VISIBLE);
                report_bug9_unselect.setVisibility(View.VISIBLE);
                report_bug1_select.setVisibility(View.GONE);
                report_bug2_select.setVisibility(View.GONE);
                report_bug3_select.setVisibility(View.GONE);
                report_bug4_select.setVisibility(View.GONE);
                report_bug6_select.setVisibility(View.GONE);
                report_bug7_select.setVisibility(View.GONE);
                report_bug8_select.setVisibility(View.GONE);
                report_bug9_select.setVisibility(View.GONE);
            }
        });

        report_bug6_unselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bug_status = "Does_not_match_with_the_image";
                report_bug6_select.setVisibility(View.VISIBLE);
                report_bug6_unselect.setVisibility(View.GONE);

                report_bug1_unselect.setVisibility(View.VISIBLE);
                report_bug2_unselect.setVisibility(View.VISIBLE);
                report_bug3_unselect.setVisibility(View.VISIBLE);
                report_bug4_unselect.setVisibility(View.VISIBLE);
                report_bug5_unselect.setVisibility(View.VISIBLE);
                report_bug7_unselect.setVisibility(View.VISIBLE);
                report_bug8_unselect.setVisibility(View.VISIBLE);
                report_bug9_unselect.setVisibility(View.VISIBLE);
                report_bug1_select.setVisibility(View.GONE);
                report_bug2_select.setVisibility(View.GONE);
                report_bug3_select.setVisibility(View.GONE);
                report_bug4_select.setVisibility(View.GONE);
                report_bug5_select.setVisibility(View.GONE);
                report_bug7_select.setVisibility(View.GONE);
                report_bug8_select.setVisibility(View.GONE);
                report_bug9_select.setVisibility(View.GONE);
            }
        });

        report_bug7_unselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bug_status = "food";
                report_bug7_select.setVisibility(View.VISIBLE);
                report_bug7_unselect.setVisibility(View.GONE);

                report_bug1_unselect.setVisibility(View.VISIBLE);
                report_bug2_unselect.setVisibility(View.VISIBLE);
                report_bug3_unselect.setVisibility(View.VISIBLE);
                report_bug4_unselect.setVisibility(View.VISIBLE);
                report_bug5_unselect.setVisibility(View.VISIBLE);
                report_bug6_unselect.setVisibility(View.VISIBLE);
                report_bug8_unselect.setVisibility(View.VISIBLE);
                report_bug9_unselect.setVisibility(View.VISIBLE);
                report_bug1_select.setVisibility(View.GONE);
                report_bug2_select.setVisibility(View.GONE);
                report_bug3_select.setVisibility(View.GONE);
                report_bug4_select.setVisibility(View.GONE);
                report_bug5_select.setVisibility(View.GONE);
                report_bug6_select.setVisibility(View.GONE);
                report_bug8_select.setVisibility(View.GONE);
                report_bug9_select.setVisibility(View.GONE);
            }
        });

        report_bug8_unselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bug_status = "drugs_or_medicines";
                report_bug8_select.setVisibility(View.VISIBLE);
                report_bug8_unselect.setVisibility(View.GONE);

                report_bug1_unselect.setVisibility(View.VISIBLE);
                report_bug2_unselect.setVisibility(View.VISIBLE);
                report_bug3_unselect.setVisibility(View.VISIBLE);
                report_bug4_unselect.setVisibility(View.VISIBLE);
                report_bug5_unselect.setVisibility(View.VISIBLE);
                report_bug6_unselect.setVisibility(View.VISIBLE);
                report_bug7_unselect.setVisibility(View.VISIBLE);
                report_bug9_unselect.setVisibility(View.VISIBLE);
                report_bug1_select.setVisibility(View.GONE);
                report_bug2_select.setVisibility(View.GONE);
                report_bug3_select.setVisibility(View.GONE);
                report_bug4_select.setVisibility(View.GONE);
                report_bug5_select.setVisibility(View.GONE);
                report_bug6_select.setVisibility(View.GONE);
                report_bug7_select.setVisibility(View.GONE);
                report_bug9_select.setVisibility(View.GONE);
            }
        });

        report_bug9_unselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bug_status = "others";
                report_bug9_select.setVisibility(View.VISIBLE);
                report_bug9_unselect.setVisibility(View.GONE);

                report_bug1_unselect.setVisibility(View.VISIBLE);
                report_bug2_unselect.setVisibility(View.VISIBLE);
                report_bug3_unselect.setVisibility(View.VISIBLE);
                report_bug4_unselect.setVisibility(View.VISIBLE);
                report_bug5_unselect.setVisibility(View.VISIBLE);
                report_bug6_unselect.setVisibility(View.VISIBLE);
                report_bug7_unselect.setVisibility(View.VISIBLE);
                report_bug8_unselect.setVisibility(View.VISIBLE);
                report_bug1_select.setVisibility(View.GONE);
                report_bug2_select.setVisibility(View.GONE);
                report_bug3_select.setVisibility(View.GONE);
                report_bug4_select.setVisibility(View.GONE);
                report_bug5_select.setVisibility(View.GONE);
                report_bug6_select.setVisibility(View.GONE);
                report_bug7_select.setVisibility(View.GONE);
                report_bug8_select.setVisibility(View.GONE);
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

        if (id == android.R.id.home) {
            finish();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_report_product) {
            if (bug_status=="") {
                Toast.makeText(getApplicationContext(),"Plaese select one bug report.",Toast.LENGTH_LONG);
            } else {
                if ((ConnectionDetector.isConnectingToInternet(ReportThisProduct.this))) {
                    new ReportTask(ReportThisProduct.this, product_id, bug_status).execute();

                } else {
                    alert.showAlertDialog(ReportThisProduct.this, "Alert!", "Please, check your internet connection.", false);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}
