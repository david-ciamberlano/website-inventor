<div class="panel panel-default">
    <div class="panel-heading">
        Advanced Search
    </div>
    <div class="panel-body">
        <form:form modelAttribute="searchFilters" method="POST" action="${pageContext.request.contextPath}/${site}/search" class="form-inline" role="form">
            <div class="form-group">
                <label for="biblioteca"> Biblioteca:</label>
                <form:input class="form-control"  path="filter1" id="biblioteca" ></form:input>
            </div>
            <div class="form-group">
                <label for="testata"> Testata:</label>
                <form:input class="form-control" path="filter2" id="testata" ></form:input>
            </div>
            <div class="form-group">
                <label for="dataUscitaDa"> Data Uscita da:</label>
                <form:input class="form-control" path="filter3" id="dataUscitaDa" placeholder="yyyy-mm-dd"></form:input>
            </div>
            <div class="form-group">
                <label for="dataUscitaA"> a:</label>
                <form:input class="form-control" path="filter4" id="dataUscitaA" placeholder="yyyy-mm-dd"></form:input>
            </div>
            <button class="btn btn-default" type="submit">Cerca</button>
            </fieldset>
        </form:form>
    </div>
</div>