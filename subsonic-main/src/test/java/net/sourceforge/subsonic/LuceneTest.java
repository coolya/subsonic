package net.sourceforge.subsonic;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;

public class LuceneTest {
    
    private static final String INDEX_DIR = "c:/tmp/lucene";

    public static void main(String[] args) throws Exception {
        new LuceneTest();
    }


    public LuceneTest() throws Exception {
//        createIndex();
        readIndex();
//        search("joe");
    }

    private void readIndex() throws Exception {
        IndexReader reader = IndexReader.open(INDEX_DIR);

        for (int i = 0; i < reader.numDocs(); i++) {
            Document doc = reader.document(i);
            printDocument(doc);
        }

        reader.close();
    }

    private void search(String queryString) throws Exception {
        IndexReader reader = IndexReader.open(INDEX_DIR);
        Searcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();

        // TODO: Use MultiFieldQueryParser
        QueryParser parser = new QueryParser("title", analyzer);

        Query query = parser.parse(queryString);

        // TODO: Paging/streaming query
        TopDocs docs = searcher.search(query, null, 100);
        System.out.println("Hits: " + docs.totalHits);
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            printDocument(doc);
        }
        searcher.close();
        reader.close();
    }

    private void printDocument(Document doc) {
        System.out.println("Artist: " + doc.get("artist"));
        System.out.println("Album: " + doc.get("album"));
        System.out.println("Title: " + doc.get("title"));
        System.out.println("Path: " + doc.get("path"));
    }

    private void createIndex() throws IOException {
        IndexWriter writer = new IndexWriter(INDEX_DIR, new StandardAnalyzer(), true, IndexWriter.MaxFieldLength.LIMITED);
        System.out.println("Indexing to directory '" + INDEX_DIR + "'...");

        writer.addDocument(createDocument("path1", "Jimi Hendrix", "Are You Experienced", "Foxy Lady"));
        writer.addDocument(createDocument("path2", "Jimi Hendrix", "Are You Experienced", "Manic Depression"));
        writer.addDocument(createDocument("path3", "Jimi Hendrix", "Are You Experienced", "Fire"));
        writer.addDocument(createDocument("path4", "Jimi Hendrix", "Are You Experienced", "Hey Joe"));
        writer.addDocument(createDocument("path5", "Kula Shaker", "Return Of The King", "Troubadour"));
        writer.addDocument(createDocument("path6", "Kula Shaker", "Return Of The King", "Six Feet Down"));

        writer.optimize();
        writer.close();
    }

    Document createDocument(String path, String artist, String album, String title) {
        Document doc = new Document();

        doc.add(new Field("path", path, Field.Store.YES, Field.Index.NO));

        // TODO: Use ANALYZED?
        doc.add(new Field("artist", artist, Field.Store.YES, Field.Index.ANALYZED_NO_NORMS));
        doc.add(new Field("album", album, Field.Store.YES, Field.Index.ANALYZED_NO_NORMS));
        doc.add(new Field("title", title, Field.Store.YES, Field.Index.ANALYZED_NO_NORMS));

        return doc;
    }
}