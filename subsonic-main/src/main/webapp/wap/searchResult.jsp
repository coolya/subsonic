<%--$Revision: 1.12 $ $Date: 2006/02/28 22:38:17 $--%>
<%@ page language="java" contentType="text/vnd.wap.wml; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ page import="net.sourceforge.subsonic.domain.*,
                 net.sourceforge.subsonic.service.*,
                 net.sourceforge.subsonic.util.*,
                 java.util.*"%>
<%
    InternationalizationService is = ServiceFactory.getInternationalizationService();
    SearchService searchService = ServiceFactory.getSearchService();

    String query = request.getParameter("query");
    if (!searchService.isIndexCreated()) {
        searchService.createIndex();
    }

    boolean creatingIndex = searchService.isIndexBeingCreated();

    final int MAX_HITS = 50;
    List result = creatingIndex ? new ArrayList() : searchService.heuristicSearch(query, MAX_HITS, false, false, true, null);

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
    if (creatingIndex) {
        out.println(is.get("wap.searchresult.index"));
    }

    for (int i = 0; i < result.size(); i++) {
        MusicFile file = (MusicFile) result.get(i);
        out.println("<a href=\"browse.jsp?path=" + file.urlEncode() + "\">" +
                              StringUtil.toHtml(file.getTitle()) + "</a><br/>");
    }
%>

     </small></p>
  </card>

</wml>

