package com.minkbox;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.minkbox.databace.DataBaseHelper;
import com.minkbox.model.Product;
import com.minkbox.utils.DialogManager;

public class LocationMap extends ActionBarActivity  {
    private Toolbar toolbar;
    DialogManager alert = new DialogManager();
    private GoogleMap googleMap;
    private MapFragment mapFragment;
    DataBaseHelper db;
    Product product;
    int product_id;
    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_map);
        Bundle extras = getIntent().getExtras();
        product_id = extras.getInt("productid");
        toolbar = (Toolbar)findViewById(R.id.toolbar); // Attaching the layout
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = getString(R.string.app_name);
        title = getString(R.string.title_activity_location_map);
        getSupportActionBar().setTitle(title);





        mapFragment = ((MapFragment) getFragmentManager().findFragmentById(
                R.id.destination_map));
        googleMap = mapFragment.getMap();
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setMyLocationEnabled(true);

        db = new DataBaseHelper(getApplicationContext());
        product = db.getProductById(product_id);

        Double latitude = Double.parseDouble(product.getProduct_latitude());
        Double longitude = Double.parseDouble(product.getProduct_longitude());

//        Double latitude = 22.725313;
//        Double longitude = 75.865555;
        LatLng lng = new LatLng(latitude, longitude);

        Marker marker =googleMap.addMarker(new MarkerOptions()
                .position(lng)
                .title("Your Current City is |Explore")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

//		Marker marker =googleMap.addMarker(new MarkerOptions()
//		.position(lng)
//		.title("Your Current City is"+" " +destAttr.getDestinationAttraction_name() +" " +"|Explore")
//		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(13).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
       finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        int id = item.getItemId();
        switch (id) {


            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}
