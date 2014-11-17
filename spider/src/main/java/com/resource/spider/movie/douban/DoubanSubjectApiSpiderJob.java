package com.resource.spider.movie.douban;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.resource.spider.SpiderJob;
import com.resource.spider.http.HttpURLConnectionWrapper;
import com.resource.spider.movie.douban.DoubanListSpiderJob.DataStatus;
import com.resources.common.BizTypeEnum;
import com.resources.common.IOUtil;
import com.resources.common.ResKVTypeEnum;
import com.resources.common.StringUtil;
import com.resources.dal.dataobject.ResKVDO;
import com.resources.dal.dataobject.ResMovieDO;
import com.resources.dal.dataobject.ResTagDO;
import com.resources.service.ResKVService;
import com.resources.service.ResMovieService;
import com.resources.service.ResTagService;
import com.resources.service.ResURLService;

@Service
public class DoubanSubjectApiSpiderJob implements SpiderJob {

    private final static Logger log              = LoggerFactory.getLogger(DoubanSubjectApiSpiderJob.class);

    public String               doubanSubjectApi = "http://api.douban.com/v2/movie/subject/%s";
    public int                  timeInterval     = 5000;

    @Resource
    private ResMovieService     resMovieService;

    @Resource
    private ResTagService       resTagService;

    @Resource
    private ResKVService        resKVService;

    @Resource
    private ResURLService       resURLService;

    public JSONObject getData(String url) {
        HttpURLConnectionWrapper con = null;
        try {
            Thread.sleep(timeInterval);
            con = new HttpURLConnectionWrapper(new URL(url));
            InputStream in = con.getInputStream();
            ByteArrayOutputStream bos = IOUtil.getByteData(in);
            return JSON.parseObject(bos.toString("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
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

    public void parseAndSave(ResMovieDO resMovieDO, JSONObject valueObject) {

        int reviewCount = StringUtil.stringToInt(valueObject.getString("reviews_count"));
        resMovieDO.setReviewCount(reviewCount);
        int wishCount = StringUtil.stringToInt(valueObject.getString("wish_count"));
        resMovieDO.setWishCount(wishCount);
        int collectCount = StringUtil.stringToInt(valueObject.getString("collect_count"));
        resMovieDO.setCollectCount(collectCount);

        String mobileUrl = valueObject.getString("mobile_url");
        resMovieDO.setMobileUrl(StringUtils.trim(mobileUrl));
        String title = valueObject.getString("title");
        resMovieDO.setTitle(StringUtils.trim(title));

        List<Long> countryIds = getTagIdList(resMovieDO.getDid(), valueObject.getJSONArray("countries"));
        if (CollectionUtils.isNotEmpty(countryIds)) {
            resMovieDO.setCountryIds(countryIds);
        }

        String originalTitle = valueObject.getString("original_title");
        resMovieDO.setOriginalTitle(StringUtils.trim(originalTitle));

        int commentCount = StringUtil.stringToInt(valueObject.getString("comments_count"));
        resMovieDO.setCommentCount(commentCount);
        int ratingCount = StringUtil.stringToInt(valueObject.getString("ratings_count"));
        resMovieDO.setRatingCount(ratingCount);

        String doubanSite = valueObject.getString("douban_site");
        resMovieDO.setDoubanSite(StringUtils.trim(doubanSite));

        JSONArray aka = valueObject.getJSONArray("aka");
        resMovieDO.setAka(StringUtil.toStringList(aka));

        ResKVDO resKVDO = new ResKVDO();
        resKVDO.setResKey(String.valueOf(resMovieDO.getId()));
        resKVDO.setCreatedTime(resMovieDO.getCreatedTime());

        // rating
        JSONObject rating = valueObject.getJSONObject("rating");
        if (null != rating && !rating.isEmpty()) {
            resKVDO.setType(ResKVTypeEnum.movie_rating);
            resKVDO.setResValue(rating.toJSONString());
            resKVService.addData(resKVDO);
            resMovieDO.setRatingId(resKVDO.getId());
        }
        // summary
        String summary = valueObject.getString("summary");
        if (null != summary) {
            resKVDO.setType(ResKVTypeEnum.movie_summay);
            resKVDO.setResValue(StringUtils.trim(summary));
            resKVService.addData(resKVDO);
            resMovieDO.setRatingId(resKVDO.getId());
        }

        // 封面图
        JSONObject converImages = valueObject.getJSONObject("images");
        if (null != converImages && !converImages.isEmpty()) {
            resKVDO.setType(ResKVTypeEnum.movie_images);
            resKVDO.setResValue(converImages.toJSONString());
            resKVService.addData(resKVDO);
            resMovieDO.setCoverImagesId(resKVDO.getId());
        }
        resKVDO = null;

        resMovieDO.setDataStatus(DataStatus.SubjectApi.value);
        resMovieService.updateMovie(resMovieDO);
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
                String qulifySubjectUrl = String.format(doubanSubjectApi, resMovieDO.getDid());
                JSONObject valueObject = getData(qulifySubjectUrl);
                if (null == valueObject || valueObject.isEmpty()) {
                    continue;
                }
                parseAndSave(resMovieDO, valueObject);
            }
        }
    }
}
