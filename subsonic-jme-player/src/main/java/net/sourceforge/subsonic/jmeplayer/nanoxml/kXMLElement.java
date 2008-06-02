/* This file is part of NanoXML.
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


import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


/**
 * kXMLElement is a representation of an XML object. The object is able to parse
 * XML code.
 * <p/>
 * Note that NanoXML is not 100% XML 1.0 compliant:
 * <UL><LI>The parser is non-validating.
 * <LI>The DTD is fully ignored, including <CODE>&lt;!ENTITY...&gt;</CODE>.
 * <LI>There is no support for mixed content (elements containing both
 * subelements and CDATA elements)
 * </UL>
 * <p/>
 * You can opt to use a SAX compatible API, by including both
 * <CODE>nanoxml.jar</CODE> and <CODE>nanoxml-sax.jar</CODE> in your classpath
 * and setting the property <CODE>org.xml.sax.parser</CODE> to
 * <CODE>nanoxml.sax.SAXParser</CODE>
 * <p/>
 * $Revision$<BR>
 * $Date$<P>
 *
 * @author Marc De Scheemaecker
 *         &lt;<A HREF="mailto:Marc.DeScheemaecker@advalvas.be"
 *         >Marc.DeScheemaecker@advalvas.be</A>&gt;
 * @version 1.6
 * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLParseException
 */
public class kXMLElement {
    /**
     * Major version of NanoXML.
     */
    public static final int NANOXML_MAJOR_VERSION = 1;


    /**
     * Minor version of NanoXML.
     */
    public static final int NANOXML_MINOR_VERSION = 6;


    /**
     * The attributes given to the object.
     */
    private Hashtable attributes;


    /**
     * Subobjects of the object. The subobjects are of class kXMLElement
     * themselves.
     */
    private Vector children;


    /**
     * The class of the object (the name indicated in the tag).
     */
    private String tagName;


    /**
     * The #PCDATA content of the object. If there is no such content, this
     * field is null.
     */
    private String contents;


    /**
     * Conversion table for &amp;...; tags.
     */
    private Hashtable conversionTable;


    /**
     * Whether to skip leading whitespace in CDATA.
     */
    private boolean skipLeadingWhitespace;


    /**
     * The line number where the element starts.
     */
    private int lineNr;


    /**
     * Whether the parsing is case sensitive.
     */
    private boolean ignoreCase;


    /**
     * Creates a new XML element. The following settings are used:
     * <DL><DT>Conversion table</DT>
     * <DD>Minimal XML conversions: <CODE>&amp;amp; &amp;lt; &amp;gt;
     * &amp;apos; &amp;quot;</CODE></DD>
     * <DT>Skip whitespace in contents</DT>
     * <DD><CODE>false</CODE></DD>
     * <DT>Ignore Case</DT>
     * <DD><CODE>true</CODE></DD>
     * </DL>
     *
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#kXMLElement(java.util.Hashtable)
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#kXMLElement(boolean)
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#kXMLElement(java.util.Hashtable,boolean)
     */
    public kXMLElement() {
        this(new Hashtable(), false, true, true);
    }


    /**
     * Creates a new XML element. The following settings are used:
     * <DL><DT>Conversion table</DT>
     * <DD><I>conversionTable</I> combined with the minimal XML
     * conversions: <CODE>&amp;amp; &amp;lt; &amp;gt;
     * &amp;apos; &amp;quot;</CODE></DD>
     * <DT>Skip whitespace in contents</DT>
     * <DD><CODE>false</CODE></DD>
     * <DT>Ignore Case</DT>
     * <DD><CODE>true</CODE></DD>
     * </DL>
     *
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#kXMLElement()
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#kXMLElement(boolean)
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#kXMLElement(java.util.Hashtable,boolean)
     */
    public kXMLElement(Hashtable conversionTable) {
        this(conversionTable, false, true, true);
    }


    /**
     * Creates a new XML element. The following settings are used:
     * <DL><DT>Conversion table</DT>
     * <DD>Minimal XML conversions: <CODE>&amp;amp; &amp;lt; &amp;gt;
     * &amp;apos; &amp;quot;</CODE></DD>
     * <DT>Skip whitespace in contents</DT>
     * <DD><I>skipLeadingWhitespace</I></DD>
     * <DT>Ignore Case</DT>
     * <DD><CODE>true</CODE></DD>
     * </DL>
     *
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#kXMLElement()
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#kXMLElement(java.util.Hashtable)
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#kXMLElement(java.util.Hashtable,boolean)
     */
    public kXMLElement(boolean skipLeadingWhitespace) {
        this(new Hashtable(), skipLeadingWhitespace, true, true);
    }


