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

<header>

</header>
<header class="page-header">
    <h1>Site name <small>${page.title}</small></h1>
</header>
<!--
<div class="row">
    <%--breadcrumbs--%>
    <ol class="breadcrumb">

        <li><a href="<spring:url value="/${site}" />">Home</a></li>
        <c:forEach items="${page.breadcrumbs}" var="bcEntry">
            <li><a href="<spring:url value="/${site}/o/${bcEntry.value}" />">${bcEntry.key}</a></li>
        </c:forEach>

        <li>${page.title}</li>
    </ol>
</div>
-->
<div class="row">
    <form:form modelAttribute="searchFilters" method="POST" action="${pageContext.request.contextPath}/${site}/s">
        <fieldset>
            <legend>Search</legend>
            <div class="col-md-4">
                <label for="dataUscita">Data uscita:</label>
                <form:input  path="filter1" id="dataUscita" ></form:input>
            </div>
            <div class="col-md-4">
                <label for="uscita">Uscita:</label>
                <form:input path="filter2" id="uscita" ></form:input>
            </div>
            <div>
                <input type="submit" value="Search"/>
            </div>
        </fieldset>
    </form:form>
</div>
<div class="row">
    <%--navigation--%>
     <aside class="col-md-3">
        <div class="panel panel-default">
            <div class="panel-heading">
                Links
            </div>
            <nav class="panel-body">
                <ul class="nav nav-pills nav-stacked">
					<li role="presentation">
						<a href="<spring:url value="/${site}/o/${page.parentId}" />"><span class="glyphicon glyphicon-circle-arrow-up"></span></a>
					</li>
					<c:forEach items="${page.links}" var="link">
						<li role="presentation"><a href="<spring:url value="/${site}/o/${link.getId()}" />">${link.getName()}</a></li>
					</c:forEach>
                </ul>
            </nav>
        </div>
    </aside>

    <%--body--%>
    <section class="col-sm-9">
        <div class="panel panel-default">
			<div class="panel-heading">
				<span><a href="<spring:url value="/${site}" />"><span class="glyphicon glyphicon-home"></span> Home</a></span>
				<c:forEach items="${page.breadcrumbs}" var="bcEntry">
					&nbsp;&raquo;&nbsp; <span><a href="<spring:url value="/${site}/o/${bcEntry.value}" />">${bcEntry.key}</a></span>
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
                                <a href="<spring:url value="/${site}/o/${link.getId()}" />">
                                    <img src="<spring:url value="/resource/icons/container.png" />" alt="Library">
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
</body>
</html>
