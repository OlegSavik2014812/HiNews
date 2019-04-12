package com.hinews.parsing;

import android.util.Log;

import com.hinews.item.RssItem;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

public class RssSaxParser {
    public List<RssItem> parse(InputSource source) {
        List<RssItem> list;
        try {
            XMLReader xmlReader = SAXParserFactory
                    .newInstance()
                    .newSAXParser()
                    .getXMLReader();
            list = new ArrayList<>(20);
            xmlReader.setContentHandler(new RssSaxHandler(list));
            xmlReader.parse(source);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            Log.e("parsing", "exception occurred while parse xml", e);
            list = Collections.emptyList();
        }
        return list;
    }
}