    /**
     * Creates a new XML element. The following settings are used:
     * <DL><DT>Conversion table</DT>
     * <DD><I>conversionTable</I> combined with the minimal XML
     * conversions: <CODE>&amp;amp; &amp;lt; &amp;gt;
     * &amp;apos; &amp;quot;</CODE></DD>
     * <DT>Skip whitespace in contents</DT>
     * <DD><I>skipLeadingWhitespace</I></DD>
     * <DT>Ignore Case</DT>
     * <DD><CODE>true</CODE></DD>
     * </DL>
     *
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#kXMLElement()
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#kXMLElement(boolean)
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#kXMLElement(java.util.Hashtable)
     */
    public kXMLElement(Hashtable conversionTable,
                       boolean skipLeadingWhitespace) {
        this(conversionTable, skipLeadingWhitespace, true, true);
    }


    /**
     * Creates a new XML element. The following settings are used:
     * <DL><DT>Conversion table</DT>
     * <DD><I>conversionTable</I>, eventually combined with the minimal XML
     * conversions: <CODE>&amp;amp; &amp;lt; &amp;gt;
     * &amp;apos; &amp;quot;</CODE>
     * (depending on <I>fillBasicConversionTable</I>)</DD>
     * <DT>Skip whitespace in contents</DT>
     * <DD><I>skipLeadingWhitespace</I></DD>
     * <DT>Ignore Case</DT>
     * <DD><I>ignoreCase</I></DD>
     * </DL>
     * <p/>
     * This constructor should <I>only</I> be called from kXMLElement itself
     * to create child elements.
     *
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#kXMLElement()
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#XMLElement(boolean)
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#XMLElement(java.util.Hashtable)
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#XMLElement(java.util.Hashtable,boolean)
     */
    public kXMLElement(Hashtable conversionTable,
                       boolean skipLeadingWhitespace,
                       boolean ignoreCase) {
        this(conversionTable, skipLeadingWhitespace, true, ignoreCase);
    }


    /**
     * Creates a new XML element. The following settings are used:
     * <DL><DT>Conversion table</DT>
     * <DD><I>conversionTable</I>, eventually combined with the minimal XML
     * conversions: <CODE>&amp;amp; &amp;lt; &amp;gt;
     * &amp;apos; &amp;quot;</CODE>
     * (depending on <I>fillBasicConversionTable</I>)</DD>
     * <DT>Skip whitespace in contents</DT>
     * <DD><I>skipLeadingWhitespace</I></DD>
     * <DT>Ignore Case</DT>
     * <DD><I>ignoreCase</I></DD>
     * </DL>
     * <p/>
     * This constructor should <I>only</I> be called from kXMLElement itself
     * to create child elements.
     *
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#XMLElement()
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#XMLElement(boolean)
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#XMLElement(java.util.Hashtable)
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#XMLElement(java.util.Hashtable,boolean)
     */
    protected kXMLElement(Hashtable conversionTable,
                          boolean skipLeadingWhitespace,
                          boolean fillBasicConversionTable,
                          boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
        this.skipLeadingWhitespace = skipLeadingWhitespace;
        this.tagName = null;
        this.contents = "";
        this.attributes = null;
        this.children = null;
        this.conversionTable = conversionTable;
        this.lineNr = 0;

        if (fillBasicConversionTable) {
            this.conversionTable.put("lt", "<");
            this.conversionTable.put("gt", ">");
            this.conversionTable.put("quot", "\"");
            this.conversionTable.put("apos", "'");
            this.conversionTable.put("amp", "&");
        }
    }

    /**
     * Adds a subobject.
     */
    public void addChild(kXMLElement child) {
        getChildren().addElement(child);
    }

    private Hashtable getAttributes() {
        if (attributes == null) {
            attributes = new Hashtable();
        }
        return attributes;
    }

    /**
     * Adds a property.
     * If the element is case insensitive, the property name is capitalized.
     */
    public void addProperty(String key,
                            Object value) {
        if (ignoreCase) {
            key = key.toUpperCase();
        }

        getAttributes().put(key, value.toString());
    }


    /**
     * Adds a property.
     * If the element is case insensitive, the property name is capitalized.
     */
    public void addProperty(String key,
                            int value) {
        if (ignoreCase) {
            key = key.toUpperCase();
        }

        getAttributes().put(key, Integer.toString(value));
    }


    /**
     * Returns the number of subobjects of the object.
     */
    public int countChildren() {
        return (children != null) ? children.size() : 0;
    }


    /**
     * Enumerates the attribute names.
     */
    public Enumeration enumeratePropertyNames() {
        return getAttributes().keys();
    }


    /**
     * Enumerates the subobjects of the object.
     */
    public Enumeration enumerateChildren() {
        return getChildren().elements();
    }


    /**
     * Returns the subobjects of the object.
     */
    public Vector getChildren() {
        if (children == null) {
            children = new Vector();
        }
        return children;
    }


    /**
     * Returns the #PCDATA content of the object. If there is no such content,
     * <CODE>null</CODE> is returned.
     */
    public String getContents() {
        return contents;
    }


    /**
     * Returns the line nr on which the element is found.
     */
    public int getLineNr() {
        return lineNr;
    }


