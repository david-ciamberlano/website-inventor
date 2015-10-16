<ul class="nav nav-tabs voffset-big">
  <li class="active"><a aria-expanded="false" href="#search-tab" data-toggle="tab">
    <span class="glyphicon glyphicon-search"></span> Ricerca</a>
  </li>
</ul>

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