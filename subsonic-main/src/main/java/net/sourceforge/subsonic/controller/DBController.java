package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.dao.DaoHelper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the DB admin page.
 *
 * @author Sindre Mehus
 */
public class DBController extends ParameterizableViewController {

    private DaoHelper daoHelper;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        String query = request.getParameter("query");
        if (query != null) {
            map.put("query", query);
            map.put("result", daoHelper.getJdbcTemplate().query(query, new ColumnMapRowMapper()));
        }

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    public void setDaoHelper(DaoHelper daoHelper) {
        this.daoHelper = daoHelper;
    }
}
