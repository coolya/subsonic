package net.sourceforge.subsonic.dao;

import net.sourceforge.subsonic.domain.*;

/**
 * Unit test of {@link TranscodingDao}.
 * @author Sindre Mehus
 */
public class TranscodingDaoTestCase extends DaoTestCaseBase {

    protected void setUp() throws Exception {
        getJdbcTemplate().execute("delete from transcoding");
    }

    public void testCreateTranscoding() {
        Transcoding transcoding = new Transcoding(null, "name", "sourceFormat", "targetFormat", "step1", "step2", "step3", true);
        transcodingDao.createTranscoding(transcoding);

        Transcoding newTranscoding = transcodingDao.getAllTranscodings()[0];
        assertTranscodingEquals(transcoding, newTranscoding);
    }

    public void testUpdateTranscoding() {
        Transcoding transcoding = new Transcoding(null, "name", "sourceFormat", "targetFormat", "step1", "step2", "step3", true);
        transcodingDao.createTranscoding(transcoding);
        transcoding = transcodingDao.getAllTranscodings()[0];

        transcoding.setName("newName");
        transcoding.setSourceFormat("newSourceFormat");
        transcoding.setTargetFormat("newTargetFormat");
        transcoding.setStep1("newStep1");
        transcoding.setStep2("newStep2");
        transcoding.setStep3("newStep3");
        transcoding.setEnabled(false);
        transcodingDao.updateTranscoding(transcoding);

        Transcoding newTranscoding = transcodingDao.getAllTranscodings()[0];
        assertTranscodingEquals(transcoding, newTranscoding);
    }

    public void testDeleteTranscoding() {
        assertEquals("Wrong number of transcodings.", 0, transcodingDao.getAllTranscodings().length);

        transcodingDao.createTranscoding(new Transcoding(null, "name", "sourceFormat", "targetFormat", "step1", "step2", "step3", true));
        assertEquals("Wrong number of transcodings.", 1, transcodingDao.getAllTranscodings().length);

        transcodingDao.createTranscoding(new Transcoding(null, "name", "sourceFormat", "targetFormat", "step1", "step2", "step3", true));
        assertEquals("Wrong number of transcodings.", 2, transcodingDao.getAllTranscodings().length);

        transcodingDao.deleteTranscoding(transcodingDao.getAllTranscodings()[0].getId());
        assertEquals("Wrong number of transcodings.", 1, transcodingDao.getAllTranscodings().length);

        transcodingDao.deleteTranscoding(transcodingDao.getAllTranscodings()[0].getId());
        assertEquals("Wrong number of transcodings.", 0, transcodingDao.getAllTranscodings().length);
    }

    private void assertTranscodingEquals(Transcoding expected, Transcoding actual) {
        assertEquals("Wrong name.", expected.getName(), actual.getName());
        assertEquals("Wrong source format.", expected.getSourceFormat(), actual.getSourceFormat());
        assertEquals("Wrong target format.", expected.getTargetFormat(), actual.getTargetFormat());
        assertEquals("Wrong step 1.", expected.getStep1(), actual.getStep1());
        assertEquals("Wrong step 2.", expected.getStep2(), actual.getStep2());
        assertEquals("Wrong step 3.", expected.getStep3(), actual.getStep3());
        assertEquals("Wrong enabled state.", expected.isEnabled(), actual.isEnabled());
    }


}
