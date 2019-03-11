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
import java.util.function.BiConsumer;

public class NewsDescriptionFragment extends Fragment {
    private static final String CSS_STYLE = "<style> img{display: inline; height: auto; max-width: 100%;} iframe{display: inline; height: auto; max-width: 100%;}</style>";
    private static final String MIME_TYPE = "text/html; charset=utf-8";
    private static final String ENCODING = "UTF-8";
    private static final String EXTRA_POSITION = "item_position";
    private static final String EXTRA_MAIN_PAGE_NUMBER = "page_number";
    private List<RssItem> rssItems;
    private int pageNumber;

    public static NewsDescriptionFragment newInstance(int position, int pageNumber) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_POSITION, position);
        args.putInt(EXTRA_MAIN_PAGE_NUMBER, pageNumber);
        NewsDescriptionFragment fragment = new NewsDescriptionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(EXTRA_POSITION);
        int mainPageNumber = getArguments().getInt(EXTRA_MAIN_PAGE_NUMBER);
        rssItems = NewsManager.getInstance().getPagePositionNews(mainPageNumber);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rss_news_description, null);
        BiConsumer<TextView, String> consumer = TextView::setText;
        BiConsumer<WebView, String> loadContent = (webView, s) -> webView.loadData(CSS_STYLE + s, MIME_TYPE, ENCODING);

        RssItem rssItem = rssItems.get(pageNumber);
        TextView dateView = view.findViewById(R.id.pubdate_news_content);
        TextView creatorView = view.findViewById(R.id.creator);
        TextView titleView = view.findViewById(R.id.title_content);

        String date = rssItem.getPublishDate().toString();
        String creator = rssItem.getCreator();
        String title = rssItem.getTitle();

        consumer.accept(dateView, date);
        consumer.accept(creatorView, creator);
        consumer.accept(titleView, title);

        WebView webView = getWebView(view);
        String content = rssItem.getContent();
        loadContent.accept(webView, content);
        return view;
    }

    private WebView getWebView(View view) {
        WebView webView = view.findViewById(R.id.content_news_content);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setOnTouchListener((v, event) -> onTouch(event));
        return webView;
    }

    private boolean onTouch(MotionEvent event) {
        return event.getAction() == MotionEvent.ACTION_MOVE;
    }
}
