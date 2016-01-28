<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta charset="utf-8">
    <title>${sitename}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <!--[if lt IE 9]>
    <script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
    <link href="${contextPath}/resource/themes/default/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resource/themes/default/css/custom.css" rel="stylesheet">
</head>
<body>
    <!--NAVIGATION-->
    <nav class="navbar navbar-default">
        <div class="container-fluid">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="#">${sitename}</a>
            </div>
            <div class="collapse navbar-collapse" id="main-nav">
                <ul class="nav navbar-nav">
                    <#if !page.homepage>
                        <li><a href="${contextPath}/${siteid}/page/${page.parentId}">Up</a></li>
                    </#if>
                    <#list page.links as link>
                        <li><a href="${contextPath}/${siteid}/page/${link.getId()}">${link.name}</a></li>
                    </#list>
                </ul>
            </div>
        </div>
    </nav>


    <div class="container">

        <header>
            <div>
                <img src="${contextPath}/resource/themes/default/img/blog.png" alt="Logo del Blog"/>
            </div>
            <div>
                <h1>${sitename}<br/><small>${sitedescription}</small></h1>
            </div>
        </header>

        <!--BREADCRUMBS-->
        <ol class="breadcrumb">
            <#if page.homepage>
                <li class="active">Home</li>
            <#else>
                <li><a href="${contextPath}/${siteid}">Home</a></li>
                <#list page.breadcrumbs?keys as bcEntry>
                    <li><a href="${contextPath}/${siteid}/page/${page.breadcrumbs[bcEntry]}">${bcEntry}</a></li>
                </#list>
                <li class="active">${page.title}</li>
            </#if>
        </ol>
    </div>

    <script src="${contextPath}/resource/themes/default/js/jquery.min.js"></script>
    <script src="${contextPath}/resource/themes/default/js/bootstrap.min.js"></script>
    <script src="${contextPath}/resource/themes/default/js/scripts.js"></script>
</body>

 </html>