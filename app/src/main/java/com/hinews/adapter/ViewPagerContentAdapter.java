package com.hinews.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hinews.fragment.NewsDescriptionFragment;
import com.hinews.item.RssItem;

import java.util.List;

public class ViewPagerContentAdapter extends FragmentPagerAdapter {
    private List<RssItem> rssItems;
    private int pageNumber;

    public ViewPagerContentAdapter(FragmentManager fragmentManager, List<RssItem> list, int pageNumber) {
        super(fragmentManager);
        rssItems = list;
        this.pageNumber = pageNumber;
    }

    @Override
    public int getCount() {
        return rssItems.size();
    }

    @Override
    public Fragment getItem(int position) {
        return NewsDescriptionFragment.newInstance(position, pageNumber);
    }
}
