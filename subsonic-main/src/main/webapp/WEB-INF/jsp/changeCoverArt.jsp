<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/dwr/interface/coverArtService.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>

    <script type="text/javascript" language="javascript">

        dwr.engine.setErrorHandler(null);
        google.load('search', '1');

        var imageSearch;

        function getImages(service) {
            $("wait").style.display = "inline";
//            $("images").style.display = "none";
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

        function searchComplete() {

            alert(imageSearch.results.length);
            // Check that we got results
            if (imageSearch.results && imageSearch.results.length > 0) {

                // Grab our content div, clear it.
                var contentDiv = document.getElementById("images");
                contentDiv.innerHTML = '';

                // Loop through our results, printing them to the page.
                var results = imageSearch.results;
                for (var i = 0; i < results.length; i++) {
                    // For each result write it's title and image to the screen
                    var result = results[i];

                    // clone the .html node from the result
                    var node = result.html.cloneNode(true);

                    // attach the node into my dom
                    contentDiv.appendChild(node);




                    //              var imgContainer = document.createElement('div');
//              var title = document.createElement('div');
//
//              // We use titleNoFormatting so that no HTML tags are left in the
//              // title
//              title.innerHTML = result.titleNoFormatting;
//                    var image = document.createElement('img');
//                    image.src = result.url;
//                    contentDiv.appendChild(image);
                }

                // Now add links to additional pages of search results.
//            addPaginationLinks(imageSearch);
            }
        }

        function search() {
            var artist = dwr.util.getValue("artist");
            var album = dwr.util.getValue("album");
            imageSearch.execute(artist + " " + album);
        }

        function onLoad() {

            // Create an Image Search instance.
            imageSearch = new google.search.ImageSearch();

          // Set searchComplete as the callback function when a search is
          // complete.  The imageSearch object will have results in it.
          imageSearch.setSearchCompleteCallback(this, searchComplete, null);

          // Find me a beautiful car.
//          imageSearch.execute("Subaru STI");

          // Include the required Google branding
//          google.search.Search.getBranding('branding');

            // tell the searcher to draw itself and tell it where to attach
        }
        google.setOnLoadCallback(onLoad);


    </script>
</head>
<body class="mainframe bgcolor1">
<h1><fmt:message key="changecoverart.title"/></h1>
<table class="indent"><tr>
    <td><fmt:message key="changecoverart.artist"/></td>
    <td><input id="artist" name="artist" type="text" value="${model.artist}"/></td>
    <td style="padding-left:0.25em"><fmt:message key="changecoverart.album"/></td>
    <td><input id="album" name="album" type="text" value="${model.album}"/></td>
    <td style="padding-left:0.5em"><input type="submit" value="<fmt:message key="changecoverart.searchdiscogs"/>" onclick="search()"/></td>
</tr></table>

<table><tr>
    <input id="path" type="hidden" name="path" value="${model.path}"/>
    <td><label for="url"><fmt:message key="changecoverart.address"/></label></td>
    <td><input type="text" name="url" size="40" id="url" value="http://" onclick="select()"/></td>
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

<div id="images">
</div>

</body></html>