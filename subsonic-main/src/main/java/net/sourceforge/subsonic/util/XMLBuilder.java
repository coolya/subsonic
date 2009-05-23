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

    private final StringBuilder buf = new StringBuilder();
    private final Stack<String> elementStack = new Stack<String>();

    /**
     * Adds an XML preamble, with the given encoding. The preamble will typically
     * look like this:
     * <p/>
     * <code>&lt;?xml version="1.0" encoding="UTF-8"?&gt;</code>
     *
     * @param encoding The encoding to put in the preamble.
     * @return A reference to this object.
     */
    public XMLBuilder preamble(String encoding) {
        buf.append("<?xml version=\"1.0\" encoding=\"").append(encoding).append("\"?>");
        newline();
        return this;
    }

    /**
     * Adds an element with the given name and attributes.
     *
     * @param element    The element name.
     * @param attributes The element attributes.
     * @return A reference to this object.
     */
    public XMLBuilder add(String element, Attribute... attributes) {
        indent();
        elementStack.push(element);
        buf.append('<').append(element);

        if (attributes.length > 0) {
            buf.append(' ');
        }
        for (int i = 0; i < attributes.length; i++) {
            attributes[i].append(buf);
            if (i < attributes.length - 1) {
                buf.append(' ');
            }
        }

        buf.append('>');
        newline();
        return this;
    }

    /**
     * Adds an element with the given name and a single attribute.
     *
     * @param element        The element name.
     * @param attributeKey   The attributes key.
     * @param attributeValue The attributes value.
     * @return A reference to this object.
     */
    public XMLBuilder add(String element, String attributeKey, Object attributeValue) {
        return add(element, new Attribute(attributeKey, attributeValue));
    }

    /**
     * Adds the element with the given name and value. The element is also closed, so
     * there is no need to call {@link #end()}.
     *
     * @param element The element name.
     * @param value   The element value. If <code>null</code>, the element will be empty.
     * @return A reference to this object.
     */
    public XMLBuilder addClosed(String element, Object value) {
        indent();
        buf.append('<').append(element).append('>');
        if (value != null) {
            buf.append(StringEscapeUtils.escapeXml(value.toString()));
        }
        buf.append("</").append(element).append('>');
        newline();
        return this;
    }

    /**
     * Closes the current element.
     *
     * @return A reference to this object.
     * @throws IllegalStateException If there are no unclosed elements.
     */
    public XMLBuilder end() throws IllegalStateException {
        if (elementStack.isEmpty()) {
            throw new IllegalStateException("There are no unclosed elements.");
        }

        String element = elementStack.pop();
        indent();
        buf.append("</").append(element).append('>');
        newline();
        return this;
    }

    /**
     * Closes all unclosed elements.
     *
     * @return A reference to this object.
     */
    public XMLBuilder endAll() {
        while (!elementStack.isEmpty()) {
            end();
        }
        return this;
    }

    private void indent() {
        int depth = elementStack.size();
        for (int i = 0; i < depth; i++) {
            buf.append(INDENTATION);
        }
    }

    private void newline() {
        buf.append(NEWLINE);
    }

    /**
     * Returns the XML document.
     *
     * @return The XML document.
     * @throws IllegalStateException If there are unclosed elements.
     */
    public String toString() throws IllegalStateException {
        if (!elementStack.isEmpty()) {
            throw new IllegalStateException("There are unclosed elements.");
        }

        return buf.toString();
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

        private void append(StringBuilder buf) {
            if (key != null && value!= null) {
                buf.append(key).append("=\"").append(StringEscapeUtils.escapeXml(value.toString())).append("\"");
            }
        }
    }
}
