package com.resources.web.controller.movie;

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
public class MovieDetail {

    private final static Logger log = LoggerFactory.getLogger(MovieDetail.class);

    @Resource
    private ResourceInfoService resourceInfoService;

    @Resource
    private MovieService        movieService;

    @RequestMapping(value = { "detail/{rid}" }, method = { RequestMethod.GET })
    public String detail(Model model, @PathVariable("id") long id) {

        ResourceInfoDO resourceInfo = resourceInfoService.getResourceInfo(id);
        if (resourceInfo == null) {
            return "common/error.vm";
        }

        if (BizType.MOVIE.getType() == resourceInfo.getBizType()) {
            MovieDO movie = movieService.getMovie(id);

            model.addAttribute("movie", movie);
        }
        model.addAttribute("resourceInfo", resourceInfo);

        return "movie/detail";
    }
}
