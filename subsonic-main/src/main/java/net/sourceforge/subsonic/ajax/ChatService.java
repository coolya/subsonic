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
package net.sourceforge.subsonic.ajax;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.util.BoundedList;
import net.sourceforge.subsonic.Logger;

/**
 * Provides AJAX-enabled services for the chatting.
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class ChatService {

    private static final Logger LOG = Logger.getLogger(ChatService.class);
    private static final Object CACHE_KEY = 1;
    private static final int MAX_MESSAGES = 10;

    private LinkedList<Message> messages;
    private SecurityService securityService;
    private Ehcache chatCache;
    private static final long TTL_MILLIS = TimeUnit.DAYS.toMillis(7);

    /**
     * Invoked by Spring.
     */
    public void init() {
        try {
            Element element = chatCache.get(CACHE_KEY);
            if (element != null && element.getValue() != null) {
                messages = (LinkedList<Message>) element.getValue();
            } else {
                messages = new BoundedList<Message>(MAX_MESSAGES);
            }
        } catch (Exception x) {
            LOG.warn("Failed to re-create chat messages.", x);
            messages = new BoundedList<Message>(MAX_MESSAGES);
        }

        // Delete old messages every hour.
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable runnable = new Runnable() {
            public void run() {
                removeOldMessages();
            }
        };
        executor.scheduleWithFixedDelay(runnable, 0, 1, TimeUnit.HOURS);
    }

    private synchronized void removeOldMessages() {
        long now = System.currentTimeMillis();
        for (Iterator<Message> iterator = messages.iterator(); iterator.hasNext();) {
            Message message = iterator.next();
            if (now - message.getDate().getTime() > TTL_MILLIS) {
                iterator.remove();
            }
        }
    }

    public synchronized void addMessage(String message) {
        WebContext webContext = WebContextFactory.get();
        message = StringUtils.trimToNull(message);
        String user = securityService.getCurrentUsername(webContext.getHttpServletRequest());
        if (message != null && user != null) {
            messages.add(new Message(message, user, new Date()));
            chatCache.put(new Element(CACHE_KEY, messages));
        }

        ScriptBuffer script = new ScriptBuffer();
        script.appendScript("receiveMessages(").appendData(messages).appendScript(");");

        // Find all the browsers showing the chat page. Invoke javascript for populating the chat log.
        String chatPage = webContext.getCurrentPage();
        Collection<ScriptSession> sessions = (Collection<ScriptSession>) webContext.getScriptSessionsByPage(chatPage);
        for (ScriptSession session : sessions) {
            session.addScript(script);
        }
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setChatCache(Ehcache chatCache) {
        this.chatCache = chatCache;
    }

    public static class Message implements Serializable {

        private final String content;
        private final String username;
        private final Date date;

        public Message(String content, String username, Date date) {
            this.content = content;
            this.username = username;
            this.date = date;
        }

        public String getContent() {
            return content;
        }

        public String getUsername() {
            return username;
        }

        public Date getDate() {
            return date;
        }

    }
}