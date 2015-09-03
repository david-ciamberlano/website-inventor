<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <link rel="stylesheet" href="<spring:url value="/resource/bootstrap-3.3.5-dist/css/bootstrap.css" />">
    <script src="<spring:url value="/resource/jquery-1.11.3.min.js"/>"></script>
    <script src="<spring:url value="/resource/bootstrap-3.3.5-dist/js/bootstrap.min.js"/>"></script>
</head>
<body class="container">

<header>

</header>
<nav class="navbar navbar-default">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="#">AWSI</a>
        </div>
        <div>
            <ul class="nav navbar-nav">
                <c:choose>
                    <c:when test="${isHomePage}">
                        <li class="disabled">
                            <a>Home</a>
                        </li>
                    </c:when>
                    <c:otherwise>
                    <li><a href="<spring:url value="/" />">Home</a><li>
                    </c:otherwise>
                </c:choose>

                <c:forEach items="${categories}" var="category">
                    <c:choose>
                        <c:when test="${category.id.equals(id)}">
                            <li role="presentation" class="active"><a href="<spring:url value="/p/${category.getId()}" />">${category.getName()}</a></li>
                        </c:when>
                        <c:otherwise>
                            <li role="presentation"><a href="<spring:url value="/p/${category.getId()}" />">${category.getName()}</a></li>
                        </c:otherwise>
                    </c:choose>

                </c:forEach>
            </ul>
        </div>
    </div>
</nav>

<div class="row">
    <aside class="col-sm-3">
        <div class="panel panel-default">
            <div class="panel-heading">
                Links
            </div>
            <nav class="panel-body">
                <ul  class="nav nav-pills nav-stacked">
                    <li role="presentation"><a href="<spring:url value="/p/${parentId}" />"><span class="glyphicon glyphicon-circle-arrow-up"></span></a></li>
                    <c:forEach items="${links}" var="link">
                        <li role="presentation"><a href="<spring:url value="/p/${link.getId()}" />">${link.getName()}</a></li>
                    </c:forEach>
                </ul>
            </nav>
    </aside>

    <section class="col-sm-9">
        <div class="panel panel-default">
            <div class="panel-body">
                <c:if test="${specialContent.containsKey('text_header')}">
                    <header class="jumbotron">
                            ${specialContent.get('text_header').text}
                    </header>
                </c:if>

                <h2>Contenuti</h2>
                <c:forEach items="${contents}" var="content">
                    <c:choose>
                        <c:when test="${content.getType() == 'IMAGE'}">
                            <figure class="media" class="center">
                                <a class="media-left" href="<spring:url value="/proxy/${content.id}" />" >
                                    <img class="img-thumbnail media-object" src="<spring:url value="/proxy/${content.getThumbnailId()}" />" alt="${content.getName()}"/>
                                </a>
                                <figcaption class="media-body media-middle">${content.getName()}</figcaption>
                            </figure>
                        </c:when>
                        <c:when test="${content.getType() == 'TEXT'}">
                            <article>
                                <p>${content.getText()}</p>
                            </article>
                        </c:when>
                        <c:otherwise>
                            <!-- default: generic file, contentType="GENERIC" -->
                            <article class="media">
                                <a href="<spring:url value="/proxy/${content.id}" />" >
                                    <div class="media-left media-middle">
                                        <img class="img-thumbnail media-object" src="<spring:url value="/proxy/${content.getThumbnailId()}" />" alt="${content.getName()}"/>
                                    </div>
                                    <p class="media-body media-middle">${content.mimeType}</p>
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
