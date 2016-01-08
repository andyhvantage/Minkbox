package com.minkbox.databace;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.minkbox.model.ChatBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MMFA-YOGESH on 9/11/2015.
 */
public class ChatDataBaseHelper {

    public static final boolean DEBUG = true;

    /******************** Logcat TAG ************/
    public static final String LOG_TAG = "DBAdapter";

    /******************** Table Fields ************/
    public static final String KEY_ID = "_id";
    public static final String KEY_CUSTOMERID = "customer_id";
    public static final String KEY_USER_NAME    = "user_name";
    public static final String KEY_SENDERID    = "sender_id";
    public static final String KEY_MESSAGE    = "message";
    public static final String KEY_PRODUCTID    = "product_id";
    public static final String KEY_PRODUCTOWNERID    = "product_ownerid";
    public static final String KEY_RECIVERID    = "reciver_id";
    public static final String KEY_STATUS    = "status";
    public static final String KEY_PRODUCTNAME    = "product_name";
    public static final String KEY_PRODUCTPRICE    = "product_price";
    public static final String KEY_PRODUCTIMAGE    = "product_image";
    public static final String KEY_PROFILEIMAGE    = "profile_image";
    public static final String KEY_DATETIME    = "datetime";
    public static final String KEY_TIME    = "time";
    public static final String KEY_SUCCESS    = "success";

    public static final String KEY_USER_IMEI    = "user_imei";

    public static final String KEY_DEVICE_IMEI  = "device_imei";

    public static final String KEY_DEVICE_NAME  = "device_name";

    public static final String KEY_DEVICE_EMAIL = "device_email";

    public static final String KEY_DEVICE_REGID = "device_regid";


    /******************** Database Name ************/
    public static final String DATABASE_NAME = "DB_ChatMinkbox";

    /**** Database Version (Increase one if want to also upgrade your database) ****/
    public static final int DATABASE_VERSION = 1;// started at 1

    /** Table names */
    public static final String USER_TABLE = "tbl_user";
    public static final String DEVICE_TABLE = "tbl_device";

    /*** Set all table with comma seperated like USER_TABLE,ABC_TABLE ***/
    private static final String[] ALL_TABLES = { USER_TABLE,DEVICE_TABLE };

    /** Create table syntax */

    private static final String USER_CREATE =
            "create table tbl_user(_id integer primary key autoincrement," +
                    "user_name text not null," +
                    "sender_id text not null," +
                    "product_id text not null," +
                    "message text not null," +
                    "status text not null," +
                    "product_ownerid text not null," +
                    "reciver_id text not null," +
                    "product_name text not null," +
                    "product_price text not null," +
                    "product_image text," +
                    "profile_image text, " +
                    "datetime text, " +
                    "customer_id text, " +
                    "success text, " +
                    "time text " +
                    ");";

    private static final String DEVICE_CREATE ="create table tbl_device(_id integer primary key autoincrement," +
            "device_name text not null," +
            "device_email text not null," +
            "device_regid text not null," +
            "device_imei text not null);";

    /**** Used to open database in syncronized way ****/
    private static DataBaseHelper DBHelper = null;

    protected ChatDataBaseHelper() {
    }

    /******************* Initialize database *************/
    public static void init(Context context) {
        if (DBHelper == null) {
            if (DEBUG)
                Log.i("DBAdapter", context.toString());
            DBHelper = new DataBaseHelper(context);
        }
    }

    /***** Main Database creation INNER class ******/
    private static class DataBaseHelper extends SQLiteOpenHelper {
        public DataBaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            if (DEBUG)
                Log.i(LOG_TAG, "new create");
            try {
                //db.execSQL(USER_MAIN_CREATE);
                db.execSQL(USER_CREATE);
                db.execSQL(DEVICE_CREATE);

            } catch (Exception exception) {
                if (DEBUG)
                    Log.i(LOG_TAG, "Exception onCreate() exception");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (DEBUG)
                Log.w(LOG_TAG, "Upgrading database from version" + oldVersion
                        + "to" + newVersion + "...");

            for (String table : ALL_TABLES) {
                db.execSQL("DROP TABLE IF EXISTS " + table);
            }
            onCreate(db);
        }

    }


    /**** Open database for insert,update,delete in syncronized manner ****/
    private static synchronized SQLiteDatabase open() throws SQLException {
        return DBHelper.getWritableDatabase();
    }




