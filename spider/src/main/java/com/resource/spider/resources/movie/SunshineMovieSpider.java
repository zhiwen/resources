package com.resource.spider.resources.movie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.resource.spider.ResourceSpider;
import com.resources.dal.module.SpiderResourcesDO;
import com.resources.service.SpiderResourcesService;

@Service
public class SunshineMovieSpider extends ResourceSpider {

    @Resource
    private SpiderResourcesService spiderResourcesService;

    // 最新电影
    // http://www.ygdy8.com/html/gndy/dyzz/list_23_1.html
    // http://www.ygdy8.com/html/gndy/dyzz/list_23_235.html

    // 日韩
    // http://www.ygdy8.com/html/gndy/rihan/list_6_1.html
    // http://www.ygdy8.com/html/gndy/rihan/list_6_37.html

    // 欧美
    // http://www.ygdy8.com/html/gndy/oumei/list_7_1.html
    // http://www.ygdy8.com/html/gndy/oumei/list_7_261.html

    private final List<String>     urls = new ArrayList<String>();
    {
        urls.add("http://www.ygdy8.com/html/gndy/dyzz/list_23_%d.html");
        urls.add("http://www.ygdy8.com/html/gndy/rihan/list_6_%d.html");
        urls.add("http://www.ygdy8.com/html/gndy/oumei/list_7_%d.html");
        urls.add("http://www.ygdy8.com/html/gndy/china/list_4_%d.html");
        urls.add("http://www.ygdy8.com/html/gndy/jddy/list_63_%d.html");
    }

    @Override
    public void parse(String urlString) throws IOException {
        for (String stringUrl : urls) {

            int page = 1;

            while (true) {
                String formatedUrl = String.format(stringUrl, page);
                // URL url = new URL(formatedUrl);

                Connection con = Jsoup.connect(formatedUrl);

                con.header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                con.header("Accept-Encoding", "gzip,deflate,sdch");
                con.header("Accept-Language", "en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4");
                con.header("Connection", "keep-alive");

                con.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.111 Safari/537.36");
                con.cookie("37cs_user", "37cs47348866896");
                con.referrer("http://www.dytt8.net/");
                con.timeout(getTimeout());

                Document document = con.get();

                Response response = con.response();
                // 翻页完了，结束了
                String location = response.header("Content-Location");
                if (null != location && location.contains("http://www.ygdy8.com/error.htm?404")) {
                    break;
                }
                String contentLength = response.header("Content-Length");
                if (null != contentLength && StringUtils.equals(contentLength, "0")) {
                    break;
                }

                parseDocument(document);
                page++;

                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    @Override
    public boolean parseDocument(Document document) {
        List<SpiderResourcesDO> resList = parseMovieResources(document);

        for (SpiderResourcesDO spiderResourcesDO : resList) {
            try {
                spiderResourcesService.addSpiderResources(spiderResourcesDO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return CollectionUtils.isEmpty(resList);
    }

    public List<SpiderResourcesDO> parseMovieResources(Document document) {

        List<SpiderResourcesDO> resList = new LinkedList<SpiderResourcesDO>();

        Elements contentEles = document.getElementsByClass("ulink");
        for (Element element : contentEles) {
            String url = element.attr("href");
            if (element.childNodeSize() > 0) {
                TextNode nameNode = (TextNode) element.childNode(0);
                SpiderResourcesDO resDO = new SpiderResourcesDO();
                resDO.setName(nameNode.text());
                resDO.setUrl(url);
                resList.add(resDO);
            }
        }
        return resList;
    }
}
