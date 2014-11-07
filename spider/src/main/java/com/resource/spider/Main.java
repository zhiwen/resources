package com.resource.spider;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    private static ApplicationContext context;

    public static void main(String[] args) throws IOException {

        context = new ClassPathXmlApplicationContext("service.xml", "spider.xml");

        ResourceSpider resSpider = (ResourceSpider) context.getBean("doubanMovieSpider");

        System.out.println(resSpider);

        // resSpider.parse("http://movie.douban.com/subject/25845393/");
        // resSpider.parse("http://movie.douban.com/subject/1428055/");

        // resSpider.parse("http://movie.douban.com/subject/3157583/");
        resSpider.parse("http://movie.douban.com/subject/25845393/");

    }
}
