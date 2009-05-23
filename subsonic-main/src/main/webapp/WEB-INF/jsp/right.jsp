<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/interface/chatService.js"/>"></script>

<body class="bgcolor1" onload="init()">

<script type="text/javascript">
    function init() {
        dwr.engine.setActiveReverseAjax(true);
    }
    function shout() {
        chatService.shout($("message").value);
    }

</script>

<p>
    Your Message:
    <input id="message" onkeypress="dwr.util.onReturn(event, shout)"/>
    <input type="button" value="Send" onclick="shout()"/>
</p>
<hr/>

<ul id="chatlog" style="list-style-type:none;">
</ul>

</body>
</html>