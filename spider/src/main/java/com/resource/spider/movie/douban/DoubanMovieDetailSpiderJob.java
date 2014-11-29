package com.resource.spider.movie.douban;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.resource.spider.http.HttpURLConnectionWrapper;
import com.resource.spider.movie.AbstractDoubanMovieSpider;
import com.resources.common.BizTypeEnum;
import com.resources.common.ResKVTypeEnum;
import com.resources.dal.dataobject.ResKVDO;
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
 * 6、电影标签
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
public class DoubanMovieDetailSpiderJob extends AbstractDoubanMovieSpider {

    private final static Logger log                  = LoggerFactory.getLogger(DoubanMovieDetailSpiderJob.class);

    @Resource
    private ResMovieService     resMovieService;

    @Resource
    private ResKVService        resKVService;

    @Resource
    private ResURLService       resURLService;

    @Resource
    private ResTagService       resTagService;

    private final String        doubanMovieDetailApi = "http://movie.douban.com/subject/%s/";

    private enum MovieInfoEnum {
        language("语言:"), writers("编剧");

        private final String value;

        private MovieInfoEnum(String name){
            this.value = name;
        }
    }

    @Override
    public int getTimeInterval() {
        return 1500;
    }

    @Override
    public int getReadTimeout() {
        return 10000;
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

    public void parseAndSave(ResMovieDO resMovieDO, Document document, Element infoEle) {

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
        if (!CollectionUtils.isEmpty(seasons)) {
            resMovieDO.setSeasonCount(seasons.size());

            List<ResMovieDO> seasonMovieList = resMovieService.getDataByDidList(seasons);
            // 如果两个人的数量不等，表示其中有一季电影没有在本地有。需要添加进去
            if (seasonMovieList.size() != seasons.size()) {
                resMovieService.addMovieList(seasons, DataStatus.doubanMovieId.getValue());
                seasonMovieList = resMovieService.getDataByDidList(seasons);
            }
            // 将查询回来的id,按seasons的id顺序合并成一个json
            List<Long> midList = sortedIdBySeasonId(seasonMovieList, seasons);
            // 将所有季的id查询出来，合并到一个kv里面
            ResKVDO kvDO = new ResKVDO();

            // 安全起见，取最小的一个did出来
            Collections.sort(seasons);
            long minDid = seasons.get(0);

            kvDO.setResKey(String.valueOf(minDid));
            kvDO.setResValue(JSON.toJSONString(midList));
            kvDO.setCreatedTime(resMovieDO.getCreatedTime());
            kvDO.setType(ResKVTypeEnum.movie_seasonId);
            resKVService.addData(kvDO);
            // 关联电视剧id
            resMovieDO.setSeasonId(kvDO.getId());
        }
        List<String> tagsName = getTags(document);
        List<Long> tagIdList = getTagIdList(resMovieDO.getDid(), tagsName);
        resMovieDO.setTagIds(tagIdList);

        // 保存播放地址的js路径
        if (isPlayable(document, resMovieDO.getDid())) {
            String playJSURL = getPlayAddressJSURL(document);
            if (StringUtils.isNotBlank(playJSURL)) {
                ResKVDO kvDO = new ResKVDO();
                kvDO.setType(ResKVTypeEnum.movie_playJSURL);
                kvDO.setResKey(String.valueOf(resMovieDO.getDid()));
                kvDO.setResValue(playJSURL);
                kvDO.setCreatedTime(resMovieDO.getCreatedTime());
                resKVService.addData(kvDO);
            }
        }

        try {
            resMovieDO.setDataStatus(DataStatus.doubanMovieDetail.getValue());
            resMovieService.updateMovie(resMovieDO);
        } catch (Exception e) {
            log.error("updateMovie-fail movieDO:[{}]", resMovieDO, e);
        }
    }

    private List<Long> sortedIdBySeasonId(List<ResMovieDO> seasonMovieList, List<Long> seasons) {
        List<Long> sortedList = new LinkedList<Long>();
        Map<Long, ResMovieDO> dIdMapping = new HashMap<Long, ResMovieDO>();
        for (ResMovieDO movie : seasonMovieList) {
            dIdMapping.put(movie.getDid(), movie);
        }
        for (Long did : seasons) {
            sortedList.add(dIdMapping.get(did).getId());
        }
        return sortedList;
    }

    public Document getDocument2(String url) throws Exception {
        HttpURLConnectionWrapper con = null;
        Document doc = null;
        try {
            Thread.sleep(getTimeInterval());
            con = new HttpURLConnectionWrapper(new URL(url), getConnectTimeout(), getReadTimeout());
            InputStream in = con.getInputStream();
            doc = Jsoup.parse(in, "utf-8", url);
        } catch (Exception e) {
            throw e;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return doc;
    }

    @Override
    public void execute() throws Exception {
        int offset = 0, length = 1000;
        while (true) {
            List<ResMovieDO> movieList = resMovieService.getMovieByPaginatorWithStatus(DataStatus.doubanMovieApi.getValue(),
                                                                                       offset, length);
            if (CollectionUtils.isEmpty(movieList)) {
                break;// the end
            }

            for (ResMovieDO resMovieDO : movieList) {

                String qulifySubjectUrl = String.format(doubanMovieDetailApi, resMovieDO.getDid());
                Document document = null;
                try {
                    document = getDocument2(qulifySubjectUrl);
                } catch (FileNotFoundException fe) {
                    resMovieDO.setDataStatus(DataStatus.doubanMovieNotFoud.getValue());
                    resMovieService.updateMovie(resMovieDO);
                    log.info("process-url-not-found:[{}]", qulifySubjectUrl);
                    continue;
                } catch (Exception e) {
                    log.error("error-process-url[{}]", qulifySubjectUrl, e);
                }
                log.info("process-url:[{}]", qulifySubjectUrl);
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

                try {
                    parseAndSave(resMovieDO, document, infoEle);
                } catch (Exception e) {
                    log.error("process-fail did[{}]", resMovieDO.getDid());
                }
            }
        }
        log.info("proccess-over-{}", this.getClass().toString());
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

    /**
     * <pre>
     * 获取 imdb_id
     * <a href="http://www.imdb.com/title/tt2872732" target="_blank" rel="nofollow">tt2872732</a>
     * </pre>
     * 
     * @param infoEle
     * @return
     */
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

    /**
     * 获取作家-编剧
     * 
     * @param infoEle
     * @return
     */
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

    public List<String> getTags(Document document) {
        Elements tagsBodyEles = document.getElementsByClass("tags-body");
        if (null == tagsBodyEles || tagsBodyEles.isEmpty()) {
            return null;
        }
        Element tagsChild = tagsBodyEles.get(0);
        Elements aEles = tagsChild.getElementsByTag("a");
        List<String> tagsName = new LinkedList<String>();
        for (Element element : aEles) {
            String tagName = HtmlUtils.getTextValue(element);
            if (StringUtils.isNotBlank(tagName)) {
                tagsName.add(StringUtils.trim(tagName));
            }
        }
        return tagsName;
    }

    static Pattern jsPattern = Pattern.compile("http://(\\S+)\\.douban.com/misc/mixed_static/(\\S+)\\.js",
                                               Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);

    /**
     * <pre>
     * 获取页面上是否支持播放
     * <a id="play-btn-24867835" data-sid="24867835" href="javascript:void(0);" class="video-link">全片播放</a>
     * </pre>
     * 
     * @param document
     * @param did
     * @return
     */
    public boolean isPlayable(Document document, long did) {
        Element playBtnEle = document.getElementById(String.format("play-btn-%s", did));
        return playBtnEle != null;
    }

    /**
     * <pre>
     * 获取电影播放地址js路径
     * http://img3.douban.com/misc/mixed_static/77d93274900d16ff.js
     * </pre>
     * 
     * @param document
     * @return
     */
    public String getPlayAddressJSURL(Document document) {
        Elements scriptEles = document.getElementsByTag("script");
        for (Element element : scriptEles) {
            String src = element.attr("src");
            if (StringUtils.isBlank(src)) {
                continue;
            }
            Matcher matcher = jsPattern.matcher(src);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return null;
    }
}
