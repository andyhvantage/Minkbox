package com.minkbox;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.minkbox.databace.DataBaseHelper;
import com.minkbox.model.Category;
import com.minkbox.model.Product;
import com.minkbox.utils.AppConstants;
import com.minkbox.utils.AppPreferences;
import com.minkbox.utils.ConnectionDetector;
import com.minkbox.utils.DialogManager;
import com.minkbox.utils.Function;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class CellAProduct extends ActionBarActivity {
    Toolbar toolbar;
    EditText product_title;
    EditText product_description;
    EditText product_price;
    ImageView cameraImage1;
    ImageView cameraImage2;
    ImageView cameraImage3;
    ImageView cameraImage4;
    ImageView product_image1;
    ImageView product_image2;
    ImageView product_image3;
    ImageView product_image4;
    private static int SELECT_PICTURE = 1;
    private static String selectedImagePath1 = "";
    private static String selectedImagePath2 = "";
    private static String selectedImagePath3 = "";
    private static String selectedImagePath4 = "";

    static String encoded_img1;
    static String encoded_img2;
    static String encoded_img3;
    static String encoded_img4;

    LinearLayout cam_ll;
    ImageView camera;
    int count = 0;
    List<String> imageList = new ArrayList<String>();

    Button ok;
    Button cancle;
    PopupWindow pwindo;
    DialogManager alert = new DialogManager();

    ArrayList<String> productcatogary = new ArrayList<String>();
    ArrayList<Integer> categoriesid = new ArrayList<Integer>();
    public String categoryName = null;
    public int categoryid = 0;
    Spinner category_spn;//usd
    TextView currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cell_aproduct);
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = getString(R.string.sell_an_item_text);
        getSupportActionBar().setTitle(title);

        currency = (TextView) findViewById(R.id.usd);

        product_title = (EditText) findViewById(R.id.cell_aproduct_title);//cell_aproduct_title
        product_description = (EditText) findViewById(R.id.description);
        product_price = (EditText) findViewById(R.id.price);

        findViewById(R.id.layout_l1).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Function.hideSoftKeyboard(CellAProduct.this);
                return false;
            }
        });

        DataBaseHelper db = new DataBaseHelper(CellAProduct.this);
        // List<String> spin_list = new ArrayList<String>();
        ArrayList<Category> lists = db.getCategoryList();
        for (int i = 0; i < lists.size(); i++) {
            productcatogary.add(lists.get(i).getCategory_name());
            Log.d("database 2", "get " + lists.get(i).getCategory_name());
            categoriesid.add(lists.get(i).getCategory_id());
        }

        category_spn = (Spinner) findViewById(R.id.category);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(CellAProduct.this, android.R.layout.simple_spinner_dropdown_item, productcatogary);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_spn.setAdapter(adapter2);

        category_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int i, long l) {
                categoryName = productcatogary.get(i).toString().trim();
                categoryid = categoriesid.get(i);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        cam_ll = (LinearLayout) findViewById(R.id.cam_ll);

        camera = (ImageView) findViewById(R.id.camera);

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        double ratio = ((float) (width)) / 255.0;
        int height = (int) (ratio * 50);

        LayoutParams cardViewParams = new LayoutParams(height, height);
        //  cardViewParams.setMargins(10, 20, 10, 10);
        // camera.setLayoutParams(cardViewParams);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder getImageFrom = new AlertDialog.Builder(CellAProduct.this);
                getImageFrom.setTitle("Select:");
                final CharSequence[] opsChars = new CharSequence[]{getResources().getString(R.string.takepic), getResources().getString(R.string.opengallery)};
                getImageFrom.setItems(opsChars, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, 0);
                        } else if (which == 1) {
                            Intent galleryIntent = new Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, SELECT_PICTURE);
                        }
                        dialog.dismiss();
                    }
                });
                getImageFrom.show();

            }
        });
    }

    public void createImage(Bitmap bm, final int i) {

        final ImageView imageView = new ImageView(CellAProduct.this);


        Display display1 = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display1.getSize(size);
        int width1 = size.x;
        int height1 = size.y;

        Display display_nw = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display_nw.getMetrics(outMetrics);

        float density  = getResources().getDisplayMetrics().density;

        System.out.println("dimen_density_original "+width1);

        System.out.println("dimen_density "+(int) getResources().getDimension(R.dimen.cell_product)+" , "+density);
       width1= (width1-(int) getResources().getDimension(R.dimen.cell_product))/4;

        System.out.println("dimen_density_width "+width1);

        LayoutParams cardViewParams = new LayoutParams(width1, (int) getResources().getDimension(R.dimen.cell_product_images_height));
        cardViewParams.setMargins((int) getResources().getDimension(R.dimen.cell_product_images_margins), (int) getResources().getDimension(R.dimen.cell_product_images_margins), (int) getResources().getDimension(R.dimen.cell_product_images_margins), (int) getResources().getDimension(R.dimen.cell_product_images_margins));
        imageView.setLayoutParams(cardViewParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageBitmap(bm);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAndShowAlertDialog(imageView);
            }
        });


        cam_ll.addView(imageView, i);
        Log.d("pos", "im" + i);

        if (count == 3) {
            camera.setVisibility(View.GONE);
        }
    }

    private void createAndShowAlertDialog(final View image) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to remove this image?");
        builder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO
                        cam_ll.removeView(image);
                        camera.setVisibility(View.VISIBLE);
                        //imageList.remove(count);
                        count--;
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /*mannis*/
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                Bitmap newBitmap = null;

                selectedImagePath1 = com.minkbox.network.ImageFilePath.getPath(CellAProduct.this,
                        selectedImageUri);

                Log.d("img path", selectedImagePath1);
                newBitmap = getImage(selectedImageUri);
                System.out.println(" bitmap image base 64 is : ------------- " + newBitmap);

                //  encodeImg1Tobase64(newBitmap);
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


                if (selectedImageUri != null) {
                    if (count < 4) {
                        createImage(bm, count);
                        count++;
                    } else {


                    }
                }
            } else if (requestCode == 0) {

                Bitmap newBitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                selectedImagePath1 = MediaStore.Images.Media.insertImage(getContentResolver(), newBitmap,
                        "title", null);
                Uri selectedImageUri = Uri.parse(selectedImagePath1);

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
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (selectedImageUri != null) {
                    if (count < 4) {
                        createImage(bm, count);
                        count++;
                    } else {


                    }
                }

            }
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public static String encodeImgTobase64(Bitmap image1) {
        Bitmap immagex = image1;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        // encoded_img1 = imageEncoded;
        Log.e("LOOK1", imageEncoded);
        return imageEncoded;
    }


    public Bitmap getImage(Uri uri) {
        InputStream stream;
        Bitmap newBitmap = null;
        try {
            stream = CellAProduct.this.getContentResolver().openInputStream(uri);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cell_aproduct, menu);
        return true;
    }

    public static Bitmap loadBitmapFromView(ImageView v) {

        Drawable drawable = v.getDrawable();
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        return bitmap;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        if (id == R.id.action_add_product) {
            if (!new Function().CheckGpsEnableOrNot(CellAProduct.this)) {
                showGpsSettingsAlert("GPS Settings...",
                        "GPS is not enabled. Do you want to go to settings menu?",
                        CellAProduct.this);
            }
            boolean cancel;
            String title_string = product_title.getText().toString();
            String price_string = product_price.getText().toString();
            String description_string = product_description.getText().toString()
                    .trim();


            int count = cam_ll.getChildCount();
            Log.d("countImage", count + "");
//            View v = null;
            ImageView v = null;
            for (int i = 0; i < count - 1; i++) {
//                v = cam_ll.getChildAt(i);
                v = (ImageView) cam_ll.getChildAt(i);

                imageList.add(encodeImgTobase64(loadBitmapFromView(v)));
                //do something with your child element
                Log.d("countImage", count + " of " + i);
            }

            //  Log.d("imageList", imageList.toString());


            if (TextUtils.isEmpty(title_string)) {
                product_title.setError(getString(R.string.error_field_required));
                cancel = false;
            } else if (title_string.length() < 2) {
                product_title.setError(getString(R.string.error_field_limit_required_title));
                cancel = false;
            } else if (TextUtils.isEmpty(price_string)) {
                product_price.setError(getString(R.string.error_field_required));
                cancel = false;
            } else if (price_string.length() > 8) {
                product_price.setError(getString(R.string.error_field_price_required_title));
                cancel = false;
            } else if (TextUtils.isEmpty(description_string)) {
                product_description
                        .setError(getString(R.string.error_field_required));
                cancel = false;
            } else if (description_string.length() < 2) {
                product_description
                        .setError(getString(R.string.error_field_limit_required_title));
                cancel = false;
            } else {
                cancel = true;
                Product product = new Product();
                product.setProduct_title(title_string);
                product.setProduct_category_name(categoryName);
                product.setProduct_price(price_string);
                product.setProduct_price_currency(currency.getText().toString().trim());//usd.getSelectedItem().toString()
                product.setProduct_description(description_string);

                for (int i = 0; i < imageList.size(); i++) {
                    if (i == 0) {
                        product.setProduct_image1(imageList.get(i));
                    } else if (i == 1) {
                        product.setProduct_image2(imageList.get(i));
                    } else if (i == 2) {
                        product.setProduct_image3(imageList.get(i));
                    } else if (i == 3) {
                        product.setProduct_image4(imageList.get(i));
                    }

                }


                if ((ConnectionDetector
                        .isConnectingToInternet(CellAProduct.this))) {

                    if (new Function().CheckGpsEnableOrNot(CellAProduct.this)) {

                        //	new FusedLocationService(getActivity());
                        if (AppPreferences.getLatutude(CellAProduct.this).equalsIgnoreCase("0.0") || AppPreferences.getLongitude(CellAProduct.this).equalsIgnoreCase("0.0")) {
                            enableMapLocation();
                        } else {


                            new AddNewProductTask(CellAProduct.this, product)
                                    .execute();
                        }
                    } else {
                        showGpsSettingsAlert("GPS Settings...",
                                "GPS is not enabled. Do you want to go to settings menu?",
                                CellAProduct.this);
                    }
                } else {
                    alert.showAlertDialog(CellAProduct.this, "Alert!",
                            "Please, check your internet connection.",
                            false);
                }


            }

            return true;
        }

        return super.onOptionsItemSelected(item);
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
                        CellAProduct.this.finish();
                    }
                });
        alertDialog.show();
    }

    private void enableMapLocation() {
        try {
            LayoutInflater inflater = (LayoutInflater) CellAProduct.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup_enable_map_location,
                    (ViewGroup) CellAProduct.this.findViewById(R.id.pop_up_exit_element));
            pwindo = new PopupWindow(layout, LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

            ok = (Button) layout.findViewById(R.id.ok);
            ok.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class AddNewProductTask extends AsyncTask<String, Void, String> {

        byte constant;
        Context context;
        String networkFlag = "false";
        String paymentId;
        private JSONArray contacts;
        private String TAG_CONTACTS = "result";
        private String TAG_COUNTRY = "code";
        private String TAG_CODE = "country";
        private String TAG_STD = "std";
        private JSONObject jsonObj;
        private Product product;
        private String TAG = AddNewProductTask.class.getSimpleName();
        private String name;
        private int serverid;
        private int status = 0;
        private ProgressDialog mProgressDialog;

        public AddNewProductTask(Context context, Product product) {
            this.context = context;
            this.product = product;
            mProgressDialog = new ProgressDialog(CellAProduct.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("nk", "cell pre method");
            mProgressDialog.show();
        }

        protected String doInBackground(String... params) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(AppConstants.ADDNEWPRODUCT);
                JSONArray jsonArray = new JSONArray();
                StringEntity se = null;
                Log.d("cur", product.getProduct_price_currency() + "");
                jsonObj = new JSONObject();
                jsonObj.put("title", product.getProduct_title());
                jsonObj.put("category", categoryid);
                jsonObj.put("price", product.getProduct_price());
                jsonObj.put("currency", "Inr");
                jsonObj.put("description", product.getProduct_description());
                jsonObj.put("fb_share_status", "0");
                jsonObj.put("customer_id",
                        AppPreferences.getCustomerId(context));

                System.out.print("customer id--------------------------" + AppPreferences.getCustomerId(context));
                System.out.print("category id--------------------------" + categoryid);

                if (product.getProduct_image1() != null) {
                    jsonObj.put("image1", product.getProduct_image1());
                } else {
                    jsonObj.put("image1", "");
                }
                if (product.getProduct_image2() != null) {
                    jsonObj.put("image2", product.getProduct_image2());
                } else {
                    jsonObj.put("image2", "");
                }

                if (product.getProduct_image3() != null) {
                    jsonObj.put("image3", product.getProduct_image3());
                } else {
                    jsonObj.put("image3", "");
                }

                if (product.getProduct_image4() != null) {
                    jsonObj.put("image4", product.getProduct_image4());
                } else {
                    jsonObj.put("image4", "");
                }

                jsonObj.put("latitude",
                        AppPreferences.getLatutude(CellAProduct.this));// "0"
                jsonObj.put("longitude",
                        AppPreferences.getLongitude(CellAProduct.this));// "0"
                jsonArray.put(jsonObj);

                try {
                    se = new StringEntity(jsonArray.toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                System.out.println("JSON sent is : " + jsonArray.toString());
                httppost.setEntity(se);

                HttpResponse response = null;
                response = httpclient.execute(httppost);
                System.out.println("response **************************"
                        + response);
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(response
                            .getEntity().getContent(), "UTF-8"));  //ISO-8859-1    UTF-8
                    System.out.println("Reader***************************"
                            + reader);
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
                if (jsonString != null && jsonString.length() > 0) {
                    if (jsonString.contains("status")) {
                        JSONTokener tokener = new JSONTokener(jsonString);

                        JSONObject finalResult = new JSONObject(jsonString);
                        System.out.println("JSON response is : " + finalResult);

                        if (finalResult.getString("status").equalsIgnoreCase(
                                "200")) {
                            System.out
                                    .println("--------- message 200 got ----------");
                            status = 200;
                            return jsonString;
                        } else if (finalResult.getString("status")
                                .equalsIgnoreCase("400")) {
                            Log.v(TAG, "Status error");
                            status = 400;
                            return "";
                        } else if (finalResult.getString("status")
                                .equalsIgnoreCase("500")) {
                            Log.v(TAG, "No Data Recieved in Request");
                            status = 500;
                            return "";
                        } else {
                            Log.v(TAG, "200 not recieved");
                            return "";
                        }

                    }
                }
            } catch (ConnectTimeoutException e) {
                networkFlag = "false";
            } catch (SocketTimeoutException e) {
                networkFlag = "false";
            } catch (Exception e) {
                e.printStackTrace();
            }
            return networkFlag;
        }

        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                mProgressDialog.dismiss();
                System.out.println("status--" + status);
                if (status == 200) {
                    System.out.println("200 dialog if");
                    selectedImagePath1 = "";
                    selectedImagePath2 = "";
                    selectedImagePath3 = "";
                    selectedImagePath4 = "";
                    encoded_img1 = "";
                    encoded_img2 = "";
                    encoded_img3 = "";
                    encoded_img4 = "";
                    product_title.setText("");
                    product_price.setText("");
                    product_description.setText("");
                    product_title.setHint("Ad title");
                    product_price.setHint("Price");
                    product_description.setHint("Description");
/*                    product_image1.setVisibility(View.GONE);
                    product_image2.setVisibility(View.GONE);
                    product_image3.setVisibility(View.GONE);
                    product_image4.setVisibility(View.GONE);
                    cameraImage1.setVisibility(View.VISIBLE);
                    cameraImage2.setVisibility(View.GONE);
                    cameraImage3.setVisibility(View.GONE);
                    cameraImage4.setVisibility(View.GONE);*/
                    // cam_ll.removeAllViews();

                    int total_product = Integer.parseInt(AppPreferences.getUserproductitems(CellAProduct.this));
                    total_product = total_product + 1;
                    String total_product_value = String.valueOf(total_product);
                    AppPreferences.setUserproductitems(CellAProduct.this, total_product_value);

                    Intent i = new Intent(CellAProduct.this, HomeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                } else if (status == 400) {
                    System.out.println("400 dialog if");
                    Toast.makeText(CellAProduct.this,
                            "Email address already exists.", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("500 dialog if");
                    Toast.makeText(CellAProduct.this, "Submission failed.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        selectedImagePath1 = "";
        selectedImagePath2 = "";
        selectedImagePath3 = "";
        selectedImagePath4 = "";
    }


    //////////////////////custom spinner///////////////////////
    public class MyAdapter extends ArrayAdapter {
        String[] objects;

        public MyAdapter(Context context, int textViewResourceId,
                         String[] objects) {
            super(context, textViewResourceId, objects);
            this.objects = objects;
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_spinner, parent, false);

            TextView tvLanguage = (TextView) layout
                    .findViewById(R.id.textcurrency);
            tvLanguage.setText(objects[position]);


            return layout;
        }

        // It gets a View that displays in the drop down popup the data at the specified position
        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        // It gets a View that displays the data at the specified position
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
    }

    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
//        Intent i = new Intent(CellAProduct.this, HomeActivity.class);
//        startActivity(i);
//        finish();
    }

}
