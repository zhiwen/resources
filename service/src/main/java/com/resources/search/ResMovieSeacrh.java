package com.resources.search;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.resources.dal.dataobject.ResMovieDO;
import com.resources.search.SearchManagerFactory.IndexSearchNameEnum;
import com.resources.service.ResMovieService;

@Service
public class ResMovieSeacrh {

    private final static Logger  log   = LoggerFactory.getLogger(ResMovieSeacrh.class);

    @Resource
    private SearchManagerFactory searchManagerFactory;

    @Resource
    private ResMovieService      resMovieService;

    private final int            limit = 1000;

    public void buildMovies() throws IOException {
        for (int i = 0; i < 1000; i++) {
            boolean ret = buildMovies(i * limit, limit);
            if (!ret) {
                return;
            }
        }
    }

    public boolean buildMovies(int offset, int limit) throws IOException {

        List<ResMovieDO> list = resMovieService.getMovieByPaginatorWithStatus(4, offset, limit);
        if (list.isEmpty()) {
            return false;
        }
        Analyzer analyzer = new StandardAnalyzer();

        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
        iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

        String filePath = searchManagerFactory.getSearchIndexPath(IndexSearchNameEnum.indexSearchMovie);

        IndexWriter writer = new IndexWriter(FSDirectory.open(new File(filePath)), iwc);

        for (ResMovieDO movieInfo : list) {

            Document document = new Document();

            addField(document, ResMovieFieldNameEnum.id.getValue(), movieInfo.getId(), true);
            addField(document, ResMovieFieldNameEnum.did.getValue(), movieInfo.getDid(), true);
            addField(document, ResMovieFieldNameEnum.title.getValue(), movieInfo.getTitle(), true);
            addField(document, ResMovieFieldNameEnum.originalTitle.getValue(), movieInfo.getOriginalTitle(), true);

            addLongFields(document, ResMovieFieldNameEnum.castIds.getValue(), movieInfo.getCastIds(), true);
            addLongFields(document, ResMovieFieldNameEnum.directorIds.getValue(), movieInfo.getDirectorIds(), true);
            addLongFields(document, ResMovieFieldNameEnum.genreIds.getValue(), movieInfo.getGenreIds(), true);
            addLongFields(document, ResMovieFieldNameEnum.writerIds.getValue(), movieInfo.getWriterIds(), true);
            addLongFields(document, ResMovieFieldNameEnum.countryIds.getValue(), movieInfo.getCountryIds(), true);
            addStringFields(document, ResMovieFieldNameEnum.aka.getValue(), movieInfo.getAka(), true);

            addField(document, ResMovieFieldNameEnum.coverImagesId.getValue(), movieInfo.getCoverImagesId(), false);
            addField(document, ResMovieFieldNameEnum.mobileUrl.getValue(), movieInfo.getMobileUrl(), false);
            addField(document, ResMovieFieldNameEnum.subType.getValue(), movieInfo.getSubType().getValue(), true);
            addField(document, ResMovieFieldNameEnum.ratingCount.getValue(), movieInfo.getRatingCount(), true);

            addField(document, ResMovieFieldNameEnum.website.getValue(), movieInfo.getWebsite(), false);
            addField(document, ResMovieFieldNameEnum.doubanSite.getValue(), movieInfo.getDoubanSite(), false);
            addField(document, ResMovieFieldNameEnum.pubdates.getValue(), movieInfo.getPubdates(), false);

            addStringFields(document, ResMovieFieldNameEnum.languages.getValue(), movieInfo.getLanguages(), true);

            addField(document, ResMovieFieldNameEnum.durations.getValue(), movieInfo.getDurations(), false);
            addField(document, ResMovieFieldNameEnum.summaryId.getValue(), movieInfo.getSummaryId(), false);
            addField(document, ResMovieFieldNameEnum.seasonCount.getValue(), movieInfo.getSeasonCount(), false);
            addField(document, ResMovieFieldNameEnum.seasonId.getValue(), movieInfo.getSeasonId(), false);
            addField(document, ResMovieFieldNameEnum.episodeCount.getValue(), movieInfo.getEpisodeCount(), false);

            addLongFields(document, ResMovieFieldNameEnum.tagIds.getValue(), movieInfo.getTagIds(), true);

            addField(document, ResMovieFieldNameEnum.playable.getValue(), movieInfo.getPlayable(), true);
            addField(document, ResMovieFieldNameEnum.year.getValue(),
                     StringUtils.defaultIfBlank(movieInfo.getYear(), "1987"), true);
            addField(document, ResMovieFieldNameEnum.createdTime.getValue(), movieInfo.getCreatedTime().getTime(), true);
            addField(document, ResMovieFieldNameEnum.modifiedTime.getValue(), movieInfo.getModifiedTime().getTime(),
                     true);

            writer.addDocument(document);
        }
        writer.close();
        return true;
    }

    private void addStringFields(Document document, String name, List<String> values, boolean store) {
        if (null == values || values.isEmpty()) {
            return;
        }
        for (String value : values) {
            addField(document, name, value, store);
        }
    }

    private void addLongFields(Document document, String name, List<Long> values, boolean store) {
        if (null == values || values.isEmpty()) {
            return;
        }
        for (Long value : values) {
            addField(document, name, value, store);
        }
    }

    private void addField(Document document, String name, String value, boolean store) {
        if (StringUtils.isBlank(name) || StringUtils.isBlank(value)) {
            return;
        }
        Field field = new StringField(name, StringUtils.defaultIfBlank(value, ""),
                                      store ? Field.Store.YES : Field.Store.NO);
        document.add(field);
    }

    private void addField(Document document, String name, int value, boolean store) {
        if (StringUtils.isBlank(name)) {
            return;
        }
        Field field = new IntField(name, value, store ? Field.Store.YES : Field.Store.NO);
        document.add(field);
    }

    private void addField(Document document, String name, long value, boolean store) {
        if (StringUtils.isBlank(name)) {
            return;
        }
        Field field = new LongField(name, value, store ? Field.Store.YES : Field.Store.NO);
        document.add(field);
    }

    public List<ResMovieDO> getMovieListByParams(SearchQueryParam queryParam) {

        ResMovieQueryParam movieQueryParam = (ResMovieQueryParam) queryParam;

        SearcherManager searchManager = searchManagerFactory.getSearchManager(IndexSearchNameEnum.indexSearchMovie);

        IndexSearcher indexSearch = null;

        int needLength = queryParam.getLength();
        try {
            indexSearch = searchManager.acquire();

            TopFieldDocs topFieldDocs = indexSearch.search(movieQueryParam.buildQuery(), needLength,
                                                           movieQueryParam.buildSort());

            ScoreDoc[] scoreDocs = topFieldDocs.scoreDocs;
            if (scoreDocs.length == 0) {
                return null;
            }
            for (int i = queryParam.getOffset(); (i < needLength && i < scoreDocs.length); i++) {
                int docID = scoreDocs[i].doc;
                Document document = indexSearch.doc(docID);
                p(document, i);
            }
        } catch (IOException e) {
            log.error("search error", e);
        } finally {
            if (null != indexSearch) {
                try {
                    searchManager.release(indexSearch);
                } catch (IOException e) {
                    log.error("release indexSearch error", e);
                }
            }
        }
        return null;
    }

    public static void p(Document document, int num) throws IOException {
        System.out.println("===================================" + num);

        String title = document.get(ResMovieFieldNameEnum.title.getValue());
        String rating = document.get(ResMovieFieldNameEnum.ratingCount.getValue());
        String year = document.get(ResMovieFieldNameEnum.year.getValue());
        String canPlay = document.get(ResMovieFieldNameEnum.playable.getValue());

        IndexableField[] akas = document.getFields(ResMovieFieldNameEnum.aka.getValue());
        for (IndexableField indexableField : akas) {
            System.out.println("akas:" + indexableField.stringValue());
        }

        IndexableField[] countries = document.getFields(ResMovieFieldNameEnum.countryIds.getValue());
        for (IndexableField indexableField : countries) {
            System.out.println("countries:" + indexableField.stringValue());
        }

        System.out.println("title:" + title);
        System.out.println("rating:" + rating);
        System.out.println("year:" + year);
        System.out.println("canPlay:" + canPlay);
    }
}
