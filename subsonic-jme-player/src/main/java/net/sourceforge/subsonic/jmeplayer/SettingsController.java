package net.sourceforge.subsonic.jmeplayer;

import javax.microedition.rms.RecordStore;

/**
 * @author Sindre Mehus
 */
public class SettingsController {

    private static final int RECORD_ID_BASE_URL = 1;
    private static final int RECORD_ID_USERNAME = 2;
    private static final int RECORD_ID_PASSWORD = 3;
    private static final int RECORD_ID_PLAYER = 4;
    private static final int RECORD_ID_DEBUG = 5;
    private static final int RECORD_ID_MOCK = 6;

    private static final String FALLBACK_BASE_URL = "http://yourhost/subsonic";
    private static final String DEFAULT_USERNAME = null;
    private static final String DEFAULT_PASSWORD = null;
    private static final int DEFAULT_PLAYER = 0;
    private static final boolean DEFAULT_DEBUG = false;
    private static final boolean DEFAULT_MOCK = false;

    private RecordStore recordStore;

    private String defaultBaseUrl;
    private String baseUrl;
    private String username = DEFAULT_USERNAME;
    private String password = DEFAULT_PASSWORD;
    private int player = DEFAULT_PLAYER;
    private boolean debug = DEFAULT_DEBUG;
    private boolean mock = DEFAULT_MOCK;

    public SettingsController(SubsonicMIDlet subsonicMidlet) {
        String baseUrlFromAppProperty = subsonicMidlet.getAppProperty("Subsonic-Base-URL");
        defaultBaseUrl = baseUrlFromAppProperty != null ? baseUrlFromAppProperty : FALLBACK_BASE_URL;
        baseUrl = defaultBaseUrl;

        try {
            recordStore = RecordStore.openRecordStore("settings", true);
            initRecordStore();
            readRecordStore();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPlayer() {
        return player;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isMock() {
        return mock;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        writeRecord(RECORD_ID_BASE_URL, baseUrl);
    }

    public void setUsername(String username) {
        this.username = username;
        writeRecord(RECORD_ID_USERNAME, username);
    }

    public void setPassword(String password) {
        this.password = password;
        writeRecord(RECORD_ID_PASSWORD, password);
    }

    public void setPlayer(int player) {
        this.player = player;
        writeRecord(RECORD_ID_PLAYER, String.valueOf(player));
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
        writeRecord(RECORD_ID_DEBUG, String.valueOf(debug));
    }

    public void setMock(boolean mock) {
        this.mock = mock;
        writeRecord(RECORD_ID_MOCK, String.valueOf(mock));
    }

    private void initRecordStore() throws Exception {
        if (recordStore.getNextRecordID() == RECORD_ID_BASE_URL) {
            addRecord(defaultBaseUrl);
        }
        if (recordStore.getNextRecordID() == RECORD_ID_USERNAME) {
            addRecord(DEFAULT_USERNAME);
        }
        if (recordStore.getNextRecordID() == RECORD_ID_PASSWORD) {
            addRecord(DEFAULT_PASSWORD);
        }
        if (recordStore.getNextRecordID() == RECORD_ID_PLAYER) {
            addRecord(String.valueOf(DEFAULT_PLAYER));
        }
        if (recordStore.getNextRecordID() == RECORD_ID_DEBUG) {
            addRecord(String.valueOf(DEFAULT_DEBUG));
        }
        if (recordStore.getNextRecordID() == RECORD_ID_MOCK) {
            addRecord(String.valueOf(DEFAULT_MOCK));
        }
    }

    private void readRecordStore() throws Exception {
        baseUrl = readRecord(RECORD_ID_BASE_URL);
        username = readRecord(RECORD_ID_USERNAME);
        password = readRecord(RECORD_ID_PASSWORD);
        player = Integer.parseInt(readRecord(RECORD_ID_PLAYER));
        debug = "true".equals(readRecord(RECORD_ID_DEBUG));
        mock = "true".equals(readRecord(RECORD_ID_MOCK));
    }

    private String readRecord(int recordId) throws Exception {
        byte[] bytes = recordStore.getRecord(recordId);
        if (bytes == null) {
            return null;
        }
        return new String(bytes, "UTF-8");
    }

    private void addRecord(String value) {
        try {
            byte[] bytes = value == null ? new byte[0] : value.getBytes("UTF-8");
            recordStore.addRecord(bytes, 0, bytes.length);
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    private void writeRecord(int recordId, String value) {
        try {
            byte[] bytes = value == null ? new byte[0] : value.getBytes("UTF-8");
            recordStore.setRecord(recordId, bytes, 0, bytes.length);
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}
