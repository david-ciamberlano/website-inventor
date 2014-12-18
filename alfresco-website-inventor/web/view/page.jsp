<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title></title>
</head>
<body>
    Elenco pagine:
    <ul>
        <li><a href="<spring:url value="/p?path=${parentPath}" />">..</a></li>
        <c:forEach items="${childPages}" var="child">
            <li><a href="<spring:url value="/p?path=${child.getPath()}" />">${child.getName()}</a></li>
        </c:forEach>
    </ul>
</body>
</html>
