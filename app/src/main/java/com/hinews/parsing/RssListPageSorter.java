package com.hinews.parsing;

import com.hinews.item.RssItem;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RssListPageSorter {
    private static final int TODAY_NEWS_PAGE_POSITION = 0;
    private static final int YESTERDAY_NEWS_PAGE_POSITION = 1;
    private static final int OTHER_NEWS_PAGE_POSITION = 2;

    private RssListPageSorter() {
    }

    public static List<RssItem> sort(List<RssItem> list, int pagePosition) {
        Function<LocalDate, Predicate<RssItem>> isEqual = date -> rssItem -> rssItem.getPublishDate().isEqual(date);
        Function<LocalDate, Predicate<RssItem>> isBefore = date -> rssItem -> rssItem.getPublishDate().isBefore(date);
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        Predicate<RssItem> predicate;
        switch (pagePosition) {
            case TODAY_NEWS_PAGE_POSITION:
                predicate = isEqual.apply(today);
                break;
            case (YESTERDAY_NEWS_PAGE_POSITION):
                predicate = isEqual.apply(yesterday);
                break;
            case (OTHER_NEWS_PAGE_POSITION):
                predicate = isBefore.apply(yesterday);
                break;
            default:
                predicate = isBefore.apply(today);
        }
        return list.stream().filter(predicate).collect(Collectors.toList());
    }
}
