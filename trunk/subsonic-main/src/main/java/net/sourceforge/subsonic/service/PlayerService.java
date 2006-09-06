package net.sourceforge.subsonic.service;

import net.sourceforge.subsonic.dao.*;
import net.sourceforge.subsonic.domain.*;

import javax.servlet.http.*;
import java.util.*;

/**
 * Provides services for maintaining the set of players.
 *
 * @see Player
 * @author Sindre Mehus
 */
public class PlayerService {
    private static final String COOKIE_NAME = "player";

    private PlayerDao playerDao;
    private StatusService statusService;
    private SecurityService securityService;

    /**
     * Equivalent to <code>getPlayer(request, response, true)</code> .
     */
    public Player getPlayer(HttpServletRequest request, HttpServletResponse response) {
        return getPlayer(request, response, true, false);
    }

    /**
     * Returns the player associated with the given HTTP request.  If no such player exists, a new
     * one is created.
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @param remoteControlEnabled Whether this method should return a remote-controlled player.
     * @param isStreamRequest Whether the HTTP request is a request for streaming data.
     * @return The player associated with the given HTTP request.
     */
    public synchronized Player getPlayer(HttpServletRequest request, HttpServletResponse response,
                                         boolean remoteControlEnabled, boolean isStreamRequest) {

        // Find by 'player' request parameter.
        Player player = getPlayerById(request.getParameter("player"));

        // Find in session context.
        if (player == null && remoteControlEnabled) {
            player = getPlayerById((String) request.getSession().getAttribute("player"));
        }

        // Find by cookie.
        if (player == null && remoteControlEnabled) {
            player = getPlayerById(getPlayerIdFromCookie(request));
        }

        // Look for player with same IP address and user name.
        String remoteUser = securityService.getCurrentUsername(request);
        if (player == null) {
            player = getPlayerByIpAddressAndUsername(request.getRemoteAddr(), remoteUser);
        }

        // If no player was found, create it.
        if (player == null) {
            player = new Player();
            playerDao.createPlayer(player);
        }

        // Update player data.
        boolean isUpdate = false;
        if (remoteUser != null && !remoteUser.equals(player.getUsername())) {
            player.setUsername(remoteUser);
            isUpdate = true;
        }
        if (player.getIpAddress() == null || isStreamRequest ||
            (!isPlayerConnected(player) && player.isDynamicIp() && !request.getRemoteAddr().equals(player.getIpAddress()))) {
            player.setIpAddress(request.getRemoteAddr());
            isUpdate = true;
        }
        String userAgent = request.getHeader("user-agent");
        if (isStreamRequest) {
            player.setType(userAgent);
            player.setLastSeen(new Date());
            isUpdate = true;
        }

        if (isUpdate) {
            updatePlayer(player);
        }

        // Set cookie in response.
        if (response != null) {
            Cookie cookie = new Cookie(COOKIE_NAME, player.getId());
            cookie.setMaxAge(Integer.MAX_VALUE);
            response.addCookie(cookie);
        }

        // Save player in session context.
        if (remoteControlEnabled) {
            request.getSession().setAttribute("player", player.getId());
        }

        return player;
    }

    /**
     * Updates the given player.
     * @param player The player to update.
     */
    public void updatePlayer(Player player) {
        playerDao.updatePlayer(player);
    }

    /**
     * Returns the player with the given ID.
     * @param id The unique player ID.
     * @return The player with the given ID, or <code>null</code> if no such player exists.
     */
    public Player getPlayerById(final String id) {
        if (id == null) {
            return null;
        }
        for (Player player : getAllPlayers()) {
            if (id.equals(player.getId())) {
                return player;
            }
        }
        return null;
    }

    /**
     * Returns whether the given player is connected.
     * @param player The player in question.
     * @return Whether the player is connected.
     */
    private boolean isPlayerConnected(Player player) {
        return statusService.getStreamStatusesForPlayer(player).length > 0;
    }

    /**
     * Returns the player with the given IP address and username. If no username is given, only IP address is
     * used as search criteria.
     * @param ipAddress The IP address.
     * @param username The remote user.
     * @return The player with the given IP address, or <code>null</code> if no such player exists.
     */
    private Player getPlayerByIpAddressAndUsername(final String ipAddress, final String username) {
        if (ipAddress == null) {
            return null;
        }
        for (Player player : getAllPlayers()) {
            boolean ipMatches = ipAddress.equals(player.getIpAddress());
            boolean userMatches = username == null || username.equals(player.getUsername());
            if (ipMatches && userMatches) {
                return player;
            }
        }
        return null;
    }

    /**
     * Reads the player ID from the cookie in the HTTP request.
     * @param request The IP HTTP request.
     * @return The player ID embedded in the cookie, or <code>null</code> if cookie is not present.
     */
    private String getPlayerIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
    * Returns all currently registered players.
    * @return All currently registered players.
    */
    public Player[] getAllPlayers() {
        return playerDao.getAllPlayers();
    }

    /**
     * Removes the player with the given ID.
     * @param id The unique player ID.
     */
    public synchronized void removePlayerById(String id) {
        playerDao.deletePlayer(id);
    }

    /**
     * Creates and returns a clone of the given player.
     * @param playerId The ID of the player to clone.
     * @return The cloned player.
     */
    public Player clonePlayer(String playerId) {
        Player player = getPlayerById(playerId);
        if (player.getName() != null) {
            player.setName(player.getName() +  " (copy)");
        }

        playerDao.createPlayer(player);
        return player;
    }

    public void setStatusService(StatusService statusService) {
        this.statusService = statusService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setPlayerDao(PlayerDao playerDao) {
        this.playerDao = playerDao;
    }
}
