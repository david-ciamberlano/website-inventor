<ul class="nav nav-tabs voffset-big">
    <li class="active"><a aria-expanded="false" href="#search-tab" data-toggle="tab">
        <span class="glyphicon glyphicon-search"></span> Ricerca</a>
    </li>
</ul>
<div id="myTabContent" class="tab-content">
    <div class="tab-pane fade active in voffset-small" id="search-tab">
        <fieldset>
            <legend>Ricerca avanzata</legend>
            <form:form modelAttribute="searchFilters" method="POST" action="${pageContext.request.contextPath}/${siteid}/search" class="form-horizontal">
                <c:forEach varStatus="vs" var="filter" items="${searchFilters.filterItems}" >
                    <c:if test="${not empty filter.name}">
                        <div class="form-group">
                            <label>${filter.name}</label>
                            <c:choose>
                                <c:when test="${fn:contains(filter.type, 'TEXT')}">
                                    <form:input type="text" class="form-control" path="filterItems[${vs.index}].content" ></form:input>
                                </c:when>
                                <c:when test="${fn:contains(filter.type, 'DATE')}">
                                    <form:input type="date" maxlength="10" size="10" class="form-control" path="filterItems[${vs.index}].content" placeholder="gg-mm-aaaa" ></form:input>
                                </c:when>
                                <c:when test="${fn:contains(filter.type, 'NUM')}">
                                    <form:input type="number" size="3" class="form-control" path="filterItems[${vs.index}].content" ></form:input>
                                </c:when>
                            </c:choose>

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