<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/webfx/range.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/webfx/timer.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/webfx/slider.js"/>"></script>
    <link type="text/css" rel="stylesheet" href="<c:url value="/script/webfx/luna.css"/>"/></head>

<body class="bgcolor2">

<div class="slider bgcolor2" id="slider-1" style="width:120px">
    <input class="slider-input" id="slider-input-1" name="slider-input-1"/>
</div>

<script type="text/javascript">

//    var slider = new Slider(document.getElementById("slider-1"), null);
var updateGainTimeoutId = 0;
var slider = new Slider(document.getElementById("slider-1"), document.getElementById("slider-input-1"));
slider.setValue(50);


slider.onchange = function () {
    clearTimeout(updateGainTimeoutId);
    updateGainTimeoutId = setTimeout("updateGain()", 250);
};

function updateGain() {
        var value = slider.getValue();
        alert(value);
    }

</script>

</body>
</html>