    /**
     * Returns a property by looking up a key in a hashtable.
     * If the property doesn't exist, the value corresponding to defaultValue
     * is returned.
     */
    public int getIntProperty(String key,
                              Hashtable valueSet,
                              String defaultValue) {
        String val = getProperty(attributes, key);
        Integer result;

        if (ignoreCase) {
            key = key.toUpperCase();
        }

        if (val == null) {
            val = defaultValue;
        }

        try {
            result = (Integer) (valueSet.get(val));
        }
        catch (ClassCastException e) {
            throw invalidValueSet(key);
        }

        if (result == null) {
            throw invalidValue(key, val, lineNr);
        }

        return result.intValue();
    }

    /**
     */

    public static String getProperty(Hashtable h, String key) {
        if (h == null) {
            return null;
        }
        return (String) h.get(key);
    }

    /**
     */

    public static String getProperty(Hashtable h, String key, String def) {
        if (h == null) {
            return def;
        }
        String val = (String) h.get(key);
        return (val != null) ? val : def;
    }

    /**
     * Returns a property of the object. If there is no such property, this
     * method returns <CODE>null</CODE>.
     */
    public String getProperty(String key) {
        if (ignoreCase) {
            key = key.toUpperCase();
        }

        return getProperty(attributes, key);
    }


    /**
     * Returns a property of the object.
     * If the property doesn't exist, <I>defaultValue</I> is returned.
     */
    public String getProperty(String key,
                              String defaultValue) {
        if (ignoreCase) {
            key = key.toUpperCase();
        }

        return getProperty(attributes, key, defaultValue);
    }


    /**
     * Returns an integer property of the object.
     * If the property doesn't exist, <I>defaultValue</I> is returned.
     */
    public int getProperty(String key,
                           int defaultValue) {
        if (ignoreCase) {
            key = key.toUpperCase();
        }

        String val = getProperty(attributes, key);

        if (val == null) {
            return defaultValue;
        } else {
            try {
                return Integer.parseInt(val);
            }
            catch (NumberFormatException e) {
                throw invalidValue(key, val, lineNr);
            }
        }
    }


    /**
     * Returns a boolean property of the object. If the property is missing,
     * <I>defaultValue</I> is returned.
     */
    public boolean getProperty(String key,
                               String trueValue,
                               String falseValue,
                               boolean defaultValue) {
        if (ignoreCase) {
            key = key.toUpperCase();
        }

        String val = getProperty(attributes, key);

        if (val == null) {
            return defaultValue;
        } else if (val.equals(trueValue)) {
            return true;
        } else if (val.equals(falseValue)) {
            return false;
        } else {
            throw invalidValue(key, val, lineNr);
        }
    }


    /**
     * Returns a property by looking up a key in the hashtable <I>valueSet</I>
     * If the property doesn't exist, the value corresponding to
     * <I>defaultValue</I>  is returned.
     */
    public Object getProperty(String key,
                              Hashtable valueSet,
                              String defaultValue) {
        if (ignoreCase) {
            key = key.toUpperCase();
        }

        String val = getProperty(attributes, key);

        if (val == null) {
            val = defaultValue;
        }

        Object result = valueSet.get(val);

        if (result == null) {
            throw invalidValue(key, val, lineNr);
        }

        return result;
    }


    /**
     * Returns a property by looking up a key in the hashtable <I>valueSet</I>.
     * If the property doesn't exist, the value corresponding to
     * <I>defaultValue</I>  is returned.
     */
    public String getStringProperty(String key,
                                    Hashtable valueSet,
                                    String defaultValue) {
        if (ignoreCase) {
            key = key.toUpperCase();
        }

        String val = getProperty(attributes, key);
        String result;

        if (val == null) {
            val = defaultValue;
        }

        try {
            result = (String) (valueSet.get(val));
        }
        catch (ClassCastException e) {
            throw invalidValueSet(key);
        }

        if (result == null) {
            throw invalidValue(key, val, lineNr);
        }

        return result;
    }


    /**
     * Returns a property by looking up a key in the hashtable <I>valueSet</I>.
     * If the value is not defined in the hashtable, the value is considered to
     * be an integer.
     * If the property doesn't exist, the value corresponding to
     * <I>defaultValue</I> is returned.
     */
    public int getSpecialIntProperty(String key,
                                     Hashtable valueSet,
                                     String defaultValue) {
        if (ignoreCase) {
            key = key.toUpperCase();
        }

        String val = getProperty(attributes, key);
        Integer result;

        if (val == null) {
            val = defaultValue;
        }

        try {
            result = (Integer) (valueSet.get(val));
        }
        catch (ClassCastException e) {
            throw invalidValueSet(key);
        }

        if (result == null) {
            try {
                return Integer.parseInt(val);
            }
            catch (NumberFormatException e) {
                throw invalidValue(key, val, lineNr);
            }
        }

        return result.intValue();
    }


    /**
     * Returns the class (i.e. the name indicated in the tag) of the object.
     */
    public String getTagName() {
        return tagName;
    }


