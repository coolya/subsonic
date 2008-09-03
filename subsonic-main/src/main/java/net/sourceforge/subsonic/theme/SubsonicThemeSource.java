package net.sourceforge.subsonic.theme;

import org.springframework.ui.context.support.ResourceBundleThemeSource;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Theme source implementation which uses two resource bundles: the
 * theme specific (e.g., barents.properties), and the default (default.properties).
 *
 * @author Sindre Mehus
 */
public class SubsonicThemeSource extends ResourceBundleThemeSource {

    private String defaultResourceBundle;

    @Override
    protected MessageSource createMessageSource(String basename) {
        ResourceBundleMessageSource messageSource = (ResourceBundleMessageSource) super.createMessageSource(basename);

        ResourceBundleMessageSource parentMessageSource = new ResourceBundleMessageSource();
        parentMessageSource.setBasename(defaultResourceBundle);
        messageSource.setParentMessageSource(parentMessageSource);

        return messageSource;
    }

    public void setDefaultResourceBundle(String defaultResourceBundle) {
        this.defaultResourceBundle = defaultResourceBundle;
    }
}
