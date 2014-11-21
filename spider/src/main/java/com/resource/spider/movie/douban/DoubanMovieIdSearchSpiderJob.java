package com.resource.spider.movie.douban;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.codec.Charsets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.resource.spider.movie.AbstractDoubanMovieSpider;
import com.resources.common.StringUtil;
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

    private final static Logger log = LoggerFactory.getLogger(DoubanMovieIdSearchSpiderJob.class);

    public static enum MovieIdApiEnum {

        // "http://api.douban.com/v2/movie/search?tag=%s&apikey=049d5a53f9bfebf30dd80efbed3fc3d9&count=%s&start=%s"

        movieSearchAPI1("http://api.douban.com/v2/movie/search?tag=%s&count=%s&start=%s"),
        //
        movieSearchAPI2(
                        "http://movie.douban.com/j/search_subjects?type=movie&tag=%s&sort=time&page_limit=%s&page_start=%s"),
        //
        movieSearchAPI3(
                        "http://movie.douban.com/j/search_subjects?type=tv&tag=%s&sort=time&page_limit=%s&page_start=%s");

        private final String value;

        private MovieIdApiEnum(String value){
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    @Resource
    private ResMovieService    resMovieService;

    @Resource
    private SpiderRecordMapper spiderRecordMapper;

    @Override
    public int getConnectTimeout() {
        return 2000;
    }

    @Override
    public int getTimeInterval() {
        return 8000;
    }

    @Override
    public int getReadTimeout() {
        return 6000;
    }

    public void tempInit() throws IOException {
        Path path = FileSystems.getDefault().getPath("/Users/zhiwenmizw/work/resources/output/spider",
                                                     "db-movie-ids.log-2014-11-20.log");
        List<String> lines = Files.readAllLines(path, Charsets.UTF_8);

        Map<String, Integer> urlsMapping = new HashMap<String, Integer>();

        for (String line : lines) {
            int index = line.indexOf("http://");
            if (-1 == index) {
                continue;
            }
            String url = line.substring(index, line.length() - 1);
            URL newURL = new URL(url);
            String[] fragment = StringUtils.split(newURL.getQuery(), "&");

            Map<String, String> params = new HashMap<String, String>();
            for (String string : fragment) {
                String param[] = StringUtils.split(string, "=");
                params.put(param[0], param[1]);
            }

            int currentOffset = 0, offsetValue = 0;
            String tag = URLDecoder.decode(params.get("tag"), "utf-8");
            StringBuilder keyBuf = new StringBuilder();

            if (url.startsWith("http://api.douban.com/v2/movie/search")) {
                // movie
                offsetValue = StringUtil.stringToInt(params.get("start"));
                keyBuf.append(MovieIdApiEnum.movieSearchAPI1.name()).append("$_$").append(tag);
            } else if (url.startsWith("http://movie.douban.com/j/search_subjects?type=movie")) {
                // movie
                offsetValue = StringUtil.stringToInt(params.get("page_start"));
                keyBuf.append(MovieIdApiEnum.movieSearchAPI2.name()).append("$_$").append(tag);
            } else {
                // tv
                offsetValue = StringUtil.stringToInt(params.get("page_start"));
                keyBuf.append(MovieIdApiEnum.movieSearchAPI3.name()).append("$_$").append(tag);
            }
            String key = keyBuf.toString();

            if (offsetValue >= currentOffset) {
                urlsMapping.put(key, offsetValue);
                currentOffset = offsetValue;
            }
        }

        List<SpiderRecordDO> spiderRecrods = spiderRecordMapper.getRecordByType(SpiderRecordDO.SpiderRecordTypeEnum.douban_movie.getValue());
        for (SpiderRecordDO spiderRecordDO : spiderRecrods) {

            StringBuilder keyBuf = new StringBuilder();

            List<ResSpiderInfoModel> list = new LinkedList<ResSpiderInfoModel>();

            String key = keyBuf.append(MovieIdApiEnum.movieSearchAPI1.name()).append("$_$").append(spiderRecordDO.getTagName()).toString();
            // 1
            Integer value = urlsMapping.get(key);
            ResSpiderInfoModel spiderInfo1 = new ResSpiderInfoModel();
            spiderInfo1.setApi(MovieIdApiEnum.movieSearchAPI1.name());
            spiderInfo1.setEnd(true);
            if (null == value) {
                value = 0;
                spiderInfo1.setEnd(false);
            }
            spiderInfo1.setOffset(value);
            list.add(spiderInfo1);

            // 2
            keyBuf = new StringBuilder();
            key = keyBuf.append(MovieIdApiEnum.movieSearchAPI2.name()).append("$_$").append(spiderRecordDO.getTagName()).toString();
            value = urlsMapping.get(key);
            ResSpiderInfoModel spiderInfo2 = new ResSpiderInfoModel();
            spiderInfo2.setApi(MovieIdApiEnum.movieSearchAPI2.name());
            spiderInfo2.setEnd(true);
            if (null == value) {
                value = 0;
                spiderInfo2.setEnd(false);
            }
            spiderInfo2.setOffset(value);
            list.add(spiderInfo2);

            // 3
            keyBuf = new StringBuilder();
            key = keyBuf.append(MovieIdApiEnum.movieSearchAPI3.name()).append("$_$").append(spiderRecordDO.getTagName()).toString();
            value = urlsMapping.get(key);
            ResSpiderInfoModel spiderInfo3 = new ResSpiderInfoModel();
            spiderInfo3.setApi(MovieIdApiEnum.movieSearchAPI3.name());
            spiderInfo3.setEnd(true);
            if (null == value) {
                value = 0;
                spiderInfo3.setEnd(false);
            }
            spiderInfo3.setOffset(value);

            list.add(spiderInfo3);

            spiderRecordDO.setSpiderDate(new Date());
            spiderRecordDO.setSpiderInfo(JSON.toJSONString(list));

            spiderRecordMapper.updateData(spiderRecordDO);
        }
    }

    @Override
    public void execute() throws Exception {

        List<SpiderRecordDO> spiderRecrods = spiderRecordMapper.getRecordByType(SpiderRecordDO.SpiderRecordTypeEnum.douban_movie.getValue());

        for (SpiderRecordDO spiderRecordDO : spiderRecrods) {

            Map<String, ResSpiderInfoModel> mapping = new HashMap<String, ResSpiderInfoModel>();

            List<ResSpiderInfoModel> spiderInfoModels;

            String spiderInfo = spiderRecordDO.getSpiderInfo();
            if (StringUtils.isNotBlank(spiderInfo)) {
                spiderInfoModels = JSON.parseArray(spiderInfo, ResSpiderInfoModel.class);
                for (ResSpiderInfoModel resSpiderInfoModel : spiderInfoModels) {
                    mapping.put(resSpiderInfoModel.getApi(), resSpiderInfoModel);
                }
            } else {
                spiderInfoModels = new LinkedList<ResSpiderInfoModel>();
            }

            String keyword = URLEncoder.encode(spiderRecordDO.getTagName(), "utf-8");

            for (MovieIdApiEnum apiEnum : MovieIdApiEnum.values()) {

                ResSpiderInfoModel resSpiderInfoModel = mapping.get(apiEnum.name());
                if (null == resSpiderInfoModel) {
                    resSpiderInfoModel = new ResSpiderInfoModel();
                    resSpiderInfoModel.setApi(apiEnum.name());
                    spiderInfoModels.add(resSpiderInfoModel);
                }

                int length = 100, offset = resSpiderInfoModel.getOffset();

                if (resSpiderInfoModel.isEnd()) {
                    continue;
                }

                while (true) {

                    resSpiderInfoModel.setOffset(offset);

                    String url = String.format(apiEnum.getValue(), keyword, length, offset);
                    log.info("process-url:[{}]", url);
                    JSONObject jsonObjectValue = getJSONData(url);
                    if (null == jsonObjectValue) {
                        resSpiderInfoModel.setEnd(false);
                        break;
                    }

                    JSONArray valueArray = jsonObjectValue.getJSONArray("subjects");
                    if (CollectionUtils.isEmpty(valueArray)) {
                        resSpiderInfoModel.setEnd(true);
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
                        resMovieService.addMovieList(movieIds, DataStatus.doubanMovieId.getValue());
                        spiderRecordDO.setSpiderInfo(JSON.toJSONString(spiderInfoModels));
                        spiderRecordDO.setSpiderDate(new Date());
                        spiderRecordMapper.updateData(spiderRecordDO);
                    } catch (Exception e) {
                        log.error("addMovieList-fail url[{}]", url, e);
                    }

                    offset += length;
                }
                try {
                    spiderRecordDO.setSpiderInfo(JSON.toJSONString(spiderInfoModels));
                    spiderRecordDO.setSpiderDate(new Date());
                    spiderRecordMapper.updateData(spiderRecordDO);
                } catch (Exception e) {
                    log.error("updateData-fail id[{}] data[{}]", spiderRecordDO.getId(), JSON.toJSON(spiderInfoModels));
                }
            }
        }
        log.info("proccess-over-{}", this.getClass().toString());
    }
}
