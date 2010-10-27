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

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * Performs Lucene-based searching and indexing.
 *
 * @author Sindre Mehus
 * @version $Id$
 * @see SearchService
 */
public class LuceneSearchService {

    private static final File INDEX_DIR = new File("/tmp/subsonic-lucene-index");

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
                System.out.println("  Adding " + song.title);
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

    private Document createDocumentForSong(SearchService.Line song) {

        // make a new, empty document
        Document doc = new Document();

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

    public static void main(String[] args) throws IOException {
        List<SearchService.Line> songs = new ArrayList<SearchService.Line>();
        for (String s : readLines(new FileInputStream("/var/subsonic/subsonic10.index"))) {

//            System.out.println(line);
            SearchService.Line line = SearchService.Line.parse(s);
            if (line.isFile) {
                songs.add(line);
            }
        }

        LuceneSearchService service = new LuceneSearchService();
        service.createSongIndex(songs);
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
}
