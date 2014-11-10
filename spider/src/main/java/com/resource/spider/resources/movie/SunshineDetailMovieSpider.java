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
                if (urlSets.contains(url)) {
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
        contentEles = document.getElementsByAttributeValue("style", "FONT-SIZE: 12px");

        Element firstEle = contentEles.get(0);

        contentEles = firstEle.getElementsContainingOwnText("ftp://");

        for (Element element : contentEles) {
            String url = findFtpUrl(element.outerHtml());
            if (StringUtils.isNotBlank(url) && StringUtil.isURL(url)) {
                urls.add(url);
                continue;
            }
        }

        // pattern 3
        contentEles = document.getElementsByAttributeValue("color", "#ff0000");
        for (Element element : contentEles) {
            Node node = element.childNode(0);
            while (!(node instanceof TextNode)) {
                node = node.childNode(0);
            }
            String url = ((TextNode) node).text();

            if (StringUtils.isNotBlank(url) && StringUtil.isURL(url)) {
                urls.add(url);
                continue;
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
        // System.out.println(findFtpUrl(s));

        // s =
        // "<td style=\"WORD-WRAP: break-word\" bgcolor=\"#fdfddf\"><font color=\"#ff0000\"><a href=\"http://dygod4.dygod.com/趁火行劫DVD/趁火行劫[www.dygod.com电影天堂].rmvb\">http://dygod4.dygod.com/趁火行劫DVD/趁火行劫[www.dygod.com电影天堂].rmvb</a></font></td>";

        StringBuilder buf = new StringBuilder();
        Document doc = Jsoup.parse(s);
        Elements eles = doc.getElementsByAttributeValue("color", "#ff0000");

        for (Element element : eles) {
            Node node = element.childNode(0);
            while (!(node instanceof TextNode)) {
                node = node.childNode(0);
            }
            buf.append(((TextNode) node).text());
        }
        System.out.println(buf.toString());

        // s =
        // "ftp://资源发布xl.dygod.com:影片发布@xunlei.dygod.com/美女的烦恼DVD[</font><a target=\"_blank\" href=\"http://www.dygod.com/\"><font size=\"2\">www.dygod.com</font></a><font size=\"2\">]/美女的烦恼1[电影天堂论坛bbs.dygod.com].rmvb<br />";
        doc = Jsoup.parse(s);
        buf = new StringBuilder();

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
        System.out.println(buf);
    }

    private static String findFtpUrl(String html) {
        if (StringUtils.isBlank(html)) {
            return null;
        }

        // ftp://迅雷发布xl.dygod.com:影片发布@xunlei.dygod.com/鬼面骑士[<a target="_blank"
        // href="http://www.dygod.com/">www.dygod.com</a>]/鬼面骑士[bbs.dygod.com].rmvb<br />
        String regex = "ftp://(.+?)<br";

        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

        Matcher m = p.matcher(html.trim());
        if (!m.find()) {
            return null;
        }

        String result = m.group(0);

        result = result.substring(0, result.length() - 3);
        // ftp://迅雷发布xl.dygod.com:影片发布@xunlei.dygod.com/鬼面骑士[<a target="_blank"
        // href="http://www.dygod.com/">www.dygod.com</a>]/鬼面骑士[bbs.dygod.com].rmvb

        StringBuilder buf = new StringBuilder(result.length());
        Document doc = Jsoup.parse(result);
        List<Node> nodes = doc.childNodes();
        Node targetNode = nodes.get(0).childNode(1);
        for (Node node : targetNode.childNodes()) {
            while (!(node instanceof TextNode)) {
                node = node.childNode(0);
            }
            buf.append(node.outerHtml().replaceAll("\\r", "").replaceAll("\\n", "").trim());
        }
        // ftp://迅雷发布xl.dygod.com:影片发布@xunlei.dygod.com/鬼面骑士[www.dygod.com]/鬼面骑士[bbs.dygod.com].rmvb

        return buf.toString();
    }

}
