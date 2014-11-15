package com.resource.spider.resources.movie;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.resource.spider.SpiderJob;
import com.resource.spider.http.HttpURLConnectionWrapper;
import com.resources.dal.dataobject.SpiderRecordDO;
import com.resources.dal.mapper.SpiderRecordMapper;
import com.resources.service.SpiderRecordService;

@Service
public class DoubanSpiderJob implements SpiderJob {

    public String               doubanTagPage = "http://movie.douban.com/tag/";

    @Resource
    private SpiderRecordMapper  spiderRecordMapper;

    @Resource
    private SpiderRecordService spiderRecordService;

    public static void main(String[] args) throws MalformedURLException, IOException {
        DoubanSpiderJob ds = new DoubanSpiderJob();
        ds.preData();
    }

    public void preData() {
        HttpURLConnectionWrapper con = null;
        Document doc = null;
        try {
            con = new HttpURLConnectionWrapper(new URL(doubanTagPage));
            InputStream in = con.getInputStream();
            doc = Jsoup.parse(in, "utf-8", doubanTagPage);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }

        List<SpiderRecordDO> spiderRecrods = spiderRecordMapper.getRecordByType(SpiderRecordDO.SpiderRecordTypeEnum.douban.getValue());

        Set<String> tagNameSet = new HashSet<String>();
        for (SpiderRecordDO recrod : spiderRecrods) {
            tagNameSet.add(recrod.getTagName());
        }

        Elements classTagColEles = doc.getElementsByClass("tagCol");

        for (Element tabEle : classTagColEles) {

            Elements tdEles = tabEle.getElementsByTag("td");

            for (Element tdEle : tdEles) {

                Node childNode = tdEle.childNode(0);
                while (!(childNode instanceof TextNode)) {
                    childNode = childNode.childNode(0);
                }
                TextNode textNode = (TextNode) childNode;
                String tagName = textNode.text();
                tagName = StringUtils.trim(tagName);
                if (StringUtils.isBlank(tagName)) {
                    continue;
                }
                if (tagNameSet.contains(tagName)) {
                    continue;
                }
                SpiderRecordDO recordDO = new SpiderRecordDO();
                recordDO.setType(SpiderRecordDO.SpiderRecordTypeEnum.douban.getValue());
                recordDO.setTagName(tagName);
                recordDO.setCreatedTime(new Date());
                spiderRecordMapper.addRecord(recordDO);
            }
        }
    }

    @Override
    public void execute() {

        // 1、加载处理过的url，
        preData();

        // 2、开始抓取列表页的数据

        // 3、保存数据

    }

}
