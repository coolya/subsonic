<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/dwr/interface/coverArtService.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>

    <script type="text/javascript" language="javascript">

        dwr.engine.setErrorHandler(null);

        function getImages(service) {
            $("wait").style.display = "inline";
            $("images").style.display = "none";
            $("success").style.display = "none";
            $("error").style.display = "none";
            $("errorDetails").style.display = "none";
            $("noImagesFound").style.display = "none";

            var artist = dwr.util.getValue("artist");
            var album = dwr.util.getValue("album");
            coverArtService.getCoverArtImages(service, artist, album, getImagesCallback);
        }

        function getImagesCallback(imageUrls) {
            var html = "";
            for (var i = 0; i < imageUrls.length; i++) {
                html += "<a href=\"javascript:setImage('" + imageUrls[i].imageDownloadUrl + "')\"><img src='" + imageUrls[i].imagePreviewUrl + "' style='padding:5pt' alt=''/></a>";
            }
            dwr.util.setValue("images", html, { escapeHtml:false });

            $("wait").style.display = "none";
            if (imageUrls.length > 0) {
                $("images").style.display = "inline";
            } else {
                $("noImagesFound").style.display = "inline";
            }
        }

        function setImage(imageUrl) {
            $("wait").style.display = "inline";
            $("images").style.display = "none";
            $("success").style.display = "none";
            $("error").style.display = "none";
            $("errorDetails").style.display = "none";
            $("noImagesFound").style.display = "none";
            var path = dwr.util.getValue("path");
            coverArtService.setCoverArtImage(path, imageUrl, setImageCallback);
        }

        function setImageCallback(errorDetails) {
            $("wait").style.display = "none";
            if (errorDetails != null) {
                dwr.util.setValue("errorDetails", "<br/>" + errorDetails, { escapeHtml:false });
                $("error").style.display = "inline";
                $("errorDetails").style.display = "inline";
            } else {
                $("success").style.display = "inline";
            }
        }
    </script>
</head>
<body class="mainframe bgcolor1">
<h1><fmt:message key="changecoverart.title"/></h1>
<table class="indent"><tr>
    <td><fmt:message key="changecoverart.artist"/></td>
    <td><input id="artist" name="artist" type="text" value="${model.artist}"/></td>
    <td style="padding-left:0.25em"><fmt:message key="changecoverart.album"/></td>
    <td><input id="album" name="album" type="text" value="${model.album}"/></td>
    <td style="padding-left:0.5em"><input type="submit" value="<fmt:message key="changecoverart.searchdiscogs"/>" onclick="getImages('discogs')"/></td>
</tr></table>

<table><tr>
    <input id="path" type="hidden" name="path" value="${model.path}"/>
    <td><label for="url"><fmt:message key="changecoverart.address"/></label></td>
    <td><input type="text" name="url" size="40" id="url" value="http://"/></td>
    <td><input type='submit' value='<fmt:message key="common.ok"/>' onclick="setImage(dwr.util.getValue('url'))"></td>
</tr></table>

<sub:url value="main.view" var="backUrl"><sub:param name="path" value="${model.path}"/></sub:url>
<div style="padding-top:0.5em;padding-bottom:0.5em">
    <div class="back"><a href="${backUrl}"><fmt:message key="common.back"/></a></div>
</div>

<h2 id="wait" style="display:none"><fmt:message key="changecoverart.wait"/></h2>
<h2 id="noImagesFound" style="display:none"><fmt:message key="changecoverart.noimagesfound"/></h2>
<h2 id="success" style="display:none"><fmt:message key="changecoverart.success"/></h2>
<h2 id="error" style="display:none"><fmt:message key="changecoverart.error"/></h2>
<div id="errorDetails" class="warning" style="display:none">
</div>
<div id="images" style="display:none;">
</div>

</body></html>