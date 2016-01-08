package com.minkbox.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.minkbox.fragment.UserLoginFragment;
import com.minkbox.fragment.UserRegisterFragment;

/**
 * Created by MMF-Dev-New on 8/27/2015.
 */
public class ViewPagerRegisterLoginAdapter extends FragmentStatePagerAdapter {
    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; //

    public ViewPagerRegisterLoginAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);
        // TODO Auto-generated constructor stub
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
    }

    @Override
    public Fragment getItem(int position) {
        // TODO Auto-generated method stub
        if (position == 0) // if the position is 0 we are returning the First tab
        {
            UserLoginFragment tab1 = new UserLoginFragment();
            return  tab1;
        } else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            UserRegisterFragment tab2 = new UserRegisterFragment();
            return tab2;
        }
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return NumbOfTabs;
    }
}
