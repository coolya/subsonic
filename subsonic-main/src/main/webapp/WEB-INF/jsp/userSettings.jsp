<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="include.jsp" %>

<html><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="<c:url value="/style.css"/>" rel="stylesheet">
</head>

<body onload="javascript:enablePasswordFields()">

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="user"/>
</c:import>

<script type="text/javascript" language="javascript">
    function enablePasswordFields() {
        var display = "none";
        if (document.getElementById("passwordChange").checked) {
            display = "inline";
        }
        document.getElementById("passwordTable").style.display = display;
    }
</script>

<table>
    <tr>
        <td><b><fmt:message key="usersettings.title"/></b></td>
        <td>
            <select name="username" onchange="location='userSettings.view?username=' + options[selectedIndex].value;">
                <option value="">-- <fmt:message key="usersettings.newuser"/> --</option>
                <c:forEach items="${command.users}" var="user">
                    <option ${user.username eq command.username ? "selected" : ""}
                            value="${user.username}">${user.username}</option>
                </c:forEach>
            </select>
        </td>
    </tr>
</table>

<p/>

<form:form method="post" action="userSettings.view" commandName="command">
    <c:if test="${not command.admin}">
        <table>
            <tr>
                <td><form:checkbox path="adminRole" id="admin"/></td>
                <td><label for="admin"><fmt:message key="usersettings.admin"/></label></td>
            </tr>
            <tr>
                <td><form:checkbox path="downloadRole" id="download"/></td>
                <td><label for="download"><fmt:message key="usersettings.download"/></label></td>
            </tr>
            <tr>
                <td><form:checkbox path="uploadRole" id="upload"/></td>
                <td><label for="upload"><fmt:message key="usersettings.upload"/></label></td>
            </tr>
            <tr>
                <td><form:checkbox path="playlistRole" id="playlist"/></td>
                <td><label for="playlist"><fmt:message key="usersettings.playlist"/></label></td>
            </tr>
            <tr>
                <td><form:checkbox path="coverArtRole" id="coverArt"/></td>
                <td><label for="coverArt"><fmt:message key="usersettings.coverart"/></label></td>
            </tr>
            <tr>
                <td><form:checkbox path="commentRole" id="comment"/></td>
                <td><label for="comment"><fmt:message key="usersettings.comment"/></label></td>
            </tr>
        </table>

        <br/>

        <c:if test="${not command.new}">
            <table>
                <tr>
                    <td><form:checkbox path="delete" id="delete"/></td>
                    <td><label for="delete"><fmt:message key="usersettings.delete"/></label></td>
                </tr>
            </table>
        </c:if>
    </c:if>

    <c:choose>
        <c:when test="${command.new}">

            <table>
                <tr>
                    <td><fmt:message key="usersettings.username"/></td>
                    <td><form:password path="username"/></td>
                    <td style="color:red"><form:errors path="username"/></td>
                </tr>
                <tr>
                    <td><fmt:message key="usersettings.password"/></td>
                    <td><form:password path="password"/></td>
                    <td style="color:red"><form:errors path="password"/></td>
                </tr>
                <tr>
                    <td><fmt:message key="usersettings.confirmpassword"/></td>
                    <td><form:password path="confirmPassword"/></td>
                    <td/>
                </tr>
            </table>
        </c:when>

        <c:otherwise>
            <table>
                <tr>
                    <td><form:checkbox path="passwordChange" id="passwordChange" onclick="javascript:enablePasswordFields()"/></td>
                    <td><label for="passwordChange"><fmt:message key="usersettings.changepassword"/></label></td>
                </tr>
            </table>

            <table id="passwordTable" style="display:none">
                <tr>
                    <td><fmt:message key="usersettings.newpassword"/></td>
                    <td><form:password path="password" id="path"/></td>
                    <td style="color:red"><form:errors path="password"/></td>
                </tr>
                <tr>
                    <td><fmt:message key="usersettings.confirmpassword"/></td>
                    <td><form:password path="confirmPassword" id="confirmPassword"/></td>
                    <td/>
                </tr>
            </table>
        </c:otherwise>
    </c:choose>

    <p/>
    <input type="submit" value="<fmt:message key="common.save"/>">
</form:form>

</body></html>
