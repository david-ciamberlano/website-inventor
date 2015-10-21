<c:forEach items="${page.links}" var="link">
  <div>
    <div>
      <a href="${contextPath}/${siteid}/page/${link.getId()}">
        <img src="${contextPath}/resource/themes/s/icons/container.png" alt="Library">
        <div>
            ${fn:replace(link.name,'_',' ')}
        </div>
      </a>
    </div>
  </div>
</c:forEach>

