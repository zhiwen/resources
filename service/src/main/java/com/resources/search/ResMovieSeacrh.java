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
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.stereotype.Service;

import com.resources.dal.dataobject.ResMovieDO;
import com.resources.search.SearchManagerFactory.IndexSearchNameEnum;
import com.resources.service.ResMovieService;

@Service
public class ResMovieSeacrh {

    @Resource
    private SearchManagerFactory searchManagerFactory;

    @Resource
    private ResMovieService      resMovieService;

    public void buildMovies() throws IOException {

        List<ResMovieDO> list = resMovieService.getMovieByPaginatorWithStatus(3, 0, 1000);

        Analyzer analyzer = new StandardAnalyzer();

        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
        iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

        String filePath = searchManagerFactory.getSearchIndexPath().get(IndexSearchNameEnum.indexSearchMovie.getValue());

        Runtime rt = Runtime.getRuntime();
        rt.exec("rm -rf " + filePath);

        IndexWriter writer = new IndexWriter(FSDirectory.open(new File(filePath)), iwc);

        for (ResMovieDO movieInfo : list) {

            Document document = new Document();

            Field title = new StringField(ResMovieFieldNameEnum.title.getValue(),
                                          StringUtils.defaultIfBlank(movieInfo.getTitle(), ""), Field.Store.YES);

            if (null != movieInfo.getAka()) {
                for (String item : movieInfo.getAka()) {
                    Field field = new StringField(ResMovieFieldNameEnum.aka.getValue(), item, Field.Store.YES);
                    document.add(field);
                }
            }

            if (null != movieInfo.getCastIds()) {
                for (Long item : movieInfo.getCastIds()) {
                    Field field = new LongField(ResMovieFieldNameEnum.castIds.getValue(), item, Field.Store.YES);
                    document.add(field);
                }
            }

            if (null != movieInfo.getCountryIds()) {
                for (Long item : movieInfo.getCountryIds()) {
                    Field field = new LongField(ResMovieFieldNameEnum.countryIds.getValue(), item, Field.Store.YES);
                    document.add(field);
                }
            }
            if (null != movieInfo.getGenreIds()) {
                for (Long item : movieInfo.getGenreIds()) {
                    Field field = new LongField(ResMovieFieldNameEnum.genreIds.getValue(), item, Field.Store.YES);
                    document.add(field);
                }
            }

            Field rating = new IntField(ResMovieFieldNameEnum.ratingCount.getValue(), movieInfo.getRatingCount(),
                                        Store.YES);

            Field canPlay = new IntField(ResMovieFieldNameEnum.playable.getValue(), movieInfo.getPlayable(),
                                         Field.Store.YES);
            Field year = new StringField(ResMovieFieldNameEnum.year.getValue(),
                                         StringUtils.defaultIfBlank(movieInfo.getYear(), "2014"), Field.Store.YES);

            Field createTime = new LongField(ResMovieFieldNameEnum.createdTime.getValue(),
                                             movieInfo.getCreatedTime().getTime(), Field.Store.YES);

            document.add(title);
            document.add(rating);
            document.add(canPlay);
            document.add(year);
            document.add(createTime);

            writer.addDocument(document);
        }
        writer.close();

    }

    public List<ResMovieDO> getMovieListByParams(String countryId, String genreId, String year,
                                                 ResMovieFieldNameEnum orderName, int offset, int length) {

        BooleanQuery query = new BooleanQuery();

        TermQuery tqCountry = new TermQuery(new Term(ResMovieFieldNameEnum.countryIds.getValue(), countryId));
        TermQuery tqGener = new TermQuery(new Term(ResMovieFieldNameEnum.genreIds.getValue(), genreId));
        TermQuery tqYear = new TermQuery(new Term(ResMovieFieldNameEnum.year.getValue(), genreId));

        query.add(tqCountry, Occur.MUST);
        query.add(tqGener, Occur.MUST);
        query.add(tqYear, Occur.MUST);

        Sort sort = new Sort(new SortField(orderName.getValue(), orderName.getType(), true));

        SearcherManager searchManager = searchManagerFactory.getSearchManager(IndexSearchNameEnum.indexSearchMovie);

        IndexSearcher indexSearch = null;

        int needLength = offset + length;
        try {
            indexSearch = searchManager.acquire();
            TopFieldDocs topFieldDocs = indexSearch.search(query, needLength, sort);
            ScoreDoc[] scoreDocs = topFieldDocs.scoreDocs;

            for (int i = offset; i < needLength; i++) {
                int docID = scoreDocs[i].doc;
                Document document = indexSearch.doc(docID);
                p(document);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != indexSearch) {
                try {
                    searchManager.release(indexSearch);
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public static void p(Document document) throws IOException {

        String title = document.get("title");
        String rating = document.get("rating");
        String year = document.get("year");
        String canPlay = document.get("canPlay");

        IndexableField[] akas = document.getFields("akas");
        for (IndexableField indexableField : akas) {
            System.out.println("akas:" + indexableField.stringValue());
        }

        IndexableField[] countries = document.getFields("countries");
        for (IndexableField indexableField : countries) {
            System.out.println("countries:" + indexableField.stringValue());
        }

        System.out.println("title:" + title);
        System.out.println("rating:" + rating);
        System.out.println("year:" + year);
        System.out.println("canPlay:" + canPlay);
    }
}
