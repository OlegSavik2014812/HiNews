package com.hinews.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ViewSwitcher;

import com.hinews.R;
import com.hinews.adapter.ViewPagerAdapter;
import com.hinews.manager.NewsManager;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private ViewSwitcher viewSwitcher;
    private ViewPagerAdapter viewPagerAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewSwitcher = findViewById(R.id.switcher);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this::refresh);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = findViewById(R.id.tab_layout_activity_main);
        viewPager = findViewById(R.id.main_activity_view_pager);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setOffscreenPageLimit(3);
        NewsManager.getInstance().init(new NewsManager.LoadRssNewsListener() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }

    public void onRefreshClick(MenuItem menuItem) {
        NewsManager.getInstance().init(new NewsManager.LoadRssNewsListener() {
            @Override
            public void start() {
                startProgress();
            }

            @Override
            public void success() {
                viewPagerAdapter.notifyDataSetChanged();
                stopProgress();
            }

            @Override
            public void failure() {
                stopProgress();
            }
        });
    }

    private void refresh() {
        NewsManager.getInstance().init(new NewsManager.LoadRssNewsListener() {
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
