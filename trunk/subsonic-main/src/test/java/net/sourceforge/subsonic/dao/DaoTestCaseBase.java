package net.sourceforge.subsonic.dao;

import junit.framework.*;
import org.springframework.jdbc.core.*;

import java.io.*;

/**
 * Superclass for all DAO test cases.
 * Creates and configures the DAO's, and resets the test database.
 *
 * @author Sindre Mehus
 */
public abstract class DaoTestCaseBase extends TestCase {

    /** Do not re-create database if it is less than one hour old. */
    private static final long MAX_DB_AGE_MILLIS = 60L * 60 * 1000;

    static {
        deleteDatabase();
    }

    private DaoHelper daoHelper;
    protected PlayerDao playerDao;
    protected InternetRadioDao internetRadioDao;
    protected MusicFileInfoDao musicFileInfoDao;
    protected MusicFolderDao musicFolderDao;
    protected UserDao userDao;
    protected TranscodingDao transcodingDao;

    protected DaoTestCaseBase() {
        daoHelper = new DaoHelper();

        playerDao = new PlayerDao();
        internetRadioDao = new InternetRadioDao();
        musicFileInfoDao = new MusicFileInfoDao();
        musicFolderDao = new MusicFolderDao();
        userDao = new UserDao();
        transcodingDao = new TranscodingDao();

        playerDao.setDaoHelper(daoHelper);
        internetRadioDao.setDaoHelper(daoHelper);
        musicFileInfoDao.setDaoHelper(daoHelper);
        musicFolderDao.setDaoHelper(daoHelper);
        userDao.setDaoHelper(daoHelper);
        transcodingDao.setDaoHelper(daoHelper);
    }

    protected JdbcTemplate getJdbcTemplate() {
        return daoHelper.getJdbcTemplate();
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
