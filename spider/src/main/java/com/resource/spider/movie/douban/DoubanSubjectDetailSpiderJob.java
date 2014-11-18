package com.resource.spider.movie.douban;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.resource.spider.SpiderJob;

/**
 * 网页详情去抓取数据(@http://movie.douban.com/subject/24404677/)
 * 
 * @author zhiwenmizw
 */
@Service
public class DoubanSubjectDetailSpiderJob implements SpiderJob {

    // 1、季数，每季id
    // 2、集数
    // 3、上映日期 2014-10-24(中国大陆) / 2014-07-25(美国)
    // 4、IMDb链接:
    // 5、语言：
    // 6、编剧:

    @Override
    public void execute() throws Exception {

    }

    public String getSeasons(Element infoEle) {
        Elements eles = infoEle.getElementsByAttributeValue("property", "v:initialReleaseDate");
        if (null == eles || eles.isEmpty()) {
            return null;
        }
        Element ele = eles.get(0);
        List<Node> childNodes = ele.childNodes();
        for (Node node : childNodes) {
            Element optionEle = (Element) node;
            String did = StringUtils.trim(optionEle.attr("value"));
            boolean isSelected = StringUtils.equalsIgnoreCase(optionEle.attr("selected"), "selected");

            if (optionEle.childNodeSize() > 0) {
                String seasonCount = optionEle.childNode(0).outerHtml();
            }
        }
        return null;
    }

    /**
     * 获取上映日期
     * 
     * @param infoEle
     * @return
     */
    public List<String> getPudates(Element infoEle) {
        Elements eles = infoEle.getElementsByAttributeValue("property", "v:initialReleaseDate");
        if (null == eles || eles.isEmpty()) {
            return null;
        }
        List<String> pubdates = new ArrayList<String>();
        for (Element element : eles) {
            String content = element.attr("content");
            if (StringUtils.isNotBlank(content)) {
                pubdates.add(content);
            }
        }
        if (CollectionUtils.isEmpty(pubdates)) {
            return null;
        }
        return pubdates;
    }
}
