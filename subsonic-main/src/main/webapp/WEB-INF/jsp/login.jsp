<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>

    <script type="text/javascript">
        if (window != window.top) {
            top.location.href = location.href;
        }
    </script>

</head><body>

<h1>Login</h1>

<form action="<c:url value="/j_acegi_security_check"/>" method="POST">
    <table>
        <tr><td>User</td><td><input type='text' name='j_username'></td></tr>
        <tr><td>Password</td><td><input type='password' name='j_password'></td></tr>
        <tr><td><input type="checkbox" name="j_acegi_security_remember_me"></td><td>Don't ask for my password for two weeks</td></tr>

        <tr><td colspan='2'><input name="submit" type="submit"></td></tr>
    </table>

</form>

</body>
</html>