    /**
     * Checks whether a character may be part of an identifier.
     */
    private boolean isIdentifierChar(char ch) {
        return (((ch >= 'A') && (ch <= 'Z')) || ((ch >= 'a') && (ch <= 'z'))
                || ((ch >= '0') && (ch <= '9')) || (".-_:".indexOf(ch) >= 0));
    }


    /**
     * Reads an XML definition from a java.io.Reader and parses it.
     *
     * @throws java.io.IOException if an error occured while reading the input
     * @throws net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLParseException
     *                             if an error occured while parsing the read data
     */
    public void parseFromReader(Reader reader)
            throws IOException, kXMLParseException {
        parseFromReader(reader, 1);
    }


    /**
     * Reads an XML definition from a java.io.Reader and parses it.
     *
     * @throws java.io.IOException if an error occured while reading the input
     * @throws net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLParseException
     *                             if an error occured while parsing the read data
     */
    public void parseFromReader(Reader reader,
                                int startingLineNr)
            throws IOException, kXMLParseException {
        int blockSize = 4096;
        char[] input = null;
        int size = 0;

        for (; ;) {
            if (input == null) {
                input = new char[blockSize];
            } else {
                char[] oldInput = input;
                input = new char[input.length + blockSize];
                System.arraycopy(oldInput, 0, input, 0, oldInput.length);
            }

            int charsRead = reader.read(input, size, blockSize);

            if (charsRead < 0) {
                break;
            }

            size += charsRead;
        }

        parseCharArray(input, 0, size, startingLineNr);
    }


    /**
     * Parses an XML definition.
     *
     * @throws net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLParseException
     *          if an error occured while parsing the string
     */
    public void parseString(String string)
            throws kXMLParseException {
        parseCharArray(string.toCharArray(), 0, string.length(), 1);
    }


    /**
     * Parses an XML definition starting at <I>offset</I>.
     *
     * @return the offset of the string following the XML data
     * @throws net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLParseException
     *          if an error occured while parsing the string
     */
    public int parseString(String string,
                           int offset)
            throws kXMLParseException {
        return parseCharArray(string.toCharArray(), offset,
                              string.length(), 1);
    }


    /**
     * Parses an XML definition starting at <I>offset</I>.
     *
     * @return the offset of the string following the XML data (<= end)
     * @throws net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLParseException
     *          if an error occured while parsing the string
     */
    public int parseString(String string,
                           int offset,
                           int end)
            throws kXMLParseException {
        return parseCharArray(string.toCharArray(), offset, end, 1);
    }


    /**
     * Parses an XML definition starting at <I>offset</I>.
     *
     * @return the offset of the string following the XML data (<= end)
     * @throws net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLParseException
     *          if an error occured while parsing the string
     */
    public int parseString(String string,
                           int offset,
                           int end,
                           int startingLineNr)
            throws kXMLParseException {
        return parseCharArray(string.toCharArray(), offset, end,
                              startingLineNr);
    }


    /**
     * Parses an XML definition starting at <I>offset</I>.
     *
     * @return the offset of the array following the XML data (<= end)
     * @throws net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLParseException
     *          if an error occured while parsing the array
     */
    public int parseCharArray(char[] input,
                              int offset,
                              int end)
            throws kXMLParseException {
        return parseCharArray(input, offset, end, 1);
    }


    /**
     * Parses an XML definition starting at <I>offset</I>.
     *
     * @return the offset of the array following the XML data (<= end)
     * @throws net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLParseException
     *          if an error occured while parsing the array
     */
    public int parseCharArray(char[] input,
                              int offset,
                              int end,
                              int startingLineNr)
            throws kXMLParseException {
        int[] lineNr = new int[1];
        lineNr[0] = startingLineNr;
        return parseCharArray(input, offset, end, lineNr);
    }


    /**
     * Parses an XML definition starting at <I>offset</I>.
     *
     * @return the offset of the array following the XML data (<= end)
     * @throws net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLParseException
     *          if an error occured while parsing the array
     */
    private int parseCharArray(char[] input,
                               int offset,
                               int end,
                               int[] currentLineNr)
            throws kXMLParseException {
        this.lineNr = currentLineNr[0];
        this.tagName = null;
        this.contents = null;
        this.attributes = null;
        this.children = null;

        try {
            offset = this.skipWhitespace(input, offset, end, currentLineNr);
        }
        catch (kXMLParseException e) {
            return offset;
        }

        offset = skipPreamble(input, offset, end, currentLineNr);
        offset = scanTagName(input, offset, end, currentLineNr);
        lineNr = currentLineNr[0];
        offset = scanAttributes(input, offset, end, currentLineNr);
        int[] contentOffset = new int[1];
        int[] contentSize = new int[1];
        int contentLineNr = currentLineNr[0];
        offset = scanContent(input, offset, end,
                             contentOffset, contentSize, currentLineNr);

        if (contentSize[0] > 0) {
            scanChildren(input, contentOffset[0], contentSize[0],
                         contentLineNr);

            if (children != null && children.size() > 0) {
                contents = null;
            } else {
                processContents(input, contentOffset[0],
                                contentSize[0], contentLineNr);

                for (int i = 0; i < contents.length(); i++) {
                    if (contents.charAt(i) > ' ') {
                        return offset;
                    }
                }

                contents = null;
            }
        }

        return offset;
    }


