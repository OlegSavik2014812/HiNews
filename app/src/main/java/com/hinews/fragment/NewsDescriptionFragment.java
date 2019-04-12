package com.hinews.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.hinews.R;
import com.hinews.item.RssItem;
import com.hinews.manager.NewsManager;

import java.util.List;
import java.util.Objects;

public class NewsDescriptionFragment extends Fragment {
    private static final String CSS_STYLE = "<style> img{display: inline; height: auto; max-width: 100%;} iframe{display: inline; height: auto; max-width: 100%;}</style>";
    private static final String MIME_TYPE = "text/html; charset=utf-8";
    private static final String ENCODING = "UTF-8";
    private static final String EXTRA_POSITION = "item_position";
    private static final String EXTRA_MAIN_PAGE_NUMBER = "page_number";
    private RssItem rssItem;

    public static NewsDescriptionFragment newInstance(int postNumber, int newsPosition) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_MAIN_PAGE_NUMBER, newsPosition);
        args.putInt(EXTRA_POSITION, postNumber);
        NewsDescriptionFragment fragment = new NewsDescriptionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = Objects.requireNonNull(getArguments());
        int newsPosition = args.getInt(EXTRA_MAIN_PAGE_NUMBER);
        int postNumber = args.getInt(EXTRA_POSITION);
        List<RssItem> rssItems = NewsManager.getInstance().getPagePositionNews(newsPosition);
        rssItem = rssItems.get(postNumber);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rss_news_description, null);

        TextView dateView = view.findViewById(R.id.date_content);
        TextView creatorView = view.findViewById(R.id.creator_content);
        TextView titleView = view.findViewById(R.id.title_content);
        TextView linkView = view.findViewById(R.id.link_content);
        WebView webView = view.findViewById(R.id.content_news_content);

        String date = rssItem.getPublishDate().toString();
        String creator = rssItem.getCreator();
        String title = rssItem.getTitle();
        String link = rssItem.getLink();
        String content = rssItem.getContent();

        dateView.setText(date);
        creatorView.setText(creator);
        titleView.setText(title);
        linkView.setText(link);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setOnTouchListener((v, event) -> onTouch(event));
        webView.loadData(CSS_STYLE + content, MIME_TYPE, ENCODING);
        return view;
    }

    private boolean onTouch(MotionEvent event) {
        return event.getAction() == MotionEvent.ACTION_MOVE;
    }
}