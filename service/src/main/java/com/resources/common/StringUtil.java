package com.resources.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

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
}
