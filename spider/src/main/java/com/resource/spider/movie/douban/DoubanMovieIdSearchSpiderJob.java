package com.resource.spider.movie.douban;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
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
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
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
        movieSearchAPI1(
                        "http://api.douban.com/v2/movie/search?tag=%s&apikey=049d5a53f9bfebf30dd80efbed3fc3d9&count=%s&start=%s"),
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
    public int getTimeInterval() {
        return 4000;
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

            if (offsetValue > currentOffset) {
                urlsMapping.put(key, offsetValue);
                currentOffset = offsetValue;
            }
        }

        List<SpiderRecordDO> spiderRecrods = spiderRecordMapper.getRecordByType(SpiderRecordDO.SpiderRecordTypeEnum.douban_movie.getValue());
        for (SpiderRecordDO spiderRecordDO : spiderRecrods) {

            List<ResSpiderInfoModel> list = new LinkedList<ResSpiderInfoModel>();

            StringBuilder keyBuf = new StringBuilder();
            String key = keyBuf.append(MovieIdApiEnum.movieSearchAPI1.name()).append("$_$").append(spiderRecordDO.getTagName()).toString();
            // 1
            Integer value = urlsMapping.get(key);
            ResSpiderInfoModel spiderInfo1 = new ResSpiderInfoModel();
            spiderInfo1.setApi(MovieIdApiEnum.movieSearchAPI1.name());
            spiderInfo1.setOffset((null == value ? 0 : value));
            spiderInfo1.setEnd(true);
            list.add(spiderInfo1);

            // 2
            key = keyBuf.append(MovieIdApiEnum.movieSearchAPI2.name()).append("$_$").append(spiderRecordDO.getTagName()).toString();
            value = urlsMapping.get(key);
            ResSpiderInfoModel spiderInfo2 = new ResSpiderInfoModel();
            spiderInfo2.setApi(MovieIdApiEnum.movieSearchAPI2.name());
            spiderInfo2.setOffset((null == value ? 0 : value));
            spiderInfo2.setEnd(true);
            list.add(spiderInfo2);

            // 3
            key = keyBuf.append(MovieIdApiEnum.movieSearchAPI3.name()).append("$_$").append(spiderRecordDO.getTagName()).toString();
            value = urlsMapping.get(key);
            ResSpiderInfoModel spiderInfo3 = new ResSpiderInfoModel();
            spiderInfo3.setApi(MovieIdApiEnum.movieSearchAPI3.name());
            spiderInfo3.setOffset((null == value ? 0 : value));
            spiderInfo3.setEnd(true);
            list.add(spiderInfo3);

            spiderRecordDO.setSpiderDate(new Date());
            spiderRecordDO.setSpiderInfo(JSON.toJSONString(list));

            spiderRecordMapper.updateData(spiderRecordDO);
        }
    }

    @Override
    public void execute() throws Exception {

        tempInit();

        //
        // List<SpiderRecordDO> spiderRecrods =
        // spiderRecordMapper.getRecordByType(SpiderRecordDO.SpiderRecordTypeEnum.douban_movie.getValue());
        //
        // for (SpiderRecordDO spiderRecordDO : spiderRecrods) {
        //
        // String spiderInfo = spiderRecordDO.getSpiderInfo();
        // if (StringUtils.isBlank(spiderInfo)) {
        // continue;
        // }
        // Map<String, ResSpiderInfoModel> mapping = new HashMap<String, ResSpiderInfoModel>();
        // List<ResSpiderInfoModel> spiderInfoModels = JSON.parseArray(spiderInfo, ResSpiderInfoModel.class);
        // for (ResSpiderInfoModel resSpiderInfoModel : spiderInfoModels) {
        // mapping.put(resSpiderInfoModel.getApi(), resSpiderInfoModel);
        // }
        //
        // String keyword = URLEncoder.encode(spiderRecordDO.getTagName(), "utf-8");
        //
        // for (MovieIdApiEnum apiEnum : MovieIdApiEnum.values()) {
        //
        // ResSpiderInfoModel resSpiderInfoModel = mapping.get(apiEnum.name());
        //
        // int length = 100, offset = resSpiderInfoModel.getOffset();
        //
        // if (resSpiderInfoModel.isEnd()) {
        // continue;
        // }
        //
        // while (true) {
        //
        // resSpiderInfoModel.setOffset(offset);
        //
        // String url = String.format(apiEnum.getValue(), keyword, length, offset);
        // log.info("process-url:[{}]", url);
        // JSONObject jsonObjectValue = getJSONData(url);
        // if (null == jsonObjectValue) {
        // break;
        // }
        //
        // JSONArray valueArray = jsonObjectValue.getJSONArray("subjects");
        // if (CollectionUtils.isEmpty(valueArray)) {
        // break;
        // }
        //
        // List<Long> movieIds = new LinkedList<Long>();
        // for (Object object : valueArray) {
        // JSONObject valueObject = (JSONObject) object;
        // if (null == valueArray) {
        // continue;
        // }
        // String did = valueObject.getString("id");
        // if (StringUtils.isBlank(did)) {
        // continue;
        // }
        // did = did.trim();
        // if (StringUtils.isNumeric(did)) {
        // movieIds.add(Long.valueOf(did));
        // }
        // }
        // try {
        // resMovieService.addMovieList(movieIds, DataStatus.doubanMovieId.getValue());
        // // update
        // spiderRecordMapper.updateData(spiderRecordDO);
        // } catch (Exception e) {
        // log.error("addMovieList-fail url[{}]", url, e);
        // }
        //
        // offset += length;
        // }
        // try {
        // spiderRecordMapper.updateData(spiderRecordDO);
        // } catch (Exception e) {
        // log.error("updateData-fail id[{}] data[{}]", spiderRecordDO.getId(), JSON.toJSON(spiderInfoModels));
        // }
        // }
        // }
        log.info("proccess-over-{}", this.getClass().toString());
    }
}
