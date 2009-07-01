<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%--@elvariable id="command" type="net.sourceforge.subsonic.command.SearchCommand"--%>

<html><head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>

    <script type="text/javascript">
        function search(offset) {
            dwr.util.setValue("offset", offset);
            document.searchForm.submit();
        }
        function previous() {
            search(parseInt(dwr.util.getValue("offset")) - parseInt(dwr.util.getValue("count")));
        }
        function next() {
            search(parseInt(dwr.util.getValue("offset")) + parseInt(dwr.util.getValue("count")));
        }
    </script>

</head>
<body class="mainframe">

<h1>
    <img src="<spring:theme code="searchImage"/>" alt=""/>
    <fmt:message key="search.title"/>
</h1>

<form:form commandName="command" method="post" action="search.view" name="searchForm">
    <form:hidden path="offset" id="offset"/>
    <form:hidden path="count" id="count"/>
    <table>
        <tr>
            <td><fmt:message key="search.query.any"/></td>
            <td><form:input path="any" size="35"/></td>
            <td style="padding-left:0.25em"><input type="submit" onclick="search(0)" value="<fmt:message key="search.search"/>"/></td>
        </tr>
        <tr>
            <td style="padding-top:1.0em"><fmt:message key="search.query.title"/></td>
            <td style="padding-top:1.0em"><form:input path="title" size="35"/></td>
            <td/>
        </tr>
        <tr>
            <td><fmt:message key="search.query.album"/></td>
            <td><form:input path="album" size="35"/></td>
            <td/>
        </tr>
        <tr>
            <td><fmt:message key="search.query.artist"/></td>
            <td><form:input path="artist" size="35"/></td>
            <td/>
        </tr>
        <tr>
            <td style="padding-top:1.0em"><fmt:message key="search.newer"/></td>
            <td style="padding-top:1.0em">
                <form:select path="time" cssStyle="vertical-align:middle" id="time">
                    <fmt:message key="search.select" var="select"/>
                    <fmt:message key="search.day" var="day"/>
                    <fmt:message key="search.week" var="week"/>
                    <fmt:message key="search.weeks" var="weeks"/>
                    <fmt:message key="search.month" var="month"/>
                    <fmt:message key="search.months" var="months"/>
                    <fmt:message key="search.year" var="year"/>

                    <form:option value="0" label="${select}"/>
                    <form:option value="1d" label="1 ${day}"/>
                    <form:option value="1w" label="1 ${week}"/>
                    <form:option value="2w" label="2 ${weeks}"/>
                    <form:option value="1m" label="1 ${month}"/>
                    <form:option value="3m" label="3 ${months}"/>
                    <form:option value="6m" label="6 ${months}"/>
                    <form:option value="1y" label="1 ${year}"/>
                </form:select>
            </td>
            <td/>
        </tr>
    </table>

</form:form>

<c:if test="${command.indexBeingCreated}">
    <p class="warning"><fmt:message key="search.index"/></p>
</c:if>

<c:if test="${command.matches != null}">

    <table class="indent"><tr>
        <c:choose>
            <c:when test="${command.totalHits eq 0}">
                <th><fmt:message key="search.hits.none"/></th>
            </c:when>
            <c:otherwise>
                <th style="padding-right:2em">
                    <fmt:message key="search.hits">
                        <fmt:param value="${command.firstHit}"/>
                        <fmt:param value="${command.lastHit}"/>
                        <fmt:param value="${command.totalHits}"/>
                    </fmt:message>
                </th>

                <c:if test="${command.firstHit > 1}">
                    <th><div class="back" style="padding-right:1em"><a href="javascript:noop()" onclick="previous()"><fmt:message key="search.hits.previous"/></a></div></th>
                </c:if>

                <c:if test="${command.lastHit < command.totalHits}">
                    <th><div class="forward"><a href="javascript:noop()" onclick="next()"><fmt:message key="search.hits.next"/></a></div></th>
                </c:if>
            </c:otherwise>
        </c:choose>
    </tr></table>
</c:if>

<table style="border-collapse:collapse">
    <c:forEach items="${command.matches}" var="match" varStatus="loopStatus">

        <sub:url value="/main.view" var="mainUrl">
            <sub:param name="path" value="${match.musicFile.parent.path}"/>
        </sub:url>

        <tr>
            <c:import url="playAddDownload.jsp">
                <c:param name="path" value="${match.musicFile.path}"/>
                <c:param name="playEnabled" value="${command.user.streamRole and not command.partyModeEnabled}"/>
                <c:param name="addEnabled" value="${command.user.streamRole and (not command.partyModeEnabled or not match.musicFile.directory)}"/>
                <c:param name="downloadEnabled" value="${command.user.downloadRole and not command.partyModeEnabled}"/>
                <c:param name="asTable" value="true"/>
            </c:import>

            <td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-left:0.25em;padding-right:1.25em">
                ${match.title}
            </td>

            <td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:1.25em">
                <a href="${mainUrl}"><span class="detail">${match.album}</span></a>
            </td>

            <td ${loopStatus.count % 2 == 1 ? "class='bgcolor2'" : ""} style="padding-right:0.25em">
                <span class="detail">${match.artist}</span>
            </td>
        </tr>
    </c:forEach>
</table>

</body></html>