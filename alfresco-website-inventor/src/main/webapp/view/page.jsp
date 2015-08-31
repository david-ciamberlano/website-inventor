<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title></title>
    <link rel="stylesheet" href="<spring:url value="/resource/bootstrap-3.3.5-dist/css/bootstrap.css" />">
</head>
<body>
    <h1></h1>
    <div class="container">
       <div class="row">
            <nav class="col-sm-4">
                <h2>Pagine:</h2>
                <div><a href="<spring:url value="/p/${parentId}" />"><span class="glyphicon glyphicon-circle-arrow-up"></span></a></div>
                <c:forEach items="${links}" var="link">
                    <div><a href="<spring:url value="/p/${link.getId()}" />">${link.getName()}</a></div>
                </c:forEach>
            </nav>

           <section class="col-sm-8">
                <h2>Contenuti</h2>
                <c:forEach items="${contents}" var="content">
                    <c:choose>
                        <c:when test="${content.getType() == 'IMAGE'}">
                            <figure>
                                <a href="<spring:url value="/proxy/${content.id}" />" >
                                    <img class="img-thumbnail" src="<spring:url value="/proxy/${content.getThumbnailId()}" />" alt="${content.getName()}"/>
                                </a>
                                <figcaption>${content.getName()}</figcaption>
                            </figure>
                        </c:when>
                        <c:when test="${content.getType() == ContentType.TEXT}">
                            <article>
                                <p>${content.getText()}</p>
                            </article>
                        </c:when>
                        <c:otherwise>
                            <!-- default: generic file, contentType="GENERIC" -->
                            <article class="media">
                                <a class="" href="<spring:url value="/proxy/${content.id}" />" >
                                    <div class="media-left media-middle">
                                        <img class="img-thumbnail media-object" src="<spring:url value="/proxy/${content.getThumbnailId()}" />" alt="${content.getName()}"/>
                                    </div>
                                    <p class="media-body media-middle">${content.mimeType}</p>
                                </a>
                            </article>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
           </section>
       </div>
    </div>
</body>
</html>
