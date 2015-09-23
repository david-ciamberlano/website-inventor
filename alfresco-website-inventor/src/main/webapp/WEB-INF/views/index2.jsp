<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Simple Sidebar - Start Bootstrap Template</title>

    <c:set var="contextPath" value="${pageContext.request.contextPath}"/>

    <!-- Bootstrap Core CSS -->
    <link href="${contextPath}/resource/themes/simple/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="${contextPath}/resource/themes/simple/css/simple-sidebar.css" rel="stylesheet">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->


</head>

<body>

    <div id="wrapper">

        <!-- Sidebar -->
        <div id="sidebar-wrapper">
            <ul class="sidebar-nav">
                <li class="sidebar-brand">
                    <a href="#">
                        ${site}
                    </a>
                </li>
                <c:forEach items="${page.links}" var="link">
                    <li><a href="${contextPath}/${site}/page/${link.getId()}">${fn:replace(link.name,'_',' ')}</a></li>
                </c:forEach>
            </ul>
        </div>
        <!-- /#sidebar-wrapper -->

        <!-- Page Content -->
        <div id="page-content-wrapper">
            <div class="container-fluid">
                <%--BREADCRUMBS--%>
                <div class="row">
                    <span><a href="<spring:url value="/${site}" />"><span class="glyphicon glyphicon-home"></span> Home</a></span>
                    <c:forEach items="${page.breadcrumbs}" var="bcEntry">
                        &nbsp;&raquo;&nbsp; <span><a href="<spring:url value="/${site}/page/${bcEntry.value}" />">${bcEntry.key}</a></span>
                    </c:forEach>
                    &nbsp;&raquo;&nbsp; <span>${page.title}</span>
                </div>

                <%--CONTENTS--%>
                <div class="row">
                    <div class="col-lg-12">
                        <h1>Simple Sidebar</h1>
                        <p>This template has a responsive menu toggling system. The menu will appear collapsed on smaller screens, and will appear non-collapsed on larger screens. When toggled using the button below, the menu will appear/disappear. On small screens, the page content will be pushed off canvas.</p>
                        <p>Make sure to keep all page content within the <code>#page-content-wrapper</code>.</p>
                        <a href="#menu-toggle" class="btn btn-default" id="menu-toggle">Toggle Menu</a>

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
            </div>
        </div>
        <!-- /#page-content-wrapper -->

    </div>
    <!-- /#wrapper -->

    <!-- jQuery -->
    <script src="${contextPath}/resource/themes/simple/js/jquery.js"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="${contextPath}/resource/themes/simple/js/bootstrap.min.js"></script>

    <!-- Menu Toggle Script -->
    <script>
    $("#menu-toggle").click(function(e) {
        e.preventDefault();
        $("#wrapper").toggleClass("toggled");
    });
    </script>

</body>

</html>
