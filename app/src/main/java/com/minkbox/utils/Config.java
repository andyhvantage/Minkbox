package com.minkbox.utils;

/**
 * Created by MMFA-YOGESH on 9/11/2015.
 */
public class Config {

    public static final boolean SECOND_SIMULATOR = false;

    // Server Url absolute url where php files are placed.
    //public static final String YOUR_SERVER_URL   =  "http://www.hvantagetechnologies.com/minkbox/api/gcm/";

    // Google project id
    public  static final String GOOGLE_SENDER_ID = "732454769994";

    /**
     * Tag used on log messages.
     */
    public  static final String TAG = "MinkBoxChat";

    // Broadcast reciever name to show gcm registration messages on screen
    public static final String DISPLAY_REGISTRATION_MESSAGE_ACTION =
            "com.minkbox.gcm.DISPLAY_REGISTRATION_MESSAGE";

    // Broadcast reciever name to show user messages on screen
    public  static final String DISPLAY_MESSAGE_ACTION =
            "com.minkbox.gcm.DISPLAY_MESSAGE";

    // Parse server message with this name
    public static final String EXTRA_MESSAGE = "message";
}
