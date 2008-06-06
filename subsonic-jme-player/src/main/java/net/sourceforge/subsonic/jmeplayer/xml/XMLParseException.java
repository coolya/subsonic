package net.sourceforge.subsonic.jmeplayer.xml;

/**
 * An XMLParseException is thrown when an error occurs while parsing an XML
 * string.
 */
public class XMLParseException
        extends RuntimeException {

    /**
     * Where the error occurred, or -1 if the line number is unknown.
     */
    private int lineNumber;

    /**
     * Creates an exception.
     *
     * @param tag     The name of the tag where the error is located.
     * @param message A message describing what went wrong.
     */
    public XMLParseException(String tag, String message) {
        super("XML Parse Exception during parsing of "
              + ((tag == null) ? "the XML definition" : ("a " + tag + "-tag"))
              + ": " + message);
        lineNumber = -1;
    }


    /**
     * Creates an exception.
     *
     * @param tag        The name of the tag where the error is located.
     * @param lineNumber The number of the line in the input.
     * @param message    A message describing what went wrong.
     */
    public XMLParseException(String tag, int lineNumber, String message) {
        super("XML Parse Exception during parsing of "
              + ((tag == null) ? "the XML definition" : ("a " + tag + "-tag"))
              + " at line " + lineNumber + ": " + message);
        this.lineNumber = lineNumber;
    }


    /**
     * Where the error occurred, or -1 if the line number is unknown.
     */
    public int getLineNumber() {
        return lineNumber;
    }

}
