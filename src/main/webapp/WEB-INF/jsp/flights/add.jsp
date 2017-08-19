<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ft" tagdir="/WEB-INF/tags/flighttracker" %>

<ft:html>
    <ft:head />
    <ft:body>

        <h1 class="ui header"><fmt:message key="addFlight" /></h1>

        <spring:form modelAttribute="flightEditor" servletRelativeAction="/flights/add" cssClass="ui form">

            <jsp:include page="include/flight-data.jsp" />

            <h3 class="ui dividing header"><fmt:message key="actions" /></h3>
            <div class="sixteen wide">
                <button class="ui primary button"><fmt:message key="save" /></button>
            </div>

        </spring:form>

    </ft:body>
</ft:html>