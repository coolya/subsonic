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
package net.sourceforge.subsonic.service;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Provides services for generating ads.
 *
 * @author Sindre Mehus
 */
public class AdService {


    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Date SFCC_CAMPAIGN_START;
    private static final Date SFCC_CAMPAIGN_END;
    private static final String SFCC_AD = "<iframe src='http://subsonic.sourceforge.net/vote.php' width='180' height='400' scrolling='no' border='0' marginwidth='0' style='border:none;' frameborder='0'></iframe>";

    static {
        try {
            SFCC_CAMPAIGN_START = DATE_FORMAT.parse("2009-06-22");
            SFCC_CAMPAIGN_END = DATE_FORMAT.parse("2009-07-21");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private final String[] ads = {
            "<iframe src='http://rcm.amazon.com/e/cm?t=subsonic-20&o=1&p=40&l=ur1&category=mp3&banner=0TBQHNYNA4B47J02NFG2&f=ifr' width='120' height='60' scrolling='no' border='0' marginwidth='0' style='border:none;' frameborder='0'></iframe>",
            "<OBJECT classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' codebase='http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab' id='Player_3d36fdd7-b2fa-4dfd-b517-c5efe035a14d'  WIDTH='120px' HEIGHT='500px'> <PARAM NAME='movie' VALUE='http://ws.amazon.com/widgets/q?ServiceVersion=20070822&MarketPlace=US&ID=V20070822%2FUS%2Fsubsonic-20%2F8010%2F3d36fdd7-b2fa-4dfd-b517-c5efe035a14d&Operation=GetDisplayTemplate'><PARAM NAME='quality' VALUE='high'><PARAM NAME='bgcolor' VALUE='#FFFFFF'><PARAM NAME='allowscriptaccess' VALUE='always'><embed src='http://ws.amazon.com/widgets/q?ServiceVersion=20070822&MarketPlace=US&ID=V20070822%2FUS%2Fsubsonic-20%2F8010%2F3d36fdd7-b2fa-4dfd-b517-c5efe035a14d&Operation=GetDisplayTemplate' id='Player_3d36fdd7-b2fa-4dfd-b517-c5efe035a14d' quality='high' bgcolor='#ffffff' name='Player_3d36fdd7-b2fa-4dfd-b517-c5efe035a14d' allowscriptaccess='always'  type='application/x-shockwave-flash' align='middle' height='500px' width='120px'/> </OBJECT> <NOSCRIPT><A HREF='http://ws.amazon.com/widgets/q?ServiceVersion=20070822&MarketPlace=US&ID=V20070822%2FUS%2Fsubsonic-20%2F8010%2F3d36fdd7-b2fa-4dfd-b517-c5efe035a14d&Operation=NoScript'>Amazon.com Widgets</A></NOSCRIPT>",
            "<iframe src='http://rcm.amazon.com/e/cm?t=subsonic-20&o=1&p=40&l=ur1&category=kindle&banner=19NTJJCKSX6TY1C567G2&f=ifr' width='120' height='60' scrolling='no' border='0' marginwidth='0' style='border:none;' frameborder='0'></iframe>",
            "<iframe src='ad/omakasa.html' width='120' height='240' scrolling='no' border='0' marginwidth='0' style='border:none;' frameborder='0'></iframe>",
            "<OBJECT classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' codebase='http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab' id='Player_3fde1609-804d-46de-8802-2a16321cf533'  WIDTH='160px' HEIGHT='400px'> <PARAM NAME='movie' VALUE='http://ws.amazon.com/widgets/q?ServiceVersion=20070822&MarketPlace=US&ID=V20070822%2FUS%2Fsubsonic-20%2F8009%2F3fde1609-804d-46de-8802-2a16321cf533&Operation=GetDisplayTemplate'><PARAM NAME='quality' VALUE='high'><PARAM NAME='bgcolor' VALUE='#FFFFFF'><PARAM NAME='allowscriptaccess' VALUE='always'><embed src='http://ws.amazon.com/widgets/q?ServiceVersion=20070822&MarketPlace=US&ID=V20070822%2FUS%2Fsubsonic-20%2F8009%2F3fde1609-804d-46de-8802-2a16321cf533&Operation=GetDisplayTemplate' id='Player_3fde1609-804d-46de-8802-2a16321cf533' quality='high' bgcolor='#ffffff' name='Player_3fde1609-804d-46de-8802-2a16321cf533' allowscriptaccess='always'  type='application/x-shockwave-flash' align='middle' height='400px' width='160px'/> </OBJECT> <NOSCRIPT><A HREF='http://ws.amazon.com/widgets/q?ServiceVersion=20070822&MarketPlace=US&ID=V20070822%2FUS%2Fsubsonic-20%2F8009%2F3fde1609-804d-46de-8802-2a16321cf533&Operation=NoScript'>Amazon.com Widgets</A></NOSCRIPT>",
            "<iframe src='http://rcm.amazon.com/e/cm?t=subsonic-20&o=1&p=40&l=ur1&category=unboxdigital&banner=10NVPFMW8ACPNX4T4E82&f=ifr' width='120' height='60' scrolling='no' border='0' marginwidth='0' style='border:none;' frameborder='0'></iframe>",
            "<OBJECT classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' codebase='http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab' id='Player_2e2141ca-ec13-4dc9-88f2-08be95e47e6d'  WIDTH='160px' HEIGHT='300px'> <PARAM NAME='movie' VALUE='http://ws.amazon.com/widgets/q?ServiceVersion=20070822&MarketPlace=US&ID=V20070822%2FUS%2Fsubsonic-20%2F8014%2F2e2141ca-ec13-4dc9-88f2-08be95e47e6d&Operation=GetDisplayTemplate'><PARAM NAME='quality' VALUE='high'><PARAM NAME='bgcolor' VALUE='#FFFFFF'><PARAM NAME='allowscriptaccess' VALUE='always'><embed src='http://ws.amazon.com/widgets/q?ServiceVersion=20070822&MarketPlace=US&ID=V20070822%2FUS%2Fsubsonic-20%2F8014%2F2e2141ca-ec13-4dc9-88f2-08be95e47e6d&Operation=GetDisplayTemplate' id='Player_2e2141ca-ec13-4dc9-88f2-08be95e47e6d' quality='high' bgcolor='#ffffff' name='Player_2e2141ca-ec13-4dc9-88f2-08be95e47e6d' allowscriptaccess='always'  type='application/x-shockwave-flash' align='middle' height='300px' width='160px'></embed></OBJECT> <NOSCRIPT><A HREF='http://ws.amazon.com/widgets/q?ServiceVersion=20070822&MarketPlace=US&ID=V20070822%2FUS%2Fsubsonic-20%2F8014%2F2e2141ca-ec13-4dc9-88f2-08be95e47e6d&Operation=NoScript'>Amazon.com Widgets</A></NOSCRIPT>"
    };
    private int adInterval;
    private int pageCount;
    private int adIndex;

    /**
     * Returns an ad or <code>null</code> if no ad should be displayed.
     */
    public String getAd() {
        if (pageCount++ % adInterval == 0) {

            if (isSourceForgeCommunityChoiceCampaign()) {
                return SFCC_AD;
            }

            adIndex = (adIndex + 1) % ads.length;
            return ads[adIndex];
        }

        return null;
    }

    private boolean isSourceForgeCommunityChoiceCampaign() {
        Date now = new Date();
        return now.after(SFCC_CAMPAIGN_START) && now.before(SFCC_CAMPAIGN_END);
    }


    /**
     * Set by Spring.
     */
    public void setAdInterval(int adInterval) {
        this.adInterval = adInterval;
    }
}