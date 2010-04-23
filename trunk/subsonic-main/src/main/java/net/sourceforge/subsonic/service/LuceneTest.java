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

 Copyright 2010 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.MusicFile;
import net.sourceforge.subsonic.domain.MusicFolder;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Sindre Mehus
 */
public class LuceneTest {

    private static final File INDEX_DIR = new File("/tmp/subsonic-index");
    private static final Logger LOG = Logger.getLogger(SearchService.class);


    /*
    Possible strategy:

    1. Iterate

     */

    public LuceneTest() {

    }


    public void createIndex() throws Exception {
        IndexWriter writer = new IndexWriter(FSDirectory.open(INDEX_DIR), new StandardAnalyzer(Version.LUCENE_CURRENT), true, IndexWriter.MaxFieldLength.LIMITED);
        System.out.println("Indexing to directory '" + INDEX_DIR + "'...");
        indexFiles(writer);
        System.out.println("Optimizing...");
        writer.optimize();
        writer.close();
    }

    private void indexFiles(IndexWriter writer) throws IOException {
        Document doc = new Document();

        File f = null;

        // Add the path of the file as a field named "path".  Use a field that is
        // indexed (i.e. searchable), but don't tokenize the field into words.
        doc.add(new Field("path", f.getPath(), Field.Store.YES, Field.Index.NOT_ANALYZED));

        // Add the last modified date of the file a field named "modified".  Use
        // a field that is indexed (i.e. searchable), but don't tokenize the field
        // into words.
        doc.add(new Field("modified",
                          DateTools.timeToString(f.lastModified(), DateTools.Resolution.MINUTE),
                          Field.Store.YES, Field.Index.NOT_ANALYZED));

        // Add the contents of the file to a field named "contents".  Specify a Reader,
        // so that the text of the file is tokenized and indexed, but not stored.
        // Note that FileReader expects the file to be in the system's default encoding.
        // If that's not the case searching for special characters will fail.
        doc.add(new Field("contents", new FileReader(f)));

        writer.addDocument(doc);
    }

    public void search() throws IOException {

        IndexReader reader = IndexReader.open(FSDirectory.open(INDEX_DIR), true);

        Searcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);

        Query query = new TermQuery(new Term("id", ""));
        searcher.search(query, 1);

    }


    private void foo() {

    }
}
