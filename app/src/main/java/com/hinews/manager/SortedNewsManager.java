package com.hinews.manager;

import com.hinews.item.RssItem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SortedNewsManager {
    private static final int TODAY_NEWS_PAGE_POSITION = 0;
    private static final int YESTERDAY_NEWS_PAGE_POSITION = 1;
    private static final int OTHER_NEWS_PAGE_POSITION = 2;

    private static List<RssItem> todayNews;
    private static List<RssItem> yesterdayNews;
    private static List<RssItem> otherNews;

    private SortedNewsManager() {
        throw new UnsupportedOperationException();
    }

    public static List<RssItem> getPagePositionNews(int pagePosition) {
        switch (pagePosition) {
            case TODAY_NEWS_PAGE_POSITION:
                return todayNews;
            case (YESTERDAY_NEWS_PAGE_POSITION):
                return yesterdayNews;
            case (OTHER_NEWS_PAGE_POSITION):
                return otherNews;
            default:
                return Collections.emptyList();
        }
    }

    static void init(List<RssItem> initialList) {
        todayNews = new ArrayList<>();
        yesterdayNews = new ArrayList<>();
        otherNews = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate yesterday = now.minusDays(1);
        initialList.forEach(item -> {
            LocalDate publishDate = item.getPublishDate();
            if (publishDate.isEqual(now)) {
                todayNews.add(item);
            } else if (publishDate.isEqual(yesterday)) {
                yesterdayNews.add(item);
            } else {
                otherNews.add(item);
            }
        });
    }
}