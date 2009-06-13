<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
    <script type="text/javascript" src="<c:url value="/dwr/interface/renameService.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>    
</head>
<body class="mainframe">
<script type="text/javascript" src="<c:url value="/script/wz_tooltip.js"/>"></script>
<script type="text/javascript" src="<c:url value="/script/tip_balloon.js"/>"></script>
<script type="text/javascript" language="javascript">
    var index = 0;
    var fileCount = ${fn:length(model.songs)};

    function preview() {
       index = 0;
       for (i = 0; i < fileCount; i++) {
           dwr.util.setValue("status" + i, "");
       }
       previewNextFile(); 
    }
    function previewNextFile(){
    	var path = dwr.util.getValue("path" + index);
    	renameService.getFileDestinationPath(path,
    			  dwr.util.getValue("patternInput"),
    	    	previewCallback);
    }

    function previewCallback(result){
    	dwr.util.setValue("status" + index, result, { escapeHtml:false });
      index++;
      if (index < fileCount) {
    	  previewNextFile();
      } 
    }
    function renameFiles(){    	
    	index = 0;
      for (i = 0; i < fileCount; i++) {
          dwr.util.setValue("status" + i, "");
      }
      renameNextFile();
    }

    function renameNextFile(){
    	if(!(document.getElementById("enabled"+index).checked)){
            index++;
            if (index < fileCount) {
            	renameNextFile();
            } 
      }
    	else{
	    	var path = dwr.util.getValue("path" + index);
	    	var musicRootSelect = document.getElementById("musicFolder");
	        renameService.renameMusicFile(path,
	              dwr.util.getValue("patternInput"),
	              musicRootSelect.options[musicRootSelect.selectedIndex].value,
	              renameFileCallback);
    	}
    }

    function renameFileCallback(result){
        var message;
        if (result == "SKIPPED") {
            message = "<fmt:message key="edittags.skipped"/>";
        }
        else if (result.substr(0,6) == "ERROR:"){
        	  message = "<div class='warning'><fmt:message key="edittags.error"/></div>";
        	  var errors = dwr.util.getValue("errors");
            errors += result + "<br/>";
            dwr.util.setValue("errors", errors, { escapeHtml:false });
        }
        else{
        	  var checkbox = document.getElementById("enabled"+index);
        	  checkbox.checked = false;
        	  checkbox.disabled = true;
        	  message = "<fmt:message key="rename.renamedTo"/>"+" "+result;
        	  
        }
        dwr.util.setValue("status" + index, 
    	    	message, 
    	    	{ escapeHtml:false });
        index++;
        if (index < fileCount) {
        	renameNextFile();
        }
    }
    
    function updateTags() {
        document.getElementById("save").disabled = true;
        index = 0;
        dwr.util.setValue("errors", "");
        for (i = 0; i < fileCount; i++) {
            dwr.util.setValue("status" + i, "");
        }
        updateNextTag();
    }
    function updateNextTag() {
        var path = dwr.util.getValue("path" + index);
        var artist = dwr.util.getValue("artist" + index);
        var track = dwr.util.getValue("track" + index);
        var album = dwr.util.getValue("album" + index);
        var title = dwr.util.getValue("title" + index);
        var year = dwr.util.getValue("year" + index);
        var genre = dwr.util.getValue("genre" + index);
        dwr.util.setValue("status" + index, "<fmt:message key="edittags.working"/>");
        tagService.setTags(path, track, artist, album, title, year, genre, setTagsCallback);
    }
    function setTagsCallback(result) {
        var message;
        if (result == "SKIPPED") {
            message = "<fmt:message key="edittags.skipped"/>";
        } else if (result == "UPDATED") {
            message = "<b><fmt:message key="edittags.updated"/></b>";
        } else {
            message = "<div class='warning'><fmt:message key="edittags.error"/></div>"
            var errors = dwr.util.getValue("errors");
            errors += result + "<br/>";
            dwr.util.setValue("errors", errors, { escapeHtml:false });
        }
        dwr.util.setValue("status" + index, message, { escapeHtml:false });
        index++;
        if (index < fileCount) {
            updateNextTag();
        } else {
            document.getElementById("save").disabled = false;
        }
    }
</script>

<h1><fmt:message key="rename.title"/></h1>
<sub:url value="main.view" var="backUrl"><sub:param name="path" value="${model.path}"/></sub:url>
<div class="back"><a href="${backUrl}"><fmt:message key="common.back"/></a></div>

<table class="ruleTable indent">
    <tr>
        <th class="ruleTableHeader" width="10pt"/>
        <th class="ruleTableHeader" width="50%"><fmt:message key="edittags.file"/></th>
        <th class="ruleTableHeader" width="50%"><fmt:message key="edittags.status"/></th>
    </tr>

    <c:forEach items="${model.songs}" var="song" varStatus="loopStatus">
        <tr>
            <str:truncateNicely lower="35" upper="40" var="fileName">${song.fileName}</str:truncateNicely>
            <input type="hidden" name="path${loopStatus.count - 1}" value="${song.path}"/>
            <td class="ruleTableCell"><input type="checkbox" checked="checked" id="enabled${loopStatus.count - 1}"/></td>
            <td class="ruleTableCell" title="${song.fileName}">${fileName}</td>
            <td class="ruleTableCell"><div id="status${loopStatus.count - 1}"/></td>
        </tr>
    </c:forEach>

</table>
<table>
  <tr>
    <td><fmt:message key="rename.musicRoot"/></td>
    <td>
	   <select id="musicFolder">
	      <c:forEach items="${model.musicFolders}" var="musicFolder">
	          <option value="${musicFolder.path}">${musicFolder.name}</option>
	      </c:forEach>
	   </select>
    </td>
	   <td>
	     <c:import url="helpToolTip.jsp"><c:param name="topic" value="renameRoot"/></c:import>
    </td>
   </tr>
   <tr>
    <td><fmt:message key="rename.pattern"/></td>
    <td>
      <input type="text" size="50" name="patternInput" value="${model.pattern}"/>
    </td>
    <td>
      <c:import url="helpToolTip.jsp"><c:param name="topic" value="renamePattern"/></c:import>
    </td>
  </tr>
</table>
<p>
<fmt:message key="rename.patterns">
  <fmt:param value="${model.artistPattern}"/>
  <fmt:param value="${model.albumPattern}"/>
  <fmt:param value="${model.titlePattern}"/>
  <fmt:param value="${model.yearPattern}"/>
  <fmt:param value="${model.genrePattern}"/>
  <fmt:param value="${model.directorySep}"/>
  <fmt:param value="${model.trackPattern}"/>
</fmt:message>
</p>
<%--TODO print unknown help  --%>
<%--TODO add help --%>
<%--TODO help for illegal characters--%>
<p>
<input type="submit" id="save" value="<fmt:message key="rename.rename"/>" onclick="javascript:renameFiles()"/>
<input type="submit" id="preview" value="<fmt:message key="rename.preview"/>" onclick="javascript:preview()"/>
</p>
<div class="warning" id="errors"/>
</body></html>