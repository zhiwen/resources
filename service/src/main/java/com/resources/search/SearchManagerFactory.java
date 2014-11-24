package com.resources.search;

import java.io.File;
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

    public synchronized void init() throws Exception {

        for (Map.Entry<String, String> entry : getSearchIndexPath().entrySet()) {

            String filePath = entry.getValue();

            SearcherManager searcherManager = new SearcherManager(FSDirectory.open(new File(filePath)), null);
            searchManagerMapping.put(entry.getKey(), searcherManager);
        }
    }

    public SearcherManager getSearchManager(IndexSearchNameEnum searchName) {
        return getSearchManagerMapping().get(searchName.getValue());
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
