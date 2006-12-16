package net.sourceforge.subsonic;

import java.io.*;
import java.util.*;

public class MissingTranslations  {

    public static void main(String[] args) throws IOException {
        diff("en", "es");
//        diff("en", "mk");
    }

    private static void diff(String locale1, String locale2) throws IOException {
        Properties en = new Properties();
        en.load(MissingTranslations.class.getResourceAsStream("/net/sourceforge/subsonic/i18n/ResourceBundle_" + locale1 + ".properties"));
        SortedMap<Object,Object> enSorted = new TreeMap<Object, Object>(en);

        Properties mk = new Properties();
        mk.load(MissingTranslations.class.getResourceAsStream("/net/sourceforge/subsonic/i18n/ResourceBundle_" + locale2 + ".properties"));

        System.out.println("\nMessages present in locale " + locale1 + " and missing in locale " + locale2 + ":");
        for (Map.Entry<Object, Object> entry : enSorted.entrySet()) {
            if (!mk.containsKey(entry.getKey())) {
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }

        }
    }
}
