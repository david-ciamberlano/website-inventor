<c:forEach items="${page.links}" var="link">
<a href="${contextPath}/${siteid}/page/${link.getId()}">
  <img src="${contextPath}/resource/themes/s/icons/container.png" alt="Library">
  <div>${fn:replace(link.name,'_',' ')}</div>
</a>
</c:forEach>