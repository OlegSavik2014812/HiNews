package com.hinews.parsing;

import android.support.annotation.NonNull;

import com.hinews.item.RssFeed;
import com.hinews.item.RssItem;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.util.Collections;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class RssResponseBodyConverter implements Converter<ResponseBody, RssFeed> {
    private RssSaxHandler handler;

    RssResponseBodyConverter() {
        handler = RssSaxHandler.getInstance();
    }

    @Override
    public RssFeed convert(@NonNull ResponseBody value) {
        List<RssItem> list;
        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = parserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(handler);
            InputSource inputSource = new InputSource(value.charStream());
            xmlReader.parse(inputSource);
            list = handler.getItems();
        } catch (Exception e) {
            list = Collections.emptyList();
        }
        return new RssFeed(list);
    }
}
