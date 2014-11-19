package com.resource.spider.movie.douban;

import java.net.URLEncoder;
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
 * 电影tag-list接口
 * 
 * @author zhiwenmizw
 */
@Service
public class DoubanMovieIdSearchSpiderJob extends AbstractDoubanMovieSpider {

    private final static Logger log                = LoggerFactory.getLogger(DoubanMovieIdSearchSpiderJob.class);

    private final String[]      movieTagSearchApis = {
            "http://api.douban.com/v2/movie/search?tag=%s?count=%s&start=%s&apikey=049d5a53f9bfebf30dd80efbed3fc3d9",
            "http://movie.douban.com/j/search_subjects?type=movie&tag=%s&sort=time&page_limit=%s&page_start=%s",
            "http://movie.douban.com/j/search_subjects?type=tv&tag=%s&sort=time&page_limit=%s&page_start=%s" };

    @Resource
    private ResMovieService     resMovieService;

    @Resource
    private SpiderRecordMapper  spiderRecordMapper;

    @Override
    public int getTimeInterval() {
        return 2000;
    }

    @Override
    public void execute() throws Exception {

        List<SpiderRecordDO> spiderRecrods = spiderRecordMapper.getRecordByType(SpiderRecordDO.SpiderRecordTypeEnum.douban_movie.getValue());

        for (SpiderRecordDO spiderRecordDO : spiderRecrods) {

            int length = 100, offset = 0, eatCount = spiderRecordDO.getEatNumber();

            for (String api : movieTagSearchApis) {

                while (true) {
                    String url = String.format(api, URLEncoder.encode(spiderRecordDO.getTagName(), "utf-8"), length,
                                               offset);
                    log.info("process-url:[{}]", url);
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
                        eatCount += resMovieService.addMovieList(movieIds, DataStatus.doubanMovieId.getValue());
                        // update
                        spiderRecordDO.setEatNumber(eatCount);
                        spiderRecordMapper.updateData(spiderRecordDO);
                    } catch (Exception e) {
                        log.error("addMovieList-fail url[{}]", url, e);
                    }

                    offset += length;
                }
            }
        }
        log.info("proccess-over-{}", this.getClass().toString());
    }
}
