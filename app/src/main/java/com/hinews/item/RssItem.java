package com.hinews.item;

import java.io.Serializable;
import java.time.LocalDate;

public class RssItem implements Serializable {
    private String title;
    private String link;
    private String image;
    private LocalDate publishDate;
    private String description;
    private String content;
    private String creator;

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getImage() {
        return image;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public String getDescription() {
        return description;
    }

    public String getContent() {
        return content;
    }

    public String getCreator() {
        return creator;
    }

    public static RssItemBuilder newBuilder() {
        return new RssItem().new RssItemBuilder();
    }

    public final class RssItemBuilder {
        private RssItemBuilder() {
        }

        public RssItemBuilder setTitle(String title) {
            RssItem.this.title = title;
            return this;
        }

        public RssItemBuilder setLink(String link) {
            RssItem.this.link = link;
            return this;
        }

        public RssItemBuilder setImage(String image) {
            RssItem.this.image = image;
            return this;
        }

        public RssItemBuilder setPublishDate(LocalDate date) {
            RssItem.this.publishDate = date;
            return this;
        }

        public RssItemBuilder setDescription(String description) {
            RssItem.this.description = description;
            return this;
        }

        public RssItemBuilder setContent(String content) {
            RssItem.this.content = content;
            return this;
        }

        public RssItemBuilder setCreator(String creator) {
            RssItem.this.creator = creator;
            return this;
        }

        public RssItem build() {
            return RssItem.this;
        }
    }

    @Override
    public String toString() {
        return "RssItem{" + "title='" + title + '\'' + ", link='" + link + '\'' + ", image='" + image + '\'' + ", publishDate=" + publishDate + ", description='" + description + '\'' + ", content='" + content + '\'' + ", creator='" + creator + '\'' + '}';
    }
}
