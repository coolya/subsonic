<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>
</head>

<body class="mainframe" onload="enablePasswordChangeFields()">

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="user"/>
</c:import>

<script type="text/javascript" language="javascript">
    function enablePasswordChangeFields() {
        var changePasswordCheckbox = $("passwordChange");
        var ldapCheckbox = $("ldapAuthenticated");
        var passwordChangeTable = $("passwordChangeTable");
        var passwordChangeCheckboxTable = $("passwordChangeCheckboxTable");

        if (changePasswordCheckbox && changePasswordCheckbox.checked && (ldapCheckbox == null || !ldapCheckbox.checked)) {
            passwordChangeTable.show();
        } else {
            passwordChangeTable.hide();
        }

        if (changePasswordCheckbox) {
            if (ldapCheckbox && ldapCheckbox.checked) {
                passwordChangeCheckboxTable.hide();
            } else {
                passwordChangeCheckboxTable.show();
            }
        }
    }
</script>

<table class="indent">
    <tr>
        <td><b><fmt:message key="usersettings.title"/></b></td>
        <td>
            <select name="username" onchange="location='userSettings.view?userIndex=' + (selectedIndex - 1);">
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
                <td><form:checkbox path="adminRole" id="admin" cssClass="checkbox"/></td>
                <td><label for="admin"><fmt:message key="usersettings.admin"/></label></td>
            </tr>
            <tr>
                <td><form:checkbox path="streamRole" id="stream" cssClass="checkbox"/></td>
                <td><label for="stream"><fmt:message key="usersettings.stream"/></label></td>
            </tr>
            <tr>
                <td><form:checkbox path="downloadRole" id="download" cssClass="checkbox"/></td>
                <td><label for="download"><fmt:message key="usersettings.download"/></label></td>
            </tr>
            <tr>
                <td><form:checkbox path="uploadRole" id="upload" cssClass="checkbox"/></td>
                <td><label for="upload"><fmt:message key="usersettings.upload"/></label></td>
            </tr>
            <tr>
                <td><form:checkbox path="playlistRole" id="playlist" cssClass="checkbox"/></td>
                <td><label for="playlist"><fmt:message key="usersettings.playlist"/></label></td>
            </tr>
            <tr>
                <td><form:checkbox path="coverArtRole" id="coverArt" cssClass="checkbox"/></td>
                <td><label for="coverArt"><fmt:message key="usersettings.coverart"/></label></td>
            </tr>
            <tr>
                <td><form:checkbox path="commentRole" id="comment" cssClass="checkbox"/></td>
                <td><label for="comment"><fmt:message key="usersettings.comment"/></label></td>
            </tr>
            <tr>
                <td><form:checkbox path="podcastRole" id="podcast" cssClass="checkbox"/></td>
                <td><label for="podcast"><fmt:message key="usersettings.podcast"/></label></td>
            </tr>
        </table>
    </c:if>

    <table class="indent">
        <tr>
            <td><fmt:message key="playersettings.maxbitrate"/></td>
            <td>
                <form:select path="transcodeSchemeName" cssStyle="width:8em">
                    <c:forEach items="${command.transcodeSchemeHolders}" var="transcodeSchemeHolder">
                        <form:option value="${transcodeSchemeHolder.name}" label="${transcodeSchemeHolder.description}"/>
                    </c:forEach>
                </form:select>
            </td>
            <td><a href="helpPopup.view?topic=transcode" onclick="return popup(this, 'help')">
                <fmt:message key="common.help" var="help"/>
                <img src="<c:url value="/icons/help_small.png"/>" alt="" title="${help}"></a></td>
            <c:if test="${not command.transcodingSupported}">
                <td class="warning"><fmt:message key="playersettings.nolame"/></td>
            </c:if>
        </tr>
    </table>

    <c:if test="${not command.new and not command.admin}">
        <table class="indent">
            <tr>
                <td><form:checkbox path="delete" id="delete" cssClass="checkbox"/></td>
                <td><label for="delete"><fmt:message key="usersettings.delete"/></label></td>
            </tr>
        </table>
    </c:if>

    <c:if test="${command.ldapEnabled and not command.admin}">
        <table>
            <tr>
                <td><form:checkbox path="ldapAuthenticated" id="ldapAuthenticated" cssClass="checkbox" onclick="javascript:enablePasswordChangeFields()"/></td>
                <td><label for="ldapAuthenticated"><fmt:message key="usersettings.ldap"/></label></td>
                <td><a href="helpPopup.view?topic=ldap" onclick="return popup(this, 'help')"><img src="<c:url value="/icons/help_small.png"/>" alt="${help}" title="${help}"></a></td>
            </tr>
        </table>
    </c:if>

    <c:choose>
        <c:when test="${command.new}">

            <table class="indent">
                <tr>
                    <td><fmt:message key="usersettings.username"/></td>
                    <td><form:input path="username"/></td>
                    <td class="warning"><form:errors path="username"/></td>
                </tr>
                <tr>
                    <td><fmt:message key="usersettings.password"/></td>
                    <td><form:password path="password"/></td>
                    <td class="warning"><form:errors path="password"/></td>
                </tr>
                <tr>
                    <td><fmt:message key="usersettings.confirmpassword"/></td>
                    <td><form:password path="confirmPassword"/></td>
                    <td/>
                </tr>
            </table>
        </c:when>

        <c:otherwise>
            <table id="passwordChangeCheckboxTable">
                <tr>
                    <td><form:checkbox path="passwordChange" id="passwordChange" onclick="javascript:enablePasswordChangeFields()" cssClass="checkbox"/></td>
                    <td><label for="passwordChange"><fmt:message key="usersettings.changepassword"/></label></td>
                </tr>
            </table>

            <table id="passwordChangeTable" style="display:none">
                <tr>
                    <td><fmt:message key="usersettings.newpassword"/></td>
                    <td><form:password path="password" id="path"/></td>
                    <td class="warning"><form:errors path="password"/></td>
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
