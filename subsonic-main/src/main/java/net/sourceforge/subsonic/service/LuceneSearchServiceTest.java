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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import net.sourceforge.subsonic.Logger;
import net.sourceforge.subsonic.domain.SearchCriteria;

public class LuceneSearchServiceTest {

    private static final Logger LOG = Logger.getLogger(LuceneSearchServiceTest.class);

    public static void main(String[] args) throws Exception {

        LuceneSearchService service = new LuceneSearchService();
        if (args.length > 0 && args[0].equals("-i")) {
            List<SearchService.Line> songs = new ArrayList<SearchService.Line>();
            List<SearchService.Line> albums = new ArrayList<SearchService.Line>();
            for (String s : readLines(new FileInputStream(new File(SettingsService.getSubsonicHome(), "subsonic11.index")))) {

                SearchService.Line line = SearchService.Line.parse(s);
                if (line.isFile) {
                    songs.add(line);
                }
                if (line.isAlbum) {
                    albums.add(line);
                }
            }

            long t0 = System.currentTimeMillis();
            service.createIndex(LuceneSearchService.IndexType.SONG, songs);
            service.createIndex(LuceneSearchService.IndexType.ALBUM, albums);
            long t1 = System.currentTimeMillis();
            System.out.println(songs.size() + " songs in " + (t1 - t0) + " ms");
            System.out.println(albums.size() + " albums in " + (t1 - t0) + " ms");
        } else {

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.print("\nEnter query: ");
                String query = in.readLine().trim();

                SearchCriteria criteria = new SearchCriteria();
                criteria.setOffset(0);
                criteria.setCount(5);
                criteria.setAny(query);
                LuceneSearchService.IndexType[] types = LuceneSearchService.IndexType.values();
                for (LuceneSearchService.IndexType indexType : types) {
                    System.out.println("\n" + indexType);
                    System.out.println("----------------------");
                    service.search(criteria, indexType);
                }
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
}
    

