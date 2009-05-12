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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.proxy.dwr.Util;

/**
 * Provides AJAX-enabled services for the chatting.
 * This class is used by the DWR framework (http://getahead.ltd.uk/dwr/).
 *
 * @author Sindre Mehus
 */
public class ChatService {

    private final List<String> messages = new ArrayList<String>();

    public void shout(String message) {
        messages.add(message);

        WebContext webContext = WebContextFactory.get();
        String chatPage = webContext.getCurrentPage();

        // Find all the browser on window open on the chat page:
        Collection<?> sessions = webContext.getScriptSessionsByPage(chatPage);

        // Use the Javascript Proxy API to empty the chatlog <ul> element
        // and re-fill it with new messages
        Util util = new Util(sessions);
        util.removeAllOptions("chatlog");
        util.addOptions("chatlog", messages.toArray(new String[0]));
    }

}