package com.minkbox.databace;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.minkbox.model.Category;
import com.minkbox.model.Product;

import java.util.ArrayList;
import java.util.List;


public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String LOG = DataBaseHelper.class.getName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MinkBoxDb";

    // Table Names
    private static final String TABLE_PRODUCT = "pRODUCT";
    private static final String TABLE_CATEGORIES = "categories";

    // Common column names
    private static final String KEY_ID = "id";

    //TABLE_PRODUCT Table - column names
    private static final String KEY_SERVER_PRODUCT_ID = "product_server_id";
    private static final String KEY_PRODUCT_TITLE = "product_title";
    private static final String KEY_PRODUCT_PRICE = "product_price";
    private static final String KEY_PRODUCT_PRICE_CURRENCY = "product_currency";
    private static final String KEY_PRODUCT_CITY = "product_city";
    private static final String KEY_PRODUCT_PINCODE = "product_pincode";
    private static final String KEY_PRODUCT_ADDRESS = "product_address";
    private static final String KEY_PRODUCT_DESCRIPTION = "product_description";
    private static final String KEY_PRODUCT_IMAGE1 = "product_image1";
    private static final String KEY_PRODUCT_IMAGE2 = "product_image2";
    private static final String KEY_PRODUCT_IMAGE3 = "product_image3";
    private static final String KEY_PRODUCT_IMAGE4 = "product_image4";
    private static final String KEY_PRODUCT_MAP_SCREENSHOT = "product_map_screenshot";
    private static final String KEY_PRODUCT_LATITUDE = "product_latitude";
    private static final String KEY_PRODUCT_LONGITUDE = "product_longitude";
    private static final String KEY_PRODUCT_TOTAL_LIKES = "product_total_likes";
    private static final String KEY_PRODUCT_TOTAL_REVIEWS = "product_total_reviews";
    private static final String KEY_PRODUCT_CATEGORY_ID = "product_category_id";
    private static final String KEY_PRODUCT_CATEGORY_NAME = "product_category_name";
    private static final String KEY_PRODUCT_RESERVE_STATUS = "product_reserve_status";
    private static final String KEY_PRODUCT_SOLD_STATUS = "product_sold_status";
    private static final String KEY_PRODUCT_LIKE_STATUS = "product_like_status";
    private static final String KEY_PRODUCT_OWNER_NAME = "product_owner_name";
    private static final String KEY_PRODUCT_OWNER_ID = "product_owner_id";
    private static final String KEY_PRODUCT_OWNER_PROFILE_REVIEWS = "product_owner_profile_reviews";
    private static final String KEY_PRODUCT_OWNER_PROFILE_PIC = "product_owner_profile_pic";
    private static final String KEY_PRODUCT_STATUS= "product_status";


    //TABLE_category Table - column names
    private static final String KEY_SERVER_CATEGORIES_ID = "category_server_id";
    private static final String KEY_CATEGORIES_NAME = "category_name";


    // table_product table create statement
    private static final String CREATE_TABLE_PRODUCT = "CREATE TABLE "
            + TABLE_PRODUCT + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_SERVER_PRODUCT_ID + " INTEGER,"
            + KEY_PRODUCT_TITLE + " TEXT,"
            + KEY_PRODUCT_PRICE + " TEXT,"
            + KEY_PRODUCT_PRICE_CURRENCY + " TEXT,"
            + KEY_PRODUCT_CITY + " TEXT,"
            + KEY_PRODUCT_PINCODE + " TEXT,"
            + KEY_PRODUCT_ADDRESS + " TEXT,"
            + KEY_PRODUCT_DESCRIPTION + " TEXT,"
            + KEY_PRODUCT_IMAGE1 + " TEXT,"
            + KEY_PRODUCT_IMAGE2 + " TEXT,"
            + KEY_PRODUCT_IMAGE3 + " TEXT,"
            + KEY_PRODUCT_IMAGE4 + " TEXT,"
            + KEY_PRODUCT_MAP_SCREENSHOT + " TEXT,"
            + KEY_PRODUCT_LATITUDE + " TEXT,"
            + KEY_PRODUCT_LONGITUDE + " TEXT,"
            + KEY_PRODUCT_TOTAL_LIKES + " TEXT,"
            + KEY_PRODUCT_TOTAL_REVIEWS + " TEXT,"
            + KEY_PRODUCT_CATEGORY_ID + " TEXT,"
            + KEY_PRODUCT_CATEGORY_NAME + " TEXT,"
            + KEY_PRODUCT_RESERVE_STATUS + " TEXT,"
            + KEY_PRODUCT_SOLD_STATUS + " TEXT,"
            + KEY_PRODUCT_LIKE_STATUS + " TEXT,"
            + KEY_PRODUCT_OWNER_NAME + " TEXT,"
            + KEY_PRODUCT_OWNER_ID + " TEXT,"
            + KEY_PRODUCT_OWNER_PROFILE_REVIEWS + " TEXT,"
            + KEY_PRODUCT_OWNER_PROFILE_PIC + " TEXT,"
            + KEY_PRODUCT_STATUS + " TEXT"
            + ")";

    // table_category table create statement
    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE "
            + TABLE_CATEGORIES + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_SERVER_CATEGORIES_ID + " INTEGER,"
            + KEY_CATEGORIES_NAME + " TEXT" + ")";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PRODUCT);
        db.execSQL(CREATE_TABLE_CATEGORIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public long insertOrUpdateProduct(Product product) {
        Product existProduct = getProductById(product
                .getProduct_server_id());
        System.out.println("insertOrUpdateexistProduct is : "
                + existProduct);
        SQLiteDatabase db = this.getWritableDatabase();
        if (existProduct != null) {
            System.out
                    .println("-----------------  exist	product in loop --------------------");

            ContentValues values = new ContentValues();

            values.put(KEY_PRODUCT_TITLE, product.getProduct_title());
            values.put(KEY_PRODUCT_PRICE, product.getProduct_price());
            values.put(KEY_PRODUCT_PRICE_CURRENCY, product.getProduct_price_currency());
            values.put(KEY_PRODUCT_CITY, product.getProduct_city());
            values.put(KEY_PRODUCT_PINCODE, product.getProduct_pincode());
            values.put(KEY_PRODUCT_ADDRESS, product.getProduct_address());
            values.put(KEY_PRODUCT_DESCRIPTION, product.getProduct_description());
            values.put(KEY_PRODUCT_IMAGE1, product.getProduct_image1());
            values.put(KEY_PRODUCT_IMAGE2, product.getProduct_image2());
            values.put(KEY_PRODUCT_IMAGE3, product.getProduct_image3());
            values.put(KEY_PRODUCT_IMAGE4, product.getProduct_image4());
            values.put(KEY_PRODUCT_MAP_SCREENSHOT, product.getProduct_map_screenshot());
            values.put(KEY_PRODUCT_LATITUDE, product.getProduct_latitude());
            values.put(KEY_PRODUCT_LONGITUDE, product.getProduct_longitude());
            values.put(KEY_PRODUCT_TOTAL_LIKES, product.getProduct_total_likes());
            values.put(KEY_PRODUCT_TOTAL_REVIEWS, product.getProduct_total_reviews());
            values.put(KEY_PRODUCT_CATEGORY_ID, product.getProduct_category_id());
            values.put(KEY_PRODUCT_CATEGORY_NAME, product.getProduct_category_name());
            values.put(KEY_PRODUCT_RESERVE_STATUS, product.getProduct_reserve_status());
            values.put(KEY_PRODUCT_SOLD_STATUS, product.getProduct_sold_status());
            values.put(KEY_PRODUCT_LIKE_STATUS, product.getProduct_like_status());
            values.put(KEY_PRODUCT_OWNER_NAME, product.getProduct_owner_name());
            values.put(KEY_PRODUCT_OWNER_ID, product.getProduct_owner_id());
            values.put(KEY_PRODUCT_OWNER_PROFILE_REVIEWS, product.getProduct_owner_profile_reviews());
            values.put(KEY_PRODUCT_OWNER_PROFILE_PIC, product.getProduct_owner_profile_pic());
            values.put(KEY_PRODUCT_STATUS, product.getProduct_status());



            int noOfRowsAffected = db.update(TABLE_PRODUCT, values,
                    KEY_SERVER_PRODUCT_ID + " = ?", new String[]{String
                            .valueOf(existProduct
                                    .getProduct_server_id())});
            System.out.println("existdriver no of rows affected : "
                    + noOfRowsAffected);
            db.close();
            return 1;
        } else {
            ContentValues values = new ContentValues();

            values.put(KEY_SERVER_PRODUCT_ID,
                    product.getProduct_server_id());
            values.put(KEY_PRODUCT_TITLE, product.getProduct_title());
            values.put(KEY_PRODUCT_PRICE, product.getProduct_price());
            values.put(KEY_PRODUCT_PRICE_CURRENCY, product.getProduct_price_currency());
            values.put(KEY_PRODUCT_CITY, product.getProduct_city());
            values.put(KEY_PRODUCT_PINCODE, product.getProduct_pincode());
            values.put(KEY_PRODUCT_ADDRESS, product.getProduct_address());
            values.put(KEY_PRODUCT_DESCRIPTION, product.getProduct_description());
            values.put(KEY_PRODUCT_IMAGE1, product.getProduct_image1());
            values.put(KEY_PRODUCT_IMAGE2, product.getProduct_image2());
            values.put(KEY_PRODUCT_IMAGE3, product.getProduct_image3());
            values.put(KEY_PRODUCT_IMAGE4, product.getProduct_image4());
            values.put(KEY_PRODUCT_MAP_SCREENSHOT, product.getProduct_map_screenshot());
            values.put(KEY_PRODUCT_LATITUDE, product.getProduct_latitude());
            values.put(KEY_PRODUCT_LONGITUDE, product.getProduct_longitude());
            values.put(KEY_PRODUCT_TOTAL_LIKES, product.getProduct_total_likes());
            values.put(KEY_PRODUCT_TOTAL_REVIEWS, product.getProduct_total_reviews());
            values.put(KEY_PRODUCT_CATEGORY_ID, product.getProduct_category_id());
            values.put(KEY_PRODUCT_CATEGORY_NAME, product.getProduct_category_name());
            values.put(KEY_PRODUCT_RESERVE_STATUS, product.getProduct_reserve_status());
            values.put(KEY_PRODUCT_SOLD_STATUS, product.getProduct_sold_status());
            values.put(KEY_PRODUCT_LIKE_STATUS, product.getProduct_like_status());
            values.put(KEY_PRODUCT_OWNER_NAME, product.getProduct_owner_name());
            values.put(KEY_PRODUCT_OWNER_ID, product.getProduct_owner_id());
            values.put(KEY_PRODUCT_OWNER_PROFILE_REVIEWS, product.getProduct_owner_profile_reviews());
            values.put(KEY_PRODUCT_OWNER_PROFILE_PIC, product.getProduct_owner_profile_pic());
            values.put(KEY_PRODUCT_STATUS, product.getProduct_status());
            long id = db.insert(TABLE_PRODUCT, null, values);
            db.close();
            return id;
        }
    }

    public long insertOrUpdateCategories(Category category) {
        Category existCategory = getCategoryById(category
                .getServer_categoryid());
        System.out.println("insertOrUpdateexistCategory is : "
                + existCategory);
        SQLiteDatabase db = this.getWritableDatabase();
        if (existCategory != null) {
            System.out.println("-----------------  exist  category in loop --------------------");

            ContentValues values = new ContentValues();
            values.put(KEY_CATEGORIES_NAME,
                    category.getCategory_name());
            int noOfRowsAffected = db.update(TABLE_CATEGORIES, values,
                    KEY_SERVER_CATEGORIES_ID + " = ?", new String[]{String
                            .valueOf(existCategory
                                    .getServer_categoryid())});
            System.out.println("existcategory no of rows affected : "
                    + noOfRowsAffected);
            db.close();
            return 1;
        } else {
            ContentValues values = new ContentValues();
            values.put(KEY_SERVER_CATEGORIES_ID,
                    category.getServer_categoryid());
            values.put(KEY_CATEGORIES_NAME,
                    category.getCategory_name());

            long id = db.insert(TABLE_CATEGORIES, null, values);
            System.out.println("row id----------------------------------" + id);
            db.close();
            return id;
        }
    }

    public Product getProductById(
            int getId) {
        String query = "select * from " + TABLE_PRODUCT + " where "
                + KEY_SERVER_PRODUCT_ID + " = " + getId + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor == null) {
            return null;
        } else if (cursor.getCount() == 0) {
            System.out.println("product cursor size is : "
                    + cursor.getCount());
            return null;
        }
        Product product = new Product();

        if (cursor.moveToFirst()) {

            product.setProduct_id(Integer.parseInt(cursor.getString(cursor
                    .getColumnIndex(KEY_ID))));
            product.setProduct_server_id(Integer.parseInt(cursor.getString(cursor
                    .getColumnIndex(KEY_SERVER_PRODUCT_ID))));
            product.setProduct_title(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_TITLE)));
            product.setProduct_price(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_PRICE)));
            product.setProduct_price_currency(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_PRICE_CURRENCY)));
            product.setProduct_city(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_CITY)));
            product.setProduct_pincode(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_PINCODE)));
            product.setProduct_address(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_ADDRESS)));
            product.setProduct_description(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_DESCRIPTION)));
            product.setProduct_image1(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_IMAGE1)));
            product.setProduct_image2(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_IMAGE2)));
            product.setProduct_image3(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_IMAGE3)));
            product.setProduct_image4(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_IMAGE4)));
            product.setProduct_map_screenshot(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_MAP_SCREENSHOT)));
            product.setProduct_latitude(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_LATITUDE)));
            product.setProduct_longitude(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_LONGITUDE)));
            product.setProduct_total_likes(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_TOTAL_LIKES)));
            product.setProduct_total_reviews(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_TOTAL_REVIEWS)));
            product.setProduct_category_id(Integer.parseInt(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_CATEGORY_ID))));
            product.setProduct_category_name(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_CATEGORY_NAME)));
            product.setProduct_reserve_status(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_RESERVE_STATUS)));
            product.setProduct_sold_status(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_SOLD_STATUS)));
            product.setProduct_like_status(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_LIKE_STATUS)));
            product.setProduct_owner_name(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_OWNER_NAME)));
            product.setProduct_owner_id(Integer.parseInt(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_OWNER_ID))));
            product.setProduct_owner_profile_reviews(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_OWNER_PROFILE_REVIEWS)));
            product.setProduct_owner_profile_pic(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_OWNER_PROFILE_PIC)));
            product.setProduct_status(cursor.getString(cursor
                    .getColumnIndex(KEY_PRODUCT_STATUS)));

        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        db.close();
        return product;
    }


    public Category getCategoryById(
            int getId) {
        String query = "select * from " + TABLE_CATEGORIES + " where "
                + KEY_SERVER_CATEGORIES_ID + " = " + getId + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor == null) {
            return null;
        } else if (cursor.getCount() == 0) {
            System.out.println("category cursor size is : "
                    + cursor.getCount());
            return null;
        }
        Category category = new Category();
        if (cursor.moveToFirst()) {

            category.setCategory_id(Integer.parseInt(cursor.getString(cursor
                    .getColumnIndex(KEY_ID))));
            category.setServer_categoryid(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_SERVER_CATEGORIES_ID))));
            category.setCategory_name(cursor.getString(cursor
                    .getColumnIndex(KEY_CATEGORIES_NAME)));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        db.close();
        return category;
    }

    public List<Product> getProductList() {
        List<Product> ProductList = new ArrayList<Product>();
        try {
            String query = "select * from " + TABLE_PRODUCT ;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            System.out.println("cursor is : " + cursor);
            if (cursor == null) {
                return ProductList;
            } else if (cursor.getCount() == 0) {
                System.out.println("PRODUCTList cursor size is : "
                        + cursor.getCount());
                return ProductList;
            }
            if (cursor.moveToFirst()) {

                do {
                    Product product = new Product();
                    product.setProduct_server_id(Integer.parseInt(cursor
                            .getString(cursor.getColumnIndex(KEY_ID))));
                    product.setProduct_server_id(Integer.parseInt(cursor.getString(cursor
                            .getColumnIndex(KEY_SERVER_PRODUCT_ID))));
                    product.setProduct_title(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_TITLE)));
                    product.setProduct_price(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_PRICE)));
                    product.setProduct_price_currency(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_PRICE_CURRENCY)));
                    product.setProduct_city(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_CITY)));
                    product.setProduct_pincode(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_PINCODE)));
                    product.setProduct_address(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_ADDRESS)));
                    product.setProduct_description(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_DESCRIPTION)));
                    product.setProduct_image1(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_IMAGE1)));
                    product.setProduct_image2(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_IMAGE2)));
                    product.setProduct_image3(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_IMAGE3)));
                    product.setProduct_image4(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_IMAGE4)));
                    product.setProduct_map_screenshot(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_MAP_SCREENSHOT)));
                    product.setProduct_latitude(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_LATITUDE)));
                    product.setProduct_longitude(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_LONGITUDE)));
                    product.setProduct_total_likes(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_TOTAL_LIKES)));
                    product.setProduct_total_reviews(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_TOTAL_REVIEWS)));
                    product.setProduct_category_id(Integer.parseInt(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_CATEGORY_ID))));
                    product.setProduct_category_name(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_CATEGORY_NAME)));
                    product.setProduct_reserve_status(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_RESERVE_STATUS)));
                    product.setProduct_sold_status(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_SOLD_STATUS)));
                    product.setProduct_like_status(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_LIKE_STATUS)));
                    product.setProduct_owner_name(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_OWNER_NAME)));
                    product.setProduct_owner_id(Integer.parseInt(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_OWNER_ID))));
                    product.setProduct_owner_profile_reviews(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_OWNER_PROFILE_REVIEWS)));
                    product.setProduct_owner_profile_pic(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_OWNER_PROFILE_PIC)));
                    product.setProduct_status(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_STATUS)));

                    ProductList.add(product);
                } while (cursor.moveToNext());

            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ProductList;
    }

    public List<Product> getProductListForRemoveClassified(int customerId) {
        List<Product> ProductList = new ArrayList<Product>();
        try {//AppPreferences.getCustomerId()
            String query = "select * from " + TABLE_PRODUCT ;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            System.out.println("cursor is : " + cursor);
            if (cursor == null) {
                return ProductList;
            } else if (cursor.getCount() == 0) {
                System.out.println("productList cursor size is : "
                        + cursor.getCount());
                return ProductList;
            }
            if (cursor.moveToFirst()) {
                do {
                    Product product = new Product();
                    product.setProduct_server_id(Integer.parseInt(cursor
                            .getString(cursor.getColumnIndex(KEY_ID))));
                    product.setProduct_server_id(Integer.parseInt(cursor.getString(cursor
                            .getColumnIndex(KEY_SERVER_PRODUCT_ID))));
                    product.setProduct_title(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_TITLE)));
                    product.setProduct_price(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_PRICE)));
                    product.setProduct_price_currency(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_PRICE_CURRENCY)));
                    product.setProduct_city(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_CITY)));
                    product.setProduct_pincode(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_PINCODE)));
                    product.setProduct_address(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_ADDRESS)));
                    product.setProduct_description(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_DESCRIPTION)));
                    product.setProduct_image1(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_IMAGE1)));
                    product.setProduct_image2(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_IMAGE2)));
                    product.setProduct_image3(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_IMAGE3)));
                    product.setProduct_image4(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_IMAGE4)));
                    product.setProduct_map_screenshot(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_MAP_SCREENSHOT)));
                    product.setProduct_latitude(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_LATITUDE)));
                    product.setProduct_longitude(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_LONGITUDE)));
                    product.setProduct_total_likes(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_TOTAL_LIKES)));
                    product.setProduct_total_reviews(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_TOTAL_REVIEWS)));
                    product.setProduct_category_id(Integer.parseInt(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_CATEGORY_ID))));
                    product.setProduct_category_name(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_CATEGORY_NAME)));
                    product.setProduct_reserve_status(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_RESERVE_STATUS)));
                    product.setProduct_sold_status(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_SOLD_STATUS)));
                    product.setProduct_like_status(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_LIKE_STATUS)));
                    product.setProduct_owner_name(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_OWNER_NAME)));
                    product.setProduct_owner_id(Integer.parseInt(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_OWNER_ID))));
                    product.setProduct_owner_profile_reviews(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_OWNER_PROFILE_REVIEWS)));
                    product.setProduct_owner_profile_pic(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_OWNER_PROFILE_PIC)));
                    product.setProduct_status(cursor.getString(cursor
                            .getColumnIndex(KEY_PRODUCT_STATUS)));
                    ProductList.add(product);
                } while (cursor.moveToNext());

            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ProductList;
    }


    public ArrayList<Category> getCategoryList() {
        ArrayList<Category> myCategoryList = new ArrayList<Category>();
        try {
            String query = "select * from " + TABLE_CATEGORIES;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            System.out.println("cursor is : " + cursor);
            if (cursor == null) {
                return myCategoryList;
            } else if (cursor.getCount() == 0) {
                System.out.println("CategoryList cursor size is : "
                        + cursor.getCount());
                return myCategoryList;
            }
            if (cursor.moveToFirst()) {
                do {
                    Category categoryOne = new Category();
                    categoryOne.setCategory_id(Integer.parseInt(cursor
                            .getString(cursor.getColumnIndex(KEY_ID))));

                    categoryOne.setServer_categoryid(Integer
                            .parseInt(cursor.getString(cursor
                                    .getColumnIndex(KEY_SERVER_CATEGORIES_ID))));

                    categoryOne.setCategory_name(cursor.getString(cursor.getColumnIndex(KEY_CATEGORIES_NAME)));
                    myCategoryList.add(categoryOne);
                } while (cursor.moveToNext());
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("----------------- exception get category list method called -------------------");
        }
        return myCategoryList;
    }

    public void deleterow(int serverId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCT, KEY_SERVER_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(serverId)});
        db.close();
    }
}
