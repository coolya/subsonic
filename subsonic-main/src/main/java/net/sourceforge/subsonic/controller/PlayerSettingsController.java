package net.sourceforge.subsonic.controller;

import net.sourceforge.subsonic.command.*;
import net.sourceforge.subsonic.domain.*;
import net.sourceforge.subsonic.service.*;
import org.springframework.web.servlet.mvc.*;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.*;
import java.util.*;

/**
 * Controller for the player settings page.
 *
 * @author Sindre Mehus
 */
public class PlayerSettingsController extends SimpleFormController {

    private PlayerService playerService;
    private SecurityService securityService;
    private TranscodingService transcodingService;

    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {

        handleRequestParameters(request);
        List<Player> players = getPlayers(request);

        User user = securityService.getCurrentUser(request);
        PlayerSettingsCommand command = new PlayerSettingsCommand();
        Player player = null;
        String playerId = request.getParameter("id");
        if (playerId != null) {
            player = playerService.getPlayerById(playerId);
        } else if (!players.isEmpty()) {
            player = players.get(0);
        }

        if (player != null) {
            command.setPlayerId(player.getId());
            command.setName(player.getName());
            command.setDescription(player.toString());
            command.setType(player.getType());
            command.setLastSeen(player.getLastSeen());
            command.setDynamicIp(player.isDynamicIp());
            command.setAutoControlEnabled(player.isAutoControlEnabled());
            command.setCoverArtSchemeName(player.getCoverArtScheme().name());
            command.setTranscodeSchemeName(player.getTranscodeScheme().name());
            command.setTechnologyName(player.getTechnology().name());
            command.setAllTranscodings(transcodingService.getAllTranscodings());
            Transcoding[] activeTranscodings = transcodingService.getTranscodingsForPlayer(player);
            int[] activeTranscodingIds = new int[activeTranscodings.length];
            for (int i = 0; i < activeTranscodings.length; i++) {
                activeTranscodingIds[i] = activeTranscodings[i].getId();
            }
            command.setActiveTranscodingIds(activeTranscodingIds);
        }

        command.setTranscodingSupported(transcodingService.isDownsamplingSupported());
        command.setTranscodeDirectory(transcodingService.getTranscodeDirectory().getPath());
        command.setCoverArtSchemes(CoverArtScheme.values());
        command.setTranscodeSchemes(TranscodeScheme.values());
        command.setTechnologies(PlayerTechnology.values());
        command.setPlayers(players.toArray(new Player[players.size()]));
        command.setAdmin(user.isAdminRole());

        return command;
    }

    @Override
    protected void doSubmitAction(Object comm) throws Exception {
        PlayerSettingsCommand command = (PlayerSettingsCommand) comm;
        Player player = playerService.getPlayerById(command.getPlayerId());

        player.setAutoControlEnabled(command.isAutoControlEnabled());
        player.setCoverArtScheme(CoverArtScheme.valueOf(command.getCoverArtSchemeName()));
        player.setDynamicIp(command.isDynamicIp());
        player.setName(StringUtils.trimToNull(command.getName()));
        player.setTranscodeScheme(TranscodeScheme.valueOf(command.getTranscodeSchemeName()));
        player.setTechnology(PlayerTechnology.valueOf(command.getTechnologyName()));

        playerService.updatePlayer(player);
        transcodingService.setTranscodingsForPlayer(player, command.getActiveTranscodingIds());

        command.setReloadNeeded(true);
    }

    private List<Player> getPlayers(HttpServletRequest request) {
        User user = securityService.getCurrentUser(request);
        String username = user.getUsername();
        Player[] players = playerService.getAllPlayers();
        List<Player> authorizedPlayers = new ArrayList<Player>();

        for (Player player : players) {
            // Only display authorized players.
            if (user.isAdminRole() || username.equals(player.getUsername())) {
                authorizedPlayers.add(player);
            }
        }
        return authorizedPlayers;
    }

    private void handleRequestParameters(HttpServletRequest request) {
        if (request.getParameter("delete") != null) {
            playerService.removePlayerById(request.getParameter("delete"));
        } else if (request.getParameter("clone") != null) {
            playerService.clonePlayer(request.getParameter("clone"));
        }
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setTranscodingService(TranscodingService transcodingService) {
        this.transcodingService = transcodingService;
    }
}
