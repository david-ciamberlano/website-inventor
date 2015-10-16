<%-- CONTENT FOLDER-LIST --%>
<c:if test="${page.contents.size() == 0}">
  <div class="row" id="main-links">
    <c:forEach items="${page.links}" var="link">
      <div class="col-md-3 mlink">
        <div class="thumbnail">
          <a href="${contextPath}/${site}/page/${link.getId()}">
            <img src="${contextPath}/resource/themes/simple/icons/container.png" alt="Library">
            <div class="caption text-center">
                ${fn:replace(link.name,'_',' ')}
            </div>
          </a>
        </div>
      </div>
    </c:forEach>
  </div>
</c:if>
