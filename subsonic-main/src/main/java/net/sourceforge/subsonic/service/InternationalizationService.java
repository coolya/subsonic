package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.*;
import org.apache.commons.io.*;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * Provides internationalization services, including methods for setting and getting the
 * currently selected locale, and methods for retrieving localized strings.
 *
 * @author Sindre Mehus
 */
public class InternationalizationService {

    private static final Logger LOG = Logger.getLogger(InternationalizationService.class);

    /** List of available locales. */
    private Locale[] locales;

    /** The currently selected locale. */
    private Locale locale;

    /** The resource bundle for the currently selected locale. */
    private ResourceBundle resourceBundle;

    private static final String LOCALES_FILE = "/net/sourceforge/subsonic/i18n/locales.txt";

    /**
     * Returns an internationalized string for the given key and the current locale.
     * @param key The key, e.g., "playlist.shuffle"
     * @return The internationalized string for the given key and the current locale.
     */
    public String get(String key) {
        return getResourceBundle().getString(key);
    }

    /**
     * Returns an internationalized string for the given key, argument list and current locale.
     * @param key The key, e.g., "playlist.shuffle"
     * @return The internationalized string for the given key, argument list and current locale.
     */
    public String get(String key, Object[] arguments) {
        String pattern = get(key);
        return MessageFormat.format(pattern, arguments);
    }

    /**
     * Convenience method. Equivalent to <code>get(key, new Object[] {arg1})</code>
     * @param key The key, e.g., "playlist.shuffle"
     * @param arg1 The single string argument.
     * @return The internationalized string for the given key, argument and current locale.
     * @see #get(String, Object[])
     */
    public String get(String key, String arg1) {
        return get(key, new Object[] {arg1});
    }

    /**
     * Convenience method. Equivalent to <code>get(key, new Object[] {arg1, arg2})</code>
     * @param key The key, e.g., "playlist.shuffle"
     * @param arg1 The first string argument.
     * @param arg2 The second string argument.
     * @return The internationalized string for the given key, argument and current locale.
     * @see #get(String, Object[])
     */
    public String get(String key, String arg1, String arg2) {
        return get(key, new Object[] {arg1, arg2});
    }

    /**
     * Convenience method. Equivalent to <code>get(key, new Object[] {String.valueOf(arg1)})</code>
     * @param key The key, e.g., "playlist.shuffle"
     * @param arg1 The single integer argument.
     * @return The internationalized string for the given key, argument and current locale.
     * @see #get(String, Object[])
     */
    public String get(String key, int arg1) {
        return get(key, new Object[] {String.valueOf(arg1)});
    }

    /**
     * Convenience method. Equivalent to <code>get(key, new Object[] {String.valueOf(arg1), String.valueIf(args2)})</code>
     * @param key The key, e.g., "playlist.shuffle"
     * @param arg1 The first integer argument.
     * @param arg2 The second integer argument.
     * @return The internationalized string for the given key, argument and current locale.
     * @see #get(String, Object[])
     */
    public String get(String key, int arg1, int arg2) {
        return get(key, new Object[] {String.valueOf(arg1), String.valueOf(arg2)});
    }

    /**
     * Sets the current locale.
     * @param locale The locale.
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
        resourceBundle = null;
        SettingsService settingsService = ServiceFactory.getSettingsService();
        settingsService.setLocale(locale);
        settingsService.save();
    }

    /**
     * Returns the current locale.
     * @return The current locale.
     */
    public Locale getLocale() {
        if (locale == null) {
            locale = ServiceFactory.getSettingsService().getLocale();
        }
        return locale;
    }

    /**
     * Returns a list of available locales.
     * @return A list of available locales.
     */
    public synchronized Locale[] getAvailableLocales() {
        if (locales == null) {

            InputStream in = null;
            BufferedReader reader = null;
            try {

                in = InternationalizationService.class.getResourceAsStream(LOCALES_FILE);
                reader = new BufferedReader(new InputStreamReader(in));
                List<Locale> result = new ArrayList<Locale>();
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    Locale loc = parseLocale(line);
                    if (loc != null) {
                        result.add(loc);
                    }
                }
                locales = result.toArray(new Locale[0]);

            } catch (IOException x) {
                LOG.error("Failed to resolve list of locales.", x);
                locales = new Locale[] {Locale.ENGLISH};
            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(reader);
            }
        }
        return locales;
    }

    private Locale parseLocale(String line) {
        line = line.trim();
        if (line.startsWith("#") || line.length() == 0) {
            return null;
        }
        String[] s = line.split("_");
        String language = s[0];
        String country = "";
        String variant = "";

        if (s.length > 1) {
            country = s[1];
        }
        if (s.length > 2) {
            variant = s[2];
        }
        return new Locale(language, country, variant);
    }

    /**
     * Returns the resource bundle for the current locale.
     * @return The resource bundle for the current locale.
     */
    private ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = ResourceBundle.getBundle("net.sourceforge.subsonic.i18n.ResourceBundle", getLocale());
        }

        return resourceBundle;
    }
}