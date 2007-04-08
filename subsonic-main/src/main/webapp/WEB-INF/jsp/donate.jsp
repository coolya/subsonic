<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jsp" %>
</head>
<body>

<h1>
    <%--<img src="<c:url value="/icons/donate.png"/>" alt=""/>--%>
    <%--<fmt:message key="donate.title"/>--%>
    Donate
</h1>

<p>
    <c:url value="https://www.paypal.com/cgi-bin/webscr" var="paypalUrl">
        <c:param name="cmd" value="_xclick"/>
        <c:param name="business" value="sindre@activeobjects.no"/>
        <c:param name="item_name" value="Subsonic"/>
        <c:param name="no_shipping" value="1"/>
        <c:param name="currency_code" value="USD"/>
        <c:param name="tax" value="0"/>
        <c:param name="bn" value="PP-DonationsBF"/>
        <c:param name="charset" value="UTF-8"/>
    </c:url>
</p>

<p>
    <a href="${paypalUrl}" target="_blank">Donate</a>, or else...

    TODO: Mention suggested amount ($20?)
    TODO: Mention that people *must* specify a proper email address.
</p>
</body></html>