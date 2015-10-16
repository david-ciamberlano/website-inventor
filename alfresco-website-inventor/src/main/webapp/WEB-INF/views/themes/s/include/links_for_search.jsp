<ul class="nav nav-tabs voffset-big">
    <li><a aria-expanded="false" href="#search-tab" data-toggle="tab">
        <span class="glyphicon glyphicon-search"></span> Ricerca</a>
    </li>
</ul>
<div id="myTabContent" class="tab-content">
    <div class="tab-pane fade in voffset-small" id="search-tab">

        <fieldset>
            <legend>Ricerca avanzata</legend>
            <ul class="nav nav-tabs voffset-big">
                <li class="active"><a aria-expanded="false" href="#search-tab" data-toggle="tab">
                    <span class="glyphicon glyphicon-search"></span> Ricerca</a>
                </li>
            </ul>
            <form:form modelAttribute="searchFilters" method="POST" action="${pageContext.request.contextPath}/${siteid}/search" class="form-horizontal">

                <c:forEach varStatus="vs" var="filter" items="${searchFilters.filterItems}" >
                    <c:if test="${not empty filter.name}">
                        <div class="form-group">
                            <label>${filter.name}</label>
                            <form:input class="form-control" path="filterItems[${vs.index}].content" ></form:input>
                        </div>
                    </c:if>
                </c:forEach>
                <div class="text-right">
                    <button type="reset" class="btn btn-default"><span class="glyphicon glyphicon-remove"></span> Cancella</button>
                    <button type="submit" class="btn btn-primary"><span class="glyphicon glyphicon-search"></span> Cerca</button>
                </div>
            </form:form>
        </fieldset>
    </div>
</div>