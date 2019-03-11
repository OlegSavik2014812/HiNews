package com.hinews.data.converter;

import android.support.annotation.NonNull;

import com.hinews.data.item.RssFeed;
import com.hinews.data.item.RssItem;
import com.hinews.data.parsing.RssSaxParser;

import org.xml.sax.InputSource;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class RssResponseBodyConverter implements Converter<ResponseBody, RssFeed> {
    @Override
    public RssFeed convert(@NonNull ResponseBody value) {
        InputSource source = new InputSource(value.charStream());
        RssSaxParser parser = new RssSaxParser();
        List<RssItem> list = parser.parse(source);
        return new RssFeed(list);
    }
}