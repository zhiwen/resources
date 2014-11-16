package com.resource.spider.resources.movie;

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
import com.resource.spider.resources.movie.DoubanListSpiderJob.DataStatus;
import com.resources.common.BizTypeEnum;
import com.resources.common.IOUtil;
import com.resources.common.MovieSubTypeEnum;
import com.resources.dal.dataobject.ResMovieDO;
import com.resources.dal.dataobject.ResTagDO;
import com.resources.service.ResMovieService;
import com.resources.service.ResTagService;

@Service
public class DoubanSubjectAbsSpiderJob implements SpiderJob {

    private final static Logger log              = LoggerFactory.getLogger(DoubanSubjectAbsSpiderJob.class);

    public String               doubanSubjectAbs = "http://movie.douban.com/j/subject_abstract?subject_id=%s";
    public int                  timeInterval     = 5000;

    @Resource
    private ResMovieService     resMovieService;

    @Resource
    private ResTagService       resTagService;

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
        // actors[] 主演、演员
        // directors[] 导演
        // types[] 类型【喜剧、战争】
        // duration 片长
        // episodes_count = 集数
        // -----is_tv 是不是电视剧 true/false
        // playable 是否可播放 true/false
        // -----region 国家
        // -----star 星级
        // release_year 发行年代
        // subtype Movie/TV

        // actors
        List<Long> castIds = getTagIdList(resMovieDO.getDid(), valueObject.getJSONArray("actors"));
        if (CollectionUtils.isNotEmpty(castIds)) {
            resMovieDO.setCastIds(castIds);
        } else {
            log.warn("castIds-is-empty: did[{}]", resMovieDO.getDid());
        }

        // directors
        List<Long> directorIds = getTagIdList(resMovieDO.getDid(), valueObject.getJSONArray("directors"));
        if (CollectionUtils.isNotEmpty(directorIds)) {
            resMovieDO.setDirectorIds(directorIds);
        } else {
            log.warn("directors-is-empty: did[{}]", resMovieDO.getDid());
        }

        // types
        List<Long> genreIds = getTagIdList(resMovieDO.getDid(), valueObject.getJSONArray("types"));
        if (CollectionUtils.isNotEmpty(genreIds)) {
            resMovieDO.setGenreIds(genreIds);
        } else {
            log.warn("genres-is-empty: did[{}]", resMovieDO.getDid());
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

        resMovieDO.setDataStatus(DataStatus.SubjectAbs.value);
        resMovieService.updateMovie(resMovieDO);
    }

    @Override
    public void execute() throws Exception {
        int offset = 0, length = 1000;
        while (true) {
            List<ResMovieDO> movieList = resMovieService.getMovieByPaginatorWithStatus(DataStatus.TagList.value,
                                                                                       offset, length);
            if (CollectionUtils.isEmpty(movieList)) {
                break;// the end
            }

            for (ResMovieDO resMovieDO : movieList) {
                String qulifySubjectUrl = String.format(doubanSubjectAbs, resMovieDO.getDid());
                JSONObject valueObject = getData(qulifySubjectUrl);
                if (null == valueObject) {
                    continue;
                }
                valueObject = valueObject.getJSONObject("subject");

                if (null == valueObject || valueObject.isEmpty()) {
                    continue;
                }
                parseAndSave(resMovieDO, valueObject);
            }
        }
    }
}
