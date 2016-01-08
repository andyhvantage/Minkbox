package com.minkbox.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.minkbox.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class ImageAdapterGallery extends PagerAdapter {//implements View.OnClickListener
    Context context;
    ArrayList<String> imageslist;
    LayoutInflater inflater;

    public ImageAdapterGallery(Context context, ArrayList<String> img_list) {
        this.context = context;
        this.imageslist = img_list;
    }

    @Override
    public int getCount() {
        return imageslist.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
//        return view == ((ImageView) object);
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.imageadaptergallery_viewpager_item, null);
//        container,
//                false
        ImageView selectedImage = (ImageView)itemView.findViewById(R.id.selected_image);
        RelativeLayout r = (RelativeLayout)itemView.findViewById(R.id.viewpager_relativelayout);
        r.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context.getApplicationContext(), "image click----------------", Toast.LENGTH_LONG).show();
            }
        });

        ImageLoader.getInstance().displayImage(imageslist.get(position).toString(),
                selectedImage);


//        selectedImage.setOnClickListener(this);
//        selectedImage.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                Toast.makeText(context.getApplicationContext(), "image click----------------", Toast.LENGTH_LONG).show();
//                System.out.println("image click----------------");
//            }
//        });

        ((ViewPager) container).addView(itemView, 0);
            return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

}
