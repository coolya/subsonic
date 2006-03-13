package net.sourceforge.subsonic.dao;

import junit.framework.*;

import java.io.*;

/**
 * Deletes the test database.
 *
 * @author Sindre Mehus
 * @version $Revision: 1.3 $ $Date: 2006/02/25 16:11:14 $
 */
public abstract class DaoTestCaseBase extends TestCase {

    /** Do not re-create database if it is less than one hour old. */
    private static final long MAX_DB_AGE_MILLIS = 60L * 60 * 1000;

    static {
        deleteDatabase();
    }

    private static void deleteDatabase() {
        File subsonicHome = new File("/tmp/subsonic");
        File dbHome = new File(subsonicHome, "db");
        System.setProperty("subsonic.home", subsonicHome.getPath());

        long now = System.currentTimeMillis();
        if (now - dbHome.lastModified() > MAX_DB_AGE_MILLIS) {
            System.out.println("Resetting test database: " + dbHome);
            delete(dbHome);
        }
    }

    private static void delete(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                delete(child);
            }
        }
        file.delete();
    }
}
