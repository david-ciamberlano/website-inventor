<ul>
  <c:if test="${page.homepage}">
    <li>Home</li>
  </c:if>
  <c:if test="${!page.homepage}">
    <li><a href="${contextPath}/${siteid}">Home</a></li>
    <c:forEach items="${page.breadcrumbs}" var="bcEntry">
      <li><a href="${contextPath}/${siteid}/page/${bcEntry.value}">${bcEntry.key}</a></li>
    </c:forEach>
    <li>${page.title}</li>
  </c:if>
</ul>