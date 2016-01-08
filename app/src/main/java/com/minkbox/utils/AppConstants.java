package com.minkbox.utils;

/**
 * AppConstants to store the constant variables and common tags to be used
 * within the application
 */
public class AppConstants {

    public static final int NETWORK_TIMEOUT_CONSTANT = 15000;
    public static final int NETWORK_CONNECTION_TIMEOUT_CONSTANT = 15000;
    public static final byte ROUTE_ACTIVITY_CONSTANT = (byte) 23;
    public static final int NETWORK_SOCKET_TIMEOUT_CONSTANT = 25000;

    public static final String REGISTER_LOGIN_TYPE = "register";
    public static final String GPLUS_LOGIN_TYPE = "googleplus";
    public static final String FB_LOGIN_TYPE = "facebook";

    // public static final String APPURL = "http://www.hvantagetechnologies.com/minkbox/api/";
    public static final String APPURL = "http://www.5kmx.com/api/";
    ///"http://www.5kmx.com/api/";

    public static final String TAG = "5kmxTag";

    public static final String URL_LOGIN = "";

    public static final boolean IS_DEV_BUILD = true;

    public static final String REGISTRATION = APPURL + "customer_api.php?method=SignUp";
//    public static final String CHATCONVERSATION = "http://www.hvantagetechnologies.com/minkbox/api/gcm_api.php?method=getChatData";
    public static final String LOGIN = APPURL + "customer_api.php?method=SignIn";
    public static final String SENDCHAT = APPURL +"gcm/sendpush.php";
    public static final String CHATGCM_REGISTERURL = APPURL + "gcm/register.php";
    public static final String CHATGCM_UNREGISTERURL = APPURL + "gcm/unregister.php";
    public static final String GETALLPRODUCT = APPURL + "customer_api.php?method=getAllProductName";
    public static final String HELP = APPURL + "help.php";
    public static final String TERM_CONDITION = APPURL + "term.php";
    public static final String PRIVACY_POLICY = APPURL + "private_policy.php";
    public static final String SAFETY = APPURL + "safety.php";
    public static final String RULES = APPURL + "rules.php";
    public static final String FAQ = APPURL + "faq.php";

    //    public static final String GETALLPOST = APPURL + "customer_api.php?method=getAllPost";
    public static final String GETALLPOST = APPURL + "function.php?method=getAllPostNew";
    //http://www.hvantagetechnologies.com/minkbox/api/

    public static final String ADDNEWPRODUCT = APPURL + "customer_api.php?method=add_product";
    public static final String FORGETPASSWORD = APPURL + "customer_api.php?method=ForgotPassword";
    public static final String LOGOUTAPPURL = APPURL + "customer_api.php?method=Logout";
    public static final String SOLDPRODUCT = APPURL + "customer_api.php?method=sold_product";
    public static final String SELLPRODUCT = APPURL + "customer_api.php?method=sell_product";
    public static final String LIKEPRODUCT = APPURL + "customer_api.php?method=like_product";
    public static final String RESERVEPRODUCTAPI = APPURL + "customer_api.php?method=reserve_product_via_customer";
    public static final String SOLDPRODUCTAPI = APPURL + "customer_api.php?method=sold_product_via_customer";
    public static final String DELETEPRODUCTAPI = APPURL + "customer_api.php?method=delete_product";
    public static final String UPDATEPRODUCTAPI = APPURL + "customer_api.php?method=update_product";
    public static final String PRODUCTALLDETAIL = APPURL + "customer_api.php?method=product_details";
    public static final String LIKEPRODUCTAPI = APPURL + "customer_api.php?method=like_product_via_user";
    public static final String REPORTPRODUCTAPI = APPURL + "customer_api.php?method=report_product_via_user";
    public static final String REPORTUSERAPI = APPURL + "customer_api.php?method=report_user";
    public static final String OTHERUSERPROFILE = APPURL + "customer_api.php?method=view_other_user_profile";
    public static final String UPDATELOCATION = APPURL + "customer_api.php?method=update_product_location";
    public static final String UPDATEMYLOCATION = APPURL + "customer_api.php?method=update_customer_location";
    public static final String SETNOTIFICATIONPREFERANCE = APPURL + "customer_api.php?method=setNotificationPreferance";
    public static final String GETNOTIFICATIONPREFERANCE = APPURL + "customer_api.php?method=getNotificationPreferance";
//    public static final String MAKEOFFER = APPURL + "";
//    public static final String WANTCOMMENT = APPURL + "";
    public static final String CHATDATA = APPURL + "gcm_api.php?method=allchatdata";
    public static final String DELETE_CHAT = APPURL + "gcm_api.php?method=deleteChatData";
    public static final String CHANGEPASSWORD = APPURL + "customer_api.php?method=update_user_password";
    public static final String CHANGEEEMAIL = APPURL + "customer_api.php?method=update_user_email";
    public static final String FINALEDITPROFILE = APPURL + "customer_api.php?method=update_user_profile";
    public static final String FAVORITEGETEGORY = APPURL + "customer_api.php?method=update_user_faviourate_category";

//    public static final String VERIFICATIONGOOGLEPLUS = APPURL + "";

    public static final String EMAILVERIFICATION = APPURL + "customer_api.php?method=Send_email_verification";
    public static final String EMAILVERIFICATIONCODE = APPURL + "customer_api.php?method=Confirm_email_verification";
    public static final String FIELTER = APPURL + "customer_api.php?method=getAllfilterData";
    public static final String FACEBOOKGPLUSVERIFICATIONAPPURL = APPURL + "customer_api.php?method=setIdentity_verification";
    public static final String NEWCOLLECTIONAPPURL = APPURL + "customer_api.php?method=collection";

    public static final String APPSHARETEXT = "5kmx App on play store : ";
    public static final String FBLIKEAPPURL = "http://www.5kmx.com";
    public static final String TWITTERFOLLOWAPPURL = "http://twitter.com/5kmxapp";
    public static final String APPSHAREURL = " https://play.google.com/store/apps/details?id=com.minkbox&hl=en";
    public static final String APPSTOREURL = "https://play.google.com/store/apps/details?id=com.minkbox";


}
