<ul class="breadcrumb">
  <c:if test="${page.homepage}">
    <li class="active"><span class="glyphicon glyphicon-home"></span> Home</li>
  </c:if>
  <c:if test="${!page.homepage}">
    <li><a href="${contextPath}/${siteid}"><span class="glyphicon glyphicon-home"></span> Home</a></li>
    <c:forEach items="${page.breadcrumbs}" var="bcEntry">
      <li><a href="${contextPath}/${siteid}/page/${bcEntry.value}">${fn:replace(bcEntry.key,'_',' ')}</a></li>
    </c:forEach>
    <li class="active">${fn:replace(page.title,'_',' ')}</li>
  </c:if>
</ul>