    // Insert installing device data
    public static void addDeviceData(String DeviceName, String DeviceEmail,
                                     String DeviceRegID,String DeviceIMEI)
    {



        try{
            final SQLiteDatabase db = open();

            String imei  = sqlEscapeString(DeviceIMEI);
            String name  = sqlEscapeString(DeviceName);
            String email = sqlEscapeString(DeviceEmail);
            String regid = sqlEscapeString(DeviceRegID);

            ContentValues cVal = new ContentValues();
            cVal.put(KEY_DEVICE_IMEI, imei);
            cVal.put(KEY_DEVICE_NAME, name);
            cVal.put(KEY_DEVICE_EMAIL, email);
            cVal.put(KEY_DEVICE_REGID, regid);

            db.insert(DEVICE_TABLE, null, cVal);
            db.close(); // Closing database connection
        } catch (Throwable t) {
            Log.i("Database", "Exception caught: " + t.getMessage(), t);
        }
    }


    // Adding new user

    public static long addUserData(ChatBean uData) {

        final SQLiteDatabase db = open();

        String senderId  = sqlEscapeString(uData.getSenderId());
        String userName  = sqlEscapeString(uData.getUserName());
        String message  = sqlEscapeString(uData.getMessage());
        String productId  = sqlEscapeString(uData.getProductId());
        String productOwnerId  = sqlEscapeString(uData.getProductOwnerId());
        String reciverId  = sqlEscapeString(uData.getReciverId());
        String status  = sqlEscapeString(uData.getSendStatus());


        ContentValues cVal = new ContentValues();
        cVal.put(KEY_SENDERID, senderId);
        cVal.put(KEY_USER_NAME, userName);
        cVal.put(KEY_MESSAGE, message);
        cVal.put(KEY_PRODUCTID, productId);
        cVal.put(KEY_PRODUCTOWNERID, productOwnerId);
        cVal.put(KEY_RECIVERID, uData.getReciverId());
        cVal.put(KEY_PRODUCTNAME, uData.getProductName());
        cVal.put(KEY_PRODUCTPRICE, uData.getProductPrice());
        cVal.put(KEY_STATUS, status);
        cVal.put(KEY_PRODUCTIMAGE, uData.getProductImage());
        cVal.put(KEY_PROFILEIMAGE, uData.getProfileImage());
        cVal.put(KEY_DATETIME, uData.getDateTime());
        cVal.put(KEY_CUSTOMERID, uData.getCustomer_id());
        cVal.put(KEY_SUCCESS, uData.isSuccess());
        cVal.put(KEY_TIME, uData.getTime());


       /* ChatBean existChat = getUserDataByPid(uData
                .getProductId());
        System.out.println("existChat : "
                + existChat.getProductId()+" senderId:"+ senderId);
*/
        /*if(existChat.getProductId() != null){

            int noOfRowsAffected = db.update(USER_TABLE, cVal,
                    KEY_PRODUCTID + " = ?", new String[]{String
                            .valueOf(existChat
                                    .getProductId())});
            Log.d("Updated", noOfRowsAffected+"");
        }else{*/
            try{

                Log.d("insert", cVal.toString());
                long id = db.insert(USER_TABLE, null, cVal);

                Log.d("insert:id ", id+"");
                db.close(); // Closing database connection
                return id;
            } catch (Throwable t) {
                Log.i("Database", "Exception caught: " + t.getMessage(), t);
            }
      //  }

        return 0;
    }

    /*Update */
    public static void updateUserData(String db_Id, String success) {

        final SQLiteDatabase db = open();

        ContentValues cVal = new ContentValues();

        cVal.put(KEY_SUCCESS, success);

        int noOfRowsAffected = db.update(USER_TABLE, cVal,
                KEY_ID + " = ?", new String[]{String
                        .valueOf(db_Id)});

        Log.d("update: ", noOfRowsAffected+" on: "+ db_Id);

        /*if(existChat.getProductId() != null){

            int noOfRowsAffected = db.update(USER_TABLE, cVal,
                    KEY_PRODUCTID + " = ?", new String[]{String
                            .valueOf(existChat
                                    .getProductId())});
            Log.d("Updated", noOfRowsAffected+"");
        }else{*/
       /* try{

            Log.d("insert", cVal.toString());
            db.insert(USER_TABLE, null, cVal);
            db.close(); // Closing database connection
        } catch (Throwable t) {
            Log.i("Database", "Exception caught: " + t.getMessage(), t);
        }*/
        //  }


    }

