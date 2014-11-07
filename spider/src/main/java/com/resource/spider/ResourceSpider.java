package com.resource.spider;

import java.io.IOException;
import java.net.URL;

import javax.annotation.Resource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.resources.dal.module.ResourceInfoDO;
import com.resources.service.ResourceInfoService;

public abstract class ResourceSpider {

    @Resource
    private ResourceInfoService resourceInfoService;

    private int                 timeout;

    public void parse(String urlString) throws IOException {

        URL url = new URL(urlString);

        Document document = Jsoup.parse(url, timeout);

        this.parseDocument(document);
    }

    public int saveResource(ResourceInfoDO resourceInfo) {

        // getResourceInfoService()

        return 0;
    }

    public abstract void parseDocument(Document document);

    public ResourceInfoService getResourceInfoService() {
        return resourceInfoService;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

}
