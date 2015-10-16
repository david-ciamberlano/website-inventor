<c:forEach items="${page.links}" var="link">
  <div class="col-md-3 mlink">
    <div class="thumbnail">
      <a href="${contextPath}/${siteid}/page/${link.getId()}">
        <img src="${contextPath}/resource/themes/default/icons/container.png" alt="Library">
        <div class="caption text-center">
            ${fn:replace(link.name,'_',' ')}
        </div>
      </a>
    </div>
  </div>
</c:forEach>

