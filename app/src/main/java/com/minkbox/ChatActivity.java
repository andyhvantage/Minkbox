package com.minkbox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.minkbox.adapter.CustomAdapter;
import com.minkbox.databace.ChatDataBaseHelper;
import com.minkbox.databace.DataBaseHelper;
import com.minkbox.model.ChatBean;
import com.minkbox.model.Product;
import com.minkbox.utils.AppConstants;
import com.minkbox.utils.AppPreferences;
import com.minkbox.utils.Config;
import com.minkbox.utils.Controller;
import com.minkbox.utils.Function;
import com.nostra13.universalimageloader.core.ImageLoader;

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
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;


public class ChatActivity extends ActionBarActivity implements View.OnClickListener {
    public static String name = "";
    public static String email = "";
    public static String imei = "";
    static TextView date_tv;
    public ArrayList<ChatBean> CustomListViewValuesArr = new ArrayList<ChatBean>();
    Toolbar toolbar;
    EditText chatMessage;
    TextView productName_tv;
    TextView productPrice_tv;
    TextView profileName_tv;
    ImageView chat_btn;
    ListView chat_lv;
    Controller aController;
    // Create a broadcast receiver to get message and show on screen
    private BroadcastReceiver mHandleMessageReceiver, handleMessageReceiver;
    CustomAdapter adapter;
    DataBaseHelper db;
    Product product;
    ImageView userImage;
    ImageView productImage;
    String serverURL = AppConstants.SENDCHAT;
    String productId = "", reciverId = "", productName, productPrice, productOwnerName,productRecieverName,recieverImage="", ProductImage = "", ProfileImage = "";
    AsyncTask<Void, Void, Void> mRegisterTask;
    Intent intent;
    static ChatActivity chatActivityInstance = null;
    boolean fromNotificationFlag = false;
    boolean todayDateForChatFlag = false;

