<ul>
    <li><a aria-expanded="false" href="#search-tab" data-toggle="tab">
        Ricerca</a>
    </li>
</ul>
<div id="myTabContent"
    <div id="search-tab">
        <fieldset>
            <legend>Ricerca avanzata</legend>
            <form:form modelAttribute="searchFilters" method="POST" action="${pageContext.request.contextPath}/${siteid}/search">
                <c:forEach varStatus="vs" var="filter" items="${searchFilters.filterItems}" >
                    <c:if test="${not empty filter.name}">
                        <div>
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
                <div>
                    <button type="reset >Cancella</button>
                    <button type="submit">Cerca</button>
                </div>
            </form:form>
        </fieldset>
    </div>
</div>