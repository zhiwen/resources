package com.resources.search;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

public abstract class SearchQueryParam {

    public static enum OrderByEnum {
        NONE, ASC, DESC;
    }

    private int offset;

    private int limit = 1;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLength() {
        return getOffset() + getLimit();
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public abstract Query buildQuery();

    public abstract Sort buildSort();
}
