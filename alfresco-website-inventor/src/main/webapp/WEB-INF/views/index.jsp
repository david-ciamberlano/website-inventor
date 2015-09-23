<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <c:set var="contextPath" value="${pageContext.request.contextPath}"/>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta charset="utf-8">
    <title>${site}</title>
    <meta name="generator" content="Bootply" />
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link href="${contextPath}/resource/themes/simple/css/bootstrap.min.css" rel="stylesheet">
    <!--[if lt IE 9]>
    <script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
    <link href="${contextPath}/resource/themes/simple/css/styles.css" rel="stylesheet">
    <link href="${contextPath}/resource/themes/simple/css/custom.css" rel="stylesheet">
</head>
<body>

<header class="navbar navbar-default navbar-static-top" role="banner">
    <div class="container">
        <div class="navbar-header">
            <button class="navbar-toggle" type="button" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a href="/" class="navbar-brand">${site}</a>
        </div>
        <nav class="collapse navbar-collapse" role="navigation">
            <form:form modelAttribute="searchFilters" method="POST" action="${pageContext.request.contextPath}/${site}/search" class="navbar-form navbar-right" role="form">
                <div class="form-group">
                    <form:input class="form-control"  path="filter1" id="biblioteca" placeholder="Biblioteca" ></form:input>
                    <form:input class="form-control" path="filter2" id="testata" placeholder="Testata" ></form:input>
                    <form:input class="form-control" path="filter3" id="dataUscitaDa" placeholder="Data da (yyyy-mm-dd)"></form:input>
                    <form:input class="form-control" path="filter4" id="dataUscitaA" placeholder="Data a (yyyy-mm-dd)"></form:input>
                </div>
                <button class="btn btn-default" type="submit">Cerca</button>
            </form:form>
        </nav>
    </div>
</header>

<!-- Begin Body -->
<div class="container">

    <%--BREADCRUMBS--%>

    <div class="row">
        <div class="col-md-3" id="leftCol">

            <div class="well">
                <h3>Links</h3>
                <div class="well">
                    Filtro: <input type="text" onkeyup="filter(this,'linkList')" />
                </div>
                <ul class="nav nav-stacked" id="sidebar">
                    <li><a href="<spring:url value="/${site}/page/${page.parentId}" />"><span class="glyphicon glyphicon-circle-arrow-up"></span></a></li>
                    <c:forEach items="${page.links}" var="link">
                        <li><a href="${contextPath}/${site}/page/${link.getId()}">${fn:replace(link.name,'_',' ')}</a></li>
                    </c:forEach>
                </ul>
            </div>

        </div>
        <div class="col-md-9">
            <div class="row">
                <ul class="breadcrumb lead">
                    <li><a href="<spring:url value="/${site}" />"><span class="glyphicon glyphicon-home"></span> Home</a></li>
                    <c:forEach items="${page.breadcrumbs}" var="bcEntry">
                        <li><a href="<spring:url value="/${site}/page/${bcEntry.value}" />">${bcEntry.key}</a></li>
                    </c:forEach>
                    <li>${page.title}</li>
                </ul>
            </div>
            <h1>Contenuto</h1>
            <div class="row">
                <c:if test="${page.specialContent.containsKey('text_header')}">
                    <header class="jumbotron">
                            ${page.specialContent.get('text_header').properties['text']}
                    </header>
                </c:if>

                <%--contents--%>
                <c:if test="${page.contents.size() == 0}">
                    <c:forEach items="${page.links}" var="link">
                        <div class="col-md-3">
                            <div class="thumbnail">
                                <a href="<spring:url value="/${site}/page/${link.getId()}" />">
                                    <img src="<spring:url value="/resource/themes/sena/icons/container.png" />" alt="Library">
                                    <div class="caption text-center">
                                            ${fn:replace(link.name,'_',' ')}
                                    </div>
                                </a>
                            </div>
                        </div>
                    </c:forEach>
                </c:if>
                <c:if test="${page.contents.size() > 0}">
                    <c:forEach items="${page.contents}" var="content">
                        <article class="row">
                            <c:choose>
                                <%--TEXT--%>
                                <c:when test="${content.getType() == 'TEXT'}">
                                    <p>${content.properties['text']}</p>
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
                                    <div class="col-sm-6 col-md-2">
                                        <a href="<spring:url value="/proxy/${content.id}" />" >
                                            <c:choose>
                                                <c:when test="${content.thumbnailId == 'default-generic'}">
                                                    <img src="<spring:url value="/resource/themes/sena/icons/default-generic-icon.png" />" alt="${content.getName()}"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <img src="<spring:url value="/proxy/${content.getThumbnailId()}" />" alt="${content.getName()}"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </a>
                                    </div>
                                    <div class="col-sm-6 col-md-10">
                                        <h3 class="media-heading"><a href="<spring:url value="/proxy/${content.id}" />" >${content.properties['testata']}</a></h3>
                                        <div><strong>Uscita</strong>: ${content.properties['uscita']}</div>
                                        <fmt:parseDate value="${content.properties['dataUscita']}" var="theDate" pattern="yyyy-MM-dd HH:mm:ss" />
                                        <div><strong>Data</strong>: <fmt:formatDate value="${theDate}" pattern="dd/MM/yyyy"/></div>
                                        <div><strong>Biblioteca</strong>: ${content.properties['biblioteca']}</div>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </article>
                    </c:forEach>
                </c:if>
            </div>
        </div>
    </div>
</div>

<!-- script references -->
<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js"></script>
<script src="${contextPath}/resource/themes/simple/js/bootstrap.min.js"></script>
<script src="${contextPath}/resource/themes/simple/js/scripts.js"></script>
<script type="text/javascript">
    function filter (element) {
        var value = $(element).val().toLowerCase();

        var links = $('#sidebar > li');
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