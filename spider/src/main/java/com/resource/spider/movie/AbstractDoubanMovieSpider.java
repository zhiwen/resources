package com.resource.spider.movie;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.resource.spider.SpiderJob;
import com.resource.spider.http.HttpURLConnectionWrapper;
import com.resources.common.IOUtil;

public abstract class AbstractDoubanMovieSpider implements SpiderJob {

    public JSONObject getJSONData(String url) {
        HttpURLConnectionWrapper con = null;
        try {
            Thread.sleep(getTimeInterval());
            con = new HttpURLConnectionWrapper(new URL(url), getConnectTimeout(), getReadTimeout());
            InputStream in = con.getInputStream();
            ByteArrayOutputStream bos = IOUtil.getByteData(in);
            return JSON.parseObject(bos.toString("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    public Document getDocument(String url) {
        HttpURLConnectionWrapper con = null;
        Document doc = null;
        try {
            Thread.sleep(getTimeInterval());
            con = new HttpURLConnectionWrapper(new URL(url), getConnectTimeout(), getReadTimeout());
            InputStream in = con.getInputStream();
            doc = Jsoup.parse(in, "utf-8", url);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return doc;
    }

    public abstract int getTimeInterval();

    public int getReadTimeout() {
        return 3000;
    }

    public int getConnectTimeout() {
        return 2000;
    }

}
