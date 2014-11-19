package com.resource.spider.movie.douban;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.resource.spider.movie.AbstractDoubanMovieSpider;
import com.resources.dal.dataobject.SpiderRecordDO;
import com.resources.dal.dataobject.SpiderRecordDO.SpiderRecordTypeEnum;
import com.resources.dal.mapper.SpiderRecordMapper;

/**
 * 抓取douban的所有 tag
 * 
 * @author zhiwenmizw
 */
@Service
public class DoubanMovieTagsSpider extends AbstractDoubanMovieSpider {

    private final static Logger log            = LoggerFactory.getLogger(DoubanMovieTagsSpider.class);
    @Resource
    private SpiderRecordMapper  spiderRecordMapper;

    public String               hotTagsPage    = "http://movie.douban.com/tag/";
    public String               allHotTagsPage = "http://movie.douban.com/tag/?view=cloud";

    private SpiderRecordDO checkIsExist(SpiderRecordTypeEnum type, String name) {
        ImmutableMap<String, ? extends Object> param = ImmutableMap.of("type", type.getValue(), "tagName", name);
        return spiderRecordMapper.getRecordByTypeWithName(param);
    }

    public void allHotTagsSpider() {
        Document doc = getDocument(allHotTagsPage);
        if (null == doc) {
            log.error("getDocument(allHotTagsPage)-fail");
            return;
        }
        Elements indents = doc.getElementsByClass("indent");
        if (null == indents) {
            log.error("doc.getElementsByClass(\"indent\")-fail");
            return;
        }
        Element allTagEle = indents.get(0);
        Elements aEles = allTagEle.getElementsByTag("a");
        for (Element element : aEles) {
            String tagName = HtmlUtils.getTextValue(element);
            tagName = StringUtils.trim(tagName);
            if (StringUtils.isBlank(tagName)) {
                continue;
            }

            if (null != checkIsExist(SpiderRecordDO.SpiderRecordTypeEnum.douban_movie, tagName)) {
                continue;
            }

            SpiderRecordDO recordDO = new SpiderRecordDO();
            recordDO.setType(SpiderRecordDO.SpiderRecordTypeEnum.douban_movie.getValue());
            recordDO.setTagName(tagName);
            recordDO.setCreatedTime(new Date());
            spiderRecordMapper.addData(recordDO);
        }
    }

    public void hotTagsSpider() {

        Document doc = getDocument(hotTagsPage);

        Elements classTagColEles = doc.getElementsByClass("tagCol");

        for (Element tabEle : classTagColEles) {

            Elements tdEles = tabEle.getElementsByTag("td");

            for (Element tdEle : tdEles) {

                String tagName = HtmlUtils.getTextValue(tdEle.childNode(0));
                tagName = StringUtils.trim(tagName);
                if (StringUtils.isBlank(tagName)) {
                    continue;
                }

                if (null != checkIsExist(SpiderRecordDO.SpiderRecordTypeEnum.douban_movie, tagName)) {
                    continue;
                }

                SpiderRecordDO recordDO = new SpiderRecordDO();
                recordDO.setType(SpiderRecordDO.SpiderRecordTypeEnum.douban_movie.getValue());
                recordDO.setTagName(tagName);
                recordDO.setCreatedTime(new Date());
                spiderRecordMapper.addData(recordDO);
            }
        }
    }

    @Override
    public void execute() throws Exception {

        hotTagsSpider();
        allHotTagsSpider();
        log.info("proccess-over-{}", this.getClass().toString());
    }

    @Override
    public int getTimeInterval() {
        return 0;
    }

}
