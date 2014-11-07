package com.resource.spider.resources.movie;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.resource.spider.resources.MovieSpider;
import com.resources.common.BizType;
import com.resources.common.StringUtil;
import com.resources.dal.module.MovieDO;
import com.resources.dal.module.ResourceInfoDO;
import com.resources.service.dto.ResourceInfoDTO;

@Service
public class DoubanMovieSpider extends MovieSpider {

    private enum MovieInfoEnum {
        country("制片国家/地区:"), language("语言:"), alias("又名:"), movieNumber("集数:"), sigleMovieLen("单集片长:");

        private final String name;

        private MovieInfoEnum(String name){
            this.name = name;
        }
    }

    @Override
    public ResourceInfoDTO<MovieDO> parseToMovie(Document document) {

        MovieDO movieDO = new MovieDO();
        ResourceInfoDO resourceInfo = new ResourceInfoDO();
        ResourceInfoDTO<MovieDO> resInfoDTO = new ResourceInfoDTO<MovieDO>(resourceInfo, movieDO);

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

        Map<String, String> movieInfo = getMovieInfos(infoEle);
        System.out.println("movieInfo:" + movieInfo);

        int movieLen = getMovieLength(infoEle);
        System.out.println("movieLen:" + movieLen);

        Date showTime = getShowTime(infoEle);

        Elements relatedInfoEles = contentEle.getElementsByClass("related-info");
        String desc = getMovieDesc(relatedInfoEles);

        long cid = 2;
        // set-resourceInfo
        resourceInfo.setTitle(StringUtils.trim(title));
        resourceInfo.setCid(cid);
        resourceInfo.setDescription(StringUtils.trim(desc));
        resourceInfo.setBizType(BizType.MOVIE.getType());

        int hashCode = Math.abs(title.hashCode());
        if (hashCode > 500) {
            hashCode = 500;
        }

        resourceInfo.setViewCounts(hashCode * 2);
        resourceInfo.setDownloadCounts(hashCode);
        // set-movie
        movieDO.setCid(cid);
        movieDO.setDirector(directors.toString());
        movieDO.setScreenwriter(screenwriters.toString());
        movieDO.setStars(stars.toString());
        String aliasName = movieInfo.get(MovieInfoEnum.alias.name);
        movieDO.setAliasName(aliasName);
        movieDO.setCountry(movieInfo.get(MovieInfoEnum.country.name));
        movieDO.setLanguage(movieInfo.get(MovieInfoEnum.language.name));
        if (movieLen == 0) {
            String mlenString = movieInfo.get(MovieInfoEnum.sigleMovieLen.name);
            movieLen = StringUtil.matchNumber(mlenString).intValue();
        }
        movieDO.setMovieLength(movieLen);
        movieDO.setShowTime(showTime.getTime());
        movieDO.setStarLevel(6.5f);

        return resInfoDTO;
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
        movieInfoMapping.put(MovieInfoEnum.movieNumber.name, null);
        movieInfoMapping.put(MovieInfoEnum.sigleMovieLen.name, null);

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
            if (key.equals(MovieInfoEnum.movieNumber.name)) {
                movieInfoMapping.put(MovieInfoEnum.movieNumber.name, valueTextNode.text());
                continue;
            }
            if (key.equals(MovieInfoEnum.sigleMovieLen.name)) {
                movieInfoMapping.put(MovieInfoEnum.sigleMovieLen.name, valueTextNode.text());
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

    public Date getShowTime(Element infoEle) {
        Elements eles = infoEle.getElementsByAttributeValue("property", "v:initialReleaseDate");
        if (eles.size() == 0) {
            throw new IllegalAccessError("getShowTime error none eles");
        }
        Element movieLenEle = eles.get(0);
        String content = movieLenEle.attr("content");
        if (StringUtils.isBlank(content)) {
            throw new IllegalAccessError("getShowTime error content is blank");
        }

        int index = content.indexOf('(');
        content = content.substring(0, index);

        try {
            return DateUtils.parseDate(content, new String[] { "yyyy-MM-dd" });
        } catch (ParseException e) {
            throw new IllegalAccessError("getShowTime error content parse Error" + content);
        }
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

        for (Element element : propertyEles) {
            String txt = element.ownText();
            return txt;
        }
        return "";
    }
}
