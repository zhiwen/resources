package com.resources.search;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.FSDirectory;

public class SearchManagerFactory {

    private Map<String, String>                searchIndexPath;

    private final Map<String, SearcherManager> searchManagerMapping = new ConcurrentHashMap<String, SearcherManager>();

    public static enum IndexSearchNameEnum {

        indexSearchMovie("index_search_movie");

        private final String value;

        IndexSearchNameEnum(String value){
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

    public SearcherManager getSearchManager(IndexSearchNameEnum searchName) {
        SearcherManager searcherManager = getSearchManagerMapping().get(searchName.getValue());
        if (null == searcherManager) {
            String filePath = getSearchIndexPath(searchName);
            try {
                searcherManager = new SearcherManager(FSDirectory.open(new File(filePath)), null);
            } catch (IOException e) {
            }
            searchManagerMapping.put(searchName.getValue(), searcherManager);
        }
        return searcherManager;
    }

    public String getSearchIndexPath(IndexSearchNameEnum searchName) {
        return getSearchIndexPath().get(searchName.getValue());
    }

    public Map<String, String> getSearchIndexPath() {
        return searchIndexPath;
    }

    public void setSearchIndexPath(Map<String, String> searchIndexPath) {
        this.searchIndexPath = searchIndexPath;
    }

    public Map<String, SearcherManager> getSearchManagerMapping() {
        return searchManagerMapping;
    }
}
