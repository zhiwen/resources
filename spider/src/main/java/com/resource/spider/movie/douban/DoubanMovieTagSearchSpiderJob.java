package com.resource.spider.movie.douban;

import java.net.URLEncoder;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.resource.spider.movie.AbstractDoubanMovieSpider;
import com.resources.dal.dataobject.SpiderRecordDO;
import com.resources.dal.mapper.SpiderRecordMapper;
import com.resources.service.ResMovieService;

/**
 * <pre>
 * tag-list列表
 * http://api.douban.com/v2/movie/search?tag=%E7%88%B1%E6%83%85?apikey=049d5a53f9bfebf30dd80efbed3fc3d9
 * </pre>
 * 
 * @author zhiwenmizw
 */
@Service
public class DoubanMovieTagSearchSpiderJob extends AbstractDoubanMovieSpider {

    private final static Logger log                = LoggerFactory.getLogger(DoubanMovieTagSearchSpiderJob.class);

    public String               doubanTagSearchApi = "http://api.douban.com/v2/movie/search?tag=%s?apikey=049d5a53f9bfebf30dd80efbed3fc3d9";
    public int                  length             = 20;

    @Resource
    private SpiderRecordMapper  spiderRecordMapper;

    @Resource
    private ResMovieService     resMovieService;

    @Override
    public int getTimeInterval() {
        return 3000;
    }

    public void parsePageList(SpiderRecordDO spiderRecordDO) throws Exception {

        String encTagName = URLEncoder.encode(spiderRecordDO.getTagName(), "utf-8");
        String qulifyURL = String.format(doubanTagSearchApi, encTagName);

        JSONObject jsonObject = getJSONData(qulifyURL);
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
