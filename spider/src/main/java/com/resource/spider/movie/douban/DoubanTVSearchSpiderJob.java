package com.resource.spider.movie.douban;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.resource.spider.movie.AbstractDoubanMovieSpider;
import com.resources.dal.dataobject.SpiderRecordDO;
import com.resources.dal.mapper.SpiderRecordMapper;
import com.resources.service.ResMovieService;

/**
 * 电影剧tag-list接口
 * 
 * @author zhiwenmizw
 */
@Service
public class DoubanTVSearchSpiderJob extends AbstractDoubanMovieSpider {

    private final static Logger log                 = LoggerFactory.getLogger(DoubanTVSearchSpiderJob.class);

    private final String        doubanSubjectSearch = "http://movie.douban.com/j/search_subjects?type=tv&tag=%s&sort=time&page_limit=%s&page_start=%s";

    public int                  timeInterval        = 5000;

    @Resource
    private ResMovieService     resMovieService;

    @Resource
    private SpiderRecordMapper  spiderRecordMapper;

    @Override
    public int getTimeInterval() {
        return 4000;
    }

    public List<SpiderRecordDO> preData() {

        List<SpiderRecordDO> spiderRecrods = spiderRecordMapper.getRecordByType(SpiderRecordDO.SpiderRecordTypeEnum.douban_tv.getValue());

        if (CollectionUtils.isNotEmpty(spiderRecrods)) {
            return spiderRecrods;
        }

        spiderRecrods = new LinkedList<SpiderRecordDO>();

        JSONObject jsonObject = getJSONData("http://movie.douban.com/j/search_tags?type=tv");
        if (null == jsonObject || jsonObject.isEmpty()) {
            return Collections.emptyList();
        }

        JSONArray tags = jsonObject.getJSONArray("tags");
        if (null == tags) {
            return spiderRecrods;
        }

        for (Object object : tags) {
            if (null == object) {
                continue;
            }

            SpiderRecordDO recordDO = new SpiderRecordDO();
            recordDO.setType(SpiderRecordDO.SpiderRecordTypeEnum.douban_tv.getValue());
            recordDO.setTagName(object.toString());
            recordDO.setCreatedTime(new Date());
            spiderRecordMapper.addData(recordDO);
            // news
            spiderRecrods.add(recordDO);
        }

        return spiderRecrods;
    }

    @Override
    public void execute() throws Exception {

        List<SpiderRecordDO> spiderRecrods = preData();

        for (SpiderRecordDO spiderRecordDO : spiderRecrods) {

            int length = 80, offset = 0, pageNumber = 0;

            if (spiderRecordDO.getEatNumber() > 0) {
                continue;
            }

            int eatCount = spiderRecordDO.getEatNumber();
            while (true) {
                String url = String.format(doubanSubjectSearch,
                                           URLEncoder.encode(spiderRecordDO.getTagName(), "utf-8"), length, offset);
                log.info("process-url:[{}]", url);
                ++pageNumber;
                JSONObject jsonObjectValue = getJSONData(url);
                if (null == jsonObjectValue) {
                    break;
                }

                JSONArray valueArray = jsonObjectValue.getJSONArray("subjects");
                if (CollectionUtils.isEmpty(valueArray)) {
                    break;
                }
                List<Long> movieIds = new LinkedList<Long>();
                for (Object object : valueArray) {
                    JSONObject valueObject = (JSONObject) object;
                    if (null == valueArray) {
                        continue;
                    }
                    String did = valueObject.getString("id");
                    if (StringUtils.isBlank(did)) {
                        continue;
                    }
                    did = did.trim();
                    if (StringUtils.isNumeric(did)) {
                        movieIds.add(Long.valueOf(did));
                    }
                }
                try {
                    eatCount += resMovieService.addMovieList(movieIds, DataStatus.TagList.value);
                    // update
                    spiderRecordDO.setPageNumber(pageNumber);
                    spiderRecordDO.setEatNumber(eatCount);
                    spiderRecordMapper.updateData(spiderRecordDO);
                } catch (Exception e) {
                    log.error("addMovieList-fail url[{}]", url, e);
                }

                offset += length;
            }
        }
    }
}
