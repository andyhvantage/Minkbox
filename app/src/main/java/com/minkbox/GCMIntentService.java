package com.minkbox;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.minkbox.databace.ChatDataBaseHelper;
import com.minkbox.fragment.UserLoginFragment;
import com.minkbox.model.ChatBean;
import com.minkbox.utils.AppPreferences;
import com.minkbox.utils.Config;
import com.minkbox.utils.Controller;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by MMFA-YOGESH on 9/11/2015.
 */
public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "GCMIntentService";

    private Controller aController = null;

    static int notify_no = 0;

    public static String productId="", reciverId="",productName, productPrice,productOwnerName;

    public GCMIntentService() {
        // Call extended class Constructor GCMBaseIntentService
        super(Config.GOOGLE_SENDER_ID);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {

        //Get Global Controller Class object (see application tag in AndroidManifest.xml)
        if(aController == null)
            aController = (Controller) getApplicationContext();

        Log.i(TAG, "---------- onRegistered -------------");
        Log.i(TAG, "Device registered: regId = " + registrationId);

        aController.displayRegistrationMessageOnScreen(context, "Your device registred with GCM");
     // Log.d("NAME", ChatActivity.name);

        aController.register(context, ChatActivity.name,
                ChatActivity.email, registrationId, ChatActivity.imei);

        ChatDataBaseHelper.addDeviceData(UserLoginFragment.chatName, UserLoginFragment.chatEmail,
                registrationId, UserLoginFragment.chatImei);
    }

    /**
     * Method called on device unregistred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {

        if(aController == null)
            aController = (Controller) getApplicationContext();

        Log.i(TAG, "---------- onUnregistered -------------");
        Log.i(TAG, "Device unregistered");

        aController.displayRegistrationMessageOnScreen(context,
                getString(R.string.gcm_unregistered));

        aController.unregister(context, registrationId, ChatActivity.imei);
    }

    /**
     * Method called on Receiving a new message from GCM server
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String datetime = df.format(c.getTime());


        Calendar c1 = Calendar.getInstance();
        SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");
        String time = df1.format(c1.getTime());


        if(aController == null)
            aController = (Controller) getApplicationContext();

        Log.i(TAG, "---------- onMessage -------------");
        String message = intent.getExtras().getString("message");

        Log.i("GCM", "message : " + message);

        String title = "";
        String reciverId  = "";
        String productId = "";
        String productName = "";
        String productPrice = "";
        String productOwnerName = "", ProductImage = "", ProfileImage = "";
        try {
            JSONObject object = new JSONObject(message);
            title = object.getString("username");

            reciverId = object.getString("reciverId");
            productId = object.getString("product_Id");
            message = object.getString("message");
            productName = object.getString("productName");
            productPrice = object.getString("productPrice");
            productOwnerName = object.getString("productOwnerName");
            ProductImage = object.getString("ProductImage");
            ProfileImage = object.getString("ProfileImage");

            Log.d("Broadcast called. name", " title: "+title +"rid"+ reciverId + " productOwnerName: "+productOwnerName + " pprice" + productPrice);

            this.productId=productId;
            this.reciverId=reciverId;
            this.productName=productName;
            this.productPrice=productPrice;
            this.productOwnerName=title;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatBean userdata = new ChatBean();

        userdata.setCustomer_id(String.valueOf(AppPreferences.getCustomerId(context)));
        userdata.setUserName(title);
        userdata.setSenderId(String.valueOf(AppPreferences.getCustomerId(context)));
        userdata.setProductId(productId);
        userdata.setMessage(message);
        userdata.setSendStatus("false");
        userdata.setProductOwnerId(productOwnerName);
        userdata.setReciverId(reciverId);
        userdata.setProductName(productName);
        userdata.setProductPrice(productPrice);
        userdata.setProductImage(ProductImage);
        userdata.setProfileImage(ProfileImage);
        userdata.setDateTime(datetime);
        userdata.setTime(time);
        userdata.setSuccess("true");

        try
        {
//            if(AppPreferences.getCustomerId(getApplicationContext()) == Integer.parseInt(reciverId))
        //    {
                aController.displayMessageOnScreen(context, userdata);
                generateNotification(context, userdata);
        //    }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        ChatDataBaseHelper.init(GCMIntentService.this);
        ChatDataBaseHelper.addUserData(userdata);

    }

    @Override
    protected void onDeletedMessages(Context context, int total) {

        if(aController == null)
            aController = (Controller) getApplicationContext();

        Log.i(TAG, "---------- onDeletedMessages -------------");
        String message = getString(R.string.gcm_deleted, total);

        String title = "DELETED";

        ChatBean userdata = new ChatBean();

        userdata.setUserName(title);
        userdata.setSenderId("");
        userdata.setProductId("");
        userdata.setMessage(message);
        userdata.setSendStatus("false");
        userdata.setProductOwnerId("");
        userdata.setReciverId("");

        // aController.displayMessageOnScreen(context, message);
        // notifies user
        generateNotification(context, userdata);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {

        if(aController == null)
            aController = (Controller) getApplicationContext();

        Log.i(TAG, "---------- onError -------------");
        Log.i(TAG, "Received error: " + errorId);
        aController.displayRegistrationMessageOnScreen(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {

        if(aController == null)
            aController = (Controller) getApplicationContext();

        Log.i(TAG, "---------- onRecoverableError -------------");

        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        aController.displayRegistrationMessageOnScreen(context,
                getString(R.string.gcm_recoverable_error,
                        errorId));

        return super.onRecoverableError(context, errorId);
    }



    /**
     * Create a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context,ChatBean userdata) {


        int icon = R.drawable.right_green;
        long when = System.currentTimeMillis();

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, userdata.getMessage(), when);

        Intent notificationIntent = new Intent(context, ChatActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.setAction("notification");
        notificationIntent.putExtra("name", userdata.getUserName());
        notificationIntent.putExtra("message", userdata.getMessage());
        notificationIntent.putExtra("reciverid", userdata.getReciverId());
        notificationIntent.putExtra("productid", userdata.getProductId());
        notificationIntent.putExtra("ProductImage", userdata.getProductImage());
        notificationIntent.putExtra("reciver_name", userdata.getUserName());
        notificationIntent.putExtra("ProfileImage", userdata.getProfileImage());

        PendingIntent intent =
                PendingIntent.getActivity(context, notify_no, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
       // notification.setLatestEventInfo(context, title, message, intent);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);
        notification = builder.setContentIntent(intent)
                .setSmallIcon(icon).setTicker(userdata.getMessage()).setWhen(when)
                .setAutoCancel(true).setContentTitle(userdata.getUserName())
                .setContentText(userdata.getMessage()).build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;

            /*notification.sound = Uri.parse("android.resource://" +
                                    context.getPackageName() + "your_sound_file_name.mp3");*/

//        if (notify_no < 9) {
//            notify_no = notify_no + 1;
//        } else {
//            notify_no = 0;
//        }
        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(notify_no, notification);
    }

}