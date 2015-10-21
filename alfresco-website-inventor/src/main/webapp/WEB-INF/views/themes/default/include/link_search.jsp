<div id="myTabContent">
    <div id="links">
      <c:if test="${!page.homepage}">
        <a href="${contextPath}/${siteid}/page/${page.parentId}">Up</a>
      </c:if>
      <c:forEach items="${page.links}" var="link">
        <a href="${contextPath}/${siteid}/page/${link.getId()}">${link.name}</a>
      </c:forEach>
    </div>
</div>

