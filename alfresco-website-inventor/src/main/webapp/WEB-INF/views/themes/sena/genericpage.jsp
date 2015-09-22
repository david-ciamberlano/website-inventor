<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <link rel="stylesheet" href="<spring:url value="/resource/themes/sena/bootstrap-3.3.5-dist/css/bootstrap.css" />">
    <link rel="stylesheet" href="<spring:url value="/resource/themes/sena/custom.css" />">
    <script src="<spring:url value="/resource/themes/sena/jquery-1.11.3.min.js"/>"></script>
    <script src="<spring:url value="/resource/themes/sena/bootstrap-3.3.5-dist/js/bootstrap.min.js"/>"></script>
</head>
<body class="container">

<header>

</header>
<header class="page-header">
    <h1>${site} - <small>${page.title}</small></h1>
</header>
<!--
<div class="row">
    <%--breadcrumbs--%>
    <ol class="breadcrumb">

        <li><a href="<spring:url value="/${site}" />">Home</a></li>
        <c:forEach items="${page.breadcrumbs}" var="bcEntry">
            <li><a href="<spring:url value="/${site}/page/${bcEntry.value}" />">${bcEntry.key}</a></li>
        </c:forEach>

        <li>${page.title}</li>
    </ol>
</div>
-->
<%--SEARCH--%>
<div class="panel panel-default">
    <div class="panel-heading">
        Advanced Search
    </div>
    <div class="panel-body">
        <form:form modelAttribute="searchFilters" method="POST" action="${pageContext.request.contextPath}/${site}/search" class="form-inline" role="form">
            <div class="form-group">
                <label for="biblioteca"> Biblioteca:</label>
                <form:input class="form-control"  path="filter1" id="biblioteca" ></form:input>
            </div>
            <div class="form-group">
                <label for="testata"> Testata:</label>
                <form:input class="form-control" path="filter2" id="testata" ></form:input>
            </div>
            <div class="form-group">
                <label for="dataUscitaDa"> Data Uscita da:</label>
                <form:input class="form-control" path="filter3" id="dataUscitaDa" placeholder="yyyy-mm-dd"></form:input>
            </div>
            <div class="form-group">
                <label for="dataUscitaA"> a:</label>
                <form:input class="form-control" path="filter4" id="dataUscitaA" placeholder="yyyy-mm-dd"></form:input>
            </div>
            <button class="btn btn-default" type="submit">Cerca</button>
            </fieldset>
        </form:form>
    </div>
</div>

<div class="row">
    <%--navigation--%>
    <aside class="col-md-3">
        <div class="panel panel-default">
            <div class="panel-heading">
                Links
            </div>
            <div class="panel-body">
                <ul class="nav nav-pills nav-stacked">
                    <li role="presentation">
                        <a href="<spring:url value="/${site}" />"><span class="glyphicon glyphicon-home"></span> Home</a>
                    </li>
                    <li role="presentation">
                        <a href="<spring:url value="/${site}/page/${page.parentId}" />"><span class="glyphicon glyphicon-circle-arrow-up"></span></a>
                    </li>
                    <c:forEach items="${page.links}" var="link">
                        <li role="presentation"><a href="<spring:url value="/${site}/page/${link.getId()}" />">${link.getName()}</a></li>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </aside>

    <%--body--%>
    <section class="col-sm-9">
        <div class="panel panel-default">
            <div class="panel-heading">
                <span><a href="<spring:url value="/${site}" />"><span class="glyphicon glyphicon-home"></span> Home</a></span>
                <c:forEach items="${page.breadcrumbs}" var="bcEntry">
                    &nbsp;&raquo;&nbsp; <span><a href="<spring:url value="/${site}/page/${bcEntry.value}" />">${bcEntry.key}</a></span>
                </c:forEach>
                &nbsp;&raquo;&nbsp; <span>${page.title}</span>
            </div>
            <div class="panel-body">
                <c:if test="${page.specialContent.containsKey('text_header')}">
                    <header class="jumbotron">
                            ${page.specialContent.get('text_header').properties['text']}
                    </header>
                </c:if>

                <%--contents--%>
                <c:if test="${page.contents.size() == 0}">
                    <c:forEach items="${page.links}" var="link">
                        <div class="col-md-2">
                            <div class="thumbnail">
                                <a href="<spring:url value="/${site}/page/${link.getId()}" />">
                                    <img src="<spring:url value="/resource/themes/sena/icons/container.png" />" alt="Library">
                                    <div class="caption text-center">
                                            ${link.getName()}
                                    </div>
                                </a>
                            </div>
                        </div>
                    </c:forEach>
                </c:if>
                <c:if test="${page.contents.size() > 0}">
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
                                                    <img class="media-object" src="<spring:url value="/resource/themes/sena/icons/default-generic-icon.png" />" alt="${content.getName()}"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <img class="media-object" src="<spring:url value="/proxy/${content.getThumbnailId()}" />" alt="${content.getName()}"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="media-body media-middle">
                                            <h4>${content.properties['testata']}</h4>
                                            <div><strong>Uscita</strong>: ${content.properties['uscita']}</div>
                                            <fmt:parseDate value="${content.properties['dataUscita']}" var="theDate" pattern="yyyy-MM-dd HH:mm:ss" />
                                            <div><strong>Data</strong>: <fmt:formatDate value="${theDate}" pattern="dd/MM/yyyy"/></div>
                                            <div><strong>Biblioteca</strong>: ${content.properties['biblioteca']}</div>
                                        </div>
                                    </a>
                                </article>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </c:if>
            </div>
        </div>
    </section>
</div>
</body>
</html>
