package com.resource.spider;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public abstract class ResourceSpider {

    private int timeout;

    public void parse(String urlString) throws IOException {

        URL url = new URL(urlString);

        Document document = Jsoup.parse(url, timeout);

        this.parseDocument(document, null);
    }

    public abstract boolean parseDocument(Document document, Map<String, Object> userData);

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

}
