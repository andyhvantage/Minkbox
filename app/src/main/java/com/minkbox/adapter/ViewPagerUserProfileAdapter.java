package com.minkbox.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.minkbox.R;
import com.minkbox.fragment.Like_Fragment;
import com.minkbox.fragment.SellFragment;
import com.minkbox.fragment.Sold_Fragment;

/**
 * Created by MMFA-YOGESH on 9/4/2015.
 */
public class ViewPagerUserProfileAdapter extends FragmentStatePagerAdapter {
    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; //
    Context context;

    public ViewPagerUserProfileAdapter(Context context, FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);
        // TODO Auto-generated constructor stub
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        // TODO Auto-generated method stub
        if (position == 0) // if the position is 0 we are returning the First tab
        {
            SellFragment tab1 = new SellFragment();
            return tab1;
        }             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        if (position == 1) {
            Sold_Fragment tab2 = new Sold_Fragment();
            return tab2;

        }
        if (position == 2) {
            Like_Fragment tab3 = new Like_Fragment();
            return tab3;


        }
       return getItem(position);
    }

    private int[] imageResId = {
            R.drawable.sell,
            R.drawable.sold_one,
            R.drawable.like
    };

    @Override
    public CharSequence getPageTitle(int position) {
// Drawable image = get
        Drawable image = context.getResources().getDrawable(imageResId[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return NumbOfTabs;
    }
}
