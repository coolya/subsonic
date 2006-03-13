 <%--$Revision: 1.4 $ $Date: 2006/02/28 22:38:17 $--%>
 <%@ page language="java" contentType="text/vnd.wap.wml; charset=utf-8" pageEncoding="iso-8859-1"%>
 <%@ page import="net.sourceforge.subsonic.service.*"%>

<%
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

      <p>
        <input name="query" value="" size="10"/>
        <anchor><%=is.get("wap.search.title")%>
          <go href="searchResult.jsp" method="get">
            <postfield name="query" value="$query"/>
          </go>
        </anchor>
      </p>

  </card>

</wml>

