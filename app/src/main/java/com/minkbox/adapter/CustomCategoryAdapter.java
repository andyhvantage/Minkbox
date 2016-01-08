package com.minkbox.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.minkbox.R;
import com.minkbox.model.Category;

import java.util.List;

/**
 * Created by mmf-su-yash on 9/11/2015.
 */
public class CustomCategoryAdapter extends BaseAdapter {

    public List<Category> data;
    private Activity activity;

    private static LayoutInflater inflater = null;

    public CustomCategoryAdapter(Activity a, List<Category> d) {
        this.activity = a;
        data = d;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateResults(List<Category> d) {
        data = d;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = null;
        convertView = null;

        if (convertView == null) {
            vi = inflater.inflate(
                    R.layout.category_custom_spinner, null);
            final ViewHolder holder = new ViewHolder();
            try {
                holder.tv = (TextView) vi
                        .findViewById(R.id.category);

                vi.setTag(holder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (data.size() <= 0 ) {

        } else {
            final ViewHolder holder = (ViewHolder) vi.getTag();
            Category cat;
            cat = data.get(position);
            holder.tv.setText(cat.getCategory_name());
        }
        return vi;
    }

    public static class ViewHolder {
        public TextView tv;

    }

}
