package com.hinews.parsing;

import com.hinews.item.RssItem;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class RssSaxParser {
    public List<RssItem> parse(InputSource source) {
        List<RssItem> list;
        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = parserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            list = new ArrayList<>();
            xmlReader.setContentHandler(new RssSaxHandler(list));
            xmlReader.parse(source);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            list = Collections.emptyList();
        }
        return list;
    }
}