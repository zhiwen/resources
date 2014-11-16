package com.resource.spider.resources.movie;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.resource.spider.SpiderJob;
import com.resource.spider.http.HttpURLConnectionWrapper;
import com.resource.spider.resources.movie.DoubanListSpiderJob.DataStatus;
import com.resources.common.IOUtil;
import com.resources.dal.dataobject.ResMovieDO;
import com.resources.service.ResMovieService;

@Service
public class DoubanSubjectAbsSpiderJob implements SpiderJob {

    public String           doubanSubjectAbs = "http://movie.douban.com/j/subject_abstract?subject_id=%s";
    public int              timeInterval     = 5000;

    @Resource
    private ResMovieService resMovieService;

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
                String qulifySubjectUrl = String.format(doubanSubjectAbs, resMovieDO.getDid());
                JSONObject valueObject = getData(qulifySubjectUrl);
                valueObject = valueObject.getJSONObject("subject");

                if (null == valueObject || valueObject.isEmpty()) {
                    continue;
                }

                // actors[] 主演
                // directors[] 导演
                // types[] 类型【喜剧、战争】
                // duration 片长
                // episodes_count = 集数
                // is_tv 是不是电视剧 true/false
                // playable 是否可播放 true/false
                // -----region 国家
                // star 星级
                // release_year 发行年代
                // subtype Movie/TV

                JSONArray actors = valueObject.getJSONArray("actors");

                resMovieDO.setDataStatus(DataStatus.SubjectApi.value);
                resMovieService.updateMovie(resMovieDO);
            }
        }
    }
}
