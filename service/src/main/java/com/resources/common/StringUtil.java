package com.resources.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

public final class StringUtil {

    static Pattern pattern = Pattern.compile("\\d+");

    public static int matchInt(String string) {
        if (StringUtils.isBlank(string)) {
            return 0;
        }

        Matcher mh = pattern.matcher(string);
        if (mh.find()) {
            String value = mh.group(0);
            return Integer.valueOf(value);
        }
        return 0;
    }

    public static boolean isURL(String url) {
        return url.startsWith("http://") || url.startsWith("ftp://") || url.startsWith("thunder://");
    }

    public static String cleanEnterChar(String html) {
        return html.replaceAll("\r", "").replaceAll("\n", "");
    }

    public static List<Object> stringToArray(String str) {

        if (StringUtils.isBlank(str)) {
            return Collections.emptyList();
        }
        JSONArray jsonArray = JSON.parseArray(str);
        return new ArrayList<Object>(jsonArray);
    }

    public static List<String> toStringList(JSONArray array) {

        if (null == array || array.isEmpty()) {
            return null;
        }
        List<String> retList = new LinkedList<String>();
        for (Object object : array) {
            if (null == object) {
                continue;
            }
            retList.add(object.toString());
        }
        return retList;
    }

    public static String escapeHtml(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        StringBuilder buf = new StringBuilder();
        char chs[] = str.toCharArray();
        for (char c : chs) {

            if ('"' == c || '\'' == c) {
                buf.append('\\');
            }
            buf.append(c);
        }
        return buf.toString();
    }

    public static int stringToInt(String str) {
        if (StringUtils.isBlank(str)) {
            return 0;
        }
        str = str.trim();
        if (StringUtils.isNumeric(str)) {
            return Integer.valueOf(str);
        }
        return 0;
    }
}