    /**
     * Decodes the entities in the contents and, if skipLeadingWhitespace is
     * <CODE>true</CODE>, removes extraneous whitespaces after newlines and
     * convert those newlines into spaces.
     *
     * @throws net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLParseException
     *          if an error occured while parsing the array
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#decodeString
     */
    private void processContents(char[] input,
                                 int contentOffset,
                                 int contentSize,
                                 int contentLineNr)
            throws kXMLParseException {
        int[] lineNr = new int[1];
        lineNr[0] = contentLineNr;

        if (!skipLeadingWhitespace) {
            String str = new String(input, contentOffset, contentSize);
            contents = decodeString(str, lineNr[0]);
            return;
        }

        StringBuffer result = new StringBuffer(contentSize);
        int end = contentSize + contentOffset;

        for (int i = contentOffset; i < end; i++) {
            char ch = input[i];

            // The end of the contents is always a < character, so there's
            // no danger for bounds violation
            while ((ch == '\r') || (ch == '\n')) {
                lineNr[0]++;
                result.append(ch);

                i++;
                ch = input[i];

                if (ch != '\n') {
                    result.append(ch);
                }

                do {
                    i++;
                    ch = input[i];
                } while ((ch == ' ') || (ch == '\t'));
            }

            if (i < end) {
                result.append(input[i]);
            }
        }

        contents = decodeString(result.toString(), lineNr[0]);
    }


    /**
     * Removes a child object. If the object is not a child, nothing happens.
     */
    public void removeChild(kXMLElement child) {
        if (children != null) {
            children.removeElement(child);
        }
    }


    /**
     * Removes an attribute.
     */
    public void removeChild(String key) {
        if (attributes != null) {
            if (ignoreCase) {
                key = key.toUpperCase();
            }

            attributes.remove(key);
        }
    }


    /**
     * Scans the attributes of the object.
     *
     * @return the offset in the string following the attributes, so that
     *         input[offset] in { '/', '>' }
     * @throws net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLParseException
     *          if an error occured while parsing the array
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#scanOneAttribute
     */
    private int scanAttributes(char[] input,
                               int offset,
                               int end,
                               int[] lineNr)
            throws kXMLParseException {
        String key, value;

        for (; ;) {
            offset = skipWhitespace(input, offset, end, lineNr);

            char ch = input[offset];

            if ((ch == '/') || (ch == '>')) {
                break;
            }

            offset = scanOneAttribute(input, offset, end, lineNr);
        }

        return offset;
    }


    /**
     * !!!
     * Searches the content for child objects. If such objects exist, the
     * content is reduced to <CODE>null</CODE>.
     *
     * @throws net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLParseException
     *          if an error occured while parsing the array
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#parseCharArray
     */
    protected void scanChildren(char[] input,
                                int contentOffset,
                                int contentSize,
                                int contentLineNr)
            throws kXMLParseException {
        int end = contentOffset + contentSize;
        int offset = contentOffset;
        int lineNr[] = new int[1];
        lineNr[0] = contentLineNr;

        while (offset < end) {
            try {
                offset = skipWhitespace(input, offset, end, lineNr);
            }
            catch (kXMLParseException e) {
                return;
            }

            if ((input[offset] != '<')
                || ((input[offset + 1] == '!') && (input[offset + 2] == '['))) {
                return;
            }

            kXMLElement child = createAnotherElement();
            offset = child.parseCharArray(input, offset, end, lineNr);
            getChildren().addElement(child);
        }
    }


    /**
     * Creates a new XML element.
     */
    protected kXMLElement createAnotherElement() {
        return new kXMLElement(conversionTable,
                               skipLeadingWhitespace,
                               false,
                               ignoreCase);
    }


