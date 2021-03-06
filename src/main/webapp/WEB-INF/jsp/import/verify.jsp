<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fl" tagdir="/WEB-INF/tags/flightlog" %>

<fl:html>
    <fl:head />
    <fl:body>

        <div class="ui header">
            <i class="cube icon"></i>
            <div class="content">
                <fmt:message key="import" />
                <div class="sub header"><fmt:message key="verifyImportedFlights" /></div>
            </div>
        </div>

        <div class="ui steps">
            <div class="step">
                <i class="file outline icon"></i>
                <div class="content">
                    <div class="title"><fmt:message key="selectSource" /></div>
                </div>
            </div>
            <div class="active step">
                <i class="search icon"></i>
                <div class="content">
                    <div class="title"><fmt:message key="verify" /></div>
                    <div class="description"><fmt:message key="verifyImportedFlights" /></div>
                </div>
            </div>
            <div class="disabled step">
                <i class="checkmark icon"></i>
                <div class="content">
                    <div class="title"><fmt:message key="completed" /></div>
                </div>
            </div>
        </div>

        <spring:form modelAttribute="importEditor" servletRelativeAction="/import/execute" cssClass="ui form">

            <div class="ui horizontal divider"><fmt:message key="actions" /></div>
            <div class="sixteen wide">
                <button class="ui primary button">
                    <i class="cube icon"></i>
                    <fmt:message key="importFlights" />
                </button>
            </div>

            <div class="ui horizontal divider"><fmt:message key="flights" /></div>
            <table class="ui celled striped compact table">
                <thead>
                    <tr>
                        <th><fmt:message key="use" /></th>
                        <th><fmt:message key="flight" /></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${importEditor.items}" var="item" varStatus="itemStatus">
                        <tr>
                            <td>
                                <div class="ui fitted checkbox">
                                    <spring:checkbox path="items[${itemStatus.index}].active"/> <label></label>
                                </div>
                            </td>
                            <td>
                                <div class="fields">
                                    <fl:inputfield bean="importEditor" path="items[${itemStatus.index}].flight.departureAirportCode" labelKey="airportCode" cssClass="two wide field" />
                                    <fl:inputfield bean="importEditor" path="items[${itemStatus.index}].flight.departureDateLocal" labelKey="departureDate" cssClass="two wide field" />
                                    <fl:inputfield bean="importEditor" path="items[${itemStatus.index}].flight.departureTimeLocal" labelKey="departureTime" cssClass="two wide field" />
                                    <fl:inputfield bean="importEditor" path="items[${itemStatus.index}].flight.arrivalAirportCode" labelKey="airportCode" cssClass="two wide field" />
                                    <fl:inputfield bean="importEditor" path="items[${itemStatus.index}].flight.arrivalDateLocal" labelKey="arrivalDate" cssClass="two wide field" />
                                    <fl:inputfield bean="importEditor" path="items[${itemStatus.index}].flight.arrivalTimeLocal" labelKey="arrivalTime" cssClass="two wide field" />
                                    <fl:inputfield bean="importEditor" path="items[${itemStatus.index}].flight.flightDuration" labelKey="durationInIso8601" cssClass="two wide field" />
                                    <fl:inputfield bean="importEditor" path="items[${itemStatus.index}].flight.flightDistance" labelKey="distanceInKm" cssClass="two wide field" />
                                </div>
                                <div class="fields">
                                    <fl:inputfield bean="importEditor" path="items[${itemStatus.index}].flight.airlineName" labelKey="airlineName" cssClass="four wide field" />
                                    <fl:inputfield bean="importEditor" path="items[${itemStatus.index}].flight.airlineCode" labelKey="airlineCode" cssClass="two wide field" />
                                    <fl:inputfield bean="importEditor" path="items[${itemStatus.index}].flight.flightNumber" labelKey="flightNumber" cssClass="two wide field" />
                                    <fl:inputfield bean="importEditor" path="items[${itemStatus.index}].flight.aircraftType" labelKey="aircraftType" cssClass="four wide field" />
                                    <fl:inputfield bean="importEditor" path="items[${itemStatus.index}].flight.aircraftRegistration" labelKey="aircraftRegistration" cssClass="two wide field" />
                                    <fl:inputfield bean="importEditor" path="items[${itemStatus.index}].flight.aircraftName" labelKey="aircraftName" cssClass="two wide field" />
                                </div>
                                <div class="fields">
                                    <fl:inputfield cssClass="two wide field" bean="importEditor" path="items[${itemStatus.index}].flight.seatNumber" labelKey="seat" />
                                    <fl:select cssClass="two wide field" bean="importEditor" path="items[${itemStatus.index}].flight.seatType" labelKey="type">
                                        <option></option>
                                        <c:forEach items="${flightEditorHelper.seatTypeValues}" var="seatTypeValue">
                                            <option ${item.flight.seatType eq seatTypeValue ? 'selected="selected"' : ''} value="${seatTypeValue}"><fmt:message key="seatType.${seatTypeValue}" /></option>
                                        </c:forEach>
                                    </fl:select>
                                    <fl:select cssClass="two wide field" bean="flightEditor" path="items[${itemStatus.index}].flight.cabinClass" labelKey="cabinClass">
                                        <option></option>
                                        <c:forEach items="${flightEditorHelper.cabinClassValues}" var="cabinClassValue">
                                            <option ${item.flight.cabinClass eq cabinClassValue ? 'selected="selected"' : ''} value="${cabinClassValue}"><fmt:message key="cabinClass.${cabinClassValue}" /></option>
                                        </c:forEach>
                                    </fl:select>
                                    <fl:select cssClass="two wide field" bean="flightEditor" path="items[${itemStatus.index}].flight.flightReason" labelKey="flightReason">
                                        <option></option>
                                        <c:forEach items="${flightEditorHelper.flightReasonValues}" var="flightReasonValue">
                                            <option ${item.flight.flightReason eq flightReasonValue ? 'selected="selected"' : ''} value="${flightReasonValue}"><fmt:message key="flightReason.${flightReasonValue}" /></option>
                                        </c:forEach>
                                    </fl:select>
                                    <fl:inputfield cssClass="eight wide field" bean="importEditor" path="items[${itemStatus.index}].flight.comment" labelKey="comment" />
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <div class="ui horizontal divider"><fmt:message key="actions" /></div>
            <div class="sixteen wide">
                <button class="ui primary button">
                    <i class="cube icon"></i>
                    <fmt:message key="importFlights" />
                </button>
            </div>

        </spring:form>

    </fl:body>
</fl:html>


