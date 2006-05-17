<%--$Revision: 1.14 $ $Date: 2006/02/28 22:38:17 $--%>
<%@ page language="java" contentType="text/vnd.wap.wml; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ page import="net.sourceforge.subsonic.domain.*,
                 net.sourceforge.subsonic.service.*,
                 net.sourceforge.subsonic.util.*,
                 java.util.*"%>
<%
    String index = request.getParameter("index");
    InternationalizationService is = ServiceFactory.getInternationalizationService();
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
    SettingsService settings = ServiceFactory.getSettingsService();

    MusicFolder[] folders = settings.getAllMusicFolders();
    if (folders.length == 0) {
        out.print(is.get("wap.index.missing"));
    } else {

        String indexString = settings.getIndexString();
        String[] ignoredArticles = settings.getIgnoredArticlesAsArray();
        String[] shortcuts = new String[0];
        Map children = MusicIndex.getIndexedChildren(folders, MusicIndex.createIndexesFromExpression(indexString), ignoredArticles, shortcuts);

        // If an index is given as parameter, only show music files for this index.
        if (index != null) {
            List musicFiles = (List) children.get(new MusicIndex(index));
            if (musicFiles == null) {
                out.print(is.get("wap.index.missing"));
            } else {
                for (Iterator iterator = musicFiles.iterator(); iterator.hasNext();) {
                    MusicFile musicFile = (MusicFile) iterator.next();
                    out.println("<a href=\"browse.jsp?path=" + musicFile.urlEncode() + "\">" +
                            StringUtil.toHtml(musicFile.getTitle()) + "</a><br/>");
                }
            }
        }

        // Otherwise, list all indexes.
        else {
            out.println("<b><a href=\"playlist.jsp\">[" + is.get("wap.index.playlist") + "]</a></b><br/>");
            out.println("<b><a href=\"search.jsp\">[" + is.get("wap.index.search") + "]</a></b><br/>");
            out.println("<b><a href=\"settings.jsp\">[" + is.get("wap.index.settings") + "]</a></b><br/></small></p><p><small>");
            for (Iterator i = children.keySet().iterator(); i.hasNext();) {
                MusicIndex musicIndex = (MusicIndex) i.next();
                out.print("<a href=\"index.jsp?index=" + StringUtil.urlEncode(musicIndex.getIndex()) +
                        "\">" + musicIndex.getIndex() + "</a> ");
            }
        }
    }
%>

     </small></p>
  </card>
</wml>

