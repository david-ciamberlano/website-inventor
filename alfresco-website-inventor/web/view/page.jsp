<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title></title>
</head>
<body>
    <h2>Pagine:</h2>
    <ul>
        <li><a href="<spring:url value="/p/${parentId}" />">..</a></li>
        <c:forEach items="${childPages}" var="child">
            <li><a href="<spring:url value="/p/${child.getId()}" />">${child.getName()}</a></li>
        </c:forEach>
    </ul>

    <h2>Contenuti</h2>
    <ul>
        <c:forEach items="${contents}" var="content">
            <li>${content.getTitle()}</li>
        </c:forEach>
    </ul>
</body>
</html>
