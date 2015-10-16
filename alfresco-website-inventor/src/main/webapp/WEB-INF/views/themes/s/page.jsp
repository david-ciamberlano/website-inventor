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
    <title>${sitename}</title>
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
            <%@include file="include/links.jsp"%>
        </div>

        <%--CONTENT--%>
        <div class="col-md-9">

            <%--BREADCRUMBS--%>
            <div class="row">
                <div class="col-md-12">
                    <%@include file="include/breadcrumbs.jsp"%>
                </div>
            </div>

            <%-- SPECIAL TEXT HEADER --%>
            <c:if test="${page.specialContent.containsKey('text_header')}">
                <div class="row">
                    <div class="col-md-12">
                        <%@include file="include/special_text_header.jsp"%>
                    </div>
                </div>
            </c:if>

            <%--CONTENTS--%>
            <%-- CONTENT FOLDER-LIST --%>
            <c:if test="${page.contents.size() == 0}">
                <div class="row" id="main-links">
                    <%@include file="include/content_folders.jsp"%>
                </div>
            </c:if>

            <%-- CONTENT OBJECT-LIST --%>
            <c:if test="${page.contents.size() > 0}">
                <%@include file="include/content.jsp"%>
            </c:if>

            <%-- SPECIAL TEXT FOOTER --%>
            <c:if test="${page.specialContent.containsKey('text_footer')}">
                <div class="row">
                    <div class="col-md-12">
                        <%@include file="include/special_text_footer.jsp"%>
                    </div>
                </div>
            </c:if>

        </div>
    </div>
</div>

<!-- script references -->
<script src="${contextPath}/resource/themes/s/js/jquery.min.js"></script>
<script src="${contextPath}/resource/themes/s/js/bootstrap.min.js"></script>
<script src="${contextPath}/resource/themes/s/js/scripts.js"></script>
</body>
</html>