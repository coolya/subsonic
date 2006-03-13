<%--$Revision: 1.4 $ $Date: 2006/02/28 22:38:17 $--%>
<%@ page language="java" contentType="text/vnd.wap.wml; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ page import="net.sourceforge.subsonic.service.*,
                 net.sourceforge.subsonic.util.*"%>
<%@ page import="java.io.*"%>

<%
    InternationalizationService is = ServiceFactory.getInternationalizationService();
    PlaylistService playlistService = ServiceFactory.getPlaylistService();
%>

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE wml PUBLIC "-//WAPFORUM//DTD WML 1.1//EN" "http://www.wapforum.org/DTD/wml_1.1.xml">

<wml>

	<head>
		<meta http-equiv="Cache-Control" content="max-age=0" forua="true"/>
		<meta http-equiv="Cache-Control" content="must-revalidate" forua="true"/>
	</head>

    <template>
        <do type="prev" name="back" label="<%=is.get("common.back")%>"><prev/></do>
    </template>


    <card id="main" title="subsonic" newcontext="false">
    <p><small>
    <%

        File[] playlists = playlistService.getSavedPlaylists();

        for (int i = 0; i < playlists.length; i++) {
            String name = StringUtil.removeSuffix(playlists[i].getName());
            String encodedName = StringUtil.urlEncode(playlists[i].getName());
            out.println("<b><a href=\"playlist.jsp?load=" + encodedName + "\">" + name + "</a></b><br/>");
        }
    %>
    </small></p>

</card>
</wml>

