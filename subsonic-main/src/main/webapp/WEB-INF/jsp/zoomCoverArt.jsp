<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html><head>
    <title>Subsonic</title>
    <%@ include file="head.jsp" %>
    <link href="<c:url value="/style/shadow.css"/>" rel="stylesheet">

<script type="text/javascript">
    function resize() {
        var width = document.images[0].width + 60;
        var height = document.images[0].height + 120;
        self.resizeTo(width, height);
    }
</script>
</head>

<body onload="resize()">

<div style="padding:10px 10px 5px 10px; float:left">
    <c:import url="coverArt.jsp">
        <c:param name="coverArtPath" value="${model.path}"/>
        <c:param name="showLink" value="false"/>
        <c:param name="showZoom" value="false"/>
        <c:param name="showChange" value="false"/>
    </c:import>
</div>

<p style="text-align:center;clear:both;">
    <a href="javascript:self.close()">[<fmt:message key="common.close"/>]</a>
</p>

</body></html>