    /**
     * Scans the content of the object.
     *
     * @return the offset after the XML element; contentOffset points to the
     *         start of the content section; contentSize is the size of the
     *         content section
     * @throws net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLParseException
     *          if an error occured while parsing the array
     */
    private int scanContent(char[] input,
                            int offset,
                            int end,
                            int[] contentOffset,
                            int[] contentSize,
                            int[] lineNr)
            throws kXMLParseException {
        if (input[offset] == '/') {
            contentSize[0] = 0;

            if (input[offset + 1] != '>') {
                throw expectedInput("'>'", lineNr[0]);
            }

            return offset + 2;
        }

        if (input[offset] != '>') {
            throw expectedInput("'>'", lineNr[0]);
        }

        if (skipLeadingWhitespace) {
            offset = skipWhitespace(input, offset + 1, end, lineNr);
        } else {
            offset++;
        }

        int begin = offset;
        contentOffset[0] = offset;
        int level = 0;
        char[] tag = tagName.toCharArray();
        end -= (tag.length + 2);

        while ((offset < end) && (level >= 0)) {
            if (input[offset] == '<') {
                boolean ok = true;

                if ((offset < (end - 3)) && (input[offset + 1] == '!')
                    && (input[offset + 2] == '-')
                    && (input[offset + 3] == '-')) {
                    offset += 3;

                    while ((offset < end)
                           && ((input[offset - 2] != '-')
                               || (input[offset - 1] != '-')
                               || (input[offset - 0] != '>'))) {
                        offset++;
                    }

                    offset++;
                    continue;
                }

                if ((offset < (end - 1)) && (input[offset + 1] == '!')
                    && (input[offset + 2] == '[')) {
                    offset++;
                    continue;
                }

                for (int i = 0; ok && (i < tag.length); i++) {
                    ok &= (input[offset + (i + 1)] == tag[i]);
                }

                ok &= !isIdentifierChar(input[offset + tag.length + 1]);

                if (ok) {
                    while ((offset < end) && (input[offset] != '>')) {
                        offset++;
                    }

                    if (input[offset - 1] != '/') {
                        level++;
                    }

                    continue;
                } else if (input[offset + 1] == '/') {
                    ok = true;

                    for (int i = 0; ok && (i < tag.length); i++) {
                        ok &= (input[offset + (i + 2)] == tag[i]);
                    }

                    if (ok) {
                        contentSize[0] = offset - contentOffset[0];
                        offset += tag.length + 2;

                        try {
                            offset = skipWhitespace(input, offset,
                                                    end + tag.length
                                                    + 2,
                                                    lineNr);
                        }
                        catch (kXMLParseException e) {
                            // ignore
                        }

                        if (input[offset] == '>') {
                            level--;
                            offset++;
                        }

                        continue;
                    }
                }
            }

            if (input[offset] == '\r') {
                lineNr[0]++;

                if ((offset != end) && (input[offset + 1] == '\n')) {
                    offset++;
                }
            } else if (input[offset] == '\n') {
                lineNr[0]++;
            }

            offset++;
        }

        if (level >= 0) {
            throw unexpectedEndOfData(lineNr[0]);
        }

        if (skipLeadingWhitespace) {
            int i = contentOffset[0] + contentSize[0] - 1;

            while ((contentSize[0] >= 0) && (input[i] <= ' ')) {
                i--;
                contentSize[0]--;
            }
        }

        return offset;
    }


    /**
     * Scans an identifier.
     *
     * @return the identifier, or <CODE>null</CODE> if offset doesn't point
     *         to an identifier
     */
    private String scanIdentifier(char[] input,
                                  int offset,
                                  int end) {
        int begin = offset;

        while ((offset < end) && (isIdentifierChar(input[offset]))) {
            offset++;
        }

        if ((offset == end) || (offset == begin)) {
            return null;
        } else {
            return new String(input, begin, offset - begin);
        }
    }


    /**
     * Scans one attribute of an object.
     *
     * @return the offset after the attribute
     * @throws net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLParseException
     *          if an error occured while parsing the array
     */
    private int scanOneAttribute(char[] input,
                                 int offset,
                                 int end,
                                 int[] lineNr)
            throws kXMLParseException {
        String key, value;

        key = scanIdentifier(input, offset, end);

        if (key == null) {
            throw syntaxError("an attribute key", lineNr[0]);
        }

        offset = skipWhitespace(input, offset + key.length(), end, lineNr);

        if (ignoreCase) {
            key = key.toUpperCase();
        }

        if (input[offset] != '=') {
            throw valueMissingForAttribute(key, lineNr[0]);
        }

        offset = skipWhitespace(input, offset + 1, end, lineNr);

        value = scanString(input, offset, end, lineNr);

        if (value == null) {
            throw syntaxError("an attribute value", lineNr[0]);
        }

        if ((value.charAt(0) == '"') || (value.charAt(0) == '\'')) {
            value = value.substring(1, (value.length() - 1));
            offset += 2;
        }

        getAttributes().put(key, decodeString(value, lineNr[0]));
        return offset + value.length();
    }


    /**
     * Scans a string. Strings are either identifiers, or text delimited by
     * double quotes.
     *
     * @return the string found, without delimiting double quotes; or null
     *         if offset didn't point to a valid string
     * @throws net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLParseException
     *          if an error occured while parsing the array
     * @see net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLElement#scanIdentifier
     */
    private String scanString(char[] input,
                              int offset,
                              int end,
                              int[] lineNr)
            throws kXMLParseException {
        char delim = input[offset];

        if ((delim == '"') || (delim == '\'')) {
            int begin = offset;
            offset++;

            while ((offset < end) && (input[offset] != delim)) {
                if (input[offset] == '\r') {
                    lineNr[0]++;

                    if ((offset != end) && (input[offset + 1] == '\n')) {
                        offset++;
                    }
                } else if (input[offset] == '\n') {
                    lineNr[0]++;
                }

                offset++;
            }

            if (offset == end) {
                return null;
            } else {
                return new String(input, begin, offset - begin + 1);
            }
        } else {
            return scanIdentifier(input, offset, end);
        }
    }


