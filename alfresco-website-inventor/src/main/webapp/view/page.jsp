<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title></title>
    <link rel="stylesheet" href="/resource/bootstrap-3.3.5-dist/css/bootstrap.css">
</head>
<body>
    <h1></h1>

    <h2>Pagine:</h2>
    <nav>
        <div><a href="<spring:url value="/p/${parentId}" />">..</a></div>
        <c:forEach items="${links}" var="link">
            <div><a href="<spring:url value="/p/${link.getId()}" />">${link.getName()}</a></div>
        </c:forEach>
    </nav>

    <h2>Contenuti</h2>
    <section>
        <c:forEach items="${contents}" var="content">
            <c:if test="${content.getType2() == 'IMAGE'}">
                <figure style="display: inline-block">
                    <a href="<spring:url value="/proxy/${content.id}" />" >
                        <img src="<spring:url value="/proxy/${content.getThumbnailId()}" />" alt="${content.getName()}"/>
                    </a>
                    <figcaption>${content.getName()}</figcaption>
                </figure>
            </c:if>
            <c:if test="${content.getType2() != 'IMAGE'}">
                <article>
                    <a href="<spring:url value="/proxy/${content.id}" />" >
                        <h2>${content.getName()}</h2>
                    </a>
                    <p>${content.getText()}</p>
                </article>
            </c:if>

        </c:forEach>
    </section>
</body>
</html>
