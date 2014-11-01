package com.proxy.web.controller.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CommonController {

    @RequestMapping(value = "/home", method = { RequestMethod.GET })
    public String goHome() {
        return "common/home";
    }
}
