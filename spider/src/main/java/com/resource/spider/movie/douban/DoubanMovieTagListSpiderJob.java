package com.resource.spider.movie.douban;

import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.resource.spider.movie.AbstractDoubanMovieSpider;
import com.resources.dal.dataobject.SpiderRecordDO;
import com.resources.dal.mapper.SpiderRecordMapper;
import com.resources.service.ResMovieService;

/**
 * tag-list列表(@http://movie.douban.com/tag/)
 * 
 * @author zhiwenmizw
 */
@Deprecated
// @Service
public class DoubanMovieTagListSpiderJob extends AbstractDoubanMovieSpider {

    private final static Logger log           = LoggerFactory.getLogger(DoubanMovieTagListSpiderJob.class);

    public String               doubanTagPage = "http://movie.douban.com/tag/";
    public int                  length        = 20;

    @Resource
    private SpiderRecordMapper  spiderRecordMapper;

    @Resource
    private ResMovieService     resMovieService;

    @Override
    public int getTimeInterval() {
        return 1000;
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
    }

    // 3、抓取每页的数据, 从最后一页开始吃起
    // int eatedCount = spiderRecordDO.getEatNumber();
    // for (int i = spiderRecordDO.getPageNumber(); i >= 1; i--) {
    //
    // int offset = (i - 1) * length;
    //
    // doubanTagPageByTag = String.format("%s%s?start=%s&type=T", doubanTagPage, encTagName, offset);
    //
    // didList = parsePageListItem(doubanTagPageByTag);
    //
    // eatedCount += resMovieService.addMovieList(didList, DataStatus.doubanMovieAbstract.getValue());
    //
    // // 4、记录总和总页条数到db
    // spiderRecordMapper.updateData(spiderRecordDO);
    // }

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
        List<SpiderRecordDO> spiderRecrods = spiderRecordMapper.getRecordByType(SpiderRecordDO.SpiderRecordTypeEnum.douban_movie.getValue());

        // 2、开始抓取列表页的数据并保存入db
        for (SpiderRecordDO spiderRecordDO : spiderRecrods) {
            parsePageList(spiderRecordDO);
        }
        log.info("proccess-over-{}", this.getClass().toString());
    }

}
