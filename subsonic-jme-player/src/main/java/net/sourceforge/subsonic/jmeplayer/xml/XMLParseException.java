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
