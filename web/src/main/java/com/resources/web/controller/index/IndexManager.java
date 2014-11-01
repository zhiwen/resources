package com.resources.web.controller.index;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

import com.resources.common.BizType;
import com.resources.dal.module.ResourceInfoDO;
import com.resources.service.MovieService;
import com.resources.service.ResourceInfoService;

@Controller
public class IndexManager {

    private final static Logger  log = LoggerFactory.getLogger(IndexManager.class);

    @Resource
    private ResourceInfoService  resourceInfoService;

    @Resource
    private MovieService         movieService;

    @Resource
    private VelocityConfigurer   velocityConfigurer;

    @Resource
    private VelocityViewResolver viewResolver;

    @Resource
    HttpServletResponse          response;

    @RequestMapping(value = { "/", "/index", "/index.htm", "/index.html" }, method = { RequestMethod.GET })
    public @ResponseBody
    String goHome() {
        log.error(response.toString());

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

        List<ResourceInfoDO> hotMovieDLResList = resourceInfoService.getOrderResourceInfoByDownloads(0, BizType.MOVIE,
                                                                                                     0, 10);

        List<ResourceInfoDO> hotGameDLResList = resourceInfoService.getOrderResourceInfoByDownloads(0, BizType.GAME, 0,
                                                                                                    10);

        List<ResourceInfoDO> hotSoftwareDLResList = resourceInfoService.getOrderResourceInfoByDownloads(0,
                                                                                                        BizType.SOFTWARE,
                                                                                                        0, 10);
        model.addAttribute("hotMovieDLResList", hotMovieDLResList);
        model.addAttribute("hotGameDLResList", hotGameDLResList);
        model.addAttribute("hotSoftwareDLResList", hotSoftwareDLResList);

        VelocityContext context = new VelocityContext();

        viewResolver.getAttributesMap();

        return "index";
    }

    protected void renderPage(VelocityContext context) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(new File("/Users/zhiwenmizw/work/resources/output/index.vm"));
            velocityConfigurer.getVelocityEngine().mergeTemplate("index_source.vm", "UTF-8", context, writer);
        } catch (IOException e) {
            log.error("error message", e);
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
                log.error("close io fail", e);
            }
        }

    }
}
