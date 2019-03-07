package com.hinews.parsing;

import com.hinews.item.RssItem;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class RssSaxParser {
    public List<RssItem> parse(InputSource source) {
        long start = System.currentTimeMillis();
        List<RssItem> list;
        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = parserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(new RssSaxHandler());
            xmlReader.parse(source);
            list = RssSaxHandler.getList();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            list = Collections.emptyList();
        }
        long end = System.currentTimeMillis();
        long l = end - start;
        new Long(l);
        return list;
    }
}