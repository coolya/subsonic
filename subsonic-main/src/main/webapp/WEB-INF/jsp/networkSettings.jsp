<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>
<%--@elvariable id="command" type="net.sourceforge.subsonic.command.NetworkSettingsCommand"--%>

<html>
<head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>
    <script type="text/javascript" language="javascript">
        function enablePortFields() {
            var checkbox = $("portForwardingEnabled");
            var field = $("portForwardingPublicPort");

            if (checkbox && checkbox.checked) {
                field.enable();
            } else {
                field.disable();
            }
        }
    </script>
</head>
<body class="mainframe bgcolor1" onload="enablePortFields()">

<c:import url="settingsHeader.jsp">
    <c:param name="cat" value="network"/>
</c:import>

<form:form commandName="command" action="networkSettings.view" method="post">
    <p><form:checkbox id="portForwardingEnabled" path="portForwardingEnabled" onclick="enablePortFields()"/>
        <label for="portForwardingEnabled"><fmt:message key="networksettings.portforwardingenabled"/></label>
    </p>

    <p>
        <fmt:message key="networksettings.portforwardingport"/>
        <form:input id="portForwardingPublicPort" path="portForwardingPublicPort" size="6" cssStyle="margin-left:0.25em"/>
    </p>
</form:form>

<p>
    <input type="submit" value="<fmt:message key="common.save"/>" style="margin-right:0.3em">
    <input type="button" value="<fmt:message key="common.cancel"/>" onclick="location.href='nowPlaying.view'">
</p>

</body>
</html>