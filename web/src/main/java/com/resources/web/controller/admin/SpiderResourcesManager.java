package com.resources.web.controller.admin;

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
    public String list(Model model, @PathVariable("page") int page, @PathVariable("count") int count) {

        int length = count;
        if (length == 0) {
            count = 50;
        }

        int offset = (Math.max(page, 1) - 1) * length;

        List<SpiderResourcesDO> list = spiderResourcesService.getSpiderResources(offset, length);

        model.addAttribute("page", page);
        model.addAttribute("list", list);

        return "admin/spider_resources";
    }

    @RequestMapping(value = "confirmed/{name}-{id}", method = { RequestMethod.GET })
    public String confirmed(Model model, @PathVariable("name") String name, @PathVariable("id") String id) {

        SpiderResourcesDO spiderResDO = spiderResourcesService.getSpiderResource(name);

        JSONArray jarray = new JSONArray();
        jarray.add(id);
        spiderResDO.setDoubanIds(jarray.toJSONString());
        spiderResDO.setStatus(1);

        spiderResourcesService.updateSpiderResources(spiderResDO);

        return "admin/spider_resources";
    }
}
