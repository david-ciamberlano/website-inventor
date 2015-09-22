<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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

<header class="page-header">
    <h1>Archivio <small>home</small></h1>
</header>
<!--
<div class="row">
    <%--breadcrumbs--%>
    <ol class="breadcrumb">
		<li class="disabled">
			<a>Home</a>
		</li>
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
    <!--
    <aside class="col-sm-3">
		<nav class="list-group">

			<a class="list-group-item" href="<spring:url value="/${site}/page/${page.parentId}" />"><span class="glyphicon glyphicon-circle-arrow-up"></span></a>
			<c:forEach items="${page.links}" var="link">
				<a class="list-group-item" href="<spring:url value="/${site}/page/${link.getId()}" />">${link.getName()}</a>
			</c:forEach>
        </nav>
    </aside>
	-->
    <aside class="col-md-3">
        <div class="panel panel-default">
            <div class="panel-heading">
                Links
            </div>
            <div class="well well-sm">
                Filtro: <input type="text" onkeyup="filter(this,'linkList')" />
            </div>
            <nav class="panel-body">
                <ul class="nav nav-pills nav-stacked" id="linkList">
                    <c:forEach items="${page.links}" var="link">
                        <li role="presentation"><a href="<spring:url value="/${site}/page/${link.getId()}" />">${fn:replace(link.name,'_',' ')}</a></li>
                    </c:forEach>
                </ul>
            </nav>
        </div>
    </aside>

    <%--body--%>
    <section class="col-md-9">
        <div class="panel panel-default">
            <div class="panel-heading">
                Archivio
            </div>
            <div class="panel-body">
                <c:if test="${page.specialContent.containsKey('text_header')}">
                    <header class="jumbotron">
                            ${page.specialContent.get('text_header').properties['text']}
                    </header>
                </c:if>

                <%--contents--%>
                <%-- only links page --%>
                <c:if test="${page.contents.size() == 0}">
                    <c:forEach items="${page.links}" var="link">
                        <div class="col-md-2">
                            <div class="thumbnail">
                                <a href="<spring:url value="/${site}/page/${link.getId()}" />">
                                    <img src="<spring:url value="/resource/themes/sena/icons/container.png" />" alt="Library">
                                    <div class="caption text-center">
                                        <span>${fn:replace(link.name,'_',' ')}</span>
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
                                            <img class="img-thumbnail media-object" src="<spring:url value="/proxy/${content.getThumbnailId()}" />" alt="${content.getName()}"/>
                                        </div>
                                        <div class="media-body media-middle">
                                            <p>${content.name}</p>
                                            <p>${content.mimeType}</p>
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

<script type="text/javascript">


    function filter (element) {
        var value = $(element).val().toLowerCase();

        var links = $('#linkList > li');
        if (value === '') {
            links.show();
        }
        else {
            var linksToHide  = links.filter( function(){
                return $(this).text().toLowerCase().indexOf(value) == -1;
            });
            var linksToShow = links.filter( function(){
                return $(this).text().toLowerCase().indexOf(value) > -1;
            });

            linksToHide.hide();
            linksToShow.show();
        }
    }
</script>
</body>
</html>
