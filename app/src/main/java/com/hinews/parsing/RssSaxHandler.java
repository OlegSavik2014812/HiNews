package com.hinews.parsing;

import com.hinews.item.RssItem;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RssSaxHandler extends DefaultHandler {
    private static final String IMG_TAG_REGEX = "<img\\s[^>]*?src\\s*=\\s*['\\\"]([^'\\\"]*?)['\\\"][^>]*?>";
    private static final String IMAGE_URL_REGEX = "(http(s?):)([/|.|\\w|\\s|-])*\\.(?:jpg|gif|png|jpeg)";
    private static final String DATE_PATTERN = "EEE, d MMM yyyy HH:mm:ss Z";
    private static final String EMPTY_STRING = "";
    private static final String TAG_ITEM = "item";
    private static final String TAG_TITLE = "title";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_LINK = "link";
    private static final String TAG_PUBLISH_DATE = "pubdate";
    private static final String TAG_CONTENT = "encoded";
    private static final String TAG_CREATOR = "creator";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private Pattern imgTagPattern;
    private Pattern imageUrlPattern;

    private Collection<RssItem> rssItems;

    private boolean isItem;
    private boolean isTitle;
    private boolean isDescription;
    private boolean isLink;
    private boolean isContent;
    private boolean isCreator;
    private boolean isDate;

    private StringBuilder tempContent;
    private String tempTitle;
    private String tempLink;
    private String tempDate;
    private String tempDescription;
    private String tempCreator;

    RssSaxHandler(Collection<RssItem> collection) {
        rssItems = collection;
        tempContent = new StringBuilder();
        imgTagPattern = Pattern.compile(IMG_TAG_REGEX);
        imageUrlPattern = Pattern.compile(IMAGE_URL_REGEX);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (TAG_ITEM.equalsIgnoreCase(localName)) {
            isItem = true;
            resetTemps();
        }
        if (!isItem) {
            return;
        }
        setFlags(localName.toLowerCase(), true);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (!isItem) {
            return;
        }
        if (TAG_ITEM.equalsIgnoreCase(localName)) {
            rssItems.add(buildItem());
            resetTemps();
            return;
        }
        setFlags(localName.toLowerCase(), false);
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (!isItem) {
            return;
        }
        String buffer = new String(ch, start, length);
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

    private void setFlags(String localName, boolean condition) {
        switch (localName) {
            case TAG_TITLE:
                isTitle = condition;
                break;
            case TAG_LINK:
                isLink = condition;
                break;
            case TAG_PUBLISH_DATE:
                isDate = condition;
                break;
            case TAG_DESCRIPTION:
                isDescription = condition;
                break;
            case TAG_CONTENT:
                isContent = condition;
                break;
            case TAG_CREATOR:
                isCreator = condition;
                break;
            default:
                break;
        }
    }

    private void resetTemps() {
        tempTitle = EMPTY_STRING;
        tempLink = EMPTY_STRING;
        tempDate = EMPTY_STRING;
        tempDescription = EMPTY_STRING;
        tempCreator = EMPTY_STRING;
        tempContent.setLength(0);
    }

    private RssItem buildItem() {
        String content = tempContent.toString();
        String imagePath = getImagePath(content);
        String formattedContent = getContent(content);
        LocalDate date = getDate(tempDate);
        return RssItem.newBuilder()
                .setTitle(tempTitle)
                .setContent(formattedContent)
                .setCreator(tempCreator)
                .setDescription(tempDescription)
                .setImagePath(imagePath)
                .setPublishDate(date)
                .setLink(tempLink)
                .build();
    }

    private String getImagePath(String content) {
        Matcher matcher = imageUrlPattern.matcher(content);
        return matcher.find() ? matcher.group() : EMPTY_STRING;
    }

    private String getContent(String content) {
        Matcher matcher = imgTagPattern.matcher(content);
        return matcher.replaceFirst(EMPTY_STRING);
    }

    private LocalDate getDate(String dateString) {
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(dateString, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            localDate = LocalDate.now();
        }
        return localDate;
    }
}