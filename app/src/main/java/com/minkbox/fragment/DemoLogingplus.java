//package com.minkbox.fragment;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.content.IntentSender;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.plus.Plus;
//import com.google.android.gms.plus.model.people.Person;
//import com.minkbox.R;
//
///**
// * Created by MMFA-YOGESH on 8/28/2015.
// */
//public class DemoLogingplus extends Fragment implements
//        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
//    EditText edit_name, edit_pass;
//    Button btn_login;
//    String username, password;
//    Button btn_gplussign, btn_gplusout;
//    private String TAG = "LoginRegistrationActivity";
//    // LoginButton loginWithFacebook;
//    RelativeLayout otherView;
//    ProgressDialog progDialog;
//    String facebook_id, facebook_email, facebook_name, gplusId, gplusName,
//            gplusEmail;
//    String type;
//    String loginType = "";
//    ProgressDialog asynDialog;
//    String status = "", result = "", loginId = "";
//
//    private GoogleApiClient mGoogleApiClient;
//    private boolean mSignInClicked;
//    private ConnectionResult mConnectionResult;
//    private boolean mIntentInProgress;
//    private static final int RC_SIGN_IN = 0;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View rootview = inflater.inflate(R.layout.user_login_fragment, container, false);
//        return rootview;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getActivity().findViewById(R.id.loginbtn);// SignInButton
//        //  btn_gplussign.setBackgroundResource(R.drawable.google);
//        btn_gplussign.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                loginType = "googlePlus";
//                signInWithGplus();
//            }
//        });
//
//        getActivity().findViewById(R.id.btn_sign_out);
//        btn_gplusout.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                signOutFromGplus();
//            }
//        });
//        getActivity().findViewById(R.id.linear);
//        otherView.setVisibility(View.VISIBLE);
//
//        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this).addApi(Plus.API)
//                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
//
//    }
//
//    private void signInWithGplus() {
//        if (!mGoogleApiClient.isConnecting()) {
//            mSignInClicked = true;
//            resolveSignInError();
//        }
//    }
//
//    private void resolveSignInError() {
//        System.out.println("mConnectionResult is ---------- "
//                + mConnectionResult);
//        if (mConnectionResult.hasResolution()) {
//            try {
//                mIntentInProgress = true;
//                mConnectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
//            } catch (IntentSender.SendIntentException e) {
//                mIntentInProgress = false;
//                mGoogleApiClient.connect();
//            }
//        }
//    }
//
//    // public void onBackPressed() {
//    // super.onBackPressed();
//    // Intent i = new Intent(MilesDealActivity.this,
//    // TextSellerMainActivity.class);
//    // i.putExtra("categoryname", categoryname);
//    // i.putExtra("categoryid", categoryid);
//    // i.putExtra("productid", productid);
//    // startActivity(i);
//    // finish();
//    // }
//
//    @Override
//    public void onConnected(Bundle arg0) {
//        mSignInClicked = false;
//        getProfileInformation();
//        updateUI(true);
//    }
//
//    public void onConnectionSuspended(int arg0) {
//        mGoogleApiClient.connect();
//        // updateUI(false);
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult result) {
//        if (!result.hasResolution()) {
//            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), getActivity(),
//                    0).show();
//            return;
//        }
//
//        if (!mIntentInProgress) {
//            mConnectionResult = result;
//
//            if (mSignInClicked) {
//                resolveSignInError();
//            }
//        }
//    }
//
//    public void onStart() {
//        super.onStart();
//        mGoogleApiClient.connect();
//    }
//
//    public void onStop() {
//        super.onStop();
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
//    }
//
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RC_SIGN_IN) {
////            if (resultCode != RESULT_OK) {
////                mSignInClicked = false;
////            }
//
//            mIntentInProgress = false;
//
//            if (!mGoogleApiClient.isConnecting()) {
//                mGoogleApiClient.connect();
//            }
//        } else {
//            System.out
//                    .println("----------- society app called -------------------requestCode ---------"
//                            + requestCode + " resultCode : " + resultCode);
////           Session.getActiveSession().onActivityResult(this, requestCode,
////                    resultCode, data);
//        }
//    }
//
//    // ////////////g+login/////////////////////////
//    private void signOutFromGplus() {
//        if (mGoogleApiClient.isConnected()) {
//            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
//            mGoogleApiClient.disconnect();
//            mGoogleApiClient.connect();
//            updateUI(false);
//        }
//    }
//
//    private void updateUI(boolean isSignedIn) {
//        if (isSignedIn) {
//            btn_gplussign.setVisibility(View.GONE);
//            btn_gplusout.setVisibility(View.VISIBLE);
//        } else {
//            btn_gplussign.setVisibility(View.VISIBLE);
//            btn_gplusout.setVisibility(View.GONE);
//        }
//    }
//
//    private void getProfileInformation() {
//        try {
//            System.out.println("people api is : --- " + Plus.PeopleApi
//                    + " mGoogleApiClient : --- " + mGoogleApiClient);
//            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
//                Person currentPerson = Plus.PeopleApi
//                        .getCurrentPerson(mGoogleApiClient);
//                String personName = currentPerson.getDisplayName();
//                String id = currentPerson.getId();
//                String personPhotoUrl = currentPerson.getImage().getUrl();
//                String personGooglePlusProfile = currentPerson.getUrl();
//                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
//                gplusId = currentPerson.getId();
//                gplusName = currentPerson.getDisplayName();
//                gplusEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
//                type = "Gplus";
////                new Login(getActivity()).execute(type, gplusId,
////                        gplusName, gplusEmail);
//                Log.e(TAG, "Name: " + personName + ", plusProfile: "
//                        + personGooglePlusProfile + ", email: " + email
//                        + ", Image: " + personPhotoUrl + " person id : " + id);
//
//            } else {
//                Toast.makeText(getActivity(),
//                        "Person information is null", Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
