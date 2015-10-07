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

<!-- Begin Body -->
<div class="container">
    <div class="row">
        <div class="col-dm-12">
            <header class="page-header">
                <h1>${site}</h1>
            </header>
        </div>
    </div>
    <div class="row">

        <%--LINKS--%>
        <div class="col-md-3">
            <h2>Navigazione</h2>
            <ul class="nav nav-tabs">
                <li class="active"><a aria-expanded="true" href="#link-tab" data-toggle="tab">
                    <span class="glyphicon glyphicon-th-list"></span> Navigation</a>
                </li>
                <li><a aria-expanded="false" href="#search-tab" data-toggle="tab">
                    <span class="glyphicon glyphicon-search"></span> Search</a>
                </li>
            </ul>
            <div id="myTabContent" class="tab-content">
                <div class="tab-pane fade active in" id="link-tab">
                    <fieldset>
                        <legend>Giornali</legend>
                    </fieldset>
                    <div class="input-group">
                        <span class="input-group-addon">Filtro</span>
                        <input class="form-control" type="text" onkeyup="filter(this,'linkList')" />
                    </div>
                    <br/>
                    <div id="links" class="list-group table-of-contents">
                        <c:if test="${!page.homepage}">
                            <a class="list-group-item" href="${contextPath}/${site}/page/${page.parentId}"><span class="glyphicon glyphicon-circle-arrow-up"></span></a>
                        </c:if>
                        <c:forEach items="${page.links}" var="link">
                            <a class="list-group-item" href="${contextPath}/${site}/page/${link.getId()}">${fn:replace(link.name,'_',' ')}</a>
                        </c:forEach>
                    </div>
                </div>
                <div class="tab-pane fade" id="search-tab">
                    <form:form modelAttribute="searchFilters" method="POST" action="${pageContext.request.contextPath}/${site}/search" class="form-horizontal">
                        <fieldset>
                            <legend>Search Form</legend>
                        </fieldset>
                        <div class="form-group">
                            <form:input class="form-control" path="filter1" id="biblioteca" placeholder="Biblioteca" ></form:input>
                            <form:input class="form-control" path="filter2" id="testata" placeholder="Testata" ></form:input>
                            <form:input class="form-control" path="filter3" id="uscita" placeholder="Uscita" size="3"></form:input>
                            <form:input class="form-control" path="filter4" id="dataUscitaDa" placeholder="Data da (yyyy-mm-dd)" size="10"></form:input>
                            <form:input class="form-control" path="filter5" id="dataUscitaA" placeholder="Data a (yyyy-mm-dd)" size="10"></form:input>
                        </div>
                        <button type="reset" class="btn btn-default">Cancella</button>
                        <button type="submit" class="btn btn-primary">Cerca</button>
                    </form:form>
                </div>

            </div>
        </div>

        <%--CONTENT--%>
        <div class="col-md-9">

            <%--BREADCRUMBS--%>
            <div class="row">
                <div class="col-md-12">
                    <ul class="breadcrumb">
                        <li><a href="${contextPath}/${site}"><span class="glyphicon glyphicon-home"></span> Home</a></li>
                        <c:if test="${!page.homepage}">
                            <c:forEach items="${page.breadcrumbs}" var="bcEntry">
                                <li><a href="${contextPath}/${site}/page/${bcEntry.value}">${bcEntry.key}</a></li>
                            </c:forEach>
                            <li class="active">${page.title}</li>
                        </c:if>
                    </ul>
                </div>
            </div>

            <%-- SPECIAL TEXT HEADER --%>
            <c:if test="${page.specialContent.containsKey('text_header')}">
                <div class="row">
                    <div class="col-md-12">
                        <header class="jumbotron">
                                ${page.specialContent.get('text_header').properties['text']}
                        </header>
                    </div>
                </div>
            </c:if>


            <%--CONTENTS--%>

            <%-- CONTENT FOLDER-LIST --%>
            <c:if test="${page.contents.size() == 0}">
                <div class="row">
                    <c:forEach items="${page.links}" var="link">
                        <div class="col-md-3">
                            <div class="thumbnail">
                                <a href="${contextPath}/${site}/page/${link.getId()}">
                                    <img src="${contextPath}/resource/themes/simple/icons/container.png" alt="Library">
                                    <div class="caption text-center">
                                            ${fn:replace(link.name,'_',' ')}
                                    </div>
                                </a>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:if>

            <%-- CONTENT OBJECT-LIST --%>
            <c:if test="${page.contents.size() > 0}">
                <c:forEach items="${page.contents}" var="content">
                    <article class="row">
                        <c:choose>

                            <%--TEXT--%>
                            <c:when test="${content.getType() == 'TEXT'}">
                                <div class="col-md-12">
                                    <h2>${content.properties['name']}</h2>
                                    <div>${content.properties['text']}</div>
                                </div>
                            </c:when>

                            <%--IMAGE--%>
                            <c:when test="${content.getType() == 'IMAGE'}">
                                <div class="col-md-10 col-md-offset-1">
                                    <figure class="">
                                        <a href="${contextPath}/proxy/${content.id}" >
                                            <img class="img-responsive center-block" src="${contextPath}/proxy/r/imgpreview/${content.id}" alt="${content.getName()}"/>
                                        </a>
                                        <figcaption class="text-center">
                                            <p>${content.getName()} (${content.properties['width']} x ${content.properties['height']})</p>
                                        </figcaption>
                                    </figure>
                                </div>
                            </c:when>

                            <%--GENERIC CONTENT (DOWNLOADABLE)--%>
                            <c:otherwise>
                                <div class="col-sm-2 col-md-2 col-md-offset-1">
                                    <a href="${contextPath}/proxy/d/${content.id}" >
                                        <img class="img-thumbnail center-block" src="${contextPath}/proxy/r/doclib/${content.id}" alt="${content.getName()}"/>
                                    </a>
                                </div>

                                <div class="col-sm-10 col-md-9">
                                    <h4 class="media-heading"><a href="${contextPath}/proxy/d/${content.id}" >${content.properties['testata']}</a></h4>
                                    <div><strong>Uscita</strong>: ${content.properties['uscita']}</div>
                                    <jsp:useBean id="dateObject" class="java.util.Date" />
                                    <jsp:setProperty name="dateObject" property="time" value="${content.properties['dataUscita']}" />
                                    <div><strong>Data</strong>: <fmt:formatDate type="date" dateStyle="full" value="${dateObject}" /></div>
                                    <div><strong>Biblioteca</strong>: ${content.properties['biblioteca']}</div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </article>
                </c:forEach>
            </c:if>

            <%-- SPECIAL TEXT FOOTER --%>
            <c:if test="${page.specialContent.containsKey('text_footer')}">
                <div class="row">
                    <header class="col-dm-12 jumbotron">
                            ${page.specialContent.get('text_footer').properties['text']}
                    </header>
                </div>
            </c:if>

        </div>
    </div>
</div>

<!-- script references -->
<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js"></script>
<script src="${contextPath}/resource/themes/simple/js/bootstrap.min.js"></script>
<script src="${contextPath}/resource/themes/simple/js/scripts.js"></script>
</body>
</html>
