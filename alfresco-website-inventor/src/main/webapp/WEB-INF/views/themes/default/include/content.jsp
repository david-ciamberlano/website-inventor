

<%-- CONTENT OBJECT-LIST --%>
<c:if test="${page.contents.size() > 0}">
  <c:forEach items="${page.contents}" var="content">
    <article class="row">
      <c:choose>

        <%--TEXT--%>
        <c:when test="${content.getType() == 'TEXT'}">
          <div class="col-md-12">
            <h2>${content.properties['name']}</h2>
            <div>${content.properties['text']}</div>
          </div>
        </c:when>

        <%--IMAGE--%>
        <c:when test="${content.getType() == 'IMAGE'}">
          <div class="col-md-10 col-md-offset-1">
            <figure class="">
              <a href="${contextPath}/proxy/${content.id}" >
                <img class="img-responsive center-block" src="${contextPath}/proxy/r/imgpreview/${content.id}" alt="${content.getName()}"/>
              </a>
              <figcaption class="text-center">
                <p>${content.getName()} (${content.properties['width']} x ${content.properties['height']})</p>
              </figcaption>
            </figure>
          </div>
        </c:when>

        <%--GENERIC CONTENT (DOWNLOADABLE)--%>
        <c:otherwise>
          <div class="col-sm-2 col-md-2 col-md-offset-1">
            <a href="${contextPath}/proxy/d/${content.id}" >
              <img class="img-thumbnail center-block" src="${contextPath}/proxy/r/doclib/${content.id}" alt=""/>
            </a>
          </div>

          <div class="col-sm-10 col-md-9">
            <h4 class="media-heading"><a href="${contextPath}/proxy/d/${content.id}" >${content.properties['testata']}</a></h4>
            <div><strong>Uscita</strong>: ${content.properties['uscita']}</div>
            <jsp:useBean id="dateObject" class="java.util.Date" />
            <jsp:setProperty name="dateObject" property="time" value="${content.properties['dataUscita']}" />
            <div><strong>Data</strong>: <fmt:formatDate type="date" dateStyle="full" value="${dateObject}" /></div>
            <div><strong>Biblioteca</strong>: ${content.properties['biblioteca']}</div>
          </div>
        </c:otherwise>
      </c:choose>
    </article>
  </c:forEach>
</c:if>