    /**
     * Scans the class (tag) name of the object.
     *
     * @return the position after the tag name
     * @throws net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLParseException
     *          if an error occured while parsing the array
     */
    private int scanTagName(char[] input,
                            int offset,
                            int end,
                            int[] lineNr)
            throws kXMLParseException {
        tagName = scanIdentifier(input, offset, end);

        if (tagName == null) {
            throw syntaxError("a tag name", lineNr[0]);
        }

        return offset + tagName.length();
    }


    /**
     * Changes the content string.
     *
     * @param content The new content string.
     */
    public void setContent(String content) {
        this.contents = content;
    }


    /**
     * Changes the tag name.
     *
     * @param tagName The new tag name.
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }


    /**
     * Skips a tag that don't contain any useful data: &lt;?...?&gt;,
     * &lt;!...&gt; and comments.
     *
     * @return the position after the tag
     * @throws net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLParseException
     *          if an error occured while parsing the array
     */
    protected int skipBogusTag(char[] input,
                               int offset,
                               int end,
                               int[] lineNr) {
        int level = 1;

        while (offset < end) {
            char ch = input[offset++];

            switch (ch) {
                case '\r':
                    if ((offset < end) && (input[offset] == '\n')) {
                        offset++;
                    }

                    lineNr[0]++;
                    break;

                case '\n':
                    lineNr[0]++;
                    break;

                case '<':
                    level++;
                    break;

                case '>':
                    level--;

                    if (level == 0) {
                        return offset;
                    }

                    break;

                default:
            }
        }

        throw unexpectedEndOfData(lineNr[0]);
    }


    /**
     * Skips a tag that don't contain any useful data: &lt;?...?&gt;,
     * &lt;!...&gt; and comments.
     *
     * @return the position after the tag
     * @throws net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLParseException
     *          if an error occured while parsing the array
     */
    private int skipPreamble(char[] input,
                             int offset,
                             int end,
                             int[] lineNr)
            throws kXMLParseException {
        char ch;

        do {
            offset = skipWhitespace(input, offset, end, lineNr);

            if (input[offset] != '<') {
                expectedInput("'<'", lineNr[0]);
            }

            offset++;

            if (offset >= end) {
                throw unexpectedEndOfData(lineNr[0]);
            }

            ch = input[offset];

            if ((ch == '!') || (ch == '?')) {
                offset = skipBogusTag(input, offset, end, lineNr);
            }
        } while (!isIdentifierChar(ch));

        return offset;
    }


    /**
     * Skips whitespace characters.
     *
     * @return the position after the whitespace
     * @throws net.sourceforge.subsonic.jmeplayer.nanoxml.kXMLParseException
     *          if an error occured while parsing the array
     */
    private int skipWhitespace(char[] input,
                               int offset,
                               int end,
                               int[] lineNr) {
        int startLine = lineNr[0];

        while (offset < end) {
            if (((offset + 6) < end) && (input[offset + 3] == '-')
                && (input[offset + 2] == '-') && (input[offset + 1] == '!')
                && (input[offset] == '<')) {
                offset += 4;

                while ((input[offset] != '-') || (input[offset + 1] != '-')
                       || (input[offset + 2] != '>')) {
                    if ((offset + 2) >= end) {
                        throw unexpectedEndOfData(startLine);
                    }

                    offset++;
                }

                offset += 2;
            } else if (input[offset] == '\r') {
                lineNr[0]++;

                if ((offset != end) && (input[offset + 1] == '\n')) {
                    offset++;
                }
            } else if (input[offset] == '\n') {
                lineNr[0]++;
            } else if (input[offset] > ' ') {
                break;
            }

            offset++;
        }

        if (offset == end) {
            throw unexpectedEndOfData(startLine);
        }

        return offset;
    }


    /**
     * Converts &amp;...; sequences to "normal" chars.
     */
    protected String decodeString(String s,
                                  int lineNr) {
        StringBuffer result = new StringBuffer(s.length());
        int index = 0;

        while (index < s.length()) {
            int index2 = (s + '&').indexOf('&', index);
            int index3 = (s + "<![CDATA[").indexOf("<![CDATA[", index);
            int index4 = (s + "<!--").indexOf("<!--", index);

            if ((index2 <= index3) && (index2 <= index4)) {
                result.append(s.substring(index, index2));

                if (index2 == s.length()) {
                    break;
                }

                index = s.indexOf(';', index2);

                if (index < 0) {
                    result.append(s.substring(index2));
                    break;
                }

                String key = s.substring(index2 + 1, index);

                if (key.charAt(0) == '#') {
                    if (key.charAt(1) == 'x') {
                        result.append((char) (Integer.
                                parseInt(key.substring(2),
                                         16)));
                    } else {
                        result.append((char) (Integer.
                                parseInt(key.substring(1),
                                         10)));
                    }
                } else {
                    result.append(getProperty(conversionTable,
                                              key, "&" + key + ';'));
                }
            } else if (index3 <= index4) {
                int end = (s + "]]>").indexOf("]]>", index3 + 9);
                result.append(s.substring(index, index3));
                result.append(s.substring(index3 + 9, end));
                index = end + 2;
            } else {
                result.append(s.substring(index, index4));
                index = (s + "-->").indexOf("-->", index4) + 2;
            }

            index++;
        }

        return result.toString();
    }


