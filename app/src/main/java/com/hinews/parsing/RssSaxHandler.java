package com.hinews.parsing;

import com.hinews.item.RssItem;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RssSaxHandler extends DefaultHandler {
    private static final String IMAGE_URL_REGEX = "\\b(https?|ftp|file):[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|](?:.jpg)|(?:.jpeg)|(?:.png)";
    private static final String DATE_PATTERN = "EEE, d MMM yyyy HH:mm:ss Z";
    private static final String NEW_LINE = "\n";
    private static final String EMPTY_STRING = "";

    private static final String TAG_ITEM = "item";
    private static final String TAG_TITLE = "title";
    private static final String TAG_MEDIA = "media";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_LINK = "link";
    private static final String TAG_ATOM_LINK = "atom:link";
    private static final String TAG_PUBLISH_DATE = "pubdate";
    private static final String TAG_CONTENT = "encoded";
    private static final String TAG_CREATOR = "creator";

    private static final ReentrantLock LOCK = new ReentrantLock();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final Pattern IMAGE_URL_PATTERN = Pattern.compile(IMAGE_URL_REGEX);

    private static RssSaxHandler instance;
    private static AtomicBoolean isInitialized = new AtomicBoolean(false);

    private boolean isItem;
    private boolean isElement;
    private boolean isTitle;
    private boolean isDescription;
    private boolean isLink;
    private boolean isContent;
    private boolean isCreator;
    private boolean isDate;

    private StringBuilder content;
    private String title;
    private String link;
    private String date;
    private String description;
    private String creator;

    private List<RssItem> rssItems;

    static RssSaxHandler getInstance() {
        if (!isInitialized.get()) {
            LOCK.lock();
            if (!isInitialized.get()) {
                instance = new RssSaxHandler();
                isInitialized.set(true);
            }
            LOCK.unlock();
        }
        return instance;
    }

    private RssSaxHandler() {
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
                content = new StringBuilder();
                break;
            case TAG_CREATOR:
                isCreator = true;
                creator = EMPTY_STRING;
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
                rssItems.add(buildItem());
                resetBufferVariables();
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
            content = content.append(buffer);
        }
        if (isCreator) {
            creator = creator + buffer;
        }
        if (isDate) {
            date = date + buffer;
        }
    }

    List<RssItem> getItems() {
        return rssItems;
    }

    private RssItem buildItem() {
        String stringContent = this.content.toString();
        return RssItem.newBuilder()
                .setTitle(title.trim())
                .setContent(getContent(stringContent))
                .setCreator(creator)
                .setDescription(description)
                .setImage(getImg(stringContent))
                .setPublishDate(getDate(date))
                .setLink(link)
                .build();
    }

    private String getImg(String content) {
        Matcher matcher = IMAGE_URL_PATTERN.matcher(content);
        return matcher.find() ? matcher.group() : EMPTY_STRING;
    }

    private String getContent(String content) {
        String[] parts = content.split(NEW_LINE, 2);
        return parts[1];
    }

    private String removeNewLine(String s) {
        return s != null ? s.replace(NEW_LINE, EMPTY_STRING) : EMPTY_STRING;
    }

    private LocalDate getDate(String dateString) {
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(dateString, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            localDate = LocalDate.of(1111, 11, 11);
        }
        return localDate;
    }

    private void resetBufferVariables() {
        title = EMPTY_STRING;
        link = EMPTY_STRING;
        date = EMPTY_STRING;
        description = EMPTY_STRING;
        content = new StringBuilder();
        creator = EMPTY_STRING;
    }
}