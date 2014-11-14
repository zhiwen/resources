package com.resource.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
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
import org.apache.lucene.util.Version;

import com.google.common.collect.ImmutableList;

public class SearchMain {

    private static String rootDir   = "/Users/zhiwenmizw/work/resources/output/search";

    private static String indexFile = rootDir + "/index";

    public static void main(String[] args) throws IOException, ParseException {
        SearchMain main = new SearchMain();
        main.buildIndex();
        main.indexSearch();
    }

    public static class MovieInfo {

        private String       title;
        private String       summary;
        private List<String> akas;
        private List<String> casts;
        private List<String> countries;
        private List<String> genres;
        private float        rating;
        private boolean      canPlay;
        private String       year;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public List<String> getAkas() {
            return akas;
        }

        public void setAkas(List<String> akas) {
            this.akas = akas;
        }

        public List<String> getCasts() {
            return casts;
        }

        public void setCasts(List<String> casts) {
            this.casts = casts;
        }

        public List<String> getCountries() {
            return countries;
        }

        public void setCountries(List<String> countries) {
            this.countries = countries;
        }

        public List<String> getGenres() {
            return genres;
        }

        public void setGenres(List<String> genres) {
            this.genres = genres;
        }

        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }

        public boolean isCanPlay() {
            return canPlay;
        }

        public void setCanPlay(boolean canPlay) {
            this.canPlay = canPlay;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }
    }

    public static List<MovieInfo> getMovieInfos() {
        List<MovieInfo> movieInfoList = new ArrayList<SearchMain.MovieInfo>();

        MovieInfo m1 = new MovieInfo();
        m1.setTitle("人再囧途之泰囧");
        ImmutableList<String> akas = ImmutableList.of("人在囧途2", "泰囧", "人再囧途之泰囧");
        m1.setAkas(akas);
        m1.setSummary("商业成功人士徐朗（徐峥 饰）用了五年时间发明了一种叫的神奇产品——每次汽车加油只需加到三分之二，再滴入2滴“油霸”，油箱的汽油就会变成满满一箱");

        ImmutableList<String> casts = ImmutableList.of("徐峥", "王宝强", "黄渤", "陶虹", "谢楠", "范冰冰");
        m1.setCasts(casts);
        ImmutableList<String> countries = ImmutableList.of("中国");
        m1.setCountries(countries);
        ImmutableList<String> genres = ImmutableList.of("喜剧", "搞笑");
        m1.setGenres(genres);
        m1.setRating(7.8f);
        m1.setCanPlay(true);
        m1.setYear("2012");

        MovieInfo m2 = new MovieInfo();
        m2.setTitle("秘术");
        akas = ImmutableList.of("秘术之盗墓江湖", "盗墓空间", "Mystery");
        m2.setAkas(akas);
        m2.setSummary("一天一夜，两个身世不明的孤男寡女狭路相逢，却牵连出两个家族绵延百年的盗墓江湖恩怨。他们因为一册封印百年的《盗墓秘术》秘籍陷入万劫不复的生死绝境。摸金、发丘、搬山、御岭，盗墓四大门派纷争再起，机关重重，敌友难辨。江湖恩怨，后辈继承，冥冥之中，自有人引导你走向墓室的黑暗深处");
        casts = ImmutableList.of("安以轩", "汪东城", "郭德纲", "宁桓宇", "陈欣淇");
        m2.setCasts(casts);
        countries = ImmutableList.of("中国大陆", "法国2");
        m2.setCountries(countries);
        genres = ImmutableList.of("悬疑", "惊悚", "冒险", "科幻");
        m2.setGenres(genres);
        m2.setRating(7.8f);
        m2.setCanPlay(true);
        m2.setYear("2015");

        MovieInfo m3 = new MovieInfo();
        m3.setTitle("超体");
        akas = ImmutableList.of("Lucy", "超能煞姬(港)", "露西(台)", "超能毒贩");
        m3.setAkas(akas);
        m3.setSummary("居住在台北的年轻女子露西（斯嘉丽·约翰逊 Scarlett Johansson 饰），被迫成为“人体快递”， 胃里被黑帮植入一种新型病毒，在一次被暴打后，胃里的病毒融入血液，从而使她意外拥有了用意念便可控制周围物体的超能力，比如预见未来、用意念移动物品、消除疼痛、瞬间变身等");
        casts = ImmutableList.of("斯嘉丽·约翰逊", "摩根·弗里曼", "崔岷植", "阿马尔·维克德", "朱利安·林希德-图特", "乔汗·菲利普·阿斯巴克", "安娜丽·提普顿",
                                 "詹·奥利弗·施罗德", "弗雷德里克·周", "克莱尔·陈", "塞德里克·舍瓦姆", "邵斯凡", "保罗·陈", "林暐恒");
        m3.setCasts(casts);
        countries = ImmutableList.of("法国");
        m3.setCountries(countries);
        genres = ImmutableList.of("动作", "科幻");
        m3.setGenres(genres);
        m3.setRating(7.8f);
        m3.setCanPlay(true);
        m3.setYear("2014");

        movieInfoList.add(m1);
        movieInfoList.add(m2);
        movieInfoList.add(m3);
        return movieInfoList;
    }

    public void indexSearch() throws IOException, ParseException {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexFile)));
        IndexSearcher searcher = new IndexSearcher(reader);

        Term t = new Term("title", "超体");
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

        TermQuery tq1 = new TermQuery(new Term("genres", "科幻"));
        TermQuery tq2 = new TermQuery(new Term("countries", "法国"));

        BooleanQuery bq = new BooleanQuery();
        bq.add(tq1, Occur.MUST);
        bq.add(tq2, Occur.MUST);

        topDocs = searcher.search(bq, 10);
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

            System.out.println("title:" + title);
            System.out.println("rating:" + rating);
            System.out.println("year:" + year);
            System.out.println("canPlay:" + canPlay);
        }
    }

    public void buildIndex() throws IOException {

        Analyzer analyzer = new StandardAnalyzer();

        File file = new File(indexFile);

        Runtime rt = Runtime.getRuntime();
        rt.exec("rm -rf " + indexFile);

        Directory dir = FSDirectory.open(file);

        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
        iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

        IndexWriter writer = new IndexWriter(dir, iwc);

        List<MovieInfo> movieInfoList = getMovieInfos();
        for (MovieInfo movieInfo : movieInfoList) {

            Document document = new Document();

            Field title = new StringField("title", movieInfo.getTitle(), Field.Store.YES);

            TextField summary = new TextField("summary", movieInfo.getSummary(), Field.Store.YES);

            for (String item : movieInfo.getAkas()) {
                Field field = new StringField("akas", item, Field.Store.YES);
                document.add(field);
            }

            for (String item : movieInfo.getCasts()) {
                Field field = new StringField("casts", item, Field.Store.YES);
                document.add(field);
            }

            for (String item : movieInfo.getCountries()) {
                Field field = new StringField("countries", item, Field.Store.YES);
                document.add(field);
            }

            for (String item : movieInfo.getGenres()) {
                Field field = new StringField("genres", item, Field.Store.YES);
                document.add(field);
            }

            Field rating = new FloatField("rating", movieInfo.getRating(), Field.Store.YES);
            Field canPlay = new IntField("canPlay", movieInfo.isCanPlay() ? 1 : 0, Field.Store.YES);
            Field year = new StringField("year", movieInfo.getYear(), Field.Store.YES);

            document.add(title);
            document.add(summary);
            document.add(rating);
            document.add(canPlay);
            document.add(year);

            writer.addDocument(document);
        }

        writer.close();
    }
}
