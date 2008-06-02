package net.sourceforge.subsonic.jmeplayer;

/**
 * @author Sindre Mehus
 */
public final class Util {

    private Util() {
    }

    /**
     * splits the URL in the parts
     * E.g: http://www.12fb.com:80/Media/MIDI/fb.mid#1
     * <p/>
     * 0: protocol (e.g. http)
     * 1: host (e.g. www.12fb.com)
     * 2: port (e.g. 80)
     * 3: path (e.g. /Media/MIDI)
     * 4: file (e.g. fb.mid)
     * 5: anchor (e.g. 1)
     * <p/>
     * LIMITATION: URL must end with a slash if it is a directory
     */
    public static String[] splitURL(String url) throws Exception {
        StringBuffer u = new StringBuffer(url);
        String[] result = new String[6];
        for (int i = 0; i <= 5; i++) {
            result[i] = "";
        }
        // get protocol
        int index = url.indexOf(":");
        if (index > 0) {
            result[0] = url.substring(0, index);
            u.delete(0, index + 1);
        } else if (index == 0) {
            throw new Exception("url format error - protocol");
        }
        // check for host/port
        if (u.length() > 2 && u.charAt(0) == '/' && u.charAt(1) == '/') {
            // found domain part
            u.delete(0, 2);
            int slash = u.toString().indexOf('/');
            if (slash < 0) {
                slash = u.length();
            }
            int colon = u.toString().indexOf(':');
            int endIndex = slash;
            if (colon >= 0) {
                if (colon > slash) {
                    throw new Exception("url format error - port");
                }
                endIndex = colon;
                result[2] = u.toString().substring(colon + 1, slash);
            }
            result[1] = u.toString().substring(0, endIndex);
            u.delete(0, slash);
        }
        // get filename
        if (u.length() > 0) {
            url = u.toString();
            int slash = url.lastIndexOf('/');
            if (slash > 0) {
                result[3] = url.substring(0, slash);
            }
            if (slash < url.length() - 1) {
                String fn = url.substring(slash + 1, url.length());
                int anchorIndex = fn.indexOf("#");
                if (anchorIndex >= 0) {
                    result[4] = fn.substring(0, anchorIndex);
                    result[5] = fn.substring(anchorIndex + 1);
                } else {
                    result[4] = fn;
                }
            }
        }
        return result;
    }


    public static String guessContentType(String url) throws Exception {
        // guess content type
        String[] sURL = splitURL(url);
        String ext = "";
        String ct = "";
        int lastDot = sURL[4].lastIndexOf('.');
        if (lastDot >= 0) {
            ext = sURL[4].substring(lastDot + 1).toLowerCase();
        }
        if (ext.equals("mpg") || url.equals("avi")) {
            ct = "video/mpeg";
        } else if (ext.equals("mid") || ext.equals("kar")) {
            ct = "audio/midi";
        } else if (ext.equals("wav")) {
            ct = "audio/x-wav";
        } else if (ext.equals("jts")) {
            ct = "audio/x-tone-seq";
        } else if (ext.equals("txt")) {
            ct = "audio/x-txt";
        } else if (ext.equals("amr")) {
            ct = "audio/amr";
        } else if (ext.equals("awb")) {
            ct = "audio/amr-wb";
        } else if (ext.equals("gif")) {
            ct = "image/gif";
        }
        return ct;
    }
}
