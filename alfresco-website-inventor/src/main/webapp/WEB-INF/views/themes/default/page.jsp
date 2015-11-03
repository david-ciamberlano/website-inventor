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
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <!--[if lt IE 9]>
    <script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
    <link href="${contextPath}/resource/themes/s/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resource/themes/s/css/custom.css" rel="stylesheet">
</head>
<body>
    <div>
        <header>
            <%@include file="include/header.jsp"%>
        </header>

        <%--NAV--%><%@include file="include/nav.jsp"%>

        <%--BREADCRUMBS--%><%@include file="include/breadcrumbs.jsp"%>

        <%--CONTENT--%>
        <div>
            <%-- SPECIAL TEXT HEADER --%>
            <c:if test="${page.specialContents.containsKey('text_header')}">
            <%@include file="include/special_text_header.jsp"%>
            </c:if>

            <%--CONTENTS--%>
            <%-- CONTENT FOLDER-LIST --%>
            <c:if test="${page.contents.size() == 0}"><%@include file="include/main_hav.jsp"%></c:if>
            <%-- CONTENT OBJECT-LIST --%>
            <c:if test="${page.contents.size() > 0}"><%@include file="include/content.jsp"%></c:if>

            <%-- SPECIAL TEXT FOOTER --%>
            <c:if test="${page.specialContents.containsKey('text_footer')}">
            <%@include file="include/special_text_footer.jsp"%>
            </c:if>
        </div>
    </div>

    <script src="${contextPath}/resource/themes/default/js/jquery.min.js"></script>
    <script src="${contextPath}/resource/themes/default/js/bootstrap.min.js"></script>
    <script src="${contextPath}/resource/themes/default/js/scripts.js"></script>
</body>
</html>