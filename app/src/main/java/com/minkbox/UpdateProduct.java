package com.minkbox;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.nostra13.universalimageloader.core.ImageLoader;

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

public class UpdateProduct extends ActionBarActivity {
    private static final int SELECT_PICTURE = 1;
    Toolbar toolbar;
    EditText product_title;
    EditText product_description;
    EditText product_price;
    private static String selectedImagePath1 = "";

    LinearLayout cam_ll;
    ImageView camera;
    int count = 0;
    List<String> imageList = new ArrayList<String>();
    int product_id;
    Button ok;
    PopupWindow pwindo;
    DialogManager alert = new DialogManager();

    ArrayList<String> productcatogary = new ArrayList<String>();
    ArrayList<Integer> categoriesid = new ArrayList<Integer>();
    public String categoryName = null;
    public int categoryid = 0;
    Spinner category_spn;
    TextView usd;
    Product product;
    DataBaseHelper db;

    //String[] currency = {"USD ($)", "EUR (?)", "GBP (BJ)", "JPY (?)", "CNY (?)"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_product);
        Bundle extras = getIntent().getExtras();
        product_id = extras.getInt("productid");
        System.out.println("product_id in update*******************" + product_id);
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = getString(R.string.sell_an_item_text);
        getSupportActionBar().setTitle(title);

        product_title = (EditText) findViewById(R.id.title);
        product_description = (EditText) findViewById(R.id.description);
        product_price = (EditText) findViewById(R.id.price);

        db = new DataBaseHelper(getApplicationContext());
        product = db.getProductById(product_id);
        product_price.setText(product.getProduct_price());
        product_title.setText(product.getProduct_title());
        product_description.setText(product.getProduct_description());



        usd = (TextView) findViewById(R.id.usd);
        usd.setText(product.getProduct_price_currency());
//        MyAdapter adapter = new MyAdapter(this, R.layout.custom_spinner,
//                getResources().getStringArray(R.array.currency));
//        usd.setAdapter(adapter);


//        int pos = adapter.getPosition(product.getProduct_price_currency());

  //      SetSpinnerSelection(usd, getResources().getStringArray(R.array.currency),null, product.getProduct_price_currency());


        DataBaseHelper db = new DataBaseHelper(UpdateProduct.this);
        // List<String> spin_list = new ArrayList<String>();
        ArrayList<Category> lists = db.getCategoryList();
        for (int i = 0; i < lists.size(); i++) {
            productcatogary.add(lists.get(i).getCategory_name());
            categoriesid.add(lists.get(i).getCategory_id());
        }

        category_spn = (Spinner) findViewById(R.id.category);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(UpdateProduct.this, android.R.layout.simple_spinner_dropdown_item, productcatogary);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_spn.setAdapter(adapter2);

        SetSpinnerSelection(category_spn, null,productcatogary, product.getProduct_category_name());

        category_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int i, long l) {
                categoryName = productcatogary.get(i).toString().trim();
                categoryid = categoriesid.get(i);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });



        /*manish*/
        cam_ll = (LinearLayout) findViewById(R.id.cam_ll);

        camera = (ImageView)findViewById(R.id.camera);
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        double ratio = ((float) (width)) / 255.0;
        int height = (int) (ratio * 50);
        LinearLayout.LayoutParams cardViewParams = new LinearLayout.LayoutParams(height, height);
   //     cardViewParams.setMargins(10, 20, 10, 10);
    //    camera.setLayoutParams(cardViewParams);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder getImageFrom = new AlertDialog.Builder(UpdateProduct.this);
                getImageFrom.setTitle("Select:");
                final CharSequence[] opsChars = {getResources().getString(R.string.takepic), getResources().getString(R.string.opengallery)};
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

        if(product.getProduct_image1().contains(".png")){
            createImageFormUrl(product.getProduct_image1(), 0);
            count++;
        }
        if(product.getProduct_image2().contains(".png")){
            createImageFormUrl(product.getProduct_image2(), 0);
            count++;
        }
        if(product.getProduct_image3().contains(".png")){
            createImageFormUrl(product.getProduct_image3(), 0);
            count++;
        }
        if(product.getProduct_image4().contains(".png")){
            createImageFormUrl(product.getProduct_image4(), 0);
            count++;
        }

        /*manish*/

    }

    public void SetSpinnerSelection(Spinner spinner,String[] arrayString, ArrayList<String> arrayList,String text)
    {
        if(arrayString!=null){

            for(int i=0;i<arrayString.length;i++)
            {
                if(arrayString[i].equals(text))
                {
                    spinner.setSelection(i);
                }
            }
        }
        if(arrayList!=null) {
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).equals(text)) {
                    spinner.setSelection(i);
                }
            }
        }
    }

    public void createImageFormUrl(String url, final int i) {

        final ImageView imageView = new ImageView(UpdateProduct.this);

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

        System.out.println("dimen_density_width " + width1);

        LinearLayout.LayoutParams cardViewParams = new LinearLayout.LayoutParams(width1, (int) getResources().getDimension(R.dimen.cell_product_images_height));
        cardViewParams.setMargins((int) getResources().getDimension(R.dimen.cell_product_images_margins), (int) getResources().getDimension(R.dimen.cell_product_images_margins), (int) getResources().getDimension(R.dimen.cell_product_images_margins), (int) getResources().getDimension(R.dimen.cell_product_images_margins));
        imageView.setLayoutParams(cardViewParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        ImageLoader.getInstance().displayImage(url,imageView);

        //imageView.setImageBitmap(bm);

        //encodeImg1Tobase64(bm);

        //imageList.add(encodeImgTobase64(bm));

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

    public void createImage(Bitmap bm, final int i) {

        final ImageView imageView = new ImageView(UpdateProduct.this);

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

        LinearLayout.LayoutParams cardViewParams = new LinearLayout.LayoutParams(width1, (int) getResources().getDimension(R.dimen.cell_product_images_height));
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

        System.out.println(" Position of image is : ----------- "+i);
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

                        System.out.println(" count value is : ----------- "+count);

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

                selectedImagePath1 = com.minkbox.network.ImageFilePath.getPath(UpdateProduct.this,
                        selectedImageUri);

                Log.d("img path", selectedImagePath1);
                newBitmap = getImage(selectedImageUri);

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


            }else if(requestCode == 0){

                Bitmap newBitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                selectedImagePath1 = MediaStore.Images.Media.insertImage(getContentResolver(), newBitmap,
                        "title" , null);
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
            stream = UpdateProduct.this.getContentResolver().openInputStream(uri);
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

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_product) {
            if (!new Function().CheckGpsEnableOrNot(UpdateProduct.this)) {
                showGpsSettingsAlert("GPS Settings...",
                        "GPS is not enabled. Do you want to go to settings menu?",
                        UpdateProduct.this);
            }
            boolean cancel;
            String title_string = product_title.getText().toString();
            String price_string = product_price.getText().toString();
            String description_string = product_description.getText().toString()
                    .trim();


            int count = cam_ll.getChildCount();
            Log.d("countImage", count+"");
            View v = null;
            for (int i = 0; i < count-1; i++) {
                v = cam_ll.getChildAt(i);
                 imageList.add(encodeImgTobase64(loadBitmapFromView(v)));
                //do something with your child element
                Log.d("countImage", count+" of "+ i);
            }
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
                product.setProduct_id(this.product.getProduct_id());
                product.setProduct_title(title_string);
                product.setProduct_category_name(categoryName);
                product.setProduct_price(price_string);
                product.setProduct_price_currency(usd.getText().toString());//.substring(0, 3));
                product.setProduct_description(description_string);

                for (int i = 0; i < imageList.size(); i++) {
                    if (i == 0) {
                        product.setProduct_image1(imageList.get(i));
                    }
                    else if (i == 1) {
                        product.setProduct_image2(imageList.get(i));
                    }
                    else if (i == 2) {
                        product.setProduct_image3(imageList.get(i));
                    }
                    else if (i == 3) {
                        product.setProduct_image4(imageList.get(i));
                    }

                }

                if ((ConnectionDetector
                        .isConnectingToInternet(UpdateProduct.this))) {

                    if (new Function().CheckGpsEnableOrNot(UpdateProduct.this)) {

                        //	new FusedLocationService(getActivity());
                        if (AppPreferences.getLatutude(UpdateProduct.this).equalsIgnoreCase("0.0") || AppPreferences.getLongitude(UpdateProduct.this).equalsIgnoreCase("0.0")) {
                            enableMapLocation();
                        } else {


                            new UpdateProductTask(UpdateProduct.this, product)
                                    .execute();
                        }
                    } else {
                        showGpsSettingsAlert("GPS Settings...",
                                "GPS is not enabled. Do you want to go to settings menu?",
                                UpdateProduct.this);
                    }
                } else {
                    alert.showAlertDialog(UpdateProduct.this, "Alert!",
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
                        UpdateProduct.this.finish();
                    }
                });
        alertDialog.show();
    }

    private void enableMapLocation() {
        try {
            LayoutInflater inflater = (LayoutInflater) UpdateProduct.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup_enable_map_location,
                    (ViewGroup) UpdateProduct.this.findViewById(R.id.pop_up_exit_element));
            pwindo = new PopupWindow(layout, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, true);
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

    public class UpdateProductTask extends AsyncTask<String, Void, String> {

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
        private String TAG = UpdateProductTask.class.getSimpleName();
        private String name;
        private int serverid;
        private int status = 0;
        private ProgressDialog mProgressDialog;

        public UpdateProductTask(Context context, Product product) {
            this.context = context;
            this.product = product;
            mProgressDialog = new ProgressDialog(UpdateProduct.this);
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
                HttpPost httppost = new HttpPost(AppConstants.UPDATEPRODUCTAPI);
                JSONArray jsonArray = new JSONArray();
                StringEntity se = null;
                Log.d("cur", product.getProduct_price_currency() + "");
                jsonObj = new JSONObject();
                jsonObj.put("product_id", product_id);
                jsonObj.put("title", product.getProduct_title());
                jsonObj.put("category", String.valueOf(categoryid));
                jsonObj.put("price", product.getProduct_price());
                jsonObj.put("currency", product.getProduct_price_currency());
                jsonObj.put("description", product.getProduct_description());
                jsonObj.put("fb_share_status", "0");
                jsonObj.put("customer_id",
                        String.valueOf(AppPreferences.getCustomerId(context)));

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
                        AppPreferences.getLatutude(UpdateProduct.this));// "0"
                jsonObj.put("longitude",
                        AppPreferences.getLongitude(UpdateProduct.this));// "0"
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
                            + reader.toString());
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


                    product_title.setText("");
                    product_price.setText("");
                    product_description.setText("");
                    product_title.setHint("Ad title");
                    product_price.setHint("Price");
                    product_description.setHint("Description");

                    Intent i = new Intent(UpdateProduct.this, HomeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                } else if (status == 400) {
                    System.out.println("400 dialog if");
                    Toast.makeText(UpdateProduct.this,
                            "Email address already exists.", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("500 dialog if");
                    Toast.makeText(UpdateProduct.this, "Submission failed.", Toast.LENGTH_SHORT).show();
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
}
