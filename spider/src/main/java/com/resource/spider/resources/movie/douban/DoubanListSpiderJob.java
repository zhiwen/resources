package com.resource.spider.resources.movie.douban;

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
public class DoubanListSpiderJob implements SpiderJob {

    public static enum DataStatus {
        TagList(1), // douban搜索页获取id ("http://movie.douban.com/tag/")
        SubjectAbs(2), // douban一网页接口（http://movie.douban.com/j/subject_abstract?subject_id=1302814）
        SubjectApi(3), // (http://api.douban.com/v2/movie/subject/1302814)
        SubjectDetail(4);// (http://movie.douban.com/subject/3269068/)

        public final int value;

        private DataStatus(int value){
            this.value = value;
        }
    }

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
                spiderRecordMapper.addData(recordDO);

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
        List<Long> didList = null;
        // 1、 吃了的条数和页数都为0，表示没有爬过。
        if (0 == spiderRecordDO.getPageNumber() || 0 == spiderRecordDO.getEatNumber()) {
            // 2、获取总页数
            int pageNumber = 1;
            Elements pagAEles = doc.getElementsByClass("paginator");
            if (CollectionUtils.isNotEmpty(pagAEles)) {
                pagAEles = pagAEles.get(0).getElementsByTag("a");
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
            }
            spiderRecordDO.setPageNumber(pageNumber);
        }

        // 3、抓取每页的数据, 从最后一页开始吃起
        int eatedCount = spiderRecordDO.getEatNumber();
        for (int i = spiderRecordDO.getPageNumber(); i >= 1; i--) {

            int offset = (i - 1) * length;

            doubanTagPageByTag = String.format("%s%s?start=%s&type=T", doubanTagPage, encTagName, offset);

            didList = parsePageListItem(doubanTagPageByTag);

            eatedCount += resMovieService.addMovieList(didList);

            // 4、记录总和总页条数到db
            spiderRecordDO.setPageNumber(i);
            spiderRecordDO.setEatNumber(eatedCount);
            spiderRecordMapper.updateData(spiderRecordDO);
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

        // 2、开始抓取列表页的数据并保存入db
        for (SpiderRecordDO spiderRecordDO : spiderRecrods) {
            parsePageList(spiderRecordDO);
        }

        // 3、保存数据
    }

}
