package com.resource.spider.movie.douban;

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
import com.resources.common.BizTypeEnum;
import com.resources.common.ResKVTypeEnum;
import com.resources.common.StringUtil;
import com.resources.dal.dataobject.ResKVDO;
import com.resources.dal.dataobject.ResMovieDO;
import com.resources.dal.dataobject.ResTagDO;
import com.resources.service.ResKVService;
import com.resources.service.ResMovieService;
import com.resources.service.ResTagService;
import com.resources.service.ResURLService;

/**
 * 部分数据通过api获取（http://api.douban.com/v2/movie/subject）
 * 
 * @author zhiwenmizw
 */
@Service
public class DoubanMovieApiSpiderJob extends AbstractDoubanMovieSpider {

    private final static Logger log              = LoggerFactory.getLogger(DoubanMovieApiSpiderJob.class);

    public String               doubanSubjectApi = "http://api.douban.com/v2/movie/subject/%s?apikey=049d5a53f9bfebf30dd80efbed3fc3d9";

    @Resource
    private ResMovieService     resMovieService;

    @Resource
    private ResTagService       resTagService;

    @Resource
    private ResKVService        resKVService;

    @Resource
    private ResURLService       resURLService;

    @Override
    public int getTimeInterval() {
        return 1000;
    }

    private List<Long> getTagIdList(long did, JSONArray arrays) {
        if (CollectionUtils.isEmpty(arrays)) {
            return Collections.emptyList();
        }
        List<Long> tagIds = new LinkedList<Long>();
        for (Object object : arrays) {
            if (null == object) {
                continue;
            }
            String tagName = StringUtils.trim(object.toString());
            ResTagDO value = new ResTagDO();
            value.setBizType(BizTypeEnum.MOVIE);
            value.setCid(0);
            value.setTagName(tagName);
            value.setCreatedTime(new Date());
            ResTagDO resTagDO = resTagService.getDataIfNotExistAdd(value);
            if (null != resTagDO) {
                tagIds.add(resTagDO.getId());
            } else {
                log.error("array-get-id not found did:[{}] value:[{}] arrays:[{}]", new Object[] { did, value, arrays });
            }
        }
        return tagIds;
    }

    private long addResKV(ResMovieDO resMovieDO, ResKVTypeEnum type, String value) {
        ResKVDO resKVDO = new ResKVDO();
        resKVDO.setResKey(String.valueOf(resMovieDO.getId()));
        resKVDO.setCreatedTime(resMovieDO.getCreatedTime());
        resKVDO.setType(type);
        resKVDO.setResValue(value);
        try {
            resKVService.addData(resKVDO);
        } catch (Exception e) {
            log.error("addResKeyValue-fail did:[{}]", resMovieDO.getDid(), e);
        }
        return resKVDO.getId();
    }

    public void parseAndSave(ResMovieDO resMovieDO, JSONObject valueObject) {

        int reviewCount = StringUtil.stringToInt(valueObject.getString("reviews_count"));
        resMovieDO.setReviewCount(reviewCount);
        int wishCount = StringUtil.stringToInt(valueObject.getString("wish_count"));
        resMovieDO.setWishCount(wishCount);
        int collectCount = StringUtil.stringToInt(valueObject.getString("collect_count"));
        resMovieDO.setCollectCount(collectCount);

        String mobileUrl = valueObject.getString("mobile_url");
        if (StringUtils.isNotBlank(mobileUrl)) {
            resMovieDO.setMobileUrl(StringUtils.trim(mobileUrl));
        }
        String title = valueObject.getString("title");
        if (StringUtils.isNotBlank(title)) {
            resMovieDO.setTitle(StringUtils.trim(title));
        }

        List<Long> countryIds = getTagIdList(resMovieDO.getDid(), valueObject.getJSONArray("countries"));
        if (CollectionUtils.isNotEmpty(countryIds)) {
            resMovieDO.setCountryIds(countryIds);
        }

        String originalTitle = valueObject.getString("original_title");
        if (StringUtils.isNotBlank(originalTitle)) {
            resMovieDO.setOriginalTitle(StringUtils.trim(originalTitle));
        }

        int commentCount = StringUtil.stringToInt(valueObject.getString("comments_count"));
        resMovieDO.setCommentCount(commentCount);
        int ratingCount = StringUtil.stringToInt(valueObject.getString("ratings_count"));
        resMovieDO.setRatingCount(ratingCount);

        String doubanSite = valueObject.getString("douban_site");
        if (StringUtils.isNotBlank(doubanSite)) {
            resMovieDO.setDoubanSite(StringUtils.trim(doubanSite));
        }

        JSONArray aka = valueObject.getJSONArray("aka");
        resMovieDO.setAka(StringUtil.toStringList(aka));

        // rating
        JSONObject rating = valueObject.getJSONObject("rating");
        if (null != rating && !rating.isEmpty()) {
            long id = addResKV(resMovieDO, ResKVTypeEnum.movie_rating, rating.toJSONString());
            if (id == 0) {
                return;
            }
            resMovieDO.setRatingId(id);
        }
        // summary
        String summary = valueObject.getString("summary");
        if (StringUtils.isNotBlank(summary)) {
            long id = addResKV(resMovieDO, ResKVTypeEnum.movie_summay, StringUtils.trim(summary));
            resMovieDO.setSummaryId(id);
            if (id == 0) {
                return;
            }
        }

        // 封面图
        JSONObject converImages = valueObject.getJSONObject("images");
        if (null != converImages && !converImages.isEmpty()) {
            long id = addResKV(resMovieDO, ResKVTypeEnum.movie_images, converImages.toJSONString());
            resMovieDO.setCoverImagesId(id);
            if (id == 0) {
                return;
            }
        }

        resMovieDO.setDataStatus(DataStatus.doubanMovieApi.getValue());
        try {
            resMovieService.updateMovie(resMovieDO);
        } catch (Exception e) {
            log.error("updateMovie-fail movieDO:[{}]", resMovieDO, e);
        }
    }

    @Override
    public void execute() throws Exception {
        int offset = 0, length = 1000;
        while (true) {
            List<ResMovieDO> movieList = resMovieService.getMovieByPaginatorWithStatus(DataStatus.doubanMovieAbstract.getValue(),
                                                                                       offset, length);
            if (CollectionUtils.isEmpty(movieList)) {
                break;// the end
            }

            for (ResMovieDO resMovieDO : movieList) {
                String qulifySubjectUrl = String.format(doubanSubjectApi, resMovieDO.getDid());
                log.info("process-url:[{}]", qulifySubjectUrl);
                JSONObject valueObject = getJSONData(qulifySubjectUrl);
                if (null == valueObject || valueObject.isEmpty()) {
                    continue;
                }
                parseAndSave(resMovieDO, valueObject);
            }
        }
        log.info("proccess-over-{}", this.getClass().toString());
    }
}
