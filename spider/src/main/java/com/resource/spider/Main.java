package com.resource.spider;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    private static ApplicationContext context;

    public static void main(String[] args) throws IOException {

        context = new ClassPathXmlApplicationContext("service.xml", "spider.xml");

        // ResourceSpider resSpider = (ResourceSpider) context.getBean("doubanMovieSpider");

        // resSpider.parse("http://movie.douban.com/subject/25845393/");
        // resSpider.parse("http://movie.douban.com/subject/1428055/");

        // resSpider.parse("http://movie.douban.com/subject/3157583/");
        // resSpider.parse("http://movie.douban.com/subject/25845393/");
        // resSpider.parse("http://www.ygdy8.com/");

        // ResourceSpider movieTTResSpider = (ResourceSpider) context.getBean("sunshineMovieSpider");
        // movieTTResSpider.parse("http://www.ygdy8.com/");

        ResourceSpider sunshineDetailResSpider = (ResourceSpider) context.getBean("sunshineDetailMovieSpider");
        sunshineDetailResSpider.parse("http://www.ygdy8.com/");

        // String domain = "AAftp://c:c@d3.dl1234.com:8006/";
        // String url = "[电影天堂www.dy2018.com]暗金丑岛君电影版2BD日语中字.rmvbZZ";
        // url = domain + URLEncoder.encode(url, "utf-8");
        // String b64 = Base64.encodeBase64String(url.getBytes("utf-8"));
        // System.out.println("thunder://" + b64);

        // b64 =
        // "QUFmdHA6Ly9jOmNAZDMuZGwxMjM0LmNvbTo4MDA2L1slRTclOTQlQjUlRTUlQkQlQjElRTUlQTQlQTklRTUlQTAlODJ3d3cuZHkyMDE4LmNvbV0lRTYlOUElOTclRTklODclOTElRTQlQjglOTElRTUlQjIlOUIlRTUlOTAlOUIlRTclOTQlQjUlRTUlQkQlQjElRTclODklODgyQkQlRTYlOTclQTUlRTglQUYlQUQlRTQlQjglQUQlRTUlQUQlOTcucm12Ylpa";
        // byte[] bb = Base64.decodeBase64(b64);
        // System.out.println(new String(bb));

        // thunder://QUFmdHA6Ly9jOmNAZDMuZGwxMjM0LmNvbTo4MDA2L1slRTclOTQlQjUlRTUlQkQlQjElRTUlQTQlQTklRTUlQTAlODJ3d3cuZHkyMDE4LmNvbV0lRTYlOUElOTclRTklODclOTElRTQlQjglOTElRTUlQjIlOUIlRTUlOTAlOUIlRTclOTQlQjUlRTUlQkQlQjElRTclODklODgyQkQlRTYlOTclQTUlRTglQUYlQUQlRTQlQjglQUQlRTUlQUQlOTcucm12Ylpa
    }
}
