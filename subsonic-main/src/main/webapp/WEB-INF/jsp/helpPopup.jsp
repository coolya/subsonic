<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
    <title><fmt:message key="helppopup.title"><fmt:param value="${model.brand}"/></fmt:message></title>
</head>
<body class="mainframe">

<script type="text/javascript">
    window.focus();
</script>

<h2><fmt:message key="helppopup.${model.topic}.title"><fmt:param value="${model.brand}"/></fmt:message></h2>
<fmt:message key="helppopup.${model.topic}.text"><fmt:param value="${model.brand}"/></fmt:message>

<p style="text-align:center">
    <a href="javascript:self.close()">[<fmt:message key="common.close"/>]</a>
</p>

</body></html>
