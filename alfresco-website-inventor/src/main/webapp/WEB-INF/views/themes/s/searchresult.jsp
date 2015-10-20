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
    <title>${siteName}</title>
    <meta name="generator" content="Bootply" />
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <!--[if lt IE 9]>
        <script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
    <link href="${contextPath}/resource/themes/s/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resource/themes/s/css/custom.css" rel="stylesheet">
</head>
<body>

<!-- Begin Body -->
<div class="container">
    <header class="row page-header">
        <%@include file="include/header.jsp"%>
    </header>

    <div class="row">

        <%--LINKS--%>
        <div class="col-md-3">
            <%@include file="include/links_for_search.jsp"%>
        </div>

        <%--CONTENT--%>
        <div class="col-md-9">

            <%--BREADCRUMBS--%>
            <div class="row">
                <div class="col-md-12">
                    <ul class="breadcrumb">
                        <li><a href="${contextPath}/${siteid}"><span class="glyphicon glyphicon-home"></span> Home</a></li>
                    </ul>
                </div>
            </div>

            <%--CONTENTS--%>

            <%-- CONTENT OBJECT-LIST --%>
            <p class="text-center">Risultati trovati: ${page.contents.size()}</p>
            <c:if test="${page.contents.size() == 0}">
                <p class="text-center text-warning">Nessun risultato ottenuto. Controllare i parametri di ricerca.</p>
            </c:if>
            <c:if test="${page.contents.size() > 0}">
                <c:if test="${page.contents.size() > 99}">
                    <p class="text-center text-warning">Attenzione: la ricerca &egrave; troppo generica. Sono stati prelevati solo i primi 100 risultati</p>
                </c:if>
                <%@include file="include/content.jsp"%>
            </c:if>

        </div>
    </div>
</div>

<!-- script references -->
<script src="${contextPath}/resource/themes/default/js/jquery.min.js"></script>
<script src="${contextPath}/resource/themes/default/js/bootstrap.min.js"></script>
<script src="${contextPath}/resource/themes/default/js/scripts.js"></script>
</body>
</html>