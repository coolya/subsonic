package net.sourceforge.subsonic.service;

/**
 * Provides services for generating ads.
 *
 * @author Sindre Mehus
 */
public class AdService {

    private final String[] ads = {
            "<iframe src='http://rcm.amazon.com/e/cm?t=subsonic-20&o=1&p=40&l=ur1&category=mp3&banner=0TBQHNYNA4B47J02NFG2&f=ifr' width='120' height='60' scrolling='no' border='0' marginwidth='0' style='border:none;' frameborder='0'></iframe>",
            "<iframe src='http://rcm.amazon.com/e/cm?t=subsonic-20&o=1&p=40&l=ur1&category=unboxdigital&banner=10NVPFMW8ACPNX4T4E82&f=ifr' width='120' height='60' scrolling='no' border='0' marginwidth='0' style='border:none;' frameborder='0'></iframe>",
            "<script type='text/javascript'>amazon_ad_tag = 'subsonic-20'; amazon_ad_width = '120'; amazon_ad_height = '240'; amazon_ad_link_target = 'new';</script><script type='text/javascript' src='http://www.assoc-amazon.com/s/ads.js'></script>"
    };
    private int adInterval;
    private int pageCount;
    private int adIndex;
    /**
     * Returns an ad or <code>null</code> if no ad should be displayed.
     */
    public String getAd() {
        if (pageCount++ % adInterval == 0) {
            adIndex = (adIndex + 1) % ads.length;
            return ads[adIndex];
        }

        return null;
    }

    /**
     * Set by Spring.
     */
    public void setAdInterval(int adInterval) {
        this.adInterval = adInterval;
    }
}