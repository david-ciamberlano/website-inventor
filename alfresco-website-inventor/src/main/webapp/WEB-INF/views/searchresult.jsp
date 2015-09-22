<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <link rel="stylesheet" href="<spring:url value="/resource/bootstrap-3.3.5-dist/css/bootstrap.css" />">
	<link rel="stylesheet" href="<spring:url value="/resource/custom.css" />">
    <script src="<spring:url value="/resource/jquery-1.11.3.min.js"/>"></script>
    <script src="<spring:url value="/resource/bootstrap-3.3.5-dist/js/bootstrap.min.js"/>"></script>
</head>

<body class="container">

<header class="page-header">
    <h1>Archivio <small>home</small></h1>
</header>

<div class="row">
   	 <aside class="col-md-3">
        <div class="panel panel-default">
            <div class="panel-heading">
                Links
            </div>
        </div>
    </aside>

    <%--body--%>
    <section class="col-md-9">
        <div class="panel panel-default">
            <div class="panel-heading">
                <a href="<spring:url value="/${site}" />"><span class="glyphicon glyphicon-home"></span> Home</a>
            </div>
            <div class="panel-body">
                <%--contents--%>
                <c:forEach items="${page.contents}" var="content">
                    <c:choose>
                        <%--TEXT--%>
                        <c:when test="${content.getType() == 'TEXT'}">
                            <article>
                                <p>${content.properties['text']}</p>
                            </article>
                        </c:when>
                        <%--IMAGE--%>
                        <c:when test="${content.getType() == 'IMAGE'}">
                            <figure class="media" class="center">
                                <a href="<spring:url value="/proxy/${content.id}" />" >
                                    <span class="media-left">
                                        <img class="img-thumbnail media-object " src="<spring:url value="/proxy/${content.getThumbnailId()}" />" alt="${content.getName()}"/>
                                    </span>
                                    <figcaption class="media-body media-middle">
                                        <p>${content.getName()}</p>
                                        <p>${content.properties['width']} x ${content.properties['height']}</p>
                                    </figcaption>
                                </a>
                            </figure>
                        </c:when>
                        <%--GENERIC--%>
                        <c:otherwise>
                            <!-- default: generic file, contentType="GENERIC" -->
                            <article class="media">
                                <a href="<spring:url value="/proxy/${content.id}" />" >
                                    <div class="media-left media-middle">
                                        <c:choose>
                                            <c:when test="${content.thumbnailId == 'default-generic'}">
                                                <img class="img-thumbnail media-object" src="<spring:url value="/resource/icons/default-generic-icon.png" />" alt="${content.getName()}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <img class="img-thumbnail media-object" src="<spring:url value="/proxy/${content.getThumbnailId()}" />" alt="${content.getName()}"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="media-body media-middle">
                                        <p>${content.name}</p>
                                    </div>
                                </a>
                            </article>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </div>
        </div>
    </section>
</div>

</body>
</html>
