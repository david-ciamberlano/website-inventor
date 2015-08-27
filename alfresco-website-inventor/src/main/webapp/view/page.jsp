<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title></title>
</head>
<body>
    <h1></h1>

    <h2>Pagine:</h2>
    <ul>
        <li><a href="<spring:url value="/p/${parentId}" />">..</a></li>
        <c:forEach items="${links}" var="link">
            <li><a href="<spring:url value="/p/${link.getId()}" />">${link.getName()}</a></li>
        </c:forEach>
    </ul>

    <h2>Contenuti</h2>
    <ul>
        <c:forEach items="${contents}" var="content">
                <h1>${content.getName()}</h1>
                <p>${content.getText()}</p>
                <p>${content.getUrl()}</p>
                <c:if test="${content.getType2() == 'IMAGE'}">
                    <img src="/proxy/${content.getId()}"/>
                </c:if>
            </li>
        </c:forEach>
    </ul>
</body>
</html>
