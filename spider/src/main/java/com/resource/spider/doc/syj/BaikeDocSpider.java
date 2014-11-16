package com.resource.spider.doc.syj;

import java.util.Map;

import org.jsoup.nodes.Document;

import com.resource.spider.ResourceSpider;

/**
 * <pre>
 * 
 * download-url
 * http://sdown.china.alibaba.com/s/QyRV%2Bpk1ZUtimUL0yjXq2A%3D%3D
 * 
 * sitemap
 * http://page.1688.com/sitemap/baike_china_alibaba_com/sitemap_index.xml.gz
 * </pre>
 * 
 * @author zhiwenmizw
 */
public class BaikeDocSpider extends ResourceSpider {

    @Override
    public boolean parseDocument(Document document, Map<String, Object> userData) {
        return false;
    }

}
