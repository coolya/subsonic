<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html><head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/scripts.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>
    <script type="text/javascript" language="javascript">
        function enableLdapFields() {
            var checkbox = $("ldap");
            var table = $("ldapTable");

            if (checkbox && checkbox.checked) {
                table.show();
            } else {
                table.hide();
            }
        }
    </script>
</head>

<body class="mainframe" onload="enableLdapFields()">

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="advanced"/>
</c:import>

<fmt:message key="common.help" var="help"/>
<c:url value="/icons/help_small.png" var="helpUrl"/>

<form:form method="post" action="advancedSettings.view" commandName="command">

    <table style="white-space:nowrap" class="indent">

        <tr>
            <td><fmt:message key="advancedsettings.downsamplecommand"/></td>
            <td>
                <form:input path="downsampleCommand" size="70"/>
                <a href="helpPopup.view?topic=downsampleCommand" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
            </td>
        </tr>

        <tr><td colspan="2">&nbsp;</td></tr>

        <tr>
            <td><fmt:message key="advancedsettings.coverartlimit"/></td>
            <td>
                <form:input path="coverArtLimit" size="8"/>
                <a href="helpPopup.view?topic=coverArtLimit" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="advancedsettings.downloadlimit"/></td>
            <td>
                <form:input path="downloadLimit" size="8"/>
                <a href="helpPopup.view?topic=downloadLimit" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="advancedsettings.uploadlimit"/></td>
            <td>
                <form:input path="uploadLimit" size="8"/>
                <a href="helpPopup.view?topic=uploadLimit" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
            </td>
        </tr>

        <tr>
            <td><fmt:message key="advancedsettings.streamport"/></td>
            <td>
                <form:input path="streamPort" size="8"/>
                <a href="helpPopup.view?topic=streamPort" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
            </td>
        </tr>

        <tr><td colspan="2">&nbsp;</td></tr>

        <tr>
            <td colspan="2">
                <form:checkbox path="ldapEnabled" id="ldap" cssClass="checkbox" onclick="javascript:enableLdapFields()"/>
                <label for="ldap"><fmt:message key="advancedsettings.ldapenabled"/></label>
                <a href="helpPopup.view?topic=ldap" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
            </td>
        </tr>

        <table class="indent" id="ldapTable" style="padding-left:2em">
            <tr>
                <td><fmt:message key="advancedsettings.ldapurl"/></td>
                <td colspan="3">
                    <form:input path="ldapUrl" size="70"/>
                    <a href="helpPopup.view?topic=ldapUrl" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
                </td>
            </tr>

            <tr>
                <td><fmt:message key="advancedsettings.ldapsearchfilter"/></td>
                <td colspan="3">
                    <form:input path="ldapSearchFilter" size="70"/>
                    <a href="helpPopup.view?topic=ldapSearchFilter" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
                </td>
            </tr>

            <tr>
                <td><fmt:message key="advancedsettings.ldapmanagerdn"/></td>
                <td>
                    <form:input path="ldapManagerDn" size="20"/>
                </td>
                <td><fmt:message key="advancedsettings.ldapmanagerpassword"/></td>
                <td>
                    <form:password path="ldapManagerPassword" size="20"/>
                    <a href="helpPopup.view?topic=ldapManagerDn" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
                </td>
            </tr>

            <tr>
                <td colspan="5">
                    <form:checkbox path="ldapAutoShadowing" id="ldapAutoShadowing" cssClass="checkbox"/>
                    <label for="ldapAutoShadowing"><fmt:message key="advancedsettings.ldapautoshadowing"/></label>
                    <a href="helpPopup.view?topic=ldapAutoShadowing" onclick="return popup(this, 'help')"><img src="${helpUrl}" alt="${help}" title="${help}"></a>
                </td>
            </tr>
        </table>

        <tr><td colspan="2"><input type="submit" value="<fmt:message key="common.save"/>"></td></tr>
    </table>
</form:form>

<c:if test="${command.reloadNeeded}">
    <script language="javascript" type="text/javascript">
        parent.frames.left.location.href="left.view?";
        parent.frames.playlist.location.href="playlist.view?";
    </script>
</c:if>

</body></html>