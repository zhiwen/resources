package com.resource.spider.movie.douban;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.resource.spider.SpiderJob;
import com.resource.spider.http.HttpURLConnectionWrapper;
import com.resources.common.BizTypeEnum;
import com.resources.dal.dataobject.ResMovieDO;
import com.resources.dal.dataobject.ResTagDO;
import com.resources.service.ResKVService;
import com.resources.service.ResMovieService;
import com.resources.service.ResTagService;
import com.resources.service.ResURLService;

/**
 * <pre>
 * 网页详情去抓取数据
 * 1、季数每季id
 * 2、每季集数
 * 3、上映日期 2014-10-24(中国大陆) / 2014-07-25(美国)
 * 4、IMDb链接:
 * 5、语言：
 * 6、编剧:
 *      
 * String url = "http://movie.douban.com/subject/24404677/";// 完整数据
 * url = "http://movie.douban.com/subject/11599330/"; // 无imdb
 * url = "http://movie.douban.com/subject/1507337/";// 无编剧
 * url = "http://movie.douban.com/subject/3222721/";// 无语言
 * url = "http://movie.douban.com/subject/3742360/";// 多语言
 * url = "http://movie.douban.com/subject/25846034/";// 多季，多集
 * url = "http://movie.douban.com/subject/25940269/";// 无季，多集
 * </pre>
 * 
 * @author zhiwenmizw
 */
@Service
public class DoubanSubjectDetailSpiderJob implements SpiderJob {

    private final static Logger log                 = LoggerFactory.getLogger(DoubanSubjectDetailSpiderJob.class);

    @Resource
    private ResMovieService     resMovieService;

    @Resource
    private ResKVService        resKVService;

    @Resource
    private ResURLService       resURLService;

    @Resource
    private ResTagService       resTagService;

    private final String        doubanSubjectDetail = "http://movie.douban.com/subject/%s/";

    private enum MovieInfoEnum {
        language("语言:"), writers("编剧");

        private final String value;

        private MovieInfoEnum(String name){
            this.value = name;
        }
    }

    private List<Long> getTagIdList(long did, List<String> arrays) {
        if (CollectionUtils.isEmpty(arrays)) {
            return Collections.emptyList();
        }
        List<Long> tagIds = new LinkedList<Long>();
        for (String value : arrays) {
            if (null == value) {
                continue;
            }
            String tagName = StringUtils.trim(value);
            ResTagDO resTagDO = new ResTagDO();
            resTagDO.setBizType(BizTypeEnum.MOVIE);
            resTagDO.setCid(0);
            resTagDO.setTagName(tagName);
            resTagDO.setCreatedTime(new Date());
            ResTagDO newResTagDO = resTagService.getDataIfNotExistAdd(resTagDO);
            if (null != newResTagDO) {
                tagIds.add(newResTagDO.getId());
            } else {
                log.error("array-get-id not found did:[{}] value:[{}] arrays:[{}]", new Object[] { did, value, arrays });
            }
        }
        return tagIds;
    }

    public void parseAndSave(ResMovieDO resMovieDO, Element infoEle) {

        List<String> pubdates = getPudates(infoEle);
        if (!CollectionUtils.isEmpty(pubdates)) {
            resMovieDO.setPubdates(JSON.toJSONString(pubdates));
        }

        List<String> writerList = getWriters(infoEle);
        if (!CollectionUtils.isEmpty(writerList)) {
            List<Long> tagIds = getTagIdList(resMovieDO.getDid(), writerList);
            if (!CollectionUtils.isEmpty(tagIds)) {
                resMovieDO.setWriterIds(tagIds);
            }
        }

        String imdbId = getIMDBID(infoEle);
        resMovieDO.setImdbId(imdbId);

        List<String> languageList = getLanguages(infoEle);
        if (!CollectionUtils.isEmpty(languageList)) {
            resMovieDO.setLanguages(languageList);
        }

        List<Long> seasons = getSeasons(infoEle);
        if (!CollectionUtils.isEmpty(languageList)) {
            resMovieDO.setSeasonCount(seasons.size());

            // 将所有季的id查询出来，合并到一个kv里面
            // resMovieService.getMovieByDid(did);
        }

    }

    @Override
    public void execute() throws Exception {
        int offset = 0, length = 1000;
        while (true) {
            List<ResMovieDO> movieList = resMovieService.getMovieByPaginatorWithStatus(DataStatus.SubjectAbs.value,
                                                                                       offset, length);
            if (CollectionUtils.isEmpty(movieList)) {
                break;// the end
            }

            for (ResMovieDO resMovieDO : movieList) {

                String qulifySubjectUrl = String.format(doubanSubjectDetail, resMovieDO.getDid());
                log.info("process-url:[{}]", qulifySubjectUrl);

                Document document = getDocument(qulifySubjectUrl);
                if (null == document) {
                    log.error("getDocument-fail did:[{}]", resMovieDO.getDid());
                    continue;
                }

                Element contentEle = document.getElementById("content");
                if (null == contentEle) {
                    log.error("document.getElementById(\"content\")-fail did:[{}]", resMovieDO.getDid());
                    continue;
                }
                Element infoEle = contentEle.getElementById("info");
                if (null == infoEle) {
                    log.error("contentEle.getElementById(\"info\")-fail did:[{}]", resMovieDO.getDid());
                    continue;
                }

                parseAndSave(resMovieDO, infoEle);
            }
            // offset += length;
        }
        log.info("proccess-over-{}", this.getClass().toString());
    }

