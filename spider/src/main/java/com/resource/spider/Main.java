package com.resource.spider;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    private static ApplicationContext context;

    public static void main(String[] args) throws Exception {

        context = new ClassPathXmlApplicationContext("service.xml", "spider.xml");

        SpiderJob spiderJob = (SpiderJob) context.getBean("doubanSpiderJob");
        spiderJob.execute();
    }
}
