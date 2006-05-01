<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="include.jsp" %>

<html><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!--[if gte IE 5.5000]>
    <script type="text/javascript" src="pngfix.js"></script>
    <![endif]-->
    <link href="<c:url value="/style.css"/>" rel="stylesheet">
    <script type="text/javascript" src="<c:url value="/scripts.js"/>"/>
</head><body>

<h2><a href="generalSettings.view?">GENERAL</a></h2>
<h2><a href="musicFolderSettings.view?"><fmt:message key="settings.musicfolder.title"/></a></h2>
<h2><a href="userSettings.view?"><fmt:message key="settings.user.title"/></a></h2>
<h2><a href="playerSettings.view?"><fmt:message key="settings.player.title"/></a></h2>
<h2><a href="internetRadioSettings.view?"><fmt:message key="settings.radio.title"/></a></h2>
<h2><a href="searchSettings.view?"><fmt:message key="settings.searchindex.title"/></a></h2>

<!--<div>-->
    <!--<div class="tabArea">-->
        <!--<a class="tab" target="myIframe" href="generalSettings.view?">GENERAL</a>-->
        <%--<a class="tab" target="myIframe" href="musicFolderSettings.view?"><fmt:message key="settings.musicfolder.title"/></a>--%>
        <%--<a class="tab" target="myIframe" href="userSettings.view?"><fmt:message key="settings.user.title"/></a>--%>
        <%--<a class="tab" target="myIframe" href="playerSettings.view?"><fmt:message key="settings.player.title"/></a>--%>
        <%--<a class="tab" target="myIframe" href="internetRadioSettings.view?"><fmt:message key="settings.radio.title"/></a>--%>
        <%--<a class="tab" target="myIframe" href="searchSettings.view?"><fmt:message key="settings.searchindex.title"/></a>--%>
    <!--</div>-->
    <!--<div class="tabMain">-->
        <!--<div class="tabIframeWrapper">-->
            <!--<iframe class="tabContent" name="myIframe" src="generalSettings.view?"-->
                    <!--marginheight="8" marginwidth="8" frameborder="0"></iframe>-->
        <!--</div>-->
    <!--</div>-->
<!--</div>-->

</body></html>