    // Getting single user data
    public static List<ChatBean> getUserData(String p_id, String c_id) {

        List<ChatBean> contactList = new ArrayList<ChatBean>();
        // Select All Query
        String selectQuery = "SELECT  * FROM "+
                USER_TABLE+
                " WHERE "+KEY_PRODUCTID+" = "+p_id+ " AND "+ KEY_CUSTOMERID +" = "+c_id+
                " ORDER BY "+KEY_ID+" ASC";
Log.d("selectQuery", selectQuery);
        final SQLiteDatabase db = open();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {


                ChatBean data = new ChatBean();
                data.setId(Integer.parseInt(cursor.getString(0)));
                data.setUserName(cursor.getString(1));
                data.setSenderId(cursor.getString(2));
                data.setProductId(cursor.getString(3));
                data.setMessage(cursor.getString(4));
                data.setSendStatus(cursor.getString(5));
                data.setProductOwnerId(cursor.getString(6));
                data.setReciverId(cursor.getString(7));
                data.setProductName(cursor.getString(8));
                data.setProductPrice(cursor.getString(9));
                data.setProductImage(cursor.getString(10));
                data.setProfileImage(cursor.getString(11));
                data.setDateTime(cursor.getString(12));
                data.setCustomer_id(cursor.getString(13));
                data.setSuccess(cursor.getString(14));
                data.setTime(cursor.getString(15));



               // Log.d("dateTime", data.getDateTime()+" ?");
                // Adding contact to list
                contactList.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return contact list
        return contactList;
    }


    // Getting single user data
    public static List<ChatBean> getUserData_receiver(String p_id, String c_id, String rec_id) {

        List<ChatBean> contactList = new ArrayList<ChatBean>();
        // Select All Query
        String selectQuery = "SELECT  * FROM "+
                USER_TABLE+
                " WHERE "+KEY_PRODUCTID+" = "+p_id+ " AND "+ KEY_CUSTOMERID +" = "+c_id+ " AND "+ KEY_RECIVERID +" = "+rec_id+
                " ORDER BY "+KEY_ID+" ASC";
        Log.d("selectQuery", selectQuery);
        final SQLiteDatabase db = open();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {


                ChatBean data = new ChatBean();
                data.setId(Integer.parseInt(cursor.getString(0)));
                data.setUserName(cursor.getString(1));
                data.setSenderId(cursor.getString(2));
                data.setProductId(cursor.getString(3));
                data.setMessage(cursor.getString(4));
                data.setSendStatus(cursor.getString(5));
                data.setProductOwnerId(cursor.getString(6));
                data.setReciverId(cursor.getString(7));
                data.setProductName(cursor.getString(8));
                data.setProductPrice(cursor.getString(9));
                data.setProductImage(cursor.getString(10));
                data.setProfileImage(cursor.getString(11));
                data.setDateTime(cursor.getString(12));
                data.setCustomer_id(cursor.getString(13));
                data.setSuccess(cursor.getString(14));
                data.setTime(cursor.getString(15));



                // Log.d("dateTime", data.getDateTime()+" ?");
                // Adding contact to list
                contactList.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return contact list
        return contactList;
    }

    public static ChatBean getUserDataByPid(String p_id) {

        ChatBean contactList = new ChatBean();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + USER_TABLE+" WHERE "+KEY_PRODUCTID+" = "+p_id+" ORDER BY "+KEY_ID+" ASC";

        final SQLiteDatabase db = open();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {


                ChatBean data = new ChatBean();
                data.setId(Integer.parseInt(cursor.getString(0)));
                data.setUserName(cursor.getString(1));
                data.setSenderId(cursor.getString(2));
                data.setProductId(cursor.getString(3));
                data.setMessage(cursor.getString(4));
                data.setSendStatus(cursor.getString(5));
                data.setProductOwnerId(cursor.getString(6));
                data.setReciverId(cursor.getString(7));
                data.setProductName(cursor.getString(8));
                data.setProductPrice(cursor.getString(9));
                data.setProductImage(cursor.getString(10));
                data.setProfileImage(cursor.getString(11));
                data.setDateTime(cursor.getString(12));
                data.setCustomer_id(cursor.getString(13));
                data.setSuccess(cursor.getString(14));
                data.setTime(cursor.getString(15));


                Log.d("get by p_id", data.getProductId() + " ?");
                // Adding contact to list
                contactList = (data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return contact list
        return contactList;
    }

    // Getting All user data
    public static List<ChatBean> getAllUserData() {
        List<ChatBean> contactList = new ArrayList<ChatBean>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + USER_TABLE+" WHERE "+KEY_SUCCESS+" = 'false'"+" ORDER BY "+KEY_ID+" DESC";

        final SQLiteDatabase db = open();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {


                ChatBean data = new ChatBean();
                data.setId(Integer.parseInt(cursor.getString(0)));
                data.setUserName(cursor.getString(1));
                data.setSenderId(cursor.getString(2));
                data.setProductId(cursor.getString(3));
                data.setMessage(cursor.getString(4));
                data.setSendStatus(cursor.getString(5));
                data.setProductOwnerId(cursor.getString(6));
                data.setReciverId(cursor.getString(7));
                data.setProductName(cursor.getString(8));
                data.setProductPrice(cursor.getString(9));
                data.setProductImage(cursor.getString(10));
                data.setProfileImage(cursor.getString(11));
                data.setDateTime(cursor.getString(12));
                data.setCustomer_id(cursor.getString(13));
                data.setTime(cursor.getString(14));
                // data.setProductName(cursor.getString());




                Log.d("setSendStatus", cursor.getString(3) + "  ");
                // Adding contact to list
                contactList.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return contact list
        return contactList;
    }

    public static List<ChatBean> getAllUserData(String c_id) {
        List<ChatBean> contactList = new ArrayList<ChatBean>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + USER_TABLE+" WHERE "+KEY_CUSTOMERID+" = "+c_id+" ORDER BY "+KEY_DATETIME+" DESC";

        final SQLiteDatabase db = open();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {


                ChatBean data = new ChatBean();
                data.setId(Integer.parseInt(cursor.getString(0)));
                data.setUserName(cursor.getString(1));
                data.setSenderId(cursor.getString(2));
                data.setProductId(cursor.getString(3));
                data.setMessage(cursor.getString(4));
                data.setSendStatus(cursor.getString(5));
                data.setProductOwnerId(cursor.getString(6));
                data.setReciverId(cursor.getString(7));
                data.setProductName(cursor.getString(8));
                data.setProductPrice(cursor.getString(9));
                data.setProductImage(cursor.getString(10));
                data.setProfileImage(cursor.getString(11));
                data.setDateTime(cursor.getString(12));
                data.setCustomer_id(cursor.getString(13));
                data.setTime(cursor.getString(14));
                // data.setProductName(cursor.getString());




                Log.d("setSendStatus", cursor.getString(3)+"  ");
                // Adding contact to list
                contactList.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return contact list
        return contactList;
    }

    // Getting users Count
    public static int getUserDataCount() {
        String countQuery = "SELECT  * FROM " + USER_TABLE;
        final SQLiteDatabase db = open();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // Getting installed device have self data or not
    public static int validateDevice() {
        String countQuery = "SELECT  * FROM " + DEVICE_TABLE;
        final SQLiteDatabase db = open();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // Getting distinct user data use in spinner
    public static List<ChatBean> getDistinctUser() {
        List<ChatBean> contactList = new ArrayList<ChatBean>();
        // Select All Query
        String selectQuery = "SELECT  distinct(user_imei),user_name FROM " + USER_TABLE+" ORDER BY "+KEY_ID+" desc";

        final SQLiteDatabase db = open();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ChatBean data = new ChatBean();

                data.setId(Integer.parseInt(cursor.getString(0)));
                data.setUserName(cursor.getString(1));
                data.setSenderId(cursor.getString(2));
                data.setProductId(cursor.getString(3));
                data.setMessage(cursor.getString(4));
                data.setSendStatus(cursor.getString(5));
                data.setProductOwnerId(cursor.getString(6));
                data.setReciverId(cursor.getString(7));
                data.setProductName(cursor.getString(8));
                data.setProductPrice(cursor.getString(9));
                data.setProductImage(cursor.getString(10));
                data.setProfileImage(cursor.getString(11));
                data.setDateTime(cursor.getString(12));
                data.setCustomer_id(cursor.getString(13));
                data.setTime(cursor.getString(14));
                // Adding contact to list
                contactList.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return contactList;
    }

    // Getting imei already in user table or not
    public static int validateNewMessageUserData(String IMEI) {
        int count = 0;
        try {
            String countQuery = "SELECT "+KEY_ID+" FROM " + USER_TABLE + " WHERE user_imei='"+IMEI+"'";

            final SQLiteDatabase db = open();
            Cursor cursor = db.rawQuery(countQuery, null);

            count = cursor.getCount();
            cursor.close();
        } catch (Throwable t) {
            count = 10;
            Log.i("Database", "Exception caught: " + t.getMessage(), t);
        }
        return count;
    }


    // Escape string for single quotes (Insert,Update)
    private static String sqlEscapeString(String aString) {
        String aReturn = "";

        if (null != aString) {
            //aReturn = aString.replace("'", "''");
            aReturn = DatabaseUtils.sqlEscapeString(aString);
            // Remove the enclosing single quotes ...
            aReturn = aReturn.substring(1, aReturn.length() - 1);
        }

        return aReturn;
    }
    // UnEscape string for single quotes (show data)
    private static String sqlUnEscapeString(String aString) {

        String aReturn = "";

        if (null != aString) {
            aReturn = aString.replace("''", "'");
        }

        return aReturn;
    }

    public static boolean  chatDelete(String p_id, String r_id, String s_id){
        final SQLiteDatabase db = open();
        String whereClause = KEY_PRODUCTID + "=?"+" and "+KEY_RECIVERID + "=?"+" and "+KEY_SENDERID + "=?";
        String[] whereArgs = new String[]{p_id, r_id, s_id};
        return  db.delete(USER_TABLE, whereClause, whereArgs) > 0;
    }

    public static boolean  deleteAllChat(){
        final SQLiteDatabase db = open();
        return  db.delete(USER_TABLE, null, null) > 0;
    }
}
