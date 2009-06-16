<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/dwr/interface/chatService.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>

<body class="bgcolor1" onload="init()">

<script type="text/javascript">
    function init() {
        dwr.engine.setActiveReverseAjax(true);
        chatService.shout(null);
    }
    function shout() {
        chatService.shout($("message").value);
        dwr.util.setValue("message", null);
    }
    function receiveMessages(messages) {

        // Delete all the rows except for the "pattern" row
        dwr.util.removeAllRows("chatlog", { filter:function(div) {
            return (div.id != "pattern");
        }});

        // Create a new set cloned from the pattern row
        for (var i = 0; i < messages.length; i++) {
            var message = messages[i];
            var id = i + 1;
            dwr.util.cloneNode("pattern", { idSuffix:id });
            dwr.util.setValue("user" + id, message.username + " " + message.date.getHours() + ":" + message.date.getMinutes());
            dwr.util.setValue("content" + id, message.content);
            $("pattern" + id).show();
        }
        //        var chatlog = "";
        //        for (var data in messages) {
        //            chatlog = "<div>" + dwr.util.escapeHtml(messages[data].content) + "</div>" + chatlog;
        //        }
        //        dwr.util.setValue("chatlog", chatlog, { escapeHtml:false });
    }

</script>


<div id="chatlog">
    <div id="pattern" style="display:none;margin:0;padding:0 0 0.15em 0;border:0">
        <span id="user" class="detail"></span> <span id="content"></span>
    </div>

    <p>
        <input id="message" onkeypress="dwr.util.onReturn(event, shout)"/>
        <input type="button" value="Send" onclick="shout()"/>
    </p>

</body>
</html>