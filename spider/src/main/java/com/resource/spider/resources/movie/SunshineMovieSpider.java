package com.resource.spider.resources.movie;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.resource.spider.ResourceSpider;
import com.resources.common.IOUtil;
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

    // 国内
    // http://www.ygdy8.com/html/gndy/china/list_4_97.html

    // 结合
    // http://www.ygdy8.com/html/gndy/jddy/list_63_167.html

    private final List<String>     urls          = new ArrayList<String>();
    {
        urls.add("http://www.ygdy8.com/html/gndy/dyzz/list_23_%d.html");
        urls.add("http://www.ygdy8.com/html/gndy/rihan/list_6_%d.html");
        urls.add("http://www.ygdy8.com/html/gndy/oumei/list_7_%d.html");
        urls.add("http://www.ygdy8.com/html/gndy/china/list_4_%d.html");
        urls.add("http://www.ygdy8.com/html/gndy/jddy/list_63_%d.html");
    }

    private final File             urlFile       = new File("/Users/zhiwenmizw/work/resources/processed_url");
    private final File             failedUrlFile = new File("/Users/zhiwenmizw/work/resources/processed_faild_url");
    private final Set<String>      urlSets       = new HashSet<String>();

    @Override
    public void parse(String urlString) throws IOException {

        // load url
        List<String> list = IOUtil.readFile(urlFile);
        if (!CollectionUtils.isEmpty(list)) {
            urlSets.addAll(list);
        }

        for (String stringUrl : urls) {

            int page = 1, times = 0;
            while (true) {
                String formatedUrl = String.format(stringUrl, page);
                if (urlSets.contains(formatedUrl)) {
                    page++;
                    continue;
                }
                Document document = null;
                try {
                    document = getDocument(formatedUrl);
                    if (document instanceof EndDocument) {
                        break;// 表示结束了
                    }
                    parseDocument(document, null);
                } catch (Exception e) {
                    System.err.println(String.format("getDocument error-url[%s], exception[%s]", formatedUrl,
                                                     e.getMessage()));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }

                    if (times > 3) {
                        IOUtil.writeFile(failedUrlFile, formatedUrl, true);
                        break;
                    }
                    times++;
                    continue;
                }
                // save point
                IOUtil.writeFile(urlFile, formatedUrl, true);
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

    public Document getDocument(String url) throws IOException {
        Connection con = Jsoup.connect(url);

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
            return new EndDocument("end");
        }
        String contentLength = response.header("Content-Length");
        if (null != contentLength && StringUtils.equals(contentLength, "0")) {
            return new EndDocument("end");
        }
        return document;
    }

    public static class EndDocument extends Document {

        public EndDocument(String baseUri){
            super(baseUri);
        }
    }

    @Override
    public boolean parseDocument(Document document, Map<String, Object> userData) {
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

                Node childNode = element.childNode(0);
                while (!(childNode instanceof TextNode)) {
                    childNode = childNode.childNode(0);
                }

                TextNode nameNode = (TextNode) childNode;

                SpiderResourcesDO resDO = new SpiderResourcesDO();
                resDO.setName(nameNode.text());
                resDO.setUrl(url);
                resDO.setCreatedTime(new Date());
                resList.add(resDO);
            }
        }
        return resList;
    }

}
