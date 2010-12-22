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
package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.service.MusicFileService;
import net.sourceforge.subsonic.service.PlayerService;
import net.sourceforge.subsonic.service.SettingsService;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

/**
 * Controller for the page used to play videos.
 *
 * @author Sindre Mehus
 */
public class VideoPlayerController extends ParameterizableViewController {

    public static final int DEFAULT_BIT_RATE = 1000;
    public static final int[] BIT_RATES = {300, 400, 500, 700, 1000, 1500, 2000};
    private static final long TRIAL_DAYS = 30L;

    private MusicFileService musicFileService;
    private SettingsService settingsService;
    private PlayerService playerService;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        String path = request.getParameter("path");
        map.put("video", musicFileService.getMusicFile(path));
        map.put("player", playerService.getPlayer(request, response).getId());
        map.put("maxBitRate", ServletRequestUtils.getIntParameter(request, "maxBitRate", DEFAULT_BIT_RATE));
        map.put("bitRates", BIT_RATES);

        if (!settingsService.isLicenseValid() && settingsService.getVideoTrialExpires() == null) {
            Date expiryDate = new Date(System.currentTimeMillis() + TRIAL_DAYS * 24L * 3600L * 1000L);
            settingsService.setVideoTrialExpires(expiryDate);
            settingsService.save();
        }
        Date trialExpires = settingsService.getVideoTrialExpires();
        map.put("trialExpires", trialExpires);
        map.put("trialExpired", trialExpires != null && trialExpires.before(new Date()));
        map.put("trial", trialExpires != null && !settingsService.isLicenseValid());

        ModelAndView result = super.handleRequestInternal(request, response);
        result.addObject("model", map);
        return result;
    }

    public void setMusicFileService(MusicFileService musicFileService) {
        this.musicFileService = musicFileService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }
}
