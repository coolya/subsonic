<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
</head>

<body class="mainframe" onload="javascript:populate(0)">
<h1><fmt:message key="albuminfo.title"/></h1>
<sub:url value="main.view" var="backUrl"><sub:param name="path" value="${command.path}"/></sub:url>
<div class="back"><a href="${backUrl}"><fmt:message key="common.back"/></a></div>
<br/>

<form:form commandName="command" method="post" action="albumInfo.view">
    <form:hidden path="path"/>
<table><tr>
    <td><fmt:message key="albuminfo.artist"/></td>
    <td><form:input path="artist"/></td>
    <td style="padding-left:10pt"><fmt:message key="albuminfo.album"/></td>
    <td><form:input path="album"/></td>
    <td><input type="submit" value="<fmt:message key="albuminfo.search"/>"/></td>
</tr></table>
</form:form>

<p>
    <c:set var="hits" value="${fn:length(command.matches)}"/>
    <c:choose>
        <c:when test="${hits == 0}">
            <fmt:message key="albuminfo.hits.none"/>
        </c:when>
        <c:when test="${hits == 1}">
            <fmt:message key="albuminfo.hits.one"/>
        </c:when>
        <c:otherwise>
            <fmt:message key="albuminfo.hits.many"><fmt:param value="${hits}"/></fmt:message>
        </c:otherwise>
    </c:choose>
</p>

<ol>
    <c:forEach items="${command.matches}" var="match" varStatus="loopStatus">
        <li><a href="javascript:populate(${loopStatus.count - 1})">${match.album}</a></li>
    </c:forEach>
</ol>

<script type="text/javascript">

    var artists = new Array();
    var released = new Array();
    var labels = new Array();
    var reviews = new Array();
    var buyUrls = new Array();
    var images = new Array();

    <c:forEach items="${command.matches}" var="match" varStatus="loopStatus">
            artists[${loopStatus.count - 1}] = "${match.artists}";
            released[${loopStatus.count - 1}] = "${match.released}";
            labels[${loopStatus.count - 1}] = "${match.label}";
            reviews[${loopStatus.count - 1}] = "${match.reviews}";
            buyUrls[${loopStatus.count - 1}] = "${match.detailPageUrl}";
            images[${loopStatus.count - 1}] = "${match.imageUrl}";
    </c:forEach>

    function populate(i) {
      document.getElementById('artists').innerHTML = artists[i];
      document.getElementById('released').innerHTML = released[i];
      document.getElementById('label').innerHTML = labels[i];
      document.getElementById('review').innerHTML = reviews[i];
      document.getElementById('buy').href = buyUrls[i];
      document.getElementById('image').src = images[i];
    }
</script>

<c:if test="${hits > 0}">
    <table class="ruleTable" width="80%">
        <tr><td width="170" rowspan="6"><img id="image" src="" alt=""/></td></tr>
        <tr><td width="80" class="ruleTableHeader"><fmt:message key="albuminfo.artist"/></td><td class="ruleTableCell" id="artists"></td></tr>
        <tr><td class="ruleTableHeader"><fmt:message key="albuminfo.released"/></td><td class="ruleTableCell" id="released"></td></tr>
        <tr><td class="ruleTableHeader"><fmt:message key="albuminfo.label"/></td><td class="ruleTableCell" id="label"></td></tr>
        <tr><td class="ruleTableHeader"><fmt:message key="albuminfo.review"/></td><td class="ruleTableCell" id="review"></td></tr>
        <tr><td  class="ruleTableCell" colspan="2"><a id="buy" target="_blank"><b><fmt:message key="albuminfo.amazon"/></b></a></td></tr>
    </table>
</c:if>

</body>
</html>