/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.util;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.XML;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Stack;


/**
 * Simplifies building of XML documents.
 * <p/>
 * <b>Example:</b><br/>
 * The following code:
 * <pre>
 * XMLBuilder builder = new XMLBuilder();
 * builder.add("foo").add("bar");
 * builder.add("zonk", 42);
 * builder.end().end();
 * System.out.println(builder.toString());
 * </pre>
 * produces the following XML:
 * <pre>
 * &lt;foo&gt;
 *   &lt;bar&gt;
 *     &lt;zonk&gt;42&lt;/zonk&gt;
 *   &lt;/bar&gt;
 * &lt;/foo&gt;
 * </pre>
 * This class is <em>not</em> thread safe.
 *
 * @author Sindre Mehus
 */
public class XMLBuilder {

    private static final String INDENTATION = "  ";
    private static final String NEWLINE = "\n";

    private final Writer writer = new StringWriter();
    private final Stack<String> elementStack = new Stack<String>();
    private final boolean json;

    /**
     * Equivalent to <code>this(false)</code>.
     */
    public XMLBuilder() {
        this(false);
    }

    /**
     * Creates a new instance.
     * @param json Whether to produce JSON rather than XML.
     */
    public XMLBuilder(boolean json) {
        this.json = json;
    }

    /**
     * Adds an XML preamble, with the given encoding. The preamble will typically
     * look like this:
     * <p/>
     * <code>&lt;?xml version="1.0" encoding="UTF-8"?&gt;</code>
     *
     * @param encoding The encoding to put in the preamble.
     * @return A reference to this object.
     */
    public XMLBuilder preamble(String encoding) throws IOException {
        writer.write("<?xml version=\"1.0\" encoding=\"");
        writer.write(encoding);
        writer.write("\"?>");
        newline();
        return this;
    }

    /**
     * Adds an element with the given name and a single attribute.
     *
     * @param element        The element name.
     * @param attributeKey   The attributes key.
     * @param attributeValue The attributes value.
     * @param close          Whether to close the element.
     * @return A reference to this object.
     */
    public XMLBuilder add(String element, String attributeKey, Object attributeValue, boolean close) throws IOException {
        return add(element, close, new Attribute(attributeKey, attributeValue));
    }

    /**
     * Adds an element with the given name and attributes.
     *
     * @param element    The element name.
     * @param close      Whether to close the element.
     * @param attributes The element attributes.
     * @return A reference to this object.
     */
    public XMLBuilder add(String element, boolean close, Attribute... attributes) throws IOException {
        return add(element, Arrays.asList(attributes), close);
    }

    /**
     * Adds an element with the given name and attributes.
     *
     * @param element    The element name.
     * @param attributes The element attributes.
     * @param close      Whether to close the element.
     * @return A reference to this object.
     */
    public XMLBuilder add(String element, Iterable<Attribute> attributes, boolean close) throws IOException {
        indent();
        elementStack.push(element);
        writer.write('<');
        writer.write(element);

        Iterator<Attribute> iterator = attributes.iterator();

        if (iterator.hasNext()) {
            writer.write(' ');
        }
        while (iterator.hasNext()) {
            Attribute attribute = iterator.next();
            attribute.append(writer);
            if (iterator.hasNext()) {
                writer.write(' ');
            }
        }

        if (close) {
            elementStack.pop();
            writer.write("/>");
        } else {
            writer.write('>');
        }

        newline();
        return this;
    }

    /**
     * Adds character data.
     *
     * @param text The character data.
     * @throws IOException
     */
    public void addText(String text) throws IOException {
        if (text != null) {
            writer.write(StringEscapeUtils.escapeXml(text));
        }
    }

    /**
     * Closes the current element.
     *
     * @return A reference to this object.
     * @throws IllegalStateException If there are no unclosed elements.
     */
    public XMLBuilder end() throws IllegalStateException, IOException {
        if (elementStack.isEmpty()) {
            throw new IllegalStateException("There are no unclosed elements.");
        }

        String element = elementStack.pop();
        indent();
        writer.write("</");
        writer.write(element);
        writer.write('>');
        newline();
        return this;
    }

    /**
     * Closes all unclosed elements.
     *
     * @return A reference to this object.
     */
    public XMLBuilder endAll() throws IOException {
        while (!elementStack.isEmpty()) {
            end();
        }
        return this;
    }

    /**
     * Returns the XML document as a string.
     */
    @Override
    public String toString() {
        String xml = writer.toString();
        if (!json) {
            return xml;
        }
        try {
            JSONObject jsonObject = XML.toJSONObject(xml);
            return jsonObject.toString(1);
        } catch (JSONException x) {
            throw new RuntimeException("Failed to convert from XML to JSON.", x);
        }
    }

    private void indent() throws IOException {
        int depth = elementStack.size();
        for (int i = 0; i < depth; i++) {
            writer.write(INDENTATION);
        }
    }

    private void newline() throws IOException {
        writer.write(NEWLINE);
    }

    /**
     * An XML element attribute.
     */
    public static class Attribute {

        private final String key;
        private final Object value;

        public Attribute(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        private void append(Writer writer) throws IOException {
            if (key != null && value != null) {
                writer.write(key);
                writer.write("=\"");
                writer.write(StringEscapeUtils.escapeXml(value.toString()));
                writer.write("\"");
            }
        }
    }
}
