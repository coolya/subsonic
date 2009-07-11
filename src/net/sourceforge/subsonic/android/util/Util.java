/*
 * (c) Copyright WesternGeco. Unpublished work, created 2009. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.android.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Sindre Mehus
 * @version $Id$
 */
public final class Util {

    private Util() {
    }

    public static long copy(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[1024 * 4];
        long count = 0;
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static void close(InputStream in) {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    public static void close(OutputStream out) {
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

}