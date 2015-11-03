<c:forEach items="${page.contents}" var="content">
  <article>
    <c:choose>
      <%--TEXT--%>
      <c:when test="${content.type == 'TEXT'}">
        <h2>${content.name}</h2>
        <div>${content.properties['text']}</div>
      </c:when>

      <%--IMAGE--%>
      <c:when test="${content.type == 'IMAGE'}">
        <figure>
          <a href="${contextPath}/proxy/${content.id}" >
            <img src="${contextPath}/proxy/r/imgpreview/${content.id}" alt="${content.getName()}"/>
          </a>
          <figcaption>
            <p>${content.properties['title']} (${content.properties['pixelXDimension']} x ${content.properties['pixelYDimension']} - ${content.properties['content_size']}MB)</p>
          </figcaption>
        </figure>
      </c:when>

      <%--GENERIC CONTENT (DOWNLOADABLE)--%>
      <c:otherwise>
        <a href="${contextPath}/proxy/d/${content.id}" target="_blank">
          <img src="${contextPath}/proxy/r/doclib/${content.id}" alt=""/>
        </a>
        <div>
          <h4><a href="${contextPath}/proxy/d/${content.id}" target="_blank">${content.properties['title']}</a></h4>

          <c:forEach items="${documentProps}" var="docprop">
              <c:choose>
                <c:when test="${docprop.type == 'TEXT'}">
                    <div><strong>${docprop.label}</strong>: ${content.properties[docprop.id]}</div>
                </c:when>
                <c:when test="${docprop.type == 'DATE'}">
                    <jsp:useBean id="dateObject" class="java.util.Date" />
                    <jsp:setProperty name="dateObject" property="time" value="${content.properties[docprop.id]}" />
                    <div><strong>${docprop.label}</strong>: <fmt:formatDate type="date" dateStyle="full" value="${dateObject}" /></div>
                </c:when>
              </c:choose>
          </c:forEach>
      </div>
      </c:otherwise>
    </c:choose>
  </article>
</c:forEach>
