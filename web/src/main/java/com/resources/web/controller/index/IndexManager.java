package com.resources.web.controller.index;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.resources.common.BizType;
import com.resources.dal.module.ResourceInfoDO;
import com.resources.service.MovieService;
import com.resources.service.ResourceInfoService;

@Controller
public class IndexManager {

    private final static Logger log = LoggerFactory.getLogger(IndexManager.class);

    @Resource
    private ResourceInfoService resourceInfoService;

    @Resource
    private MovieService        movieService;

    @RequestMapping(value = { "/", "/index", "/index.htm", "/index.html" }, method = { RequestMethod.GET })
    public String goHome() {
        return "index";
    }

    /**
     * 生成首页.vm
     * 
     * @return
     */
    @RequestMapping(value = "/index_build", method = { RequestMethod.GET })
    public String buildHome(Model model) {

        /**
         * <pre>
         * 1、头部大banner
         * 2、最热搜索词
         * 3、热门资源及数字（按照分类，提取相应下载次数最多的数据）及热门资源的数量
         * 4、右边的广告位数据
         * 3、热新资源及数字（按照分类，提取最近更新的的数据）及最新资源的数量
         * 6、左边广告位
         * 7、根据类别提取各浏览量最大的12张图片
         * 8、类别 + 最热的文库 + 最新的文庘
         * 9、最热的商品
         * 9、最热的游戏 + 最新的游戏
         * </pre>
         */
        List<ResourceInfoDO> resourceInfoList = resourceInfoService.getOrderResourceInfoByViews(0, BizType.MOVIE, 0, 10);

        model.addAttribute("resourceInfoList", resourceInfoList);
        return "index";
    }
}
