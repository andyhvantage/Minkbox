package com.minkbox.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.minkbox.R;
import com.minkbox.UserRegisterLoginActivity;
import com.minkbox.model.RegisterBean;
import com.minkbox.utils.AppConstants;
import com.minkbox.utils.AppPreferences;
import com.minkbox.utils.DialogManager;
import com.minkbox.utils.Function;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;


public class UserRegisterFragment extends Fragment {
    EditText edit_name, edit_email, edit_password;
    Button btn_register;
    String name, email, password;
    HttpClient client;
    HttpPost post;
    String status = "", result = "", registrationId = "";
    Button ok;
    Button cancle;
    PopupWindow pwindo;
    DialogManager alert = new DialogManager();
    static final int DATE_DIALOG_ID = 999;
    String ed_datee = "";
    RelativeLayout parentlayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_register_fragment, null);
        edit_name = (EditText) rootView.findViewById(R.id.name_edit);
        edit_email = (EditText) rootView.findViewById(R.id.email_edit);
        edit_password = (EditText) rootView.findViewById(R.id.password_edit);

        btn_register = (Button) rootView.findViewById(R.id.register_btn);

        parentlayout = (RelativeLayout) rootView.findViewById(R.id.linear);

        parentlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Function.hideSoftKeyboard(getActivity());
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!new Function().CheckGpsEnableOrNot(getActivity())) {
                    showGpsSettingsAlert("GPS Settings...",
                            "GPS is not enabled. Do you want to go to settings menu?",
                            getActivity());
                }

                edit_name.setError(null);
                edit_email.setError(null);
                edit_password.setError(null);

                Log.d("Json data", "111");
                name = edit_name.getText().toString().trim();
                email = edit_email.getText().toString().trim();
                password = edit_password.getText().toString().trim();

                if (name.length() == 0) {
                    edit_name.setError(getResources().getString(
                            R.string.error_field_required));
                } else if (email.length() == 0) {
                    edit_email.setError(getResources().getString(
                            R.string.error_field_required));
                } else if (!email.contains("@")) {
                    edit_email.setError(getString(R.string.error_invalid_email));
                } else if (password.length() == 0) {
                    edit_password.setError(getResources().getString(
                            R.string.error_field_required));
                } else if (password.length() < 6) {
                    edit_password.setError(getResources().getString(
                            R.string.error_field_password_lenght));
                } else {
                    RegisterBean registerBean = new RegisterBean();
                    registerBean.setName(name);
                    registerBean.setEmail(email);
                    registerBean.setPassword(password);
                    new RegisterAsyn(registerBean).execute();
                }

            }
        });

        return rootView;
    }

    public void showGpsSettingsAlert(String tittle, String message,
                                     final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(tittle);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                        //	count = 1;
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
//						Intent i = new Intent(SplashScreenActivity.this,
//								 HomeActivity.class);
//								 startActivity(i);
                        getActivity().finish();
                    }
                });
        alertDialog.show();
    }

    private void enableMapLocation() {
        try {
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup_enable_map_location,
                    (ViewGroup) getActivity().findViewById(R.id.pop_up_exit_element));
            pwindo = new PopupWindow(layout, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

            ok = (Button) layout.findViewById(R.id.ok);
            ok.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class RegisterAsyn extends AsyncTask<Void, Void, String> {
        private ProgressDialog mProgressDialog;
        RegisterBean registerBean;
        private JSONObject jsonObj;
        ArrayList<Integer> registrationId;
        private int status = 0;

        public RegisterAsyn(RegisterBean registerBean) {
            this.registerBean = registerBean;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                System.out.println("************HYPERLOCAL*****************");
                HttpParams httpParameters = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParameters,
                        AppConstants.NETWORK_TIMEOUT_CONSTANT);
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        AppConstants.NETWORK_CONNECTION_TIMEOUT_CONSTANT);
                HttpConnectionParams.setSoTimeout(httpParameters,
                        AppConstants.NETWORK_SOCKET_TIMEOUT_CONSTANT);

                HttpClient httpclient = new DefaultHttpClient();
                System.out.println("Registartion value : ------------ "
                        + AppConstants.REGISTRATION);
                HttpPost httppost = new HttpPost(AppConstants.REGISTRATION);
                jsonObj = new JSONObject();
                jsonObj.put("user_name", registerBean.getName());
                jsonObj.put("user_email_id", registerBean.getEmail());
                jsonObj.put("user_pwd", registerBean.getPassword());
                jsonObj.put("user_lat",
                        AppPreferences.getLatutude(getActivity()));// "0"
                jsonObj.put("user_long",
                        AppPreferences.getLongitude(getActivity()));// "0"

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

                Log.d("json Data", jsonArray.toString());

               // httppost.setHeader("Accept", "application/json");
                //httppost.setHeader("Content-type", "application/json");
                StringEntity se = null;
                try {
                    se = new StringEntity(jsonArray.toString(), "UTF-8");

                   // se.setContentEncoding(new BasicHeader(
                     //       HTTP.CONTENT_ENCODING, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.v("json : ", jsonArray.toString(2));
                System.out.println("Sent JSON is : " + jsonArray.toString());
                httppost.setEntity(se);
                HttpResponse response = null;
                response = httpclient.execute(httppost);
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(response
                            .getEntity().getContent(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String jsonString = "";
                try {
                    jsonString = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("JSONString response is : " + jsonString);
                if (jsonString != null) {
                    if (jsonString.contains("result")) {
                        JSONObject jsonObj = new JSONObject(jsonString);
                        jsonString = jsonObj.getString("result");
                        // JSONArray jsonChildArray = jsonObj
                        // .getJSONArray("result");
                        if (jsonObj.getString("status").equalsIgnoreCase("200")) {
                            System.out
                                    .println("--------- message 200 got ----------");
                            status = 200;
                            return jsonString;
                        } else if (jsonObj.getString("status")
                                .equalsIgnoreCase("400")) {
                            status = 400;
                            return jsonString;
                        } else if (jsonObj.getString("status")
                                .equalsIgnoreCase("500")) {
                            status = 500;
                            return jsonString;
                        }
                    }
                }

            } catch (ConnectTimeoutException e) {
                System.out.println("Time out");
                status = 600;
            } catch (SocketTimeoutException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            Log.d("status", status + "");
            if (status == 200) {
                edit_name.setText("");
                edit_email.setText("");
                edit_password.setText("");

                Toast.makeText(getActivity(),
                        "Registered Successfully...", Toast.LENGTH_LONG).show();
                AppPreferences.setId(getActivity().getApplicationContext(), registerBean.getEmail());
                Intent intent = new Intent(getActivity(),
                        UserRegisterLoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }else if(status == 400)
            {
                Toast.makeText(getActivity(),
                        "Email Id already exists", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(getActivity(),
                        "Please Try Again Later", Toast.LENGTH_LONG).show();
            }
        }

    }

      @Override
    public void onResume() {
          super.onResume();
          getActivity().setTitle("SignUp");
      }

}
