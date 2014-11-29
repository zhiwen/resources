package com.resources.search;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.NumericUtils;

public final class SearchUtil {

    public static Query longToQuery(String name, Long value) {

        if (StringUtils.isBlank(name) || null == value) {
            return null;
        }

        BytesRefBuilder brb = new BytesRefBuilder();
        brb.append(new BytesRef());
        NumericUtils.longToPrefixCoded(value, 0, brb);
        return new TermQuery(new Term(name, brb.get()));
    }

    public static Query stringToQuery(String name, String value) {
        if (StringUtils.isBlank(value) || StringUtils.isBlank(name)) {
            return null;
        }
        return new TermQuery(new Term(name, value));
    }

}
