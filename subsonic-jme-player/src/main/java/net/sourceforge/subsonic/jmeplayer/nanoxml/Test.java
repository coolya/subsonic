/*
 * (c) Copyright WesternGeco. Unpublished work, created 2008. All rights
 * reserved under copyright laws. This information is confidential and is
 * the trade property of WesternGeco. Do not use, disclose, or reproduce
 * without the prior written permission of the owner.
 */
package net.sourceforge.subsonic.jmeplayer.nanoxml;

import java.util.Vector;

/**
 * @author Sindre Mehus
 */
public class Test {

    public static void main(String[] args) {

        String xml = "<artists>\n" +
                     "  <artist name='abba'/>\n" +
                     "  <artist name='acdc'/>\n" +
                     "</artists>";


        kXMLElement element = new kXMLElement();
        element.parseString(xml);
        System.out.println(element);

        Vector children = element.getChildren();
        for (int i = 0; i < children.size(); i++) {
            kXMLElement child = (kXMLElement) children.elementAt(i);
//            System.out.println(child);
            System.out.println(child.getProperty("name"));
        }
        System.out.println(children.size());
    }
}
