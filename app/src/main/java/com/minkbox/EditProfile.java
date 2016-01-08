package com.minkbox;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Toast;

import com.minkbox.model.EditProfileBean;
import com.minkbox.network.ImageFilePath;
import com.minkbox.utils.AppConstants;
import com.minkbox.utils.AppPreferences;
import com.minkbox.utils.Function;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;



public class EditProfile extends ActionBarActivity {

    Calendar mCalendar = Calendar.getInstance();
    public EditProfile editProfileInstance = null;
    LinearLayout parentlayout;
    Dialog dialog;

    static final int DATE_DIALOG_ID = 999;
    String ed_datee = "";
    EditText edit_name, edit_lastname, edit_dob, edit_place, edit_email, edit_varification, edit_notification, edit_changpassword, category_value;
    RadioButton radio_male, radio_female;
    String name, lastname, dateofBirth, place, email, idvariffication, notification, changepassword, gender, category, userimage;
    PopupWindow pwindopassword, pwindoemail;
    EditText currentpass_edit, newpass_edit, confirmpass_edit, newemail_edit;

    String currentpasssword, newpassword, confirmpassword, changeemail;

    Toolbar toolbar;
    ArrayList<String> productcatogary = new ArrayList<String>();
    ArrayList<Integer> categoriesid = new ArrayList<Integer>();
    public String categoryName = null;
    public int categoryid = 0;

    ImageView cameraImage1;
    ImageView product_image1;
    private static int SELECT_PICTURE = 1;
    private static String selectedImagePath1 = "";
    public static String encoded_img1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        Function.hideSoftKeyboard(this);

        String title = getString(R.string.title_activity_profileedit);
        getSupportActionBar().setTitle(title);

        parentlayout = (LinearLayout) findViewById(R.id.linear3);

        parentlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Function.hideSoftKeyboard (EditProfile.this);
            }
        });

        edit_name = (EditText) findViewById(R.id.name_edit);
        edit_lastname = (EditText) findViewById(R.id.lastname_edit);
        edit_dob = (EditText) findViewById(R.id.edit_dob);
        edit_place = (EditText) findViewById(R.id.placeedit);
        edit_email = (EditText) findViewById(R.id.changeemail_edit);
        edit_varification = (EditText) findViewById(R.id.verification_edit);
        edit_notification = (EditText) findViewById(R.id.notification_edit);
        edit_changpassword = (EditText) findViewById(R.id.changepasswrd_edit);
        category_value = (EditText) findViewById(R.id.category_edit);
        radio_male = (RadioButton) findViewById(R.id.rd_male);
        radio_female = (RadioButton) findViewById(R.id.rd_female);
        cameraImage1 = (ImageView) findViewById(R.id.cameraimage1);
        product_image1 = (ImageView) findViewById(R.id.capture_img1);
        edit_name.setText(AppPreferences.getUsername(EditProfile.this));
        edit_dob.setText(AppPreferences.getUserdob(EditProfile.this));
        edit_lastname.setText(AppPreferences.getUserlastname(EditProfile.this));

        if(AppPreferences.getUserprofilepic(EditProfile.this).equalsIgnoreCase("")){
            cameraImage1.setVisibility(View.VISIBLE);
            product_image1.setVisibility(View.GONE);
        }else{
            cameraImage1.setVisibility(View.GONE);
            product_image1.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(AppPreferences.getUserprofilepic(getApplicationContext()),
                    product_image1);
        }

        if(AppPreferences.getUsergender(EditProfile.this).equalsIgnoreCase("Female")){
            radio_female.setChecked(true);
        } if(AppPreferences.getUsergender(EditProfile.this).equalsIgnoreCase("Male")){
            radio_male.setChecked(true);
        }

        product_image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImagePath1 = "";
                encoded_img1="";
                Intent galleryIntent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, SELECT_PICTURE);
            }
        });

        edit_place.setText(AppPreferences.getUsercity(EditProfile.this)+ " - " +AppPreferences.getUserpincode(EditProfile.this));
        System.out.println(" usercity is ***************** " + AppPreferences.getUsercity(getApplicationContext()));
        System.out.println(" email id is ***************** " + AppPreferences.getId(getApplicationContext()));

        //edit_place.setText(AppPreferences.getUsercity(EditProfile.this));
        cameraImage1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent galleryIntent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, SELECT_PICTURE);
            }
        });
        setcureentdateonview();
        edit_changpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopupWindowChangePassword();


            }
        });
        edit_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopupWindowChangeemail();

            }
        });
        edit_varification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfile.this, IdentiVerificationActivity.class);
                startActivity(intent);

            }
        });
        edit_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfile.this, MyLocationmapUpdate.class);
                startActivity(intent);

            }
        });
        category_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfile.this, FavouriteCategoryActivity.class);
                startActivity(intent);

            }
        });
        edit_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EditProfile.this,UserNotification.class);//NotificationActivity
                startActivity(intent);

            }
        });


    }

    private void setcureentdateonview() {
        // TODO Auto-generated method stub
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updatelabel();
            }

            private void updatelabel() {
                // TODO Auto-generated method stub
                String myformat = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myformat);
                edit_dob.setText(sdf.format(mCalendar.getTime()));

                String myformat1 = "yyyy-MM-dd";
                SimpleDateFormat sdf1 = new SimpleDateFormat(myformat1);
                ed_datee = sdf1.format(mCalendar.getTime());

            }
        };
        edit_dob.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                new DatePickerDialog(EditProfile.this, date, mCalendar
                        .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rd_male:
                if (checked)
                    break;
            case R.id.rd_female:
                if (checked)
                    break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
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
        if (id == R.id.action_right) {

            //  dateofBirth = ed_datee;
            System.out.println("encode img------------------"+encoded_img1);
            if (radio_male.isChecked()) {
                gender = "Male";
            } else if (radio_female.isChecked()) {
                gender = "Female";
            }
            edit_name.setError(null);
            edit_lastname.setError(null);
            edit_dob.setError(null);
            edit_place.setError(null);

            name = edit_name.getText().toString().trim();
            lastname = edit_lastname.getText().toString().trim();
            dateofBirth = ed_datee;
            place = edit_place.getText().toString().trim();

            EditProfileBean editProfileBean = new EditProfileBean();
            editProfileBean.setUpdatename(name);
            editProfileBean.setLastname(lastname);
            editProfileBean.setDateofbirth(dateofBirth);
            editProfileBean.setNewplace(place);
            editProfileBean.setGender(gender);
            editProfileBean.setImage(encoded_img1);
            new FinalEditAsync(editProfileBean).execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initiatePopupWindowChangePassword() {
        try {
            dialog = new Dialog(EditProfile.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.change_passwod_popup);
            currentpass_edit = (EditText) dialog.findViewById(R.id.currentpasswrd_edit);
            newpass_edit = (EditText) dialog.findViewById(R.id.newpasswrd_edit);
            confirmpass_edit = (EditText) dialog.findViewById(R.id.cnfirmpasswrd_edit);

            Button btn_ok = (Button) dialog.findViewById(R.id.okbtn);
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentpass_edit.setError(null);
                    newpass_edit.setError(null);
                    confirmpass_edit.setError(null);

                    currentpasssword = currentpass_edit.getText().toString().trim();
                    newpassword = newpass_edit.getText().toString().trim();
                    confirmpassword = confirmpass_edit.getText().toString().trim();
                    if (currentpasssword.length() == 0) {
                        Toast.makeText(getApplicationContext(), "Please fill in all of the fields", Toast.LENGTH_LONG).show();
                    }
//                    else if (currentpasssword.length() < 6) {
//                        Toast.makeText(getApplicationContext(), "The password should be contain at least 6 characters.", Toast.LENGTH_LONG).show();
//                    }
                    else if (newpassword.length() == 0) {
                        Toast.makeText(getApplicationContext(), "Please fill in all of the fields", Toast.LENGTH_LONG).show();
                    } else if (confirmpassword.length() == 0) {
                        Toast.makeText(getApplicationContext(), "Please fill in all of the fields", Toast.LENGTH_LONG).show();
                    } else if (!newpassword.equals(confirmpassword)) {
                        Toast.makeText(getApplicationContext(), "The password do not seem to match", Toast.LENGTH_LONG).show();

                    } else {
                        EditProfileBean passwordbean = new EditProfileBean();
                        passwordbean.setCureeentpassword(currentpasssword);
                        passwordbean.setNewpassword(newpassword);
                        new PasswordAsyn(passwordbean).execute();
                    }
                }
            });
            Button btn_cancel = (Button) dialog.findViewById(R.id.cancelbtn);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initiatePopupWindowChangeemail() {
        try {
            dialog = new Dialog(EditProfile.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.change_email_popup);
            newemail_edit = (EditText) dialog.findViewById(R.id.changeemail_edit);
            newemail_edit.setText(AppPreferences.getId(EditProfile.this));
            Button btn_ok = (Button) dialog.findViewById(R.id.okbtn);
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newemail_edit.setError(null);
                    changeemail = newemail_edit.getText().toString().trim();
                    EditProfileBean emailbean = new EditProfileBean();
                    emailbean.setChangeemail(changeemail);
                    new EmailAsync(emailbean).execute();
                }
            });
            Button btn_cancel = (Button) dialog.findViewById(R.id.cancelbtn);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class PasswordAsyn extends AsyncTask<Void, Void, String> {
        private ProgressDialog mProgressDialog;
        EditProfileBean passwordbean;
        private JSONObject jsonObj;
        ArrayList<Integer> registrationId;
        private int status = 0;

        public PasswordAsyn(EditProfileBean passwordbean) {
            this.passwordbean = passwordbean;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(EditProfile.this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                System.out.println("************passwordchanging*****************");
                HttpParams httpParameters = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParameters,
                        AppConstants.NETWORK_TIMEOUT_CONSTANT);
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        AppConstants.NETWORK_CONNECTION_TIMEOUT_CONSTANT);
                HttpConnectionParams.setSoTimeout(httpParameters,
                        AppConstants.NETWORK_SOCKET_TIMEOUT_CONSTANT);

                HttpClient httpclient = new DefaultHttpClient();
                System.out.println("paasword value : ------------ "
                        + AppConstants.CHANGEPASSWORD);
                HttpPost httppost = new HttpPost(AppConstants.CHANGEPASSWORD);
                jsonObj = new JSONObject();
                jsonObj.put("oldpassword", passwordbean.getCureeentpassword());
                jsonObj.put("newpassword", passwordbean.getNewpassword());
                jsonObj.put("customerid",
                        AppPreferences.getCustomerId(getApplicationContext()));


                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

                Log.d("json Data", jsonArray.toString());

                httppost.setHeader("Accept", "application/json");
                httppost.setHeader("Content-type", "application/json");
                StringEntity se = null;
                try {
                    se = new StringEntity(jsonArray.toString());

                    se.setContentEncoding(new BasicHeader(
                            HTTP.CONTENT_ENCODING, "UTF-8"));
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
                                .equalsIgnoreCase("404")) {
                            status = 404;
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
                currentpass_edit.setText("");
                newpass_edit.setText("");
                confirmpass_edit.setText("");


                Toast.makeText(EditProfile.this,
                        "Password Change Successfully...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(EditProfile.this,
                        EditProfile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                //    pwindopassword.dismiss();
            } else {
                Toast.makeText(EditProfile.this,
                        "Please Try Again Later", Toast.LENGTH_LONG).show();

            }
        }

    }

    private class EmailAsync extends AsyncTask<Void, Void, String> {
        private ProgressDialog mProgressDialog;
        EditProfileBean emailbean;
        private JSONObject jsonObj;
        ArrayList<Integer> registrationId;
        private int status = 0;

        public EmailAsync(EditProfileBean emailbean) {
            this.emailbean = emailbean;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(EditProfile.this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                System.out.println("************Emailchanging*****************");
                HttpParams httpParameters = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParameters,
                        AppConstants.NETWORK_TIMEOUT_CONSTANT);
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        AppConstants.NETWORK_CONNECTION_TIMEOUT_CONSTANT);
                HttpConnectionParams.setSoTimeout(httpParameters,
                        AppConstants.NETWORK_SOCKET_TIMEOUT_CONSTANT);

                HttpClient httpclient = new DefaultHttpClient();
                System.out.println("email value : ------------ "
                        + AppConstants.CHANGEEEMAIL);
                HttpPost httppost = new HttpPost(AppConstants.CHANGEEEMAIL);
                jsonObj = new JSONObject();
                jsonObj.put("newemail", emailbean.getChangeemail());
                jsonObj.put("customerid",
                        AppPreferences.getCustomerId(getApplicationContext()));


                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

                Log.d("json Data", jsonArray.toString());

                httppost.setHeader("Accept", "application/json");
                httppost.setHeader("Content-type", "application/json");
                StringEntity se = null;
                try {
                    se = new StringEntity(jsonArray.toString());

                    se.setContentEncoding(new BasicHeader(
                            HTTP.CONTENT_ENCODING, "UTF-8"));
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
                                .equalsIgnoreCase("404")) {
                            status = 404;
                            return jsonString;
                        } else if (jsonObj.getString("status")
                                .equalsIgnoreCase("500")) {
                            status = 500;
                            return jsonString;
                        }else if (jsonObj.getString("status")
                                .equalsIgnoreCase("600")) {
                            status = 600;
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
                AppPreferences.setId(EditProfile.this, emailbean.getChangeemail());
                Toast.makeText(EditProfile.this,
                        "Email Change Successfully...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(EditProfile.this,
                        EditProfile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }else
            if (status == 600) {
//                {"message":"This Email Id already exists","result":[{"msg":"Error"}],"status":"600"}
                Toast.makeText(EditProfile.this,
                        "This Email already exist. Please choose other one.", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(EditProfile.this,
                        "Error Occured Failed..", Toast.LENGTH_LONG).show();

            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("11111");
        if (resultCode == RESULT_OK) {
            System.out.println("12222");
            if (requestCode == SELECT_PICTURE) {
                System.out.println("13333");
                Uri selectedImageUri = data.getData();
                Bitmap newBitmap = null;

                if (selectedImagePath1.equalsIgnoreCase("")) {
                    System.out.println("14444");

                    selectedImagePath1 = ImageFilePath.getPath(EditProfile.this,
                            selectedImageUri);
                    product_image1.setVisibility(View.VISIBLE);
                    cameraImage1.setVisibility(View.GONE);

                    Log.d("img path", selectedImagePath1);
                    newBitmap = getImage(selectedImageUri);
                    encodeImg1Tobase64(newBitmap);
                    Bitmap bm = newBitmap;
                    try {
                        ExifInterface ei = new ExifInterface(selectedImagePath1);
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                bm = rotateImage(newBitmap, 270);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                bm = rotateImage(newBitmap, 90);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                bm = rotateImage(newBitmap, 180);
                                break;
                            // etc.
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    product_image1.setImageBitmap(bm);
                }


            }
        }else{
            System.out.println("121212");
        }
    }

    public Bitmap getImage(Uri uri) {
        InputStream stream;
        Bitmap newBitmap = null;
        try {
            stream = EditProfile.this.getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap realImage = BitmapFactory.decodeStream(stream, null, options);
//            Bitmap realImage = BitmapFactory.decodeStream(stream);
            float ratio = Math.min((float) 800 / realImage.getWidth(),
                    (float) 800 / realImage.getHeight());
            Log.i("PixMe", "Ratio: " + String.valueOf(ratio));
            int width = Math.round((float) ratio * realImage.getWidth());
            int height = Math.round((float) ratio * realImage.getHeight());
            newBitmap = Bitmap.createScaledBitmap(realImage, width, height,
                    true);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            Log.d("old size",
                    realImage.getWidth() + " " + realImage.getHeight());
            Log.d("new size",
                    newBitmap.getWidth() + " " + newBitmap.getHeight());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return newBitmap;
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public static String encodeImg1Tobase64(Bitmap image1) {
        Bitmap immagex = image1;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        encoded_img1 = imageEncoded;
        Log.e("LOOK1", imageEncoded);
        System.out.println("imageEncoded1------------------------"
                + encoded_img1);
        return imageEncoded;
    }

    private class FinalEditAsync extends AsyncTask<Void, Void, String> {
        private ProgressDialog mProgressDialog;
        EditProfileBean editProfileBean;
        private JSONObject jsonObj;
        ArrayList<Integer> registrationId;
        private int status = 0;
        String user_profile_pic;
        private String TAG = FinalEditAsync.class.getSimpleName();
        public FinalEditAsync(EditProfileBean editProfileBean) {
            this.editProfileBean = editProfileBean;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(EditProfile.this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                System.out.println("************Final edit profile changing json*****************");
                HttpParams httpParameters = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParameters,
                        AppConstants.NETWORK_TIMEOUT_CONSTANT);
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        AppConstants.NETWORK_CONNECTION_TIMEOUT_CONSTANT);
                HttpConnectionParams.setSoTimeout(httpParameters,
                        AppConstants.NETWORK_SOCKET_TIMEOUT_CONSTANT);

                HttpClient httpclient = new DefaultHttpClient();
                System.out.println("email value : ------------ "
                        + AppConstants.FINALEDITPROFILE);
                HttpPost httppost = new HttpPost(AppConstants.FINALEDITPROFILE);
                jsonObj = new JSONObject();
                System.out.println("imagessssssssssss" + editProfileBean.getImage());
                jsonObj.put("profile_pic_user", editProfileBean.getImage());
                jsonObj.put("new_name", editProfileBean.getUpdatename());
                jsonObj.put("lastname", editProfileBean.getLastname());
                jsonObj.put("date_of_birth", editProfileBean.getDateofbirth());
                jsonObj.put("new_place", editProfileBean.getNewplace());
                jsonObj.put("gender", editProfileBean.getGender());
                jsonObj.put("customerid",
                        AppPreferences.getCustomerId(getApplicationContext()));


                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

                Log.d("json Data", jsonArray.toString());

                httppost.setHeader("Accept", "application/json");
                httppost.setHeader("Content-type", "application/json");
                StringEntity se = null;
                try {
                    se = new StringEntity(jsonArray.toString());

                    se.setContentEncoding(new BasicHeader(
                            HTTP.CONTENT_ENCODING, "UTF-8"));
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

                        try {
                            JSONObject jsonObj = new JSONObject(jsonString);
                            JSONArray jsonChildArray = jsonObj.getJSONArray("result");//result


                            if (jsonObj.getString("status").equalsIgnoreCase("200")) {
                                status = 200;

                                Log.v(TAG, "after jsonChildArray" + jsonChildArray + " result array length is : " + jsonChildArray.length());
                                if (jsonChildArray != null && jsonChildArray.length() > 0) {
                                    for (int i = 0; i < jsonChildArray.length(); i++) {
                                        JSONObject jsonchildObj = jsonChildArray.getJSONObject(i);

                                        user_profile_pic = jsonchildObj.getString("profile_pic");
                                    }


                                } else {
                                }
                            } else if (jsonObj.getString("status").equalsIgnoreCase("404")) {
                                Log.v(TAG, "Status error");
                                status = 404;
                            } else if (jsonObj.getString("status").equalsIgnoreCase("500")) {
                                status = 500;
                                Log.v(TAG, "No Data Recieved in Request");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
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

                Toast.makeText(EditProfile.this,
                        "Profile edit successfully.", Toast.LENGTH_LONG).show();
                AppPreferences.setUsername(EditProfile.this, editProfileBean.getUpdatename());
                AppPreferences.setUserprofilepic(EditProfile.this, user_profile_pic);
                AppPreferences.setUserlastname(EditProfile.this, editProfileBean.getLastname());
                AppPreferences.setUserdob(EditProfile.this, editProfileBean.getDateofbirth());
                AppPreferences.setUsergender(EditProfile.this, editProfileBean.getGender());
                System.out.println("gender" + AppPreferences.getUsergender(EditProfile.this));
                encoded_img1="";
                selectedImagePath1="";

                Intent intent = new Intent(EditProfile.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(EditProfile.this,
                        "Error Occured Failed..", Toast.LENGTH_LONG).show();
            }
        }

    }

}