    /**
     * Writes the XML element to a string.
     */
    public String toString() {
        kXMLStringWriter writer = new kXMLStringWriter();
        write(writer);
        return writer.toString();
    }


    /**
     * Writes the XML element to a writer.
     */
    public void write(Writer writer) {
        write(writer, 0);
    }


    /**
     * Writes the XML element to a writer.
     */
    public void write(Writer writer,
                      int indent) {
        kXMLPrintWriter out = new kXMLPrintWriter(writer);

        for (int i = 0; i < indent; i++) {
            out.print(' ');
        }

        if (tagName == null) {
            writeEncoded(out, contents);
            return;
        }

        out.print('<');
        out.print(tagName);

        if (attributes != null && !attributes.isEmpty()) {
            Enumeration enum = attributes.keys();

            while (enum.hasMoreElements()) {
                out.print(' ');
                String key = (String) (enum.nextElement());
                String value = (String) (attributes.get(key));
                out.print(key);
                out.print("=\"");
                writeEncoded(out, value);
                out.print('"');
            }
        }

        if ((contents != null) && (contents.length() > 0)) {
            if (skipLeadingWhitespace) {
                out.println('>');

                for (int i = 0; i < indent + 4; i++) {
                    out.print(' ');
                }

                out.println(contents);

                for (int i = 0; i < indent; i++) {
                    out.print(' ');
                }
            } else {
                out.print('>');
                writeEncoded(out, contents);
            }

            out.print("</");
            out.print(tagName);
            out.println('>');
        } else if (children == null || children.isEmpty()) {
            out.println("/>");
        } else {
            out.println('>');
            Enumeration enum = enumerateChildren();

            while (enum.hasMoreElements()) {
                kXMLElement child = (kXMLElement) (enum.nextElement());
                child.write(writer, indent + 4);
            }

            for (int i = 0; i < indent; i++) {
                out.print(' ');
            }

            out.print("</");
            out.print(tagName);
            out.println('>');
        }
    }


    /**
     * Writes a string encoded to a writer.
     */
    protected void writeEncoded(kXMLPrintWriter out,
                                String str) {
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);

            switch (ch) {
                case '<':
                    out.write("&lt;");
                    break;

                case '>':
                    out.write("&gt;");
                    break;

                case '&':
                    out.write("&amp;");
                    break;

                case '"':
                    out.write("&quot;");
                    break;

                case '\'':
                    out.write("&apos;");
                    break;

                case '\r':
                case '\n':
                    out.write(ch);
                    break;

                default:
                    if (((int) ch < 32) || ((int) ch > 126)) {
                        out.write("&#x");
                        out.write(Integer.toString((int) ch, 16));
                        out.write(';');
                    } else {
                        out.write(ch);
                    }
            }
        }
    }


    /**
     * Creates a parse exception for when an invalid valueset is given to
     * a method.
     */
    private kXMLParseException invalidValueSet(String key) {
        String msg = "Invalid value set (key = \"" + key + "\")";
        return new kXMLParseException(getTagName(), msg);
    }


    /**
     * Creates a parse exception for when an invalid value is given to a
     * method.
     */
    private kXMLParseException invalidValue(String key,
                                            String value,
                                            int lineNr) {
        String msg = "Attribute \"" + key + "\" does not contain a valid "
                     + "value (\"" + value + "\")";
        return new kXMLParseException(getTagName(), lineNr, msg);
    }


    /**
     * The end of the data input has been reached.
     */
    private kXMLParseException unexpectedEndOfData(int lineNr) {
        String msg = "Unexpected end of data reached";
        return new kXMLParseException(getTagName(), lineNr, msg);
    }


    /**
     * A syntax error occured.
     */
    private kXMLParseException syntaxError(String context,
                                           int lineNr) {
        String msg = "Syntax error while parsing " + context;
        return new kXMLParseException(getTagName(), lineNr, msg);
    }


    /**
     * A character has been expected.
     */
    private kXMLParseException expectedInput(String charSet,
                                             int lineNr) {
        String msg = "Expected: " + charSet;
        return new kXMLParseException(getTagName(), lineNr, msg);
    }


    /**
     * A value is missing for an attribute.
     */
    private kXMLParseException valueMissingForAttribute(String key,
                                                        int lineNr) {
        String msg = "Value missing for attribute with key \"" + key + "\"";
        return new kXMLParseException(getTagName(), lineNr, msg);
    }

}
