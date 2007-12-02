package net.sourceforge.subsonic.util;

import java.util.ArrayDeque;
import java.util.Deque;

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
    private final Deque<String> elementStack = new ArrayDeque<String>();

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
     * Adds an element with the given name and properties.
     *
     * @param element    The element name.
     * @param attributes The element attributes.
     * @return A reference to this object.
     */
    public XMLBuilder add(String element, Attribute... attributes) {
        indent();
        elementStack.addFirst(element);
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
     * Adds the element with the given name and value. The element is also closed, so
     * there is no need to call {@link #end()}.
     *
     * @param element The element name.
     * @param value   The element value. If <code>null</code>, the element will be empty.
     * @return A reference to this object.
     */
    public XMLBuilder add(String element, Object value) {
        indent();
        buf.append('<').append(element).append('>');
        if (value != null) {
            buf.append(value);
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

        String element = elementStack.removeFirst();
        indent();
        buf.append("</").append(element).append('>');
        newline();
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
        private final String value;

        public Attribute(String key, String value) {
            this.key = key;
            this.value = value;
        }

        private void append(StringBuilder buf) {
            buf.append(key).append("=\"").append(value).append("\"");
        }
    }
}
