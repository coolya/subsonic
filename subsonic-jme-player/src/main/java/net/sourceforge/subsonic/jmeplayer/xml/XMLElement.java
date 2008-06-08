package net.sourceforge.subsonic.jmeplayer.xml;

import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Vector;


/**
 * XMLElement is a representation of an XML object. The object is able to parse
 * XML code.
 */
public class XMLElement {

    /**
     * The attributes given to the object.
     */
    private Hashtable attributes;


    /**
     * Subobjects of the object. The subobjects are of class XMLElement
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
    private int lineNumber;


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
     */
    public XMLElement() {
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
     */
    public XMLElement(Hashtable conversionTable) {
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
     */
    public XMLElement(boolean skipLeadingWhitespace) {
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
     */
    public XMLElement(Hashtable conversionTable,
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
     * This constructor should <I>only</I> be called from XMLElement itself
     * to create child elements.
     *
     * @see XMLElement#XMLElement()
     * @see XMLElement#XMLElement(boolean)
     * @see XMLElement#XMLElement(java.util.Hashtable)
     * @see XMLElement#XMLElement(java.util.Hashtable,boolean)
     */
    public XMLElement(Hashtable conversionTable,
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
     * This constructor should <I>only</I> be called from XMLElement itself
     * to create child elements.
     *
     * @see XMLElement#XMLElement()
     * @see XMLElement#XMLElement(boolean)
     * @see XMLElement#XMLElement(java.util.Hashtable)
     * @see XMLElement#XMLElement(java.util.Hashtable,boolean)
     */
    protected XMLElement(Hashtable conversionTable,
                         boolean skipLeadingWhitespace,
                         boolean fillBasicConversionTable,
                         boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
        this.skipLeadingWhitespace = skipLeadingWhitespace;
        this.conversionTable = conversionTable;
        tagName = null;
        contents = "";
        attributes = null;
        children = null;
        lineNumber = 0;

        if (fillBasicConversionTable) {
            this.conversionTable.put("lt", "<");
            this.conversionTable.put("gt", ">");
            this.conversionTable.put("quot", "\"");
            this.conversionTable.put("apos", "'");
            this.conversionTable.put("amp", "&");
        }
    }

    private Hashtable getAttributes() {
        if (attributes == null) {
            attributes = new Hashtable();
        }
        return attributes;
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
     * Returns the line number on which the element is found.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     */

    private static String getProperty(Hashtable h, String key) {
        if (h == null) {
            return null;
        }
        return (String) h.get(key);
    }

    /**
     */

    private static String getProperty(Hashtable h, String key, String def) {
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
            throw invalidValue(key, val, lineNumber);
        }
    }

    /**
     * Returns the class (i.e. the name indicated in the tag) of the object.
     */
    private String getTagName() {
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
     * @throws java.io.IOException if an error occurred while reading the input
     * @throws XMLParseException   if an error occurred while parsing the read data
     */
    public void parseFromReader(Reader reader) throws IOException, XMLParseException {
        parseFromReader(reader, 1);
    }


    /**
     * Reads an XML definition from a java.io.Reader and parses it.
     *
     * @throws java.io.IOException if an error occurred while reading the input
     * @throws XMLParseException   if an error occurred while parsing the read data
     */
    public void parseFromReader(Reader reader, int startingLineNr) throws IOException, XMLParseException {
        int blockSize = 4096;
        char[] input = null;
        int size = 0;

        while (true) {
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

        System.out.println(new String(input, 0, size));
        parseCharArray(input, 0, size, startingLineNr);
    }


    /**
     * Parses an XML definition.
     *
     * @throws XMLParseException if an error occurred while parsing the string
     */
    public void parseString(String string) throws XMLParseException {
        parseCharArray(string.toCharArray(), 0, string.length(), 1);
    }


    /**
     * Parses an XML definition starting at <I>offset</I>.
     *
     * @return the offset of the array following the XML data (<= end)
     * @throws XMLParseException if an error occurred while parsing the array
     */
    private int parseCharArray(char[] input, int offset, int end, int startingLineNr) throws XMLParseException {
        int[] lineNr = new int[1];
        lineNr[0] = startingLineNr;
        return parseCharArray(input, offset, end, lineNr);
    }


    /**
     * Parses an XML definition starting at <I>offset</I>.
     *
     * @return the offset of the array following the XML data (<= end)
     * @throws XMLParseException if an error occurred while parsing the array
     */
    private int parseCharArray(char[] input, int offset, int end, int[] currentLineNr) throws XMLParseException {
        lineNumber = currentLineNr[0];
        tagName = null;
        contents = null;
        attributes = null;
        children = null;

        try {
            offset = skipWhitespace(input, offset, end, currentLineNr);
        }
        catch (XMLParseException e) {
            return offset;
        }

        offset = skipPreamble(input, offset, end, currentLineNr);
        offset = scanTagName(input, offset, end, currentLineNr);
        lineNumber = currentLineNr[0];
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
     * <CODE>true</CODE>, removes extraneous whitespace after newlines and
     * convert those newlines into spaces.
     *
     * @throws XMLParseException if an error occurred while parsing the array
     * @see XMLElement#decodeString
     */
    private void processContents(char[] input, int contentOffset, int contentSize, int contentLineNr)
            throws XMLParseException {
        int[] lineNr = new int[1];
        lineNr[0] = contentLineNr;

        if (!skipLeadingWhitespace) {
            String str = new String(input, contentOffset, contentSize);
            contents = decodeString(str);
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

        contents = decodeString(result.toString());
    }


    /**
     * Scans the attributes of the object.
     *
     * @return the offset in the string following the attributes, so that
     *         input[offset] in { '/', '>' }
     * @throws XMLParseException if an error occurred while parsing the array
     * @see XMLElement#scanOneAttribute
     */
    private int scanAttributes(char[] input, int offset, int end, int[] lineNr) throws XMLParseException {

        while (true) {
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
     * @throws XMLParseException if an error occurred while parsing the array
     */
    private void scanChildren(char[] input, int contentOffset, int contentSize, int contentLineNr)
            throws XMLParseException {
        int end = contentOffset + contentSize;
        int offset = contentOffset;
        int lineNr[] = new int[1];
        lineNr[0] = contentLineNr;

        while (offset < end) {
            try {
                offset = skipWhitespace(input, offset, end, lineNr);
            }
            catch (XMLParseException e) {
                return;
            }

            if ((input[offset] != '<')
                || ((input[offset + 1] == '!') && (input[offset + 2] == '['))) {
                return;
            }

            XMLElement child = new XMLElement(conversionTable, skipLeadingWhitespace, false, ignoreCase);
            offset = child.parseCharArray(input, offset, end, lineNr);
            getChildren().addElement(child);
        }
    }

    /**
     * Scans the content of the object.
     *
     * @return the offset after the XML element; contentOffset points to the
     *         start of the content section; contentSize is the size of the
     *         content section
     * @throws XMLParseException if an error occurred while parsing the array
     */
    private int scanContent(char[] input, int offset, int end, int[] contentOffset, int[] contentSize, int[] lineNr)
            throws XMLParseException {
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
                               || (input[offset] != '>'))) {
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
                        catch (XMLParseException e) {
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
    private String scanIdentifier(char[] input, int offset, int end) {
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
     * @throws XMLParseException if an error occurred while parsing the array
     */
    private int scanOneAttribute(char[] input, int offset, int end, int[] lineNr)
            throws XMLParseException {
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

        getAttributes().put(key, decodeString(value));
        return offset + value.length();
    }


    /**
     * Scans a string. Strings are either identifiers, or text delimited by
     * double quotes.
     *
     * @return the string found, without delimiting double quotes; or null
     *         if offset didn't point to a valid string
     * @throws XMLParseException if an error occurred while parsing the array
     * @see XMLElement#scanIdentifier
     */
    private String scanString(char[] input, int offset, int end, int[] lineNr)
            throws XMLParseException {
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
     * @throws XMLParseException if an error occurred while parsing the array
     */
    private int scanTagName(char[] input, int offset, int end, int[] lineNr)
            throws XMLParseException {
        tagName = scanIdentifier(input, offset, end);

        if (tagName == null) {
            throw syntaxError("a tag name", lineNr[0]);
        }

        return offset + tagName.length();
    }

    /**
     * Skips a tag that don't contain any useful data: &lt;?...?&gt;,
     * &lt;!...&gt; and comments.
     *
     * @return the position after the tag
     * @throws XMLParseException if an error occurred while parsing the array
     */
    private int skipBogusTag(char[] input, int offset, int end, int[] lineNr) {
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
     * @throws XMLParseException if an error occurred while parsing the array
     */
    private int skipPreamble(char[] input, int offset, int end, int[] lineNr) throws XMLParseException {
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
     * @throws XMLParseException if an error occurred while parsing the array
     */
    private int skipWhitespace(char[] input, int offset, int end, int[] lineNr) {
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
    private String decodeString(String s) {
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
     * Creates a parse exception for when an invalid value is given to a
     * method.
     */
    private XMLParseException invalidValue(String key, String value, int lineNr) {
        String msg = "Attribute \"" + key + "\" does not contain a valid "
                     + "value (\"" + value + "\")";
        return new XMLParseException(getTagName(), lineNr, msg);
    }


    /**
     * The end of the data input has been reached.
     */
    private XMLParseException unexpectedEndOfData(int lineNr) {
        String msg = "Unexpected end of data reached";
        return new XMLParseException(getTagName(), lineNr, msg);
    }


    /**
     * A syntax error occurred.
     */
    private XMLParseException syntaxError(String context, int lineNr) {
        String msg = "Syntax error while parsing " + context;
        return new XMLParseException(getTagName(), lineNr, msg);
    }


    /**
     * A character has been expected.
     */
    private XMLParseException expectedInput(String charSet, int lineNr) {
        String msg = "Expected: " + charSet;
        return new XMLParseException(getTagName(), lineNr, msg);
    }


    /**
     * A value is missing for an attribute.
     */
    private XMLParseException valueMissingForAttribute(String key, int lineNr) {
        String msg = "Value missing for attribute with key \"" + key + "\"";
        return new XMLParseException(getTagName(), lineNr, msg);
    }

}
