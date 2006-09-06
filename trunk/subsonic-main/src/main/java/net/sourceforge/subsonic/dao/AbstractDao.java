package net.sourceforge.subsonic.dao;

import org.springframework.jdbc.core.*;

/**
 * Abstract superclass for all DAO's.
 *
 * @author Sindre Mehus
 */
public class AbstractDao {
    private DaoHelper daoHelper;

    /**
     * Returns a JDBC template for performing database operations.
     * @return A JDBC template.
     */
    public JdbcTemplate getJdbcTemplate() {
        return daoHelper.getJdbcTemplate();
    }

    protected String questionMarks(String columns) {
        int count = columns.split(", ").length;
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < count; i++) {
            buf.append('?');
            if (i < count - 1) {
                buf.append(", ");
            }
        }
        return buf.toString();
    }

    public void setDaoHelper(DaoHelper daoHelper) {
        this.daoHelper = daoHelper;
    }
}
