package com.hinews.parsing;

import com.hinews.item.RssItem;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class XmlRssParser extends DefaultHandler {
    private static final String OPEN_P = "<p>";
    private static final String CLOSE_P = "</p>";
    private static final String DATE_PATTERN = "EEE, d MMM yyyy HH:mm:ss Z";
    private static final String SRC_PATTERN = "src=\"";
    private static final String NEW_LINE = "\n";
    private static final String EMPTY_STRING = "";
    private static final String IMG_ATTR = "img";
    private static final String TAG_ITEM = "item";
    private static final String TAG_TITLE = "title";
    private static final String TAG_MEDIA = "media";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_LINK = "link";
    private static final String TAG_ATOM_LINK = "atom:link";
    private static final String TAG_PUBLISH_DATE = "pubdate";
    private static final String TAG_CONTENT = "encoded";
    private static final String TAG_CREATOR = "creator";
    private static final String TAG_LANGUAGE = "language";
    private boolean isItem;
    private boolean isElement;
    private boolean isTitle;
    private boolean isDescription;
    private boolean isLink;
    private boolean isContent;
    private boolean isCreator;
    private boolean isLanguage;
    private boolean isDate;
    private String title;
    private String link;
    private String date;
    private String description;
    private String content;
    private String creator;
    private String language;
    private List<RssItem> rssItems;
    private DateTimeFormatter formatter;
    private static XmlRssParser instance;
    private static AtomicBoolean isInitialized = new AtomicBoolean(false);
    private static final ReentrantLock LOCK = new ReentrantLock();

    static XmlRssParser getInstance() {
        if (!isInitialized.get()) {
            LOCK.lock();
            if (!isInitialized.get()) {
                instance = new XmlRssParser();
                isInitialized.set(true);
            }
            LOCK.unlock();
        }
        return instance;
    }

    private XmlRssParser() {
        formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
    }

    @Override
    public void startDocument() {
        rssItems = new ArrayList<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        isElement = true;
        if (localName.equalsIgnoreCase(TAG_ITEM)) {
            isItem = true;
        }
        if (!isItem) {
            return;
        }
        switch (localName.toLowerCase()) {
            case TAG_ITEM:
                isItem = true;
                break;
            case TAG_TITLE:
                if (!qName.contains(TAG_MEDIA)) {
                    isTitle = true;
                    title = EMPTY_STRING;
                }
                break;
            case TAG_DESCRIPTION:
                isDescription = true;
                description = EMPTY_STRING;
                break;
            case TAG_LINK:
                if (!qName.equalsIgnoreCase(TAG_ATOM_LINK)) {
                    isLink = true;
                    link = EMPTY_STRING;
                }
                break;
            case TAG_PUBLISH_DATE:
                isDate = true;
                date = EMPTY_STRING;
                break;
            case TAG_CONTENT:
                isContent = true;
                content = EMPTY_STRING;
                break;
            case TAG_CREATOR:
                isCreator = true;
                creator = EMPTY_STRING;
                break;
            case TAG_LANGUAGE:
                isLanguage = true;
                language = EMPTY_STRING;
                break;
            default:
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        isElement = false;
        if (!isItem) {
            return;
        }
        switch (localName.toLowerCase()) {
            case TAG_ITEM:
                RssItem rssItem = RssItem.newBuilder().setTitle(title.trim()).setContent(getContent(content)).setCreator(creator).setDescription(description).setImage(getImg(content)).setPublishDate(getDate(date)).setLink(link).build();
                rssItems.add(rssItem);
                title = EMPTY_STRING;
                link = EMPTY_STRING;
                date = EMPTY_STRING;
                description = EMPTY_STRING;
                content = EMPTY_STRING;
                creator = EMPTY_STRING;
                language = EMPTY_STRING;
                break;
            case TAG_TITLE:
                if (!qName.contains(TAG_MEDIA)) {
                    isTitle = false;
                    title = removeNewLine(title);
                }
                break;
            case TAG_LINK:
                isLink = false;
                break;
            case TAG_PUBLISH_DATE:
                isDate = false;
                break;
            case TAG_DESCRIPTION:
                isDescription = false;
                break;
            case TAG_CONTENT:
                if (!qName.contains(TAG_CONTENT)) {
                    isContent = false;
                }
                break;
            case TAG_CREATOR:
                isCreator = false;
                break;
            case TAG_LANGUAGE:
                isLanguage = false;
                break;
            default:
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (!isItem) {
            return;
        }
        String buffer = new String(ch, start, length);
        if (isElement && buffer.length() > 2) {
            isElement = false;
        }
        if (isTitle) {
            title = title + buffer;
        }
        if (isDescription) {
            description = description + buffer;
        }
        if (isLink) {
            link = link + buffer;
        }
        if (isContent) {
            content = content + buffer;
        }
        if (isCreator) {
            creator = creator + buffer;
        }
        if (isLanguage) {
            language = language + buffer;
        }
        if (isDate) {
            date = date + buffer;
        }
    }

    private String getImg(String content) {
        if (content.contains(IMG_ATTR) && content.contains(SRC_PATTERN)) {
            String[] split = content.split(OPEN_P);
            String[] split1 = split[1].split(CLOSE_P);
            String img = split1[0];
            int ix = img.indexOf(SRC_PATTERN);
            return img.substring(ix + 5, img.indexOf("\" alt"));
        }
        return EMPTY_STRING;
    }

    private String getContent(String content) {
        String[] parts = content.split(NEW_LINE, 2);
        String part1 = parts[1];
        String[] parts1 = part1.split(NEW_LINE, 2);
        return parts1[1];
    }

    private String removeNewLine(String s) {
        return s != null ? s.replace(NEW_LINE, EMPTY_STRING) : EMPTY_STRING;
    }

    private LocalDate getDate(String dateString) {
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(dateString, formatter);
        } catch (Exception e) {
            localDate = LocalDate.of(1111, 11, 11);
        }
        return localDate;
    }

    List<RssItem> getItems() {
        return Collections.unmodifiableList(rssItems);
    }
}
