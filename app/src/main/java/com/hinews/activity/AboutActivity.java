package com.hinews.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hinews.R;
import com.hinews.adapter.ViewPagerContentAdapter;
import com.hinews.item.RssItem;
import com.hinews.manager.NewsManager;

import java.util.List;
import java.util.Objects;

public class AboutActivity extends AppCompatActivity {
    private static final String EMPTY_STRING = "";
    private static final String TEXT_PLAIN_TYPE = "text/plain";
    private static final String EXTRA_POSITION = "item_position";
    private static final String EXTRA_MAIN_PAGE_NUMBER = "page_number";
    private ImageView imageContent;
    private List<RssItem> rssItems;
    private String message;

    public static void start(Context context, int position, int pageNumber) {
        context.startActivity(new Intent(context, AboutActivity.class)
                .putExtra(EXTRA_MAIN_PAGE_NUMBER, pageNumber)
                .putExtra(EXTRA_POSITION, position));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutt);
        int position = getIntent().getIntExtra(EXTRA_POSITION, 0);
        int pageNumber = getIntent().getIntExtra(EXTRA_MAIN_PAGE_NUMBER, 0);
        rssItems = NewsManager.getInstance().getPagePositionNews(pageNumber);
        RssItem rssItem = rssItems.get(position);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(EMPTY_STRING);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = findViewById(R.id.viewpager);
        imageContent = findViewById(R.id.image_news_content);

        Glide.with(this)
                .load(rssItem.getPreviewImagePath())
                .centerCrop()
                .into(imageContent);
        message = rssItem.getLink();

        ViewPagerContentAdapter contentAdapter = new ViewPagerContentAdapter(getSupportFragmentManager(), rssItems, pageNumber);
        viewPager.setAdapter(contentAdapter);
        viewPager.setCurrentItem(position);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //unnecessary implementation
            }

            @Override
            public void onPageSelected(int position) {
                String imagePath = rssItems.get(position).getPreviewImagePath();
                Glide.with(AboutActivity.this)
                        .load(imagePath)
                        .fitCenter()
                        .into(imageContent);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //unnecessary implementation
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share, menu);
        return true;
    }

    public void onShareClick(MenuItem menuItem) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(TEXT_PLAIN_TYPE);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        String chosenTitle = getString(R.string.chooser_title);
        Intent chosenIntent = Intent.createChooser(intent, chosenTitle);
        startActivity(chosenIntent);
    }
}