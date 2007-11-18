<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
    <link href="<c:url value="/style/shadow.css"/>" rel="stylesheet">
    <title>Subsonic</title>

    <script type="text/javascript">
        if (window != window.top) {
            top.location.href = location.href;
        }
    </script>

</head>
<body class="mainframe" onload="document.getElementById('j_username').focus()">

<form action="<c:url value="/j_acegi_security_check"/>" method="POST">
    <div class="bgcolor2" align="center" style="border:1px solid black; padding:20px 50px 20px 50px; margin-top:100px">
        <table>
        <tr>
            <td colspan="2" style="padding-bottom:10px">
                <img src="<spring:theme code="logoImage"/>" alt=""/>
            </td>
        </tr>

        <tr>
            <td style="padding-right:10px"><fmt:message key="login.username"/></td>
            <td><input type="text" id="j_username" name="j_username" style="width:12em" tabindex="1"></td>
        </tr>

        <tr>
            <td style="padding-bottom:10px"><fmt:message key="login.password"/></td>
            <td style="padding-bottom:10px"><input type="password" name="j_password" style="width:12em" tabindex="2"/></td>
        </tr>

        <tr>
            <td><input name="submit" type="submit" value="<fmt:message key="login.login"/>" tabindex="4"></td>
            <td class="detail">
                <input type="checkbox" name="_acegi_security_remember_me" id="remember" class="checkbox" tabindex="3">
                <label for="remember"><fmt:message key="login.remember"/></label>
            </td>
        </tr>
            <c:if test="${model.logout}">
                <tr><td colspan="2" style="padding-top:10px"><b><fmt:message key="login.logout"/></b></td></tr>
            </c:if>
            <c:if test="${model.error}">
                <tr><td colspan="2" style="padding-top:10px"><b class="warning"><fmt:message key="login.error"/></b></td></tr>
            </c:if>

        </table>

        <c:if test="${model.insecure}">
            <p><b class="warning"><fmt:message key="login.insecure"/></b></p>
        </c:if>

    </div>
</form>
</body>
</html>
