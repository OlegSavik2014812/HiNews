package com.hinews.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hinews.fragment.NewsFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private static final int LIST_SIZE = 3;
    private static final int TODAY_POSITION = 0;
    private static final int YESTERDAY_POSITION = 1;
    private static final int THIS_WEEK_POSITION = 2;
    private static final String TODAY = "Today";
    private static final String YESTERDAY = "Yesterday";
    private static final String THIS_WEEK = "This week";

    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return LIST_SIZE;
    }

    @Override
    public Fragment getItem(int position) {
        return NewsFragment.newInstance(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case TODAY_POSITION:
                return TODAY;
            case YESTERDAY_POSITION:
                return YESTERDAY;
            case THIS_WEEK_POSITION:
                return THIS_WEEK;
            default:
                return super.getPageTitle(position);
        }
    }
}