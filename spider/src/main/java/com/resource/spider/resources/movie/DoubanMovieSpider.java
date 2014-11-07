package com.resource.spider.resources.movie;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import com.resource.spider.resources.MovieSpider;

public class DoubanMovieSpider extends MovieSpider {

    private enum MovieInfoEnum {
        country("制片国家/地区:"), language("语言:"), alias("又名:");

        private final String name;

        private MovieInfoEnum(String name){
            this.name = name;
        }
    }

    @Override
    public void parseDocument(Document document) {

        // content
        Element contentEle = document.getElementById("content");

        // title
        String title = getTitle(contentEle);
        System.out.println("title:" + title);
        // 影片信息
        Element infoEle = contentEle.getElementById("info");

        //
        List<String> directors = getDirectors(infoEle);
        System.out.println("directors:" + directors);

        List<String> screenwriters = getScreenwriters(infoEle);
        System.out.println("screenwriters:" + screenwriters);

        List<String> stars = getStars(infoEle);
        System.out.println("stars:" + stars);

        List<String> movieTypes = getMovieType(infoEle);
        System.out.println("movieTypes:" + movieTypes);

        Map<String, String> countrys = getMovieInfos(infoEle);
        System.out.println("countrys:" + countrys);

        int movieLen = getMovieLength(infoEle);
        System.out.println("movieLen:" + movieLen);

        String showTime = getShowTime(infoEle);
        System.out.println("showTime:" + showTime);

        Elements relatedInfoEles = contentEle.getElementsByClass("related-info");
        String desc = getMovieDesc(relatedInfoEles);
        System.out.println("desc:" + desc);
    }

    public String getTitle(Element contentEle) {
        Elements titleSpan = contentEle.getElementsByTag("span");
        TextNode titleNode = (TextNode) titleSpan.get(0).childNode(0);
        return titleNode.text();
    }

    public List<String> getDirectors(Element infoEle) {
        List<String> directors = new LinkedList<String>();
        Element spanChild = infoEle.child(0);
        Elements aEles = spanChild.getElementsByTag("a");
        for (Element element : aEles) {
            if (element.childNodeSize() > 0) {
                TextNode textNode = (TextNode) element.childNode(0);
                directors.add(textNode.text());
            }
        }
        return directors;
    }

    public List<String> getScreenwriters(Element infoEle) {
        List<String> screenwriters = new LinkedList<String>();
        Element spanChild = infoEle.child(2);
        Elements aEles = spanChild.getElementsByTag("a");
        for (Element element : aEles) {
            if (element.childNodeSize() > 0) {
                TextNode textNode = (TextNode) element.childNode(0);
                screenwriters.add(textNode.text());
            }
        }
        return screenwriters;
    }

    public List<String> getStars(Element infoEle) {
        List<String> stars = new LinkedList<String>();
        Element spanChild = infoEle.child(4);
        Elements aEles = spanChild.getElementsByTag("a");
        for (Element element : aEles) {
            if (element.childNodeSize() > 0) {
                TextNode textNode = (TextNode) element.childNode(0);
                stars.add(textNode.text());
            }
        }
        return stars;
    }

    public List<String> getMovieType(Element infoEle) {
        List<String> stars = new LinkedList<String>();
        Elements spanEles = infoEle.getElementsByAttributeValue("property", "v:genre");
        for (Element element : spanEles) {
            if (element.childNodeSize() > 0) {
                TextNode textNode = (TextNode) element.childNode(0);
                stars.add(textNode.text());
            }
        }
        return stars;
    }

    public Map<String, String> getMovieInfos(Element infoEle) {

        Map<String, String> movieInfoMapping = new HashMap<String, String>();
        movieInfoMapping.put(MovieInfoEnum.country.name, null);
        movieInfoMapping.put(MovieInfoEnum.language.name, null);
        movieInfoMapping.put(MovieInfoEnum.alias.name, null);

        List<Node> childNodes = infoEle.childNodes();

        boolean isUsed = false;
        for (int i = 0, size = childNodes.size(); i < size; i++) {
            Node node = childNodes.get(i);
            if (!isUsed && !node.hasAttr("class")) {
                continue;
            }
            isUsed = false;
            if (node.childNodeSize() == 0) {
                continue;
            }
            Node childNode = node.childNode(0);
            if (!(childNode instanceof TextNode)) {
                continue;
            }

            TextNode keyTextNode = (TextNode) childNode;
            String key = StringUtils.trim(keyTextNode.text());
            isUsed = movieInfoMapping.containsKey(key);
            if (!isUsed) {
                continue;
            }

            TextNode valueTextNode = (TextNode) childNodes.get(++i);
            if (key.equals(MovieInfoEnum.country.name)) {
                movieInfoMapping.put(MovieInfoEnum.country.name, valueTextNode.text());
                continue;
            }
            if (key.equals(MovieInfoEnum.language.name)) {
                movieInfoMapping.put(MovieInfoEnum.language.name, valueTextNode.text());
                continue;
            }
            if (key.equals(MovieInfoEnum.alias.name)) {
                movieInfoMapping.put(MovieInfoEnum.alias.name, valueTextNode.text());
                continue;
            }
        }
        return movieInfoMapping;
    }

    public int getMovieLength(Element infoEle) {
        Elements eles = infoEle.getElementsByAttributeValue("property", "v:runtime");
        if (eles.size() == 0) {
            return 0;
        }
        Element movieLenEle = eles.get(0);
        String content = movieLenEle.attr("content");
        if (StringUtils.isNumeric(content)) {
            return Integer.valueOf(content);
        }
        return 0;
    }

    public String getShowTime(Element infoEle) {
        Elements eles = infoEle.getElementsByAttributeValue("property", "v:initialReleaseDate");
        if (eles.size() == 0) {
            return "";
        }
        Element movieLenEle = eles.get(0);
        String content = movieLenEle.attr("content");
        return content;
    }

    public String getMovieDesc(Elements relatedInfoEles) {
        if (relatedInfoEles.size() == 0) {
            return "";
        }

        Element ele = relatedInfoEles.get(0);
        Elements propertyEles = ele.getElementsByAttributeValue("property", "v:summary");
        if (propertyEles.size() == 0) {
            return "";
        }

        TextNode textNode = (TextNode) propertyEles.get(0).childNode(0);
        return textNode.text();
    }
}
