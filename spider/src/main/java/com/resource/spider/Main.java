package com.resource.spider;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    private static ApplicationContext context;

    public static void main(String[] args) throws Exception {

        context = new ClassPathXmlApplicationContext("service.xml", "spider.xml");

        SpiderJob spiderJob = null;

        // spiderJob = (SpiderJob) context.getBean("doubanMovieTagsSpider");
        // spiderJob.execute();

        spiderJob = (SpiderJob) context.getBean("doubanMovieTagListSpiderJob");
        // spiderJob.execute();

        // spiderJob = (SpiderJob) context.getBean("doubanMovieSearchSpiderJob");
        // spiderJob.execute();

        // spiderJob = (SpiderJob) context.getBean("doubanSubjectTVSearchSpiderJob");
        // spiderJob.execute();

        // spiderJob = (SpiderJob) context.getBean("doubanSubjectAbsSpiderJob");
        // spiderJob.execute();

        spiderJob = (SpiderJob) context.getBean("doubanSubjectApiSpiderJob");
        // spiderJob.execute();
    }
}