    public static Document getDocument(String url) {
        HttpURLConnectionWrapper con = null;
        Document doc = null;
        try {
            con = new HttpURLConnectionWrapper(new URL(url));
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

    public static void main(String[] args) {
        DoubanSubjectDetailSpiderJob spider = new DoubanSubjectDetailSpiderJob();

        String url = "http://movie.douban.com/subject/24404677/";// 完整数据
        url = "http://movie.douban.com/subject/11599330/"; // 无imdb
        url = "http://movie.douban.com/subject/1507337/";// 无编剧
        url = "http://movie.douban.com/subject/3222721/";// 无语言
        url = "http://movie.douban.com/subject/3742360/";// 多语言
        url = "http://movie.douban.com/subject/25846034/";// 多季，多集
        // url = "http://movie.douban.com/subject/25940269/";// 无季，多集

        Document document = getDocument(url);

        Element contentEle = document.getElementById("content");

        Element infoEle = contentEle.getElementById("info");

        List<String> pubdates = spider.getPudates(infoEle);
        System.out.println("pubdates:" + pubdates);

        List<String> writerList = spider.getWriters(infoEle);
        System.out.println("writerList:" + writerList);

        String imdb = spider.getIMDBID(infoEle);
        System.out.println("imdb:" + imdb);

        List<String> languageList = spider.getLanguages(infoEle);
        System.out.println("languageList:" + languageList);

        List<Long> seasons = spider.getSeasons(infoEle);
        System.out.println("seasons:" + seasons);
    }

    public List<String> getLanguages(Element infoEle) {
        if (null == infoEle) {
            return null;
        }

        boolean isNeeded = false;
        List<String> languageList = new LinkedList<String>();
        List<Node> nodes = infoEle.childNodes();
        for (Node node : nodes) {

            if (isNeeded) {
                String languageString = HtmlUtils.getTextValue(node);
                String[] languages = org.springframework.util.StringUtils.delimitedListToStringArray(languageString,
                                                                                                     "/");
                if (null == languages || languages.length == 0) {
                    return null;
                }
                for (String language : languages) {
                    if (StringUtils.isNotBlank(language)) {
                        languageList.add(StringUtils.trim(language));
                    }
                }
                return languageList;
            }

            if (!StringUtils.equalsIgnoreCase("span", node.nodeName())) {
                continue;
            }
            String className = node.attr("class");
            if (!StringUtils.equals("pl", className)) {
                continue;
            }

            String value = HtmlUtils.getTextValue(node);
            isNeeded = StringUtils.equalsIgnoreCase(MovieInfoEnum.language.value, value);
        }
        return languageList;
    }

    // <a href="http://www.imdb.com/title/tt2872732" target="_blank" rel="nofollow">tt2872732</a>
    public String getIMDBID(Element infoEle) {
        if (null == infoEle) {
            return null;
        }

        Elements imdbEles = infoEle.getElementsByAttributeValueContaining("href", "http://www.imdb.com/title/");
        for (Element element : imdbEles) {
            String value = HtmlUtils.getTextValue(element);
            if (StringUtils.isNotBlank(value)) {
                return StringUtils.trim(value);
            }
        }
        return null;
    }

    public List<String> getWriters(Element infoEle) {
        if (null == infoEle) {
            return null;
        }

        List<Node> childNodes = infoEle.childNodes();
        for (Node node : childNodes) {
            if (!StringUtils.equalsIgnoreCase("span", node.nodeName())) {
                continue;
            }

            List<Node> chiNodes = node.childNodes();

            boolean isNeeded = false;

            for (Node node2 : chiNodes) {
                if (!StringUtils.equalsIgnoreCase("span", node.nodeName())) {
                    continue;
                }
                String className = node2.attr("class");
                if (StringUtils.equals("pl", className)) {
                    String value = HtmlUtils.getTextValue(node2);
                    isNeeded = StringUtils.equalsIgnoreCase(MovieInfoEnum.writers.value, value);
                    continue;
                }
                if (isNeeded && StringUtils.equals("attrs", className)) {
                    List<String> writerList = new LinkedList<String>();
                    Element writerEle = (Element) node2;
                    Elements aEles = writerEle.getElementsByTag("a");
                    for (Element element : aEles) {
                        String value = HtmlUtils.getTextValue(element);
                        if (StringUtils.isBlank(value)) {
                            continue;
                        }
                        writerList.add(StringUtils.trim(value));
                    }
                    return writerList;
                }
            }
        }
        return null;
    }

    public List<Long> getSeasons(Element infoEle) {
        Element seasonEle = infoEle.getElementById("season");
        if (null == seasonEle) {
            return null;
        }
        List<Long> dids = new LinkedList<Long>();
        List<Node> childNodes = seasonEle.childNodes();
        for (Node node : childNodes) {
            Element optionEle = (Element) node;
            String did = StringUtils.trim(optionEle.attr("value"));

            if (StringUtils.isBlank(did)) {
                continue;
            }

            dids.add(Long.valueOf(did));
        }
        if (CollectionUtils.isEmpty(dids)) {
            return null;
        }
        // 这里再去映射一下本地的id,然后返回；如果本地没有或不全，则出错打日志
        return dids;
    }

    /**
     * 获取上映日期
     * 
     * @param infoEle
     * @return
     */
    public List<String> getPudates(Element infoEle) {
        Elements eles = infoEle.getElementsByAttributeValue("property", "v:initialReleaseDate");
        if (null == eles || eles.isEmpty()) {
            return null;
        }
        List<String> pubdates = new ArrayList<String>();
        for (Element element : eles) {
            String content = element.attr("content");
            if (StringUtils.isNotBlank(content)) {
                pubdates.add(content);
            }
        }
        return pubdates;
    }
}
