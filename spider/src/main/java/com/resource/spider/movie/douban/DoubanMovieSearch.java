package com.resource.spider.movie.douban;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.resource.spider.ResourceSpider;
import com.resource.spider.http.HttpURLConnectionWrapper;
import com.resources.dal.module.SpiderResourcesDO;
import com.resources.service.SpiderResourcesService;

//@Service
@Deprecated
public class DoubanMovieSearch extends ResourceSpider {

    @Resource
    private SpiderResourcesService spiderResourcesService;

    private final static String    searchApi  = "https://api.douban.com/v2/movie/search?q=%s&count=5";
    private final static String    suggestApi = "http://movie.douban.com/j/subject_suggest?q=%s";

    // /v2/movie/search?q=

    @Override
    public boolean parseDocument(Document document, Map<String, Object> userData) {
        return false;
    }

    @Override
    public void parse(String urlString) throws IOException {

        int offset = 0, length = 1000;

        long id = 1;

        while (true) {

            List<SpiderResourcesDO> resList = spiderResourcesService.getSpiderResources(offset, length);

            if (CollectionUtils.isEmpty(resList)) {
                break;
            }

            for (SpiderResourcesDO spiderResourcesDO : resList) {

                if (StringUtils.isBlank(spiderResourcesDO.getCleanedName())) {
                    continue;
                }
                if (StringUtils.isNotBlank(spiderResourcesDO.getDoubanIds())) {
                    continue;
                }
                String name = spiderResourcesDO.getName();
                String cleanedName = cleanMovieName(name);

                List<String> idList = idNameMappingWithSuggest(name, cleanedName);
                if (!idList.isEmpty()) {

                    spiderResourcesDO.setCleanedName(StringUtils.trim(cleanedName));
                    spiderResourcesDO.setStatus(0);
                    spiderResourcesDO.setName(StringUtils.trim(name));

                    spiderResourcesDO.setDoubanIds(JSON.toJSONString(idList));
                    spiderResourcesDO.setId(id++);
                    spiderResourcesService.updateSpiderResources(spiderResourcesDO);
                }

                try {
                    System.out.println("======" + spiderResourcesDO.getId());
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            offset += length;
        }
    }

    static int     idMarkLength    = "/subject/".length();

    static Pattern doubanIdPattern = Pattern.compile("/subject/(\\d+)/");

    public List<String> idNameMappingWithSuggest(String name, String cleanedName) throws MalformedURLException,
                                                                                 UnsupportedEncodingException {

        String url = String.format(suggestApi, URLEncoder.encode(cleanedName, "utf-8"));
        String jsonContent = getHttpHtml(new URL(url));

        if (StringUtils.isBlank(jsonContent)) {
            return Collections.emptyList();
        }

        JSONArray jsonArray = JSON.parseArray(jsonContent);

        List<String> idList = new ArrayList<String>();

        for (Object object : jsonArray) {

            JSONObject subject = (JSONObject) object;
            String movieUrl = subject.get("url").toString();
            String title = subject.get("title").toString();
            // String subTitle = (String) subject.get("sub_title");
            String type = (String) subject.get("type");
            if (!StringUtils.equals(type, "movie")) {
                continue;
            }
            // if (StringUtils.equals(cleanedName, title) || StringUtils.equals(cleanedName, subTitle)) {
            // http://movie.douban.com/subject/1436891/?suggest=%E5%A4%A7%E5%81%B7%E8%A2%AD
            Matcher m = doubanIdPattern.matcher(movieUrl);
            if (m.find()) {
                String id = m.group(0);
                id = id.substring(idMarkLength, id.length() - 1);
                idList.add(id);
            }
            // }
        }
        return idList;
    }

    public List<String> idNameMapping(String name, String cleanedName) throws MalformedURLException {

        String url = String.format(suggestApi, cleanedName);
        String jsonContent = getHttpsHtml(new URL(url));

        JSONObject jsonObject = JSON.parseObject(jsonContent);
        // int total = (Integer) jsonObject.get("total");
        // int count = (Integer) jsonObject.get("count");

        List<String> idList = new ArrayList<String>();
        JSONArray subjects = (JSONArray) jsonObject.get("subjects");
        for (Object object : subjects) {

            JSONObject subject = (JSONObject) object;
            String id = subject.get("id").toString();
            String title = subject.get("title").toString();
            String originalTitle = subject.get("original_title").toString();
            String genres = subject.get("genres").toString();

            if (StringUtils.equals(cleanedName, title) || StringUtils.equals(cleanedName, originalTitle)) {
                idList.add(id);
            }
        }
        return idList;
    }

    public static void main(String[] args) throws Exception {
        String name = "游龙戏凤";
        new DoubanMovieSearch().idNameMappingWithSuggest(name, name);
    }

    public static String cleanMovieName(String name) {
        boolean start = false;

        StringBuilder buf = new StringBuilder();
        char[] chs = name.toCharArray();
        for (char c : chs) {
            if ('《' == c) {
                start = true;
                continue;
            }
            if ('》' == c) {
                break;
            }
            if (start) {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    public String getHttpsHtml(URL url) {
        InputStream in = null;
        try {
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setConnectTimeout(getTimeout());

            in = con.getInputStream();

            return getHtml(in, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public String getHttpHtml(URL url) {
        HttpURLConnectionWrapper con = null;
        try {
            con = new HttpURLConnectionWrapper(url);
            // con.setConnectTimeout(getTimeout());
            InputStream in = con.getInputStream();
            return getHtml(in, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public String getHtml(InputStream in, String charset) {
        if (null == in) {
            return null;
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(2048);
            int count = 0;
            byte[] b = new byte[1024];
            while ((count = in.read(b, 0, b.length)) > 0) {
                bos.write(b, 0, count);
            }
            return bos.toString(charset);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
