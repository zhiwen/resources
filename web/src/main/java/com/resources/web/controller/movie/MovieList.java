package com.resources.web.controller.movie;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.resources.common.BizType;
import com.resources.dal.module.MovieDO;
import com.resources.dal.module.ResourceInfoDO;
import com.resources.service.MovieService;
import com.resources.service.ResourceInfoService;

@Controller
@RequestMapping("/movie")
public class MovieList {

    private final static Logger log      = LoggerFactory.getLogger(MovieList.class);

    private final int           pageSize = 50;

    @Resource
    private ResourceInfoService resourceInfoService;

    @Resource
    private MovieService        movieService;

    @RequestMapping(value = "list", method = { RequestMethod.GET })
    public String list(Model model) {
        return this.list(model, 0, 0, 0, 1, 0);
    }

    @RequestMapping(value = "list/{cid}-{country}-{showTime}-{page}-{order}", method = { RequestMethod.GET })
    public String list(Model model, @PathVariable("cid") long cid, @PathVariable("showTime") long showTime,
                       @PathVariable("country") int country, @PathVariable("page") int page,
                       @PathVariable("order") int order) {

        int offset = (Math.max(page, 1) - 1) * pageSize;

        List<MovieDO> movieList = movieService.getMovieOrderByCreated(cid, country, showTime, offset, pageSize);

        List<Long> resIdList = new LinkedList<Long>();
        for (MovieDO movieDO : movieList) {
            resIdList.add(movieDO.getResId());
        }
        Map<Long, ResourceInfoDO> mappingedResource = new HashMap<Long, ResourceInfoDO>();
        List<ResourceInfoDO> resourceList = resourceInfoService.getResourceInfoByIds(resIdList);
        for (ResourceInfoDO resourceInfoDO : resourceList) {
            mappingedResource.put(resourceInfoDO.getId(), resourceInfoDO);
        }

        model.addAttribute("movieList", movieList);
        model.addAttribute("mappingedResource", mappingedResource);

        model.addAllAttributes(getMovieCategorys());

        model.addAttribute("cid", cid).addAttribute("showTime", showTime);
        model.addAttribute("country", country).addAttribute("page", page);
        return "movie/list";
    }

    public Map<String, Map<String, String>> getMovieCategorys() {

        Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();

        Map<String, String> movieTypes = new LinkedHashMap<String, String>();
        result.put("movieTypes", movieTypes);
        String[] typesName = new String[] { "全部", "偶像", "言情", "军旅", "武侠", "历史", "神话", "古装", "战争", "警匪", "悬疑", "伦理",
                "科幻", "都市", "喜剧", "谍战", "宫廷", "年代", "农村", "情景", "传记", "动作", "惊悚", "魔幻", "罪案", "时装", "商战", "儿童", "网络剧",
                "小清新", "重口味", "撒狗血", "雷剧", "励志" };
        for (int i = 0; i < typesName.length; i++) {
            movieTypes.put(String.valueOf(i), typesName[i]);
        }

        Map<String, String> movieCountrys = new LinkedHashMap<String, String>();
        result.put("movieCountrys", movieCountrys);

        String[] countrysName = new String[] { "全部", "中国内地", "香港", "台湾", "韩国", "新加坡", "美国", "英国", "日本", "印度", "其它" };
        for (int i = 0; i < countrysName.length; i++) {
            movieCountrys.put(String.valueOf(i), countrysName[i]);
        }

        Map<String, String> movieShowTimes = new LinkedHashMap<String, String>();
        movieShowTimes.put("0", "全部");
        result.put("movieShowTimes", movieShowTimes);
        for (int i = 2014; i > 1997; i--) {
            String item = String.valueOf(i);
            movieShowTimes.put(item, item);
        }
        return result;

    }

   
}
