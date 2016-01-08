package com.minkbox.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minkbox.FragmentDrawer;
import com.minkbox.HomeActivity;
import com.minkbox.HomeFragment;
import com.minkbox.R;
import com.minkbox.databace.DataBaseHelper;
import com.minkbox.model.Category;
import com.minkbox.model.NavDrawerItem;
import com.minkbox.utils.AppPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by MMF-Dev-New on 8/27/2015.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
    List<NavDrawerItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    boolean isExpanded = true;
    DataBaseHelper db;

    public NavigationDrawerAdapter(Context context, List<NavDrawerItem> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        db = new DataBaseHelper(context);
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        NavDrawerItem current = data.get(position);
        holder.title.setText(current.getTitle());
        holder.imageicon.setImageResource(current.getImage());//icons[position]

        holder.text_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position == 3) {

                    if (isExpanded) {

                        ArrayList<Category> lists = db.getCategoryList();
                        for (int i = 0; i < lists.size(); i++) {
                            final TextView textView = new TextView(context);
                            Log.d("item Add", i + " size:" + lists.size());
                            textView.setText(lists.get(i).getCategory_name());
                            textView.setTextColor(Color.parseColor("#b2b2b2"));
                            textView.setPadding(10, 10, 10, 10);
                            textView.setGravity(Gravity.CENTER_VERTICAL);
                            textView.setBackgroundResource(R.drawable.touch_effect_drawer);
                            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.white_dot, 0, 0, 0);
                            textView.setCompoundDrawablePadding(10);

                            final String cat_id = String.valueOf(lists.get(i).getCategory_id());
                            final String cat_name = textView.getText().toString();

                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(context, textView.getText().toString() + "", Toast.LENGTH_LONG).show();

                                    AppPreferences.setFiltercategoryid(context, "");

                                    AppPreferences.setFiltercategoryid(context, String.valueOf(cat_id));
                                    AppPreferences.setFiltercategoryName(context, cat_name);
                                    Fragment f1 = new HomeFragment();
                                   // f1.setArguments(bundle);
                                    FragmentManager fragmentManager = HomeActivity.homeActivityInstance.getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.container_body, f1);
                                    fragmentTransaction.commit();
                                    FragmentDrawer.mDrawerLayout.closeDrawers();
                                    AppPreferences.setFilterDatatype(context, "menu");
                                }
                            });
                            holder.text_ll.addView(textView);
                        }
                        isExpanded = false;
                    } else {

                        ArrayList<Category> lists = db.getCategoryList();
                        for (int i = 0; i < lists.size(); i++) {
                            Log.d("item Add", i + " size::" + lists.size());
                            holder.text_ll.removeViewAt(1);
                        }
                        isExpanded = true;
                    }
                }
            }
        });



    }
    Integer[] icons = {R.drawable.chat, R.drawable.notification, R.drawable.collection,
            R.drawable.category_icon, R.drawable.newinurarea, R.drawable.invite_frn,
            R.drawable.help};

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView imageicon;
        LinearLayout text_ll;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            imageicon = (ImageView) itemView.findViewById(R.id.iconimage);
            text_ll = (LinearLayout)itemView.findViewById(R.id.text_ll);
        }
    }
}
