package com.resource.search;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.Version;
import org.springframework.context.ApplicationContext;

import com.resources.dal.dataobject.ResMovieDO;
import com.resources.service.ResMovieService;

public class SearchMain {

    private static String             rootDir   = "/Users/zhiwenmizw/work/resources/output/search";

    private static String             indexFile = rootDir + "/index";

    private static ResMovieService    resMovieService;

    private static ApplicationContext context;

    public static void main(String[] args) throws Exception, ParseException {

        // context = new ClassPathXmlApplicationContext("service.xml", "service.xml");
        // resMovieService = (ResMovieService) context.getBean("resMovieService");

        // SearchMain main = new SearchMain();
        // main.buildIndex();
        // main.indexSearch();

        String end = "20141203", start = "20141102";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date endDate = sdf.parse(end);
        Date startDate = sdf.parse(start);

        Calendar cd = Calendar.getInstance();
        cd.setTime(endDate);
        int days1 = cd.get(Calendar.DAY_OF_YEAR);
        System.out.println(days1);

        cd.setTime(startDate);
        int days2 = cd.get(Calendar.DAY_OF_YEAR);
        System.out.println(days2);

        System.out.println(days1 - days2);

        long r = (endDate.getTime() - startDate.getTime()) / 1000 / 60 / 60 / 24;
        System.out.println(r);

    }

    public void indexSearch() throws IOException, ParseException {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexFile)));
        IndexSearcher searcher = new IndexSearcher(reader);

        Term t = new Term("title", "莲娜的甜美生活");
        TermQuery tQuery = new TermQuery(t);
        TopDocs topDocs = searcher.search(tQuery, 1);

        p(topDocs, searcher);

        Analyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser("summary", analyzer);
        Query query = parser.parse("却牵连出两个");
        topDocs = searcher.search(query, 10);
        System.out.println("===============1========================");
        p(topDocs, searcher);
        System.out.println("================2=======================");
        Sort sort = new Sort(new SortField("year", Type.STRING, true));
        query = parser.parse("的");
        topDocs = searcher.search(query, 10, sort);
        p(topDocs, searcher);
        System.out.println("================3=======================");

        BytesRefBuilder brb = new BytesRefBuilder();
        brb.append(new BytesRef());
        NumericUtils.longToPrefixCoded(new Long(14), 0, brb);

        TermQuery tq1 = new TermQuery(new Term("genres", brb.get()));

        brb = new BytesRefBuilder();
        brb.append(new BytesRef());
        NumericUtils.longToPrefixCoded(new Long(52249), 0, brb);

        TermQuery tq2 = new TermQuery(new Term("countries", brb.get()));
        TermQuery tq3 = new TermQuery(new Term("year", "2006"));

        BooleanQuery bq = new BooleanQuery();
        bq.add(tq1, Occur.MUST);
        bq.add(tq2, Occur.MUST);
        bq.add(tq3, Occur.MUST);

        topDocs = searcher.search(bq, 30);
        p(topDocs, searcher);
    }

    public static void p(TopDocs topDocs, IndexSearcher searcher) throws IOException {
        ScoreDoc[] scoreDoc = topDocs.scoreDocs;
        for (int i = 0; i < scoreDoc.length; i++) {
            int docID = scoreDoc[i].doc;
            Document document = searcher.doc(docID);
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

            IndexableField[] genres = document.getFields("genres");
            for (IndexableField indexableField : genres) {
                System.out.println("genres:" + indexableField.stringValue());
            }

            System.out.println("title:" + title);
            System.out.println("rating:" + rating);
            System.out.println("year:" + year);
            System.out.println("canPlay:" + canPlay);
            System.out.println("--------------------------------");
        }
    }

    public void buildIndex() throws IOException {

        List<ResMovieDO> list = resMovieService.getMovieByPaginatorWithStatus(3, 0, 1000);

        Analyzer analyzer = new StandardAnalyzer();

        File file = new File(indexFile);

        Runtime rt = Runtime.getRuntime();
        rt.exec("rm -rf " + indexFile);

        Directory dir = FSDirectory.open(file);

        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
        iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

        IndexWriter writer = new IndexWriter(dir, iwc);

        for (ResMovieDO movieInfo : list) {

            Document document = new Document();

            Field title = new StringField("title", movieInfo.getTitle(), Field.Store.YES);

            // TextField summary = new TextField("summary", movieInfo.getSummary(), Field.Store.YES);
            if (null != movieInfo.getAka()) {
                for (String item : movieInfo.getAka()) {
                    Field field = new StringField("akas", item, Field.Store.YES);
                    document.add(field);
                }
            }
            if (null != movieInfo.getCastIds()) {
                for (Long item : movieInfo.getCastIds()) {
                    Field field = new LongField("casts", item, Field.Store.YES);
                    document.add(field);
                }
            }
            if (null != movieInfo.getCountryIds()) {
                for (Long item : movieInfo.getCountryIds()) {
                    Field field = new LongField("countries", item, Field.Store.YES);
                    document.add(field);
                }
            }

            if (null != movieInfo.getGenreIds()) {
                for (Long item : movieInfo.getGenreIds()) {
                    Field field = new LongField("genres", item, Field.Store.YES);
                    document.add(field);
                }
            }

            Field canPlay = new IntField("canPlay", movieInfo.getPlayable(), Field.Store.YES);
            Field year = new StringField("year", movieInfo.getYear(), Field.Store.YES);

            document.add(title);
            document.add(canPlay);
            document.add(year);

            writer.addDocument(document);
        }

        writer.close();
    }
}
