<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%--@elvariable id="command" type="net.sourceforge.subsonic.command.SearchCommand"--%>

<html><head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>

    <%--<script type="text/javascript">--%>
        <%--function search(offset) {--%>
            <%--dwr.util.setValue("offset", offset);--%>
            <%--document.searchForm.submit();--%>
        <%--}--%>
        <%--function previous() {--%>
            <%--search(parseInt(dwr.util.getValue("offset")) - parseInt(dwr.util.getValue("count")));--%>
        <%--}--%>
        <%--function next() {--%>
            <%--search(parseInt(dwr.util.getValue("offset")) + parseInt(dwr.util.getValue("count")));--%>
        <%--}--%>
    <%--</script>--%>

</head>
<body class="mainframe bgcolor1">

<h1>
    <img src="<spring:theme code="searchImage"/>" alt=""/>
    <fmt:message key="search.title"/>
</h1>

<form:form commandName="command" method="post" action="search.view" name="searchForm">
    <table>
        <tr>
            <td><fmt:message key="search.query"/></td>
            <td style="padding-left:0.25em"><form:input path="query" size="35"/></td>
            <td style="padding-left:0.25em"><input type="submit" onclick="search(0)" value="<fmt:message key="search.search"/>"/></td>
        </tr>
    </table>

</form:form>

<c:if test="${command.indexBeingCreated}">
    <p class="warning"><fmt:message key="search.index"/></p>
</c:if>

<c:if test="${empty command.songs}">
    <p class="warning"><fmt:message key="search.hits.none"/></p>
</c:if>

<table style="border-collapse:collapse">
    <c:forEach items="${command.songs}" var="match" varStatus="loopStatus">

        <sub:url value="/main.view" var="mainUrl">
            <sub:param name="path" value="${match.parent.path}"/>
        </sub:url>

        <tr>
            <c:import url="playAddDownload.jsp">
                <c:param name="path" value="${match.path}"/>
                <c:param name="playEnabled" value="${command.user.streamRole and not command.partyModeEnabled}"/>
                <c:param name="addEnabled" value="${command.user.streamRole and (not command.partyModeEnabled or not match.directory)}"/>
                <c:param name="downloadEnabled" value="${command.user.downloadRole and not command.partyModeEnabled}"/>
                <c:param name="asTable" value="true"/>
            </c:import>

            <td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-left:0.25em;padding-right:1.25em">
                ${match.metaData.title}
            </td>

            <td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1.25em">
                <a href="${mainUrl}"><span class="detail">${match.metaData.album}</span></a>
            </td>

            <td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:0.25em">
                <span class="detail">${match.metaData.artist}</span>
            </td>
        </tr>
    </c:forEach>
</table>

</body></html>