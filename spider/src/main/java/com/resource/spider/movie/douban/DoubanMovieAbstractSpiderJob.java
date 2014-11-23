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
import com.resources.common.MovieSubTypeEnum;
import com.resources.dal.dataobject.ResMovieDO;
import com.resources.dal.dataobject.ResTagDO;
import com.resources.service.ResMovieService;
import com.resources.service.ResTagService;

/**
 * 部分数据获取接口（@http://movie.douban.com/j/subject_abstract?subject_id=）
 * 
 * @author zhiwenmizw
 */
@Service
public class DoubanMovieAbstractSpiderJob extends AbstractDoubanMovieSpider {

    private final static Logger log              = LoggerFactory.getLogger(DoubanMovieAbstractSpiderJob.class);

    public String               doubanSubjectAbs = "http://movie.douban.com/j/subject_abstract?subject_id=%s";

    @Resource
    private ResMovieService     resMovieService;

    @Resource
    private ResTagService       resTagService;

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

    public void parseAndSave(ResMovieDO resMovieDO, JSONObject valueObject) {
        // actors
        List<Long> castIds = getTagIdList(resMovieDO.getDid(), valueObject.getJSONArray("actors"));
        if (CollectionUtils.isNotEmpty(castIds)) {
            resMovieDO.setCastIds(castIds);
        } else {
            log.info("castIds-is-empty: did[{}]", resMovieDO.getDid());
        }

        // directors
        List<Long> directorIds = getTagIdList(resMovieDO.getDid(), valueObject.getJSONArray("directors"));
        if (CollectionUtils.isNotEmpty(directorIds)) {
            resMovieDO.setDirectorIds(directorIds);
        } else {
            log.info("directors-is-empty: did[{}]", resMovieDO.getDid());
        }

        // types
        List<Long> genreIds = getTagIdList(resMovieDO.getDid(), valueObject.getJSONArray("types"));
        if (CollectionUtils.isNotEmpty(genreIds)) {
            resMovieDO.setGenreIds(genreIds);
        } else {
            log.info("genres-is-empty: did[{}]", resMovieDO.getDid());
        }

        // duration
        String durations = StringUtils.trim(valueObject.getString("duration"));
        if (StringUtils.isNotBlank(durations)) {
            resMovieDO.setDurations(durations);
        }

        // episodes_count
        String episodesCount = StringUtils.trim(valueObject.getString("episodes_count"));
        if (StringUtils.isNotBlank(episodesCount)) {
            if (StringUtils.isNumeric(episodesCount)) {
                resMovieDO.setEpisodeCount(Integer.valueOf(episodesCount));
            } else {
                log.error("episodesCount-not-an-number did[{}] episodesCount[{}]", resMovieDO.getDid(), episodesCount);
            }
        }

        // playable
        String playable = StringUtils.trim(valueObject.getString("playable"));
        if (StringUtils.isNotBlank(playable)) {
            Boolean bPlayable = Boolean.valueOf(playable);
            resMovieDO.setPlayable(bPlayable ? 1 : 0);
        }

        // release_year
        String releaseYear = StringUtils.trim(valueObject.getString("release_year"));
        if (StringUtils.isNotBlank(releaseYear)) {
            if (StringUtils.isNumeric(releaseYear)) {
                resMovieDO.setYear(releaseYear);
            } else {
                log.error("releaseYear-not-an-number did[{}] releaseYear[{}]", resMovieDO.getDid(), releaseYear);
            }
        }

        // subtype
        String subtype = StringUtils.trim(valueObject.getString("subtype"));
        if (StringUtils.isNotBlank(subtype)) {
            if (StringUtils.equalsIgnoreCase(subtype, MovieSubTypeEnum.movie.name())) {
                resMovieDO.setSubType(MovieSubTypeEnum.movie);
            } else if (StringUtils.equalsIgnoreCase(subtype, MovieSubTypeEnum.tv.name())) {
                resMovieDO.setSubType(MovieSubTypeEnum.tv);
            } else {
                log.error("unknow-subtype did[{}] releaseYear[{}]", resMovieDO.getDid(), subtype);
            }
        }

        resMovieDO.setDataStatus(DataStatus.doubanMovieAbstract.getValue());
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
            List<ResMovieDO> movieList = resMovieService.getMovieByPaginatorWithStatus(DataStatus.doubanMovieId.getValue(),
                                                                                       offset, length);
            if (CollectionUtils.isEmpty(movieList)) {
                break;// the end
            }

            for (ResMovieDO resMovieDO : movieList) {
                String qulifySubjectUrl = String.format(doubanSubjectAbs, resMovieDO.getDid());
                log.info("processor-url:[{}]", qulifySubjectUrl);
                JSONObject valueObject = getJSONData(qulifySubjectUrl);
                if (null == valueObject) {
                    continue;
                }
                valueObject = valueObject.getJSONObject("subject");

                if (null == valueObject || valueObject.isEmpty()) {
                    continue;
                }
                try {
                    parseAndSave(resMovieDO, valueObject);
                } catch (Exception e) {
                    log.error("processor-movie-fail did:[{}]", resMovieDO.getDid(), e);
                }
            }
            // offset += length;
        }
        log.info("proccess-over-{}", this.getClass().toString());
    }
}