    public static void setDate(String date) {
        date_tv.setText(date);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatActivityInstance = this;

        Function.cancelNotification(this, 0);

        mHandleMessageReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                String newMessage = intent.getExtras().getString(Config.EXTRA_MESSAGE);

                // Waking up mobile if it is sleeping
                aController.acquireWakeLock(getApplicationContext());


//                Toast.makeText(getApplicationContext(),
//                        "Got Message: " + newMessage,
//                        Toast.LENGTH_LONG).show();

                // Releasing wake lock
                aController.releaseWakeLock();
            }
        };

        handleMessageReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                String newMessage = intent.getExtras().getString(Config.EXTRA_MESSAGE);
                String newName = intent.getExtras().getString("name");
                String tempreciverid = intent.getExtras().getString("reciverid");
                String tempProductId = intent.getExtras().getString("productid");

          //      productId = tempProductId;
           //     reciverId = tempreciverid;
                Log.d("on reciverId1", reciverId);

                Log.i("GCMBroadcast", "Broadcast called. name" + newMessage);

                System.out.println("receiver id" + reciverId + "," + tempreciverid);

                System.out.println("productId_handleMessageReceiver" + productId + "," + tempProductId);

                if (productId != null && tempProductId != null && productId.equalsIgnoreCase(tempProductId)&& reciverId != null && tempreciverid != null && reciverId.equalsIgnoreCase(tempreciverid)) {
                 //   && reciverId != null && tempreciverid != null && reciverId.equalsIgnoreCase(tempreciverid)
                    System.out.println("productId_in condition" + productId + "," + tempProductId);
                    // Waking up mobile if it is sleeping
                    aController.acquireWakeLock(getApplicationContext());

                    final ChatBean chatBeanObject = new ChatBean();

                    /******* Firstly take data in model object ********/
                    chatBeanObject.setUserName(newName);
                    chatBeanObject.setMessage(newMessage);
                    chatBeanObject.setSendStatus("false");
                    if (todayDateForChatFlag) {
                        chatBeanObject.setDateTime("");
                        chatBeanObject.setTime("");

                    } else {
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        String todayDate = df.format(c.getTime());
                        chatBeanObject.setDateTime("" + todayDate);

                        Calendar c1 = Calendar.getInstance();
                        SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");
                        String todayDate1 = df1.format(c1.getTime());
                        chatBeanObject.setTime(""+todayDate1);
                    }
                    CustomListViewValuesArr.add(chatBeanObject);
                    //   adapter.notifyDataSetChanged();
                    adapter.updateResults(CustomListViewValuesArr);
                    aController.releaseWakeLock();
                    Function.cancelNotification(chatActivityInstance, 0);
                }


            }
        };

        AppPreferences.setDate(ChatActivity.this, "");
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = "Conversations";
        getSupportActionBar().setTitle(title);

        userImage = (ImageView) findViewById(R.id.profileimage);
        productImage = (ImageView) findViewById(R.id.productimage);

        intitView();
        intiListener();

        ChatDataBaseHelper.init(this);

        aController = (Controller) getApplicationContext();

        String deviceIMEI = "";
        if (Config.SECOND_SIMULATOR) {

            //Make it true in CONFIG if you want to open second simutor
            // for testing actually we are using IMEI number to save a unique device

            deviceIMEI = "000000000000001";
        } else {
            // GET IMEI NUMBER
            TelephonyManager tManager = (TelephonyManager) getBaseContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            deviceIMEI = tManager.getDeviceId();
        }

        // Getting name, email from intent
        intent = getIntent();

        if (intent != null && intent.getAction().equalsIgnoreCase("registration")) {
            name = intent.getStringExtra("name");
            email = intent.getStringExtra("email");
            productId = intent.getStringExtra("productId");
            reciverId = intent.getStringExtra("productOwnerId");
            productName = intent.getStringExtra("productName");
            productOwnerName = intent.getStringExtra("productOwnerName");
            productRecieverName= intent.getStringExtra("productOwnerName");

        } else if (intent.getAction().equalsIgnoreCase("productDetail")) {
            productId = String.valueOf(intent.getIntExtra("productId", 0));
            reciverId = String.valueOf(intent.getIntExtra("productOwnerId", 0));
            productName = intent.getStringExtra("productName");
            productPrice = intent.getStringExtra("productPrice");
            productOwnerName = intent.getStringExtra("productOwnerName");
            productRecieverName= intent.getStringExtra("productOwnerName");
            ProductImage = intent.getStringExtra("ProductImage");
            ProfileImage = intent.getStringExtra("ProfileImage");
            recieverImage= intent.getStringExtra("ProfileImage");
        } else if (intent.getAction().equalsIgnoreCase("productDetail_MakeAnOffer")) {
            productId = String.valueOf(intent.getIntExtra("productId", 0));
            reciverId = String.valueOf(intent.getIntExtra("productOwnerId", 0));
            productName = intent.getStringExtra("productName");
            productPrice = intent.getStringExtra("productPrice");
            productOwnerName = intent.getStringExtra("productOwnerName");
            productRecieverName= intent.getStringExtra("productOwnerName");
            ProductImage = intent.getStringExtra("ProductImage");
            ProfileImage = intent.getStringExtra("ProfileImage");
            recieverImage= intent.getStringExtra("ProfileImage");
            String offerRate = intent.getStringExtra("offerRate");
            String offerComment = intent.getStringExtra("offerComment");
            Function.hideKeyboard(ChatActivity.this, chat_btn.getRootView());
            String message = offerRate + "\n" + offerComment;
            String senderId = String.valueOf(AppPreferences.getCustomerId(ChatActivity.this));
            String userName = AppPreferences.getUsername(ChatActivity.this);

            final ChatBean chatList = new ChatBean();

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String datetime = df.format(c.getTime());

            Calendar c1 = Calendar.getInstance();
            SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");
            String datetime1 = df1.format(c1.getTime());

            /******* Firstly take data in model object ********/
            chatList.setCustomer_id(String.valueOf(AppPreferences.getCustomerId(ChatActivity.this)));
            chatList.setUserName(productRecieverName);
            chatList.setSenderId(senderId);
            chatList.setProductId(productId);
            chatList.setMessage(message);
            chatList.setSendStatus("true");
            chatList.setProductOwnerId(productRecieverName);
            chatList.setReciverId(reciverId);
            chatList.setProductName(productName);
            chatList.setProductPrice(productPrice);
            chatList.setProductImage(ProductImage);
            chatList.setProfileImage(recieverImage);
            chatList.setDateTime(datetime);
            chatList.setTime(datetime1);
            chatList.setSuccess("false");

            long Id = ChatDataBaseHelper.addUserData(chatList);
            String db_Id = String.valueOf(Id);
            if (aController.isConnectingToInternet()) {
                new LongOperation().execute(serverURL, senderId, message, userName, productId, reciverId, db_Id);
            }

        } else if (intent.getAction().equalsIgnoreCase("notification")) {

            fromNotificationFlag = true;
            reciverId = GCMIntentService.reciverId;
            productId = GCMIntentService.productId;
            productOwnerName = AppPreferences.getUsername(ChatActivity.this);
            productName = GCMIntentService.productName;
            productPrice = GCMIntentService.productPrice;
            ProductImage = intent.getStringExtra("ProductImage");
            ProfileImage = intent.getStringExtra("ProfileImage");
            recieverImage= intent.getStringExtra("ProfileImage");
            productRecieverName= intent.getStringExtra("reciver_name");
            Log.d("p_id", productId + " CA");
            Log.d("on reciverId2", reciverId + " Name " + productOwnerName);
        } else if (intent.getAction().equalsIgnoreCase("converstion")) {
            productId = String.valueOf(intent.getStringExtra("productId"));
            reciverId = String.valueOf(intent.getStringExtra("reciverId"));
            productName = intent.getStringExtra("productName");
            productPrice = intent.getStringExtra("productPrice");
            String message = intent.getStringExtra("message");
            productRecieverName= intent.getStringExtra("reciver_name");
            productOwnerName = intent.getStringExtra("productOwnerName");
            ProductImage = intent.getStringExtra("ProductImage");
            ProfileImage = intent.getStringExtra("ProfileImage");
            recieverImage = intent.getStringExtra("ProfileImage");
        }


        db = new DataBaseHelper(getApplicationContext());

        product = db.getProductById(Integer.parseInt(productId));

        if (ProductImage == null || ProductImage.equalsIgnoreCase("")) {
            Bitmap profileicon = BitmapFactory.decodeResource(getResources(), R.drawable.def_product);
            productImage.setImageBitmap(profileicon);
        } else {
            ImageLoader.getInstance().displayImage(ProductImage,
                    productImage);
        }

        if (recieverImage == null || recieverImage.equalsIgnoreCase("")) {
            Bitmap profileicon = BitmapFactory.decodeResource(getResources(), R.drawable.def_user_img);
            userImage.setImageBitmap(profileicon);
        } else {
            ImageLoader.getInstance().displayImage(recieverImage,
                    userImage);
        }

        profileName_tv.setText(productRecieverName);
        productName_tv.setText(productName);
        productPrice_tv.setText(productPrice);
        imei = deviceIMEI;
        Resources res = getResources();

        final List<ChatBean> ChatData = ChatDataBaseHelper.getUserData_receiver(productId, String.valueOf(AppPreferences.getCustomerId(ChatActivity.this)),reciverId);

        String dateFlag = "";

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = df.format(c.getTime());

        //for (ChatBean spinnerdt : SpinnerUserData) {
        for (int i = 0; i < ChatData.size(); i++) {

            ChatBean chatBean = new ChatBean();

            /******* Firstly take data in model object ********/
            chatBean.setUserName(ChatData.get(i).getUserName());
            chatBean.setMessage(ChatData.get(i).getMessage());
            chatBean.setSendStatus(ChatData.get(i).getSendStatus());

            String date = "";
            String time = "";
            if (ChatData.get(i).getDateTime() != null && !ChatData.get(i).getDateTime().equalsIgnoreCase("")) {
                String dateTime = ChatData.get(i).getDateTime();
                System.out.println("date time is : " + dateTime);
                StringTokenizer tk = new StringTokenizer(dateTime);
                date = tk.nextToken();

                String Time = ChatData.get(i).getTime();
                time=Time;
            } else {
                date = "";
                time = "";
            }

            if (todayDate.equalsIgnoreCase(date)) {
                todayDateForChatFlag = true;
            }

            //ChatData.get(i).getDateTime()

            if (dateFlag.equalsIgnoreCase(date)) {
                chatBean.setDateTime("");
                chatBean.setTime("");
            } else {
                chatBean.setDateTime(date);
                chatBean.setTime(time);
                dateFlag = date;
            }

            Log.i("GCMspinner", "-----" + ChatData.get(i).getMessage() + " dateTime:" + ChatData.get(i).getDateTime());

            /******** Take Model Object in ArrayList **********/
            CustomListViewValuesArr.add(chatBean);


        }

        adapter = new CustomAdapter(ChatActivity.this, R.layout.spinner_rows, CustomListViewValuesArr, res);
        chat_lv.setAdapter(adapter);


        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                Config.DISPLAY_MESSAGE_ACTION));
        registerReceiver(handleMessageReceiver, new IntentFilter(
                Config.DISPLAY_MESSAGE_ACTION));

        System.out.println("productId_oncreate" + productId);

    }

    private void intitView() {
        chatMessage = (EditText) findViewById(R.id.chatmessage);
        chat_lv = (ListView) findViewById(R.id.chat_lv);
        chat_btn = (ImageView) findViewById(R.id.chat_btn);
        profileName_tv = (TextView) findViewById(R.id.profilename);
        date_tv = (TextView) findViewById(R.id.date);
        productName_tv = (TextView) findViewById(R.id.productename);
        productPrice_tv = (TextView) findViewById(R.id.price);
    }

    private void intiListener() {

        chat_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_btn:
                chat_btn.setBackgroundResource(R.drawable.send_bitmap);
                String message = chatMessage.getText().toString();
                Function.hideKeyboard(ChatActivity.this, chat_btn.getRootView());

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String datetime = df.format(c.getTime());

                Calendar c1 = Calendar.getInstance();
                SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");
                String datetime1 = df1.format(c1.getTime());

                if (!message.equalsIgnoreCase("")) {
                    // WebServer Request URL
                    System.out.println("message" + message);

                    String senderId = String.valueOf(AppPreferences.getCustomerId(ChatActivity.this));
                    String userName = AppPreferences.getUsername(ChatActivity.this);

                    final ChatBean chatList = new ChatBean();
                    chatList.setCustomer_id(String.valueOf(AppPreferences.getCustomerId(ChatActivity.this)));
                    chatList.setUserName(productRecieverName);
                    chatList.setSenderId(senderId);
                    chatList.setProductId(productId);
                    chatList.setMessage(message);
                    chatList.setSendStatus("true");
                    chatList.setProductOwnerId(productRecieverName);
                    chatList.setReciverId(reciverId);
                    chatList.setProductName(productName);
                    chatList.setProductPrice(productPrice);
                    chatList.setProductImage(ProductImage);
                    chatList.setProfileImage(recieverImage);

                    if (todayDateForChatFlag) {
                        chatList.setDateTime("");
                        chatList.setTime("");
                    } else {
                        chatList.setDateTime(datetime);
                        chatList.setTime(datetime1);
                        todayDateForChatFlag = true;
                    }
                    chatList.setSuccess("false");
                    CustomListViewValuesArr.add(chatList);
                    adapter.notifyDataSetChanged();
                    long Id = ChatDataBaseHelper.addUserData(chatList);
                    String db_Id = String.valueOf(Id);
                    if (aController.isConnectingToInternet()) {
                        Log.d("send Data", "senderId:" + senderId + ", productId:" + productId + ", productOwnerId:" + reciverId);
                        new LongOperation().execute(serverURL, senderId, message, userName, productId, reciverId, db_Id);
                    }

                }
                chatMessage.setText("");
                break;
        }

    }

    @Override
    protected void onDestroy() {
        // Cancel AsyncTask
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        try {
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            Log.e("UnRegister Receiver Err", "> " + e.getMessage());
        }
        super.onDestroy();
        chatActivityInstance = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                if (intent.getAction().equalsIgnoreCase("converstion")) {
                    startActivity(new Intent(ChatActivity.this, Conversations.class));
                    finish();
                } else {
                    if (fromNotificationFlag) {
                        Intent i = new Intent(ChatActivity.this, HomeActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        finish();
                    }
                }
                break;
            case R.id.action_delete:
                Log.d("delete", "delete");
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        ChatActivity.this);

                alertDialog.setTitle("Confirm Delete...");

                alertDialog.setMessage("Are you sure you want delete all conversation of this chat ?");

                alertDialog.setIcon(R.drawable.delete);

                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new deleteChatTask().execute();

                            }
                        });

                alertDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.cancel();
                            }
                        });

                alertDialog.show();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (intent.getAction().equalsIgnoreCase("converstion")) {
            startActivity(new Intent(ChatActivity.this, Conversations.class));
            finish();
        } else {
            finish();
        }
    }

    public class LongOperation extends AsyncTask<String, Void, String> {

        // Required initialization

        //  private ProgressDialog Dialog = new ProgressDialog(ChatActivity.this);
        String data = "";
        int sizeData = 0;
        String db_Id = "";
        //private final HttpClient Client = new DefaultHttpClient();
        // private Controller aController = null;
        private String Error = null;

        protected void onPreExecute() {


        }

        // Call after onPreExecute method
        protected String doInBackground(String... params) {

            /************ Make Post Call To Web Server ***********/
            BufferedReader reader = null;
            String Content = "";
            db_Id = params[6];
            // Send data
            try {

                // Defined URL  where to send data
                URL url = new URL(params[0]);
                if (!params[1].equals("")) //iemi
                    data += URLEncoder.encode("senderId", "UTF-8") + "=" + params[1].toString();//"&" +
                if (!params[2].equals(""))  // message
                    data += "&" + URLEncoder.encode("message", "UTF-8") + "=" + params[2].toString();
                if (!params[3].equals(""))  // username
                    data += "&" + URLEncoder.encode("userName", "UTF-8") + "=" + params[3].toString();
                if (!params[4].equals("")) // pro id
                    data += "&" + URLEncoder.encode("productId", "UTF-8") + "=" + params[4].toString();
                if (!params[5].equals(""))  // pro owner id
                    data += "&" + URLEncoder.encode("productOwnerId", "UTF-8") + "=" + params[5].toString();

                Log.d("json sent", data);
                System.out.println(" json sent data is : " + data);

                // Send POST data request

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                // Get the server response

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    // Append server response in string
                    sb.append(line + " ");
                }

                // Append Server Response To Content String
                Content = sb.toString();
            } catch (Exception ex) {
                Error = ex.getMessage();
            } finally {
                try {

                    reader.close();
                } catch (Exception ex) {
                }
            }

            /*****************************************************/
            return Content;
        }

        protected void onPostExecute(String Result) {
            Log.d("json Result", Result);
            if (Error != null) {
                Toast.makeText(getBaseContext(), "Error: " + Error, Toast.LENGTH_LONG).show();

            } else {

                chatMessage.setText("");
                ChatDataBaseHelper.updateUserData(db_Id, "true");

            }
        }

    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(mHandleMessageReceiver);
            unregisterReceiver(handleMessageReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    private class deleteChatTask extends AsyncTask<Void, Void, Void> {
        int status = 0;
String senderId = String.valueOf(AppPreferences.getCustomerId(ChatActivity.this));
        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpParams httpParameters = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParameters,
                        AppConstants.NETWORK_TIMEOUT_CONSTANT);
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        AppConstants.NETWORK_CONNECTION_TIMEOUT_CONSTANT);
                HttpConnectionParams.setSoTimeout(httpParameters,
                        AppConstants.NETWORK_SOCKET_TIMEOUT_CONSTANT);

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(AppConstants.DELETE_CHAT);
                JSONObject jsonObj = new JSONObject();

                jsonObj.put("sender_id", senderId);
                jsonObj.put("reciver_id", reciverId);
                jsonObj.put("product_id", productId);

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

                StringEntity se = null;
                try {
                    se = new StringEntity(jsonArray.toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                httppost.setEntity(se);
                HttpResponse response = null;
                response = httpclient.execute(httppost);
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
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
                if (jsonString != null) {
                    System.out.println("json response string is -------------    " + jsonString);
                    if (jsonString.contains("result")) {

                        try {
                            JSONObject jsonObj1 = new JSONObject(jsonString);
                            JSONArray jsonChildArray = jsonObj1.getJSONArray("result");//result

                            if (jsonObj1.getString("status").equalsIgnoreCase("200")) {
                                status = 200;


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
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(status == 200){
                boolean isdeleted = ChatDataBaseHelper.chatDelete(productId, reciverId, senderId);
                if (isdeleted) {
                    finish();
                    AppPreferences.setDate(ChatActivity.this, "");
                }
            }
        }
    }
}