package com.minkbox.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.minkbox.FavouriteCategoryActivity;
import com.minkbox.R;
import com.minkbox.model.Category;

import java.util.ArrayList;


/**
 * Created by MMFA-YOGESH on 9/9/2015.
 */
public class FavorityCategoryAdapter extends ArrayAdapter<Category> {

    Context context;
    ArrayList<Category> objects;
    int row;

    public FavorityCategoryAdapter(Context context, int row,
                                  ArrayList<Category> objects) {
        super(context, row, objects);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.row = row;
        this.objects = objects;


    }

    public void updateResults(ArrayList<Category> objects) {
        this.objects = objects;
        notifyDataSetChanged();
    }

    @Override
    public View getView( final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View v = convertView;
        final ViewHolder holder;



        if (v == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            v = inflater.inflate(this.row, parent, false);
            holder = new ViewHolder();
            holder.tv1 = (TextView) v.findViewById(R.id.textViewcategory);
            holder.chk1 = (CheckBox) v.findViewById(R.id.checkBoxitem);



            final Category category = objects.get(position);
            final int pos = position;

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        final Category bean = objects.get(position);
        holder.tv1.setText(bean.getCategory_name());

        if(bean.isChkBox())
        {
            holder.chk1.setChecked(true);

        }else
        {
            holder.chk1.setChecked(false);

        }

        if(holder.chk1.isChecked()){
         //   holder.tv1.setTextColor(Color.parseColor("#6faf20"));
        }


        holder.chk1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    objects.get(position).setChkBox(true);
               //     holder.tv1.setTextColor(Color.parseColor("#6faf20"));
                    FavouriteCategoryActivity.catId.add(String.valueOf(bean.getCategory_id()));
                    FavouriteCategoryActivity.catName.add(bean.getCategory_name());

                }else if(!isChecked){
                    objects.get(position).setChkBox(false);
                    Log.d("pos", position + "");

                    if(!FavouriteCategoryActivity.catId.isEmpty()){
                        FavouriteCategoryActivity.catId.remove(String.valueOf(bean.getCategory_id()));
                        FavouriteCategoryActivity.catName.remove(bean.getCategory_name());
                      //  holder.tv1.setTextColor(Color.parseColor("#000000"));
                    }

                }
            }
        });

        return v;
    }

    class ViewHolder {
        TextView tv1;
        CheckBox chk1;
    }

}

