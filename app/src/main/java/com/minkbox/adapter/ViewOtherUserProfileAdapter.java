package com.minkbox.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.minkbox.R;
import com.minkbox.fragment.OtherUserSellFragment;
import com.minkbox.fragment.OtherUserSoldFragment;

/**
 * Created by mmf-su-yash on 9/12/2015.
 */
public class ViewOtherUserProfileAdapter extends FragmentStatePagerAdapter {
    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; //
    int customer ;
    Context context;

    public ViewOtherUserProfileAdapter(Context context, FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb, int customer) {
        super(fm);
        // TODO Auto-generated constructor stub
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this.customer=customer;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        // TODO Auto-generated method stub
        if (position == 0) // if the position is 0 we are returning the First tab
        {
            OtherUserSellFragment tab1 = new OtherUserSellFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("customerId", customer);
            tab1.setArguments(bundle);
            return tab1;
        }             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        if (position == 1) {
            OtherUserSoldFragment tab2 = new OtherUserSoldFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("customerId",customer);
            tab2.setArguments(bundle);
            return tab2;

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
