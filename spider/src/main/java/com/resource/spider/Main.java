package com.resource.spider;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    private static ApplicationContext context;

    public static void main(String[] args) throws Exception {

        context = new ClassPathXmlApplicationContext("service.xml", "spider.xml");

        SpiderJob spiderJob = null;

        // spiderJob = (SpiderJob) context.getBean("doubanMovieTagsSpiderJob");
        // spiderJob.execute();

        // spiderJob = (SpiderJob) context.getBean("doubanMovieIdSearchSpiderJob");
        // spiderJob.execute();

        spiderJob = (SpiderJob) context.getBean("doubanMovieAbstractSpiderJob");
        spiderJob.execute();

        // spiderJob = (SpiderJob) context.getBean("doubanMovieApiSpiderJob");
        // spiderJob.execute();

        // spiderJob = (SpiderJob) context.getBean("doubanMovieDetailSpiderJob");
        // spiderJob.execute();
    }
}
