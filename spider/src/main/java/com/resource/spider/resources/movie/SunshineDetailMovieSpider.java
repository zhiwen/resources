package com.resource.spider.resources.movie;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.resource.spider.ResourceSpider;
import com.resources.common.IOUtil;
import com.resources.common.StringUtil;
import com.resources.dal.module.SpiderResourcesDO;
import com.resources.service.SpiderResourcesService;

@Service
public class SunshineDetailMovieSpider extends ResourceSpider {

    @Resource
    private SpiderResourcesService spiderResourcesService;

    private final File             urlFile       = new File("/Users/zhiwenmizw/work/resources/sun_detail_processed_url");
    private final File             failedUrlFile = new File(
                                                            "/Users/zhiwenmizw/work/resources/sun_detail_processed_faild_url");
    private final Set<String>      urlSets       = new HashSet<String>();

    @Override
    public void parse(String urlString) throws IOException {

        // load url
        List<String> list = IOUtil.readFile(urlFile);
        if (!CollectionUtils.isEmpty(list)) {
            urlSets.addAll(list);
        }

        int offset = 0, length = 1000;
        while (true) {

            List<SpiderResourcesDO> resList = spiderResourcesService.getSpiderResources(offset, length);
            if (CollectionUtils.isEmpty(resList)) {
                break;
            }

            Map<String, Object> userData = new HashMap<String, Object>();
            for (SpiderResourcesDO spiderResourcesDO : resList) {
                String url = "http://www.ygdy8.com" + spiderResourcesDO.getUrl();
                if (StringUtils.isNotBlank(spiderResourcesDO.getDownloadUrl()) && urlSets.contains(url)) {
                    continue;
                }

                Document document = null;
                try {
                    document = getDocument(url);
                    if (null == document) {
                        IOUtil.writeFile(failedUrlFile, url, true);
                        continue;
                    }
                    userData.put("resDO", spiderResourcesDO);
                    boolean flag = parseDocument(document, userData);
                    if (flag) {
                        IOUtil.writeFile(urlFile, url, true);
                    } else {
                        IOUtil.writeFile(failedUrlFile, url, true);
                    }
                } catch (Exception e) {
                    System.err.println(String.format("getDocument error-url[%s], exception[%s]", url, e.getMessage()));
                    IOUtil.writeFile(failedUrlFile, url, true);
                    continue;
                }
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }

            }
            offset += length;
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
        con.referrer("http://www.dytt8.com/");
        con.timeout(getTimeout());

        Document document = con.get();

        return document;
    }

