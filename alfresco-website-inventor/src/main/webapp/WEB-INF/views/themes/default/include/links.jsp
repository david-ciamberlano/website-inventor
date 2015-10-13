<ul class="nav nav-tabs voffset-big">
  <li class="active"><a aria-expanded="true" href="#link-tab" data-toggle="tab">
    <span class="glyphicon glyphicon-th-list"></span> Navigazione</a>
  </li>
  <li><a aria-expanded="false" href="#search-tab" data-toggle="tab">
    <span class="glyphicon glyphicon-search"></span> Ricerca</a>
  </li>
</ul>
<div id="myTabContent" class="tab-content">
  <div class="tab-pane fade active in voffset-small" id="link-tab">
    <fieldset>
      <legend>Giornali storici</legend>

      <div class="input-group">
        <span class="input-group-addon">Filtro</span>
        <input class="form-control" type="text" onkeyup="filter(this,'linkList')" id="main-filter"/>
        <span class="input-group-addon" onclick="clearFilter('#main-filter')" ><span class="glyphicon glyphicon-remove" style="cursor: pointer"></span></span>
      </div>
      <br/>
      <div id="links" class="list-group table-of-contents">
        <c:if test="${!page.homepage}">
          <a class="list-group-item" href="${contextPath}/${siteid}/page/${page.parentId}"><span class="glyphicon glyphicon-circle-arrow-up"></span></a>
        </c:if>
        <c:forEach items="${page.links}" var="link">
          <a class="list-group-item" href="${contextPath}/${siteid}/page/${link.getId()}">${fn:replace(link.name,'_',' ')}</a>
        </c:forEach>
      </div>
    </fieldset>
  </div>
  <div class="tab-pane fade in voffset-small" id="search-tab">
    <fieldset>
      <legend>Ricerca avanzata</legend>

      <form:form modelAttribute="searchFilters" method="POST" action="${pageContext.request.contextPath}/${siteid}/search" class="form-horizontal">

        <c:if test="${not empty searchFilters.searchFilterItem1.name}">
          <div class="form-group">
            <label for="filter1">${searchFilters.searchFilterItem1.name}</label>
            <form:input class="form-control" path="searchFilterItem1.content" id="filter1"></form:input>
          </div>
        </c:if>
        <c:if test="${not empty searchFilters.searchFilterItem2.name}">
          <div class="form-group">
            <label for="filter2">${searchFilters.searchFilterItem2.name}</label>
            <form:input class="form-control" path="searchFilterItem2.content" id="filter2"></form:input>
          </div>
        </c:if>
        <div class="text-right">
          <button type="reset" class="btn btn-default"><span class="glyphicon glyphicon-remove"></span> Cancella</button>
          <button type="submit" class="btn btn-primary"><span class="glyphicon glyphicon-search"></span> Cerca</button>
        </div>
      </form:form>
    </fieldset>
  </div>

</div>
