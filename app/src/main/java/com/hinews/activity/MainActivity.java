package com.hinews.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ViewSwitcher;

import com.hinews.R;
import com.hinews.adapter.ViewPagerAdapter;
import com.hinews.manager.NewsManager;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private ViewSwitcher viewSwitcher;
    private ViewPagerAdapter viewPagerAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NewsManager manager = NewsManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewSwitcher = findViewById(R.id.switcher);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this::refresh);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_green_dark,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = findViewById(R.id.tab_layout_activity_main);
        viewPager = findViewById(R.id.main_activity_view_pager);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setOffscreenPageLimit(3);
        manager.init(new NewsManager.LoadRssNewsListener() {
            @Override
            public void start() {
                startProgress();
            }

            @Override
            public void success() {
                viewPager.setAdapter(viewPagerAdapter);
                stopProgress();
            }

            @Override
            public void failure() {
                stopProgress();
            }
        });
    }

    private void refresh() {
        manager.init(new NewsManager.LoadRssNewsListener() {
            @Override
            public void success() {
                viewPagerAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void startProgress() {
        viewSwitcher.setDisplayedChild(0);
    }

    private void stopProgress() {
        viewSwitcher.setDisplayedChild(1);
    }
}
