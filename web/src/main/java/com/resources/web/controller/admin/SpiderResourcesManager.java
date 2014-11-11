package com.resources.web.controller.admin;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONArray;
import com.resources.dal.module.SpiderResourcesDO;
import com.resources.service.SpiderResourcesService;

@Controller
@RequestMapping("/admin")
public class SpiderResourcesManager {

    @Resource
    private SpiderResourcesService spiderResourcesService;

    @RequestMapping(value = { "list/{page}" }, method = { RequestMethod.GET })
    public String list(Model model, @PathVariable("page") int page) {

        int length = 100;

        int offset = (Math.max(page, 1) - 1) * length;

        List<SpiderResourcesDO> list = spiderResourcesService.getSpiderResources(offset, length);

        model.addAttribute("page", page);
        model.addAttribute("list", list);

        return "admin/spider_resources";
    }

    @RequestMapping(value = "confirmed/{rid}-{did}", method = { RequestMethod.POST })
    public String confirmed(Model model, @PathVariable("rid") long rid, @PathVariable("did") String did)
                                                                                                        throws UnsupportedEncodingException {

        SpiderResourcesDO spiderResDO = spiderResourcesService.getSpiderResourceById(rid);

        JSONArray jarray = new JSONArray();
        jarray.add(did);
        spiderResDO.setDoubanIds(jarray.toJSONString());
        spiderResDO.setStatus(1);

        spiderResourcesService.updateSpiderResources(spiderResDO);

        return "admin/spider_resources";
    }
}
