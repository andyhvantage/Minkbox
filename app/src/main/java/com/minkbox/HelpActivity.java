package com.minkbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.LikeView;
import com.inthecheesefactory.lib.fblike.widget.FBLikeView;
import com.minkbox.utils.AppConstants;

public class HelpActivity extends ActionBarActivity {

    private WebView help_detail;
    Toolbar toolbar;
    RelativeLayout termconditionlayout, privacypolicylayout, safetylayout, ruleslayout,
            likefblayout, twitterlayout, faqlayout, versionlayout;
    PopupWindow pwindoLike;
    LinearLayout btnLoginToLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_help);

//        FBLikeView fbLikeView = (FBLikeView) findViewById(R.id.fbLikeView);
//        fbLikeView.getLikeView().setObjectIdAndType(
//                AppConstants.FBLIKEAPPURL,
//                LikeView.ObjectType.OPEN_GRAPH);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = getString(R.string.action_help);
        getSupportActionBar().setTitle(title);

        termconditionlayout = (RelativeLayout) findViewById(R.id.termcnditnlayout);
        privacypolicylayout = (RelativeLayout) findViewById(R.id.privecypolicylayout);
        safetylayout = (RelativeLayout) findViewById(R.id.safetylayout);
        ruleslayout = (RelativeLayout) findViewById(R.id.ruleslayout);
        likefblayout = (RelativeLayout) findViewById(R.id.likefblayout);
        twitterlayout = (RelativeLayout) findViewById(R.id.minktwitterlayout);
        faqlayout = (RelativeLayout) findViewById(R.id.faqlayout);
        versionlayout = (RelativeLayout) findViewById(R.id.versionlayout);

        termconditionlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HelpActivity.this, TermConditionActivity.class);
                startActivity(i);
            }
        });
        privacypolicylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HelpActivity.this, PrivacyPolicyActivity.class);
                startActivity(i);
            }
        });
        safetylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HelpActivity.this, SafetyActivity.class);
                startActivity(i);
            }
        });
        ruleslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HelpActivity.this, RulesActivity.class);
                startActivity(i);
            }
        });
        faqlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HelpActivity.this, FAQActivity.class);
                startActivity(i);
            }
        });
        likefblayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiatePopupWindowLike();
                System.out.println(" ----------------- initiate popup like window call ------------------");
//                String myUrl = "https://www.facebook.com/Minkbox-1656985724573308/?skip_nax_wizard=true";
//                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(myUrl)) ;
//                startActivity(i);
            }
        });
        twitterlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HelpActivity.this, Twitter_Activity.class);//Intent.ACTION_VIEW
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initiatePopupWindowLike() {
        try {
            LayoutInflater inflater = (LayoutInflater) HelpActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup_like_layout,
                    (ViewGroup) findViewById(R.id.pop_up_like_parent));
            pwindoLike = new PopupWindow(layout, LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT, true);
            pwindoLike.showAtLocation(layout, Gravity.CENTER, 0, 0);

            FBLikeView fbLikeView = (FBLikeView) layout.findViewById(R.id.fbLikeView);
                  fbLikeView.getLikeView().setObjectIdAndType(
                          AppConstants.FBLIKEAPPURL,
                          LikeView.ObjectType.OPEN_GRAPH);

            Button exitButton =  (Button)layout.findViewById(R.id.exit_button);
            exitButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    pwindoLike.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isLoggedIn() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FBLikeView.onActivityResult(requestCode, resultCode, data);
    }
}
