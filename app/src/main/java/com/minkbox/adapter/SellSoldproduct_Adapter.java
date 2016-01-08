package com.minkbox.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.minkbox.model.UserProfileBean;

import java.util.ArrayList;

/**
 * Created by MMFA-YOGESH on 9/4/2015.
 */
public class SellSoldproduct_Adapter extends ArrayAdapter<UserProfileBean> {

    Context context;
    ArrayList<UserProfileBean> objects;
    int row;

    public SellSoldproduct_Adapter(Context context, int row,
                                   ArrayList<UserProfileBean> objects) {
        super(context, row, objects);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.row = row;
        this.objects = objects;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View v = convertView;
        ViewHolder holder;
        if (v == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            v = inflater.inflate(this.row, parent, false);
            holder = new ViewHolder();
//            holder.tv1 = (TextView) v.findViewById(R.id.title_notice);
//            holder.tv2 = (TextView) v.findViewById(R.id.day);
//            holder.tv3 = (TextView) v.findViewById(R.id.month);
//            holder.tv4 = (TextView) v.findViewById(R.id.year);
//            holder.tv5 = (TextView) v.findViewById(R.id.content);
//            holder.tv6 = (TextView) v.findViewById(R.id.imageView1);
//            holder.tv7 = (TextView) v.findViewById(R.id.week_day);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        UserProfileBean bean = objects.get(position);
//        holder.tv1.setText(bean.getTitle());
//        holder.tv2.setText(bean.getDay());
//        holder.tv3.setText(bean.getMonth());
//        holder.tv4.setText(bean.getYear());
//        holder.tv5.setText(bean.getContent());
//        holder.tv6.setText(bean.getImage());
//        holder.tv7.setText(bean.getDayweek());
        return v;
    }

    class ViewHolder {
        TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7;
        // ImageView img1;
    }

}
