<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>
<%@ include file="/include.jsp" %>

<html><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="<c:url value="/style.css"/>" rel="stylesheet">
    <title><fmt:message key="helppopup.title"/></title>
</head><body><html>

<script type="text/javascript">
    window.focus();
</script>

<h2><fmt:message key="helppopup.${model.topic}.title"/></h2>
<fmt:message key="helppopup.${model.topic}.text"/>

<p style="text-align:center">
    <a href="javascript:self.close()">[<fmt:message key="common.close"/>]</a>
</p>

</body></html>
