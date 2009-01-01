<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/scriptaculous.js?load=effects"/>"></script>
</head>

<body class="bgcolor2">


<script type="text/javascript">

    window.addEventListener('load', function() {
        alert("h");
        setTimeout("$('image1').appear()", 1000);
    }, false);


    var player = null;
    var songs;

    function onload() {
//        new Effect.Opacity('image1', { from: 0.0, to: 1.0, duration: 2.5 });
//        new Effect.Opacity('image2', { from: 0.0, to: 1.0, duration: 2.5 });
//        new Effect.Opacity('image3', { from: 0.0, to: 1.0, duration: 2.5 });

//        setTimeout("$('image1').appear()", 1000);
//        $('image1').appear();

//        setTimeout("$('image1').appear()", 0);
//        setTimeout("$('image2').appear()", 250);
//        setTimeout("$('image3').appear()", 500);

//        $('image2').appear();
//        $('image3').appear();
    }


</script>

<%--<div id="foo" style="opacity:0.1; width:80px; height:80px; background:#c2defb; border:1px solid #333;"></div>--%>
<div id="image1" style="opacity:0.1;">
    <img  src="/icons/gpl.png" alt="" width="88" height="31"/>
</div>
<img id="image2" src="/icons/gpl.png" alt="" width="88" height="31" style="opacity:0.1;"/>
<img id="image3" src="/icons/gpl.png" alt="" width="88" height="31" style="opacity:0.1;"/>

</body>
</html>