package net.sourceforge.subsonic.dao;

/**
 * Unit test of {@link net.sourceforge.subsonic.dao.InternetRadioDao}.
 * @author Sindre Mehus
 * @version $Revision: 1.3 $ $Date: 2006/02/25 16:11:14 $
 */

import net.sourceforge.subsonic.domain.*;
import org.springframework.jdbc.core.*;

public class InternetRadioDaoTestCase extends DaoTestCaseBase {

    private InternetRadioDao internetRadioDao;

    protected void setUp() throws Exception {
        internetRadioDao = new InternetRadioDao();
        JdbcTemplate template = internetRadioDao.getJdbcTemplate();
        template.execute("delete from internet_radio");
    }

    public void testCreateInternetRadio() {
        InternetRadio radio = new InternetRadio("name", "streamUrl", "homePageUrl", true);
        internetRadioDao.createInternetRadio(radio);

        InternetRadio newRadio = internetRadioDao.getAllInternetRadios()[0];
        assertInternetRadioEquals(radio, newRadio);
    }

    public void testUpdateInternetRadio() {
        InternetRadio radio = new InternetRadio("name", "streamUrl", "homePageUrl", true);
        internetRadioDao.createInternetRadio(radio);
        radio = internetRadioDao.getAllInternetRadios()[0];

        radio.setName("newName");
        radio.setStreamUrl("newStreamUrl");
        radio.setHomepageUrl("newHomePageUrl");
        radio.setEnabled(false);
        internetRadioDao.updateInternetRadio(radio);

        InternetRadio newRadio = internetRadioDao.getAllInternetRadios()[0];
        assertInternetRadioEquals(radio, newRadio);
    }

    public void testDeleteInternetRadio() {
        assertEquals("Wrong number of radios.", 0, internetRadioDao.getAllInternetRadios().length);

        internetRadioDao.createInternetRadio(new InternetRadio("name", "streamUrl", "homePageUrl", true));
        assertEquals("Wrong number of radios.", 1, internetRadioDao.getAllInternetRadios().length);

        internetRadioDao.createInternetRadio(new InternetRadio("name", "streamUrl", "homePageUrl", true));
        assertEquals("Wrong number of radios.", 2, internetRadioDao.getAllInternetRadios().length);

        internetRadioDao.deleteInternetRadio(internetRadioDao.getAllInternetRadios()[0].getId());
        assertEquals("Wrong number of radios.", 1, internetRadioDao.getAllInternetRadios().length);

        internetRadioDao.deleteInternetRadio(internetRadioDao.getAllInternetRadios()[0].getId());
        assertEquals("Wrong number of radios.", 0, internetRadioDao.getAllInternetRadios().length);
    }

    private void assertInternetRadioEquals(InternetRadio expected, InternetRadio actual) {
        assertEquals("Wrong name.", expected.getName(), actual.getName());
        assertEquals("Wrong stream url.", expected.getStreamUrl(), actual.getStreamUrl());
        assertEquals("Wrong home page url.", expected.getHomepageUrl(), actual.getHomepageUrl());
        assertEquals("Wrong enabled state.", expected.isEnabled(), actual.isEnabled());
    }


}