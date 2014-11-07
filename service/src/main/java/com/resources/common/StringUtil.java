package com.resources.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public final class StringUtil {

    static Pattern pattern = Pattern.compile("\\d+");

    public static Long matchNumber(String string) {
        if (StringUtils.isBlank(string)) {
            return 0L;
        }

        Matcher mh = pattern.matcher(string);
        if (mh.find()) {
            String value = mh.group(0);
            return Long.valueOf(value);
        }
        return 0L;
    }
}