    @Override
    public boolean parseDocument(Document document, Map<String, Object> userData) {

        SpiderResourcesDO spiderResourcesDO = (SpiderResourcesDO) userData.get("resDO");

        // pattern 1
        List<String> urls = new LinkedList<String>();
        Elements contentEles = document.getElementsByAttributeValueStarting("href", "ftp://");
        for (Element element : contentEles) {
            String url = element.attr("href");
            if (StringUtils.isNotBlank(url) && StringUtil.isURL(url)) {
                urls.add(url);
                continue;
            }
        }

        // pattern 2
        if (urls.isEmpty()) {
            contentEles = document.getElementsByAttributeValue("style", "FONT-SIZE: 12px");

            if (!contentEles.isEmpty()) {
                List<String> urlSet = findUrlByPattern(contentEles.outerHtml(), ftpPattern);
                if (!CollectionUtils.isEmpty(urlSet)) {
                    urls.addAll(urlSet);
                }
            }
        }
        // pattern 3
        if (urls.isEmpty()) {
            contentEles = document.getElementsByAttributeValue("color", "#ff0000");
            for (Element element : contentEles) {
                String url = findUrl(element.outerHtml());
                if (StringUtils.isNotBlank(url) && StringUtil.isURL(url)) {
                    urls.add(url);
                    continue;
                }
            }
        }

        // pattern 4
        if (urls.isEmpty()) {
            contentEles = document.getElementsByAttributeValue("color", "#000000");
            for (Element element : contentEles) {
                String url = findUrl(element.outerHtml());
                if (StringUtils.isNotBlank(url) && StringUtil.isURL(url)) {
                    urls.add(url);
                    continue;
                }
            }
        }

        // pattern 5
        if (urls.isEmpty()) {
            contentEles = document.getElementsByAttributeValue("style", "WORD-WRAP: break-word");
            for (Element element : contentEles) {
                String url = findUrl(element.outerHtml());
                if (StringUtils.isNotBlank(url) && StringUtil.isURL(url)) {
                    urls.add(url);
                    continue;
                }
            }
        }

        // pattern 6
        if (urls.isEmpty()) {
            contentEles = document.getElementsContainingText("thunder://");
            if (!contentEles.isEmpty()) {
                List<String> urlSet = findUrlByThunderPattern(contentEles.outerHtml(), thunderPattern);
                if (!CollectionUtils.isEmpty(urlSet)) {
                    urls.addAll(urlSet);
                }
            }
        }

        if (!CollectionUtils.isEmpty(urls)) {
            spiderResourcesDO.setDownloadUrl(JSON.toJSONString(urls));

            try {
                spiderResourcesService.updateSpiderResources(spiderResourcesDO);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        return false;
    }

    public static void main(String[] args) {
        String s = "ftp://迅雷发布xl.dygod.com:影片发布@xunlei.dygod.com/鬼面骑士[<a target=\"_blank\" href=\"http://www.dygod.com/\">www.dygod.com</a>]/鬼面骑士[bbs.dygod.com].rmvb<br><br>";

        s = "ftp://资源发布xl.dygod.com:影片发布@xunlei.dygod.com/美女的烦恼DVD[</font><a target=\"_blank\" href=\"http://www.dygod.com/\"><font size=\"2\">www.dygod.com</font></a><font size=\"2\">]/美女的烦恼1[电影天堂论坛bbs.dygod.com].rmvb<br />";

        s = "<td style=\"WORD-WRAP: break-word\" bgcolor=\"#fdfddf\"><font color=\"#ff0000\"><a href=\"http://dygod4.dygod.com/趁火行劫DVD/趁火行劫[www.dygod.com电影天堂].rmvb\">http://dygod4.dygod.com/趁火行劫DVD/趁火行劫[www.dygod.com电影天堂].rmvb</a></font></td>";

        System.out.println(findUrlByPattern(s, ftpPattern));
        System.out.println(findUrl(s));

        s = ">迅雷下载地址<br /><br />thunder://QUFmdHA6Ly9keWdvZDpkeWdvZEB4dW5sZWkuZHlnb2QuY29tL1vov4Xpm7fnlLXlvbHkuIvovb14bC5keWdvZC5jb21d5qOu5YakRFZELnJtdmJaWg==<br /><";
        System.out.println(findUrlByThunderPattern(s, thunderPattern));
    }

    static Pattern ftpPattern = Pattern.compile("ftp://(.+?)(.rmvb|.3pg|.mp4)", Pattern.CASE_INSENSITIVE
                                                                                | Pattern.DOTALL | Pattern.MULTILINE);

    private static List<String> findUrlByPattern(String html, Pattern pattern) {
        if (StringUtils.isBlank(html)) {
            return null;
        }
        html = StringUtil.cleanEnterChar(html);
        Matcher m = pattern.matcher(html.trim());

        List<String> orderList = new LinkedList<String>();
        Set<String> urls = new HashSet<String>();

        while (m.find()) {
            String result = m.group(0);
            String url = findUrl(result);
            if (null != url && !urls.contains(url)) {
                urls.add(url);
                orderList.add(url);
            }
        }
        return orderList.isEmpty() ? null : orderList;
    }

    static Pattern thunderPattern = Pattern.compile("thunder://(.+?)<br", Pattern.CASE_INSENSITIVE | Pattern.DOTALL
                                                                          | Pattern.MULTILINE);

    private static List<String> findUrlByThunderPattern(String html, Pattern pattern) {
        if (StringUtils.isBlank(html)) {
            return null;
        }
        html = StringUtil.cleanEnterChar(html);
        Matcher m = pattern.matcher(html.trim());

        List<String> orderList = new LinkedList<String>();
        Set<String> urls = new HashSet<String>();

        while (m.find()) {
            String result = m.group(0);
            result = result.substring(0, result.length() - 3);
            String url = findUrl(result);
            if (null != url && !urls.contains(url)) {
                urls.add(url);
                orderList.add(url);
            }
        }
        return orderList.isEmpty() ? null : orderList;
    }

    private static String findUrl(String html) {
        StringBuilder buf = new StringBuilder(html.length());

        Document doc = Jsoup.parse(html);
        List<Node> nodes = doc.child(0).childNode(1).childNodes();
        for1: for (Node node : nodes) {
            while (!(node instanceof TextNode)) {
                if (node.childNodeSize() == 0) {
                    continue for1;
                }
                node = node.childNode(0);
            }
            buf.append(((TextNode) node).text());
        }
        String url = buf.toString();
        return StringUtil.isURL(url) ? url : null;
    }

}
