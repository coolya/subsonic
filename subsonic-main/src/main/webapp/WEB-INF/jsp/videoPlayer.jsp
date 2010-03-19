<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

<html>
<head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/script/swfobject.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/script/prototype.js"/>"></script>
</head>

<body class="mainframe bgcolor1" style="margin:15px">


<script type="text/javascript" language="javascript">

    function play(streamUrl) {

        var flashvars = {
            id:"player1",
            file:streamUrl,
            <%--duration:"${model.video.metaData.duration}",--%>
            autostart:"true",
            backcolor:"<spring:theme code="backgroundColor"/>",
            frontcolor:"<spring:theme code="textColor"/>",
            provider:"video"
        };
        var params = {
            allowfullscreen:"true",
            allowscriptaccess:"always"
        };
        var attributes = {
            id:"player1",
            name:"player1"
        };

        $("control").hide();
        swfobject.embedSWF("<c:url value="/flash/jw-player-5.0.swf"/>", "placeholder1", "100%", "100%", "9.0.0", false, flashvars, params, attributes);
    }

</script>

<div style="width:100%; height:97%">

    <div id="control">
        <c:if test="${not model.play}">

            <c:choose>
                <c:when test="${empty model.processedVideos}">
                    <p>This video must be processed before it can be streamed over the network.</p>
                </c:when>
                <c:otherwise>
                    <p>The video is available in the following qualities:</p>
                    <table style="width:100%;border-collapse:collapse;white-space:nowrap">
                    <tr>
                        <td class="bgcolor2" style="font-weight: bold;">Quality</td>
                        <td class="bgcolor2" style="font-weight: bold;">Status</td>
                        <td class="bgcolor2" style="font-weight: bold;">Bit rate</td>
                        <td class="bgcolor2" style="font-weight: bold;">Size</td>
                        <td class="bgcolor2" style="font-weight: bold;">Actions</td>
                    </tr>
                        <c:forEach items="${model.processedVideos}" var="video" varStatus="loopStatus">

                            <c:choose>
                                <c:when test="${loopStatus.count % 2 == 0}">
                                    <c:set var="class" value="class='bgcolor2'"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="class" value=""/>
                                </c:otherwise>
                            </c:choose>

                            <sub:url value="/stream" var="streamUrl">
                                <sub:param name="path" value="${model.video.path}"/>
                            </sub:url>
                            <tr>
                                <td ${class}>${video.quality}</td>
                                <td ${class}>${video.status}</td>
                                <td ${class}>${video.bitRate}</td>
                                <td ${class}>${video.size}</td>
                                <td ${class}><a href="#" onclick="play('${streamUrl}'); return false;">[Play]</a>
                                    <a href="videoPlayer.view?action=delete&id=${video.id}">[Delete]</a></td>
                            </tr>
                        </c:forEach>
                    </table>
                </c:otherwise>
            </c:choose>

            <c:choose>
                <c:when test="${empty model.qualities}">
                    <p>No video processing scripts found.</p>
                </c:when>
                <c:otherwise>
                    <form action="videoPlayer.view" method="POST">
                        Process video in quality
                        <select name="quality">
                            <c:forEach items="${model.qualities}" var="quality">
                                <option value="${quality}">${quality}</option>
                            </c:forEach>
                        </select>
                        <input type="hidden" name="action" value="create"/>
                        <input type="hidden" name="path" value="${model.video.path}"/>
                        <input type="submit" value="Start"/>
                    </form>
                </c:otherwise>
            </c:choose>
        </c:if>
    </div>

    <div id="wrapper" style="padding-top:1em">
        <div id="placeholder1"></div>
    </div>
</div>

</body>
</html>
