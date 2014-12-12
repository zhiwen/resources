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
import org.apache.lucene.document.Field.Store;
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

            Field id = new LongField(ResMovieFieldNameEnum.id.getValue(), movieInfo.getId(), Field.Store.YES);
            Field did = new LongField(ResMovieFieldNameEnum.did.getValue(), movieInfo.getDid(), Field.Store.YES);
            Field title = new StringField(ResMovieFieldNameEnum.title.getValue(),
                                          StringUtils.defaultIfBlank(movieInfo.getTitle(), ""), Field.Store.YES);
            Field originalTitle = new StringField(ResMovieFieldNameEnum.originalTitle.getValue(),
                                                  StringUtils.defaultIfBlank(movieInfo.getOriginalTitle(), ""),
                                                  Field.Store.YES);

            if (null != movieInfo.getCastIds()) {
                for (Long item : movieInfo.getCastIds()) {
                    Field field = new LongField(ResMovieFieldNameEnum.castIds.getValue(), item, Field.Store.YES);
                    document.add(field);
                }
            }

            if (null != movieInfo.getDirectorIds()) {
                for (Long item : movieInfo.getDirectorIds()) {
                    Field field = new LongField(ResMovieFieldNameEnum.directorIds.getValue(), item, Field.Store.YES);
                    document.add(field);
                }
            }

            if (null != movieInfo.getGenreIds()) {
                for (Long item : movieInfo.getGenreIds()) {
                    Field field = new LongField(ResMovieFieldNameEnum.genreIds.getValue(), item, Field.Store.YES);
                    document.add(field);
                }
            }

            if (null != movieInfo.getWriterIds()) {
                for (Long item : movieInfo.getWriterIds()) {
                    Field field = new LongField(ResMovieFieldNameEnum.writerIds.getValue(), item, Field.Store.YES);
                    document.add(field);
                }
            }

            if (null != movieInfo.getCountryIds()) {
                for (Long item : movieInfo.getCountryIds()) {
                    Field field = new LongField(ResMovieFieldNameEnum.countryIds.getValue(), item, Field.Store.YES);
                    document.add(field);
                }
            }

            if (null != movieInfo.getAka()) {
                for (String item : movieInfo.getAka()) {
                    Field field = new StringField(ResMovieFieldNameEnum.aka.getValue(), item, Field.Store.YES);
                    document.add(field);
                }
            }

            Field coverImagesId = new LongField(ResMovieFieldNameEnum.coverImagesId.getValue(),
                                                movieInfo.getCoverImagesId(), Field.Store.NO);

            Field mobileUrl = new StringField(ResMovieFieldNameEnum.mobileUrl.getValue(),
                                              StringUtils.defaultIfBlank(movieInfo.getMobileUrl(), ""), Field.Store.NO);

            Field subType = new LongField(ResMovieFieldNameEnum.subType.getValue(), movieInfo.getSubType().getValue(),
                                          Field.Store.YES);

            Field ratingCount = new IntField(ResMovieFieldNameEnum.ratingCount.getValue(), movieInfo.getRatingCount(),
                                             Store.YES);

            Field website = new StringField(ResMovieFieldNameEnum.website.getValue(),
                                            StringUtils.defaultIfBlank(movieInfo.getWebsite(), ""), Field.Store.NO);
            Field doubanSite = new StringField(ResMovieFieldNameEnum.doubanSite.getValue(),
                                               StringUtils.defaultIfBlank(movieInfo.getDoubanSite(), ""),
                                               Field.Store.NO);

            Field pubdates = new StringField(ResMovieFieldNameEnum.pubdates.getValue(),
                                             StringUtils.defaultIfBlank(movieInfo.getPubdates(), ""), Field.Store.NO);

            if (null != movieInfo.getLanguages()) {
                for (String item : movieInfo.getLanguages()) {
                    Field field = new StringField(ResMovieFieldNameEnum.languages.getValue(), item, Field.Store.YES);
                    document.add(field);
                }
            }

            Field durations = new StringField(ResMovieFieldNameEnum.durations.getValue(),
                                              StringUtils.defaultIfBlank(movieInfo.getDurations(), ""), Field.Store.NO);

            Field summaryId = new LongField(ResMovieFieldNameEnum.summaryId.getValue(), movieInfo.getSummaryId(),
                                            Field.Store.NO);

            Field seasonCount = new IntField(ResMovieFieldNameEnum.seasonCount.getValue(), movieInfo.getSeasonCount(),
                                             Field.Store.NO);

            Field seasonId = new LongField(ResMovieFieldNameEnum.seasonId.getValue(), movieInfo.getSeasonId(),
                                           Field.Store.NO);

            Field episodeCount = new IntField(ResMovieFieldNameEnum.episodeCount.getValue(),
                                              movieInfo.getEpisodeCount(), Field.Store.NO);

            if (null != movieInfo.getTagIds()) {
                for (Long item : movieInfo.getTagIds()) {
                    Field field = new LongField(ResMovieFieldNameEnum.tagIds.getValue(), item, Field.Store.YES);
                    document.add(field);
                }
            }

            Field canPlay = new IntField(ResMovieFieldNameEnum.playable.getValue(), movieInfo.getPlayable(),
                                         Field.Store.YES);
            Field year = new StringField(ResMovieFieldNameEnum.year.getValue(),
                                         StringUtils.defaultIfBlank(movieInfo.getYear(), "2014"), Field.Store.YES);

            Field createTime = new LongField(ResMovieFieldNameEnum.createdTime.getValue(),
                                             movieInfo.getCreatedTime().getTime(), Field.Store.YES);
            Field modifiedTime = new LongField(ResMovieFieldNameEnum.modifiedTime.getValue(),
                                               movieInfo.getModifiedTime().getTime(), Field.Store.YES);

            document.add(id);
            document.add(did);
            document.add(title);
            document.add(originalTitle);
            document.add(coverImagesId);
            document.add(mobileUrl);
            document.add(subType);
            document.add(website);
            document.add(doubanSite);
            document.add(pubdates);
            document.add(durations);
            document.add(summaryId);
            document.add(seasonCount);
            document.add(seasonId);
            document.add(episodeCount);
            document.add(ratingCount);
            document.add(canPlay);
            document.add(year);
            document.add(createTime);
            document.add(modifiedTime);

            writer.addDocument(document);
        }
        writer.close();
        return true;
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
