/* This file is part of NanoXML.
 *
 * NanoXML 1.6.8 for J2ME -- This file has been altered from the
 * original, whose copyright message is shown below.  The alterations
 * are small and only enough to make it compatible with CLDC-based
 * J2ME profiles.
 *
 * Modifications are by Eric Giguere, ericgiguere@ericgiguere.com.
 *
 * $Revision$
 * $Date$
 * $Name: RELEASE_1_6_8 $
 *
 * Copyright (C) 2000 Marc De Scheemaecker, All Rights Reserved.
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from the
 * use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software in
 *     a product, an acknowledgment in the product documentation would be
 *     appreciated but is not required.
 *
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 *
 *  3. This notice may not be removed or altered from any source distribution.
 */


package net.sourceforge.subsonic.jmeplayer.nanoxml;


/**
 * An kXMLParseException is thrown when an error occures while parsing an XML
 * string.
 * <p/>
 * $Revision$<BR>
 * $Date$<P>
 *
 * @author Marc De Scheemaecker
 *         &lt;<A HREF="mailto:Marc.DeScheemaecker@advalvas.be"
 *         >Marc.DeScheemaecker@advalvas.be</A>&gt;
 * @version 1.6
 * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement
 */
public class kXMLParseException
        extends RuntimeException {

    /**
     * Where the error occurred, or -1 if the line number is unknown.
     */
    private int lineNr;


    /**
     * Creates an exception.
     *
     * @param tag     The name of the tag where the error is located.
     * @param message A message describing what went wrong.
     */
    public kXMLParseException(String tag,
                              String message) {
        super("XML Parse Exception during parsing of "
              + ((tag == null) ? "the XML definition" : ("a " + tag + "-tag"))
              + ": " + message);
        this.lineNr = -1;
    }


    /**
     * Creates an exception.
     *
     * @param tag     The name of the tag where the error is located.
     * @param lineNr  The number of the line in the input.
     * @param message A message describing what went wrong.
     */
    public kXMLParseException(String tag,
                              int lineNr,
                              String message) {
        super("XML Parse Exception during parsing of "
              + ((tag == null) ? "the XML definition" : ("a " + tag + "-tag"))
              + " at line " + lineNr + ": " + message);
        this.lineNr = lineNr;
    }


    /**
     * Where the error occurred, or -1 if the line number is unknown.
     */
    public int getLineNr() {
        return this.lineNr;
    }

}
