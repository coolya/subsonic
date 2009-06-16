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

import net.sourceforge.subsonic.service.SecurityService;
import net.sourceforge.subsonic.util.BoundedList;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides AJAX-enabled services for the chatting.
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class ChatService {

    private final LinkedList<Message> messages = new BoundedList<Message>(10);
    private SecurityService securityService;

    public synchronized void shout(String message) {
        WebContext webContext = WebContextFactory.get();
        message = StringUtils.trimToNull(message);
        String user = securityService.getCurrentUsername(webContext.getHttpServletRequest());
        if (message != null && user != null) {
            messages.add(new Message(message, user, new Date()));
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

    public static class Message {

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