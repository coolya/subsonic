/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.domain.SearchCriteria;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.FilterIndexReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.queryParser.QueryParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Performs Lucene-based searching and indexing.
 *
 * @author Sindre Mehus
 * @version $Id$
 * @see SearchService
 */
public class LuceneSearchService {

    private static final File INDEX_DIR = new File("/tmp/subsonic-lucene-index");
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_CREATED = "created";
    private static final String FIELD_ALBUM = "album";
    private static final String FIELD_ARTIST = "artist";
    private static final String FIELD_PATH = "path";

    /**
     * Creates a search index for the given list of songs.
     *
     * @param songs List of songs.
     */
    public void createSongIndex(List<SearchService.Line> songs) {

        try {
            IndexWriter writer = new IndexWriter(FSDirectory.open(INDEX_DIR), new StandardAnalyzer(Version.LUCENE_CURRENT), true, IndexWriter.MaxFieldLength.LIMITED);
            System.out.println("Indexing to directory '" + INDEX_DIR + "'...");
            for (SearchService.Line song : songs) {
                writer.addDocument(createDocumentForSong(song));
            }
            System.out.println("Optimizing...");
            writer.optimize();
            writer.close();
            System.out.println("Done.");
        } catch (Throwable x) {
            x.printStackTrace();

        }
    }

    public void search(SearchCriteria criteria) throws Exception {
        IndexReader reader = IndexReader.open(FSDirectory.open(INDEX_DIR), true);

//        if (normsField != null)
//            reader = new OneNormsReader(reader, normsField);

        Searcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
        QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, FIELD_TITLE, analyzer);

        // TODO: trim
        Query query = parser.parse(criteria.getTitle());
        System.out.println("Searching for: " + query.toString(FIELD_TITLE));

        // TODO: paging
        TopDocs topDocs = searcher.search(query, null, 10);
        System.out.println("Total hits: " + topDocs.totalHits);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println(doc.get(FIELD_TITLE));
        }
    }

    private Document createDocumentForSong(SearchService.Line song) {

        // make a new, empty document
        Document doc = new Document();
        doc.add(new Field(FIELD_PATH, song.file.getPath(), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));

        if (song.artist != null) {
            doc.add(new Field(FIELD_ARTIST, song.artist, Field.Store.YES, Field.Index.ANALYZED));
        }
        if (song.album != null) {
            doc.add(new Field(FIELD_ALBUM, song.album, Field.Store.YES, Field.Index.ANALYZED));
        }
        if (song.title != null) {
            doc.add(new Field(FIELD_TITLE, song.title, Field.Store.YES, Field.Index.ANALYZED));
        }
        doc.add(new Field(FIELD_CREATED, DateTools.timeToString(song.created, DateTools.Resolution.DAY),
                          Field.Store.YES, Field.Index.NOT_ANALYZED));

//        // Add the path of the file as a field named "path".  Use a field that is
//        // indexed (i.e. searchable), but don't tokenize the field into words.
//        doc.add(new Field("path", f.getPath(), Field.Store.YES, Field.Index.NOT_ANALYZED));
//
//        // Add the last modified date of the file a field named "modified".  Use
//        // a field that is indexed (i.e. searchable), but don't tokenize the field
//        // into words.
//        doc.add(new Field("modified",
//                          DateTools.timeToString(f.lastModified(), DateTools.Resolution.MINUTE),
//                          Field.Store.YES, Field.Index.NOT_ANALYZED));
//
//        // Add the contents of the file to a field named "contents".  Specify a Reader,
//        // so that the text of the file is tokenized and indexed, but not stored.
//        // Note that FileReader expects the file to be in the system's default encoding.
//        // If that's not the case searching for special characters will fail.
//        doc.add(new Field("contents", new FileReader(f)));

        // return the document
        return doc;
    }

    public static void main(String[] args) throws Exception {
        LuceneSearchService service = new LuceneSearchService();
        if (args.length > 0 && args[0].equals("-i")) {
            List<SearchService.Line> songs = new ArrayList<SearchService.Line>();
            for (String s : readLines(new FileInputStream("/var/subsonic/subsonic10.index"))) {

//            System.out.println(line);
                SearchService.Line line = SearchService.Line.parse(s);
                if (line.isFile) {
                    songs.add(line);
                }
            }

            long t0 = System.currentTimeMillis();
            service.createSongIndex(songs);
            long t1 = System.currentTimeMillis();
            System.out.println(songs.size() + " songs in " + (t1 - t0) + " ms");
        } else {

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.print("\nEnter query: ");
                String query = in.readLine().trim();

                SearchCriteria criteria = new SearchCriteria();
                criteria.setTitle(query);
                service.search(criteria);
            }
        }

    }

    private static String[] readLines(InputStream in) throws IOException {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(in));
            List<String> result = new ArrayList<String>();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if (!line.startsWith("#") && line.length() > 0) {
                    result.add(line);
                }
            }
            return result.toArray(new String[result.size()]);

        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(reader);
        }
    }

    /**
     * Use the norms from one field for all fields.  Norms are read into memory,
     * using a byte of memory per document per searched field.  This can cause
     * search of large collections with a large number of fields to run out of
     * memory.  If all of the fields contain only a single token, then the norms
     * are all identical, then single norm vector may be shared.
     */
    private static class OneNormsReader extends FilterIndexReader {

        private final String field;

        public OneNormsReader(IndexReader in, String field) {
            super(in);
            this.field = field;
        }

        @Override
        public byte[] norms(String field) throws IOException {
            return in.norms(this.field);
        }
    }
}
