package com.resource.spider;

import java.io.IOException;

import com.resource.spider.resources.movie.DoubanMovieSpider;

public class Main {

    public static void main(String[] args) throws IOException {

        ResourceSpider resSpider = new DoubanMovieSpider();
        // resSpider.parse("http://movie.douban.com/subject/25845393/");
        // resSpider.parse("http://movie.douban.com/subject/1428055/");

        resSpider.parse("http://movie.douban.com/subject/3157583/");

    }
}
