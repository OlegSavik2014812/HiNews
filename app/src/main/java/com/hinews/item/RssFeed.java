package com.hinews.item;

import java.util.Collections;
import java.util.List;

public class RssFeed {
    private List<RssItem> rssItems;

    public RssFeed(List<RssItem> rssItems) {
        this.rssItems = rssItems;
    }

    public List<RssItem> getItems() {
        return Collections.unmodifiableList(rssItems);
    }
}
