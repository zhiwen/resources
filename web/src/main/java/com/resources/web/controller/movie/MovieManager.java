package com.resources.web.controller.movie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/movie")
public class MovieManager {

    private final static Logger log = LoggerFactory.getLogger(MovieManager.class);

    // @Resource
    // private MovieDAO movieDAO;
    //
    // @Resource
    // private ResourceCounterDAO resourceCounterDAO;
    //
    // @Resource
    // private ResourceInfoDAO resourceExtInfoDAO;

    @RequestMapping(value = "list/-{cid}/{showTime}/{country}/{page}", method = { RequestMethod.GET })
    public String list(Model model, @PathVariable("categoryId") final long cid,
                       @PathVariable("showTime") final String showTime, @PathVariable("country") final int country,
                       @PathVariable("page") int page) {

        return "movie/list";
    }
}
