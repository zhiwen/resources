package com.resource.spider.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class HttpURLConnectionWrapper {

    HttpURLConnection httpURLConnection;
    // 简单的CookieManager
    CookieManager     cookieManager = CookieManager.getInstance();

    private final URL url;

    public HttpURLConnectionWrapper(URL url, int conTimeout, int readTimeout) throws IOException{
        // super(u);
        this.url = url;
        httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setReadTimeout(readTimeout);
        httpURLConnection.setConnectTimeout(conTimeout);
        fillRequestHeadField();
    }

    /**
     * 填充Request Header信息
     */
    private void fillRequestHeadField() {
        httpURLConnection.setRequestProperty("User-Agent",
                                             "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.111 Safari/537.36");
        httpURLConnection.setRequestProperty("Accept",
                                             "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpURLConnection.setRequestProperty("Accept-Language", "zh-cn,zh;q=0.5");
        httpURLConnection.setRequestProperty("Accept-Encoding", "GB2312,utf-8;q=0.7,*;q=0.7");
        httpURLConnection.setRequestProperty("Referer", "http://movie.douban.com/");
        httpURLConnection.setRequestProperty("Cache-Control", "max-age=0");

        String defaultCookie = "ll=\"108296\"; viewed=\"20142617_25867042\"; dbcl2=\"51607875:61ZHECqTHtI\"; ct=y; ck=\"nLK7\"; __utmt=1; __utma=30149280.1491508282.1413787324.1415762103.1415772217.36; __utmb=30149280.10.10.1415772217; __utmc=30149280; __utmz=30149280.1415724266.34.12.utmcsr=res.com:8080|utmccn=(referral)|utmcmd=referral|utmcct=/admin/list/1; __utmv=30149280.5160; __utma=223695111.318823314.1413353554.1415762106.1415772247.33; __utmb=223695111.1.10.1415772247; __utmc=223695111; __utmz=223695111.1415772247.33.24.utmcsr=douban.com|utmccn=(referral)|utmcmd=referral|utmcct=/explore/; _pk_ref.100001.4cf6=%5B%22%22%2C%22%22%2C1415772247%2C%22http%3A%2F%2Fwww.douban.com%2Fexplore%2F%22%5D; _pk_id.100001.4cf6=0a9a924b729d29c2.1413353554.34.1415772247.1415763647.; _pk_ses.100001.4cf6=*; push_noty_num=0; push_doumail_num=3; bid=\"atKS74w++/U\"";
        cookieManager.setCookies(url.getHost(), defaultCookie);

        httpURLConnection.setRequestProperty("Cookie", cookieManager.getCookies(url.getHost()));

    }

    public InputStream getInputStream() throws IOException {
        InputStream is = httpURLConnection.getInputStream();
        // 取到输入流中后处理Cookie信息
        resolveCookies();
        int responseCode = httpURLConnection.getResponseCode();
        if (responseCode != 200 && responseCode != 404) {
            // 清除cookie并重新发请求
            CookieManager.getInstance().removeCookies(url.getHost());
            try {
                disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            httpURLConnection = (HttpURLConnection) this.getUrl().openConnection();
            HttpURLConnection.setFollowRedirects(false);
            fillRequestHeadField();
            is = httpURLConnection.getInputStream();
        }
        return is;
    }

    private void resolveCookies() {
        List<String> setCookies = httpURLConnection.getHeaderFields().get("Set-Cookie");
        if (setCookies != null && !setCookies.isEmpty()) {
            for (String setCookie : setCookies) {
                cookieManager.setCookies(this.url.getHost(), setCookie);
            }
        }
    }

    public URL getUrl() {
        return url;
    }

    public void disconnect() {
        httpURLConnection.disconnect();
    }
}
