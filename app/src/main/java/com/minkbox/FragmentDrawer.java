package com.minkbox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.minkbox.adapter.NavigationDrawerAdapter;
import com.minkbox.lazyload.ImageLoader;
import com.minkbox.model.NavDrawerItem;
import com.minkbox.utils.AppPreferences;
import com.minkbox.utils.TextDrawable;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by MMF-Dev-New on 8/27/2015.
 */
public class FragmentDrawer extends Fragment {

    private static String TAG = FragmentDrawer.class.getSimpleName();

    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    public static DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter adapter;
    private View containerView;
    private static String[] titles = null;
    private FragmentDrawerListener drawerListener;
    Bitmap profileicon;
    public static TextView userName;
    public static TextView userItems;
    public static ImageView userImage, userImage1;
    public com.minkbox.lazyload.ImageLoader imageLoader;
    public FragmentDrawer() {
    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    public static List<NavDrawerItem> getData() {
        List<NavDrawerItem> data = new ArrayList<>();

        Integer[] icons = {R.drawable.chat, R.drawable.notification, R.drawable.collection,
                R.drawable.category_icon, R.drawable.newinurarea, R.drawable.invite_frn,
                R.drawable.help};

// preparing navigation drawer items
        for (int i = 0; i < titles.length; i++) {
            NavDrawerItem navItem = new NavDrawerItem();
            navItem.setTitle(titles[i]);
            navItem.setImage(icons[i]);
            data.add(navItem);
        }        return data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // drawer labels
        titles = getActivity().getResources().getStringArray(R.array.nav_drawer_labels);
        imageLoader= new ImageLoader(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);

        adapter = new NavigationDrawerAdapter(getActivity(), getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                drawerListener.onDrawerItemSelected(view, position);
                if(position != 3){
                    mDrawerLayout.closeDrawer(containerView);
                }
                //mDrawerLayout.closeDrawer(containerView);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        userName=(TextView)layout.findViewById(R.id.user_name);
        if(AppPreferences.getUsername(getActivity().getApplicationContext()).equalsIgnoreCase("")){
            userName.setText("Log in or Register");
        }else {
            userName.setText(AppPreferences.getUsername(getActivity().getApplicationContext()));
        }
        userItems=(TextView)layout.findViewById(R.id.item_quantity);
        if(AppPreferences.getUserproductitems(getActivity().getApplicationContext()).equalsIgnoreCase("")){
            userItems.setText(getResources().getString(R.string.at_appname_text));
        }else {
            userItems.setText(AppPreferences.getUserproductitems(getActivity().getApplicationContext()) + " " + "items");
        }
        userImage = (ImageView)layout.findViewById(R.id.user_profile_drawer);
        userImage1 = (ImageView)layout.findViewById(R.id.user_profile_drawer1);

        if(AppPreferences.getUserprofilepic(getActivity().getApplicationContext()).equalsIgnoreCase("")){

            if(AppPreferences.getUsername(getActivity().getApplicationContext()).equalsIgnoreCase("")){
                profileicon = BitmapFactory.decodeResource(getResources(), R.drawable.def_user_img);
                userImage.setVisibility(View.VISIBLE);
                userImage.setImageBitmap(profileicon);
            }else {
                userImage.setVisibility(View.GONE);
                userImage1.setVisibility(View.VISIBLE);

                String name = AppPreferences.getUsername(getActivity().getApplicationContext());
                char firstLetter = name.charAt(0);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(String.valueOf(firstLetter), Color.parseColor("#6faf20"));

                userImage1.setImageDrawable(drawable);
            }
        }else{
            imageLoader.DisplayImage(AppPreferences.getUserprofilepic(getActivity()), userImage);
            System.out.print("ap blank not------------------");
        }

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(containerView);
                if(AppPreferences.getId(getActivity().getApplicationContext()).equalsIgnoreCase(""))
                {
                    Intent i = new Intent(getActivity(), UserRegisterLoginActivity.class);
                    startActivity(i);
                }else
                {
                    Intent i = new Intent(getActivity(), UserProfile.class);
                    i.putExtra("customerid", AppPreferences.getCustomerId(getActivity()));
                    startActivity(i);
                }


            }
        });

        userImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(containerView);
                if(AppPreferences.getId(getActivity().getApplicationContext()).equalsIgnoreCase(""))
                {
                    Intent i = new Intent(getActivity(), UserRegisterLoginActivity.class);
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
//                        HomeActivity.homeActivityInstance.finish();
                }else
                {
                    Intent i = new Intent(getActivity(), UserProfile.class);
                    i.putExtra("customerid", AppPreferences.getCustomerId(getActivity()));
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }


            }
        });
        return layout;
    }


    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
//        mDrawerLayout.setRight(1);
    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {

        }
    }

    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }
}
