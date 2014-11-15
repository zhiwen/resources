package com.resource.spider.resources.movie;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.resource.spider.SpiderJob;
import com.resource.spider.http.HttpURLConnectionWrapper;
import com.resources.dal.dataobject.SpiderRecordDO;
import com.resources.dal.mapper.SpiderRecordMapper;
import com.resources.service.ResMovieService;

@Service
public class DoubanSpiderJob implements SpiderJob {

    public String              doubanTagPage = "http://movie.douban.com/tag/";
    public int                 length        = 20, timeInterval = 5000;

    @Resource
    private SpiderRecordMapper spiderRecordMapper;

    @Resource
    private ResMovieService    resMovieService;

    public Document getDocument(String url) {
        HttpURLConnectionWrapper con = null;
        Document doc = null;
        try {
            Thread.sleep(timeInterval);
            con = new HttpURLConnectionWrapper(new URL(url));
            InputStream in = con.getInputStream();
            doc = Jsoup.parse(in, "utf-8", doubanTagPage);
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

    public List<SpiderRecordDO> preData() {

        List<SpiderRecordDO> spiderRecrods = spiderRecordMapper.getRecordByType(SpiderRecordDO.SpiderRecordTypeEnum.douban.getValue());

        if (CollectionUtils.isNotEmpty(spiderRecrods)) {
            return spiderRecrods;
        }

        Document doc = getDocument(doubanTagPage);

        Set<String> tagNameSet = new HashSet<String>();
        for (SpiderRecordDO recrod : spiderRecrods) {
            tagNameSet.add(recrod.getTagName());
        }

        Elements classTagColEles = doc.getElementsByClass("tagCol");

        for (Element tabEle : classTagColEles) {

            Elements tdEles = tabEle.getElementsByTag("td");

            for (Element tdEle : tdEles) {

                Node childNode = tdEle.childNode(0);
                while (!(childNode instanceof TextNode)) {
                    childNode = childNode.childNode(0);
                }
                TextNode textNode = (TextNode) childNode;
                String tagName = textNode.text();
                tagName = StringUtils.trim(tagName);
                if (StringUtils.isBlank(tagName)) {
                    continue;
                }
                if (tagNameSet.contains(tagName)) {
                    continue;
                }
                SpiderRecordDO recordDO = new SpiderRecordDO();
                recordDO.setType(SpiderRecordDO.SpiderRecordTypeEnum.douban.getValue());
                recordDO.setTagName(tagName);
                recordDO.setCreatedTime(new Date());
                spiderRecordMapper.addRecord(recordDO);

                // news
                spiderRecrods.add(recordDO);
            }
        }
        return spiderRecrods;
    }

    public void parsePageList(SpiderRecordDO spiderRecordDO) throws Exception {

        String encTagName = URLEncoder.encode(spiderRecordDO.getTagName(), "utf-8");
        String doubanTagPageByTag = String.format("%s%s", doubanTagPage, encTagName);
        Document doc = getDocument(doubanTagPageByTag);
        if (null == doc) {
            return;
        }
        // 1、获取总页数
        int pageNumber = 0;
        Elements pagAEles = doc.getElementsByClass("paginator").get(0).getElementsByTag("a");
        for (Element element : pagAEles) {
            TextNode textNode = (TextNode) element.childNode(0);
            String pageNumberString = StringUtils.trim(textNode.text());
            if (!StringUtils.isNumeric(pageNumberString)) {
                continue;
            }
            int pn = Integer.valueOf(pageNumberString);
            if (pn > pageNumber) {
                pageNumber = pn;
            }
        }

        List<Long> didList = null;
        // 2、提前计算页面上有多少条数据，减少不必要的爬取
        if (pageNumber == spiderRecordDO.getPageNumber()) {
            int offset = (pageNumber - 1) * length;
            doubanTagPageByTag = String.format("%s%s?start=%s&type=T", doubanTagPage, encTagName, offset);
            didList = parsePageListItem(doubanTagPageByTag);

            // 如果总条数和已经吃掉的条数一样则不需要继续爬了，表示没有新东西产生
            int totalCount = didList.size() * pageNumber;
            if (spiderRecordDO.getEatNumber() >= totalCount) {
                return;
            }
        }

        // 3、抓取每页的数据
        int eatedCount = spiderRecordDO.getEatNumber();
        for (int i = 1; i <= pageNumber; i++) {
            int offset = (i - 1) * length;
            doubanTagPageByTag = String.format("%s%s?start=%s&type=T", doubanTagPage, encTagName, offset);
            didList = parsePageListItem(doubanTagPageByTag);
            // 保存id到db，如果返回0表示当前爬的数据db都已经有了，直接结束了不需要继续爬了
            eatedCount += resMovieService.addMovieList(didList);

            // 4、记录总和总页条数到db
            if (eatedCount > spiderRecordDO.getEatNumber()) {
                spiderRecordDO.setPageNumber(pageNumber);
                spiderRecordDO.setEatNumber(eatedCount);
                spiderRecordMapper.updateRecord(spiderRecordDO);
            }
        }
    }

    static int     idMarkLength    = "/subject/".length();

    static Pattern doubanIdPattern = Pattern.compile("/subject/(\\d+)/");

    public List<Long> parsePageListItem(String url) {
        List<Long> didList = new LinkedList<Long>();
        Document doc = getDocument(url);
        if (null == doc) {
            return didList;
        }
        Elements clasNameNbgEles = doc.getElementsByClass("nbg");
        for (Element element : clasNameNbgEles) {
            String hrefValue = StringUtils.trim(element.attr("href"));
            if (StringUtils.isBlank(hrefValue)) {
                continue;
            }
            try {
                Matcher m = doubanIdPattern.matcher(hrefValue);
                if (m.find()) {
                    String did = m.group(0);
                    did = did.substring(idMarkLength, did.length() - 1);
                    if (StringUtils.isNotBlank(did) && StringUtils.isNumeric(did)) {
                        didList.add(Long.valueOf(did));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            continue;
        }
        return didList;
    }

    @Override
    public void execute() throws Exception {

        // 1、加载处理过的url，
        List<SpiderRecordDO> spiderRecrods = preData();

        // 2、开始抓取列表页的数据
        for (SpiderRecordDO spiderRecordDO : spiderRecrods) {
            parsePageList(spiderRecordDO);
        }

        // 3、保存数据
    }

}
