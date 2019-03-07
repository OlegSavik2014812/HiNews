package com.hinews.parsing;

import com.hinews.item.RssItem;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RssSaxHandler extends DefaultHandler {
    private static final String IMAGE_URL_REGEX = "\\b(https?|ftp|file):[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|](?:.jpg)|(?:.jpeg)|(?:.png)";
    private static final String DATE_PATTERN = "EEE, d MMM yyyy HH:mm:ss Z";
    private static final String NEW_LINE = "\n";
    private static final String EMPTY_STRING = "";

    private static final String TAG_BUILD_DATE = "lastBuildDate";
    private static final String TAG_ITEM = "item";
    private static final String TAG_TITLE = "title";
    private static final String TAG_MEDIA = "media";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_LINK = "link";
    private static final String TAG_ATOM_LINK = "atom:link";
    private static final String TAG_PUBLISH_DATE = "pubdate";
    private static final String TAG_CONTENT = "encoded";
    private static final String TAG_CREATOR = "creator";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final Pattern IMAGE_URL_PATTERN = Pattern.compile(IMAGE_URL_REGEX);

    private static boolean isContentUpdated = false;

    private boolean isPubDate;
    private boolean isItem;
    private boolean isElement;
    private boolean isTitle;
    private boolean isDescription;
    private boolean isLink;
    private boolean isContent;
    private boolean isCreator;
    private boolean isDate;

    private static List<RssItem> list;

    private static LocalDateTime pubDateTime;
    private StringBuilder tempContent;
    private String tempTitle;
    private String tempLink;
    private String tempDate;
    private String tempDescription;
    private String tempCreator;

    public RssSaxHandler() {
        tempContent = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (isContentUpdated) {
            return;
        }
        isElement = true;
        if (localName.equalsIgnoreCase(TAG_BUILD_DATE)) {
            isPubDate = true;
            return;
        }
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
                    tempTitle = EMPTY_STRING;
                }
                break;
            case TAG_DESCRIPTION:
                isDescription = true;
                tempDescription = EMPTY_STRING;
                break;
            case TAG_LINK:
                if (!qName.equalsIgnoreCase(TAG_ATOM_LINK)) {
                    isLink = true;
                    tempLink = EMPTY_STRING;
                }
                break;
            case TAG_PUBLISH_DATE:
                isDate = true;
                tempDate = EMPTY_STRING;
                break;
            case TAG_CONTENT:
                isContent = true;
                tempContent.setLength(0);
                break;
            case TAG_CREATOR:
                isCreator = true;
                tempCreator = EMPTY_STRING;
                break;
            default:
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (isContentUpdated) {
            return;
        }
        isElement = false;
        if (localName.equalsIgnoreCase(TAG_BUILD_DATE)) {
            isPubDate = false;
        }
        if (!isItem) {
            return;
        }
        switch (localName.toLowerCase()) {
            case TAG_ITEM:
                RssItem rssItem = buildItem();
                list.add(rssItem);
                resetTempVariables();
                break;
            case TAG_TITLE:
                if (!qName.contains(TAG_MEDIA)) {
                    isTitle = false;
                    tempTitle = removeNewLine(tempTitle);
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
        if (isContentUpdated) {
            return;
        }
        if (isPubDate) {
            initIsUpdated(LocalDateTime.parse(new String(ch, start, length), DATE_TIME_FORMATTER));
        }
        if (!isItem) {
            return;
        }
        String buffer = new String(ch, start, length);
        if (isElement && buffer.length() > 2) {
            isElement = false;
        }
        if (isTitle) {
            tempTitle = tempTitle + buffer;
        }
        if (isDescription) {
            tempDescription = tempDescription + buffer;
        }
        if (isLink) {
            tempLink = tempLink + buffer;
        }
        if (isContent) {
            tempContent = tempContent.append(buffer);
        }
        if (isCreator) {
            tempCreator = tempCreator + buffer;
        }
        if (isDate) {
            tempDate = tempDate + buffer;
        }
    }

    private RssItem buildItem() {
        String text = this.tempContent.toString();
        String content = getContent(text);
        String image = getImage(text);
        LocalDate date = getDate(tempDate);

        return RssItem.newBuilder()
                .setTitle(tempTitle.trim())
                .setContent(content)
                .setCreator(tempCreator)
                .setDescription(tempDescription)
                .setImage(image)
                .setPublishDate(date)
                .setLink(tempLink)
                .build();
    }

    private String getImage(String content) {
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
        } catch (DateTimeParseException e) {
            localDate = LocalDate.of(1111, 11, 11);
        }
        return localDate;
    }

    private void initIsUpdated(LocalDateTime updateTime) {
        if (pubDateTime != null) {
            if (pubDateTime.isEqual(updateTime)) {
                isContentUpdated = true;
            } else {
                pubDateTime = updateTime;
                list = new ArrayList<>();
                isContentUpdated = false;
            }
        } else {
            pubDateTime = updateTime;
            list = new ArrayList<>();
            isContentUpdated = false;
        }
    }

    private void resetTempVariables() {
        tempTitle = EMPTY_STRING;
        tempLink = EMPTY_STRING;
        tempDate = EMPTY_STRING;
        tempDescription = EMPTY_STRING;
        tempContent.setLength(0);
        tempCreator = EMPTY_STRING;
    }

    public static List<RssItem> getList() {
        return list;
    }
}