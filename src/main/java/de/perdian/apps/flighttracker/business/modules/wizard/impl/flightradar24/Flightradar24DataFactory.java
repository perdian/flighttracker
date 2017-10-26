package de.perdian.apps.flighttracker.business.modules.wizard.impl.flightradar24;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.perdian.apps.flighttracker.business.modules.wizard.WizardData;
import de.perdian.apps.flighttracker.business.modules.wizard.WizardDataFactory;
import de.perdian.apps.flighttracker.persistence.entities.AircraftTypeEntity;
import de.perdian.apps.flighttracker.persistence.entities.AirportEntity;
import de.perdian.apps.flighttracker.persistence.repositories.AircraftTypesRepository;
import de.perdian.apps.flighttracker.persistence.repositories.AirportsRepository;
import de.perdian.apps.flighttracker.support.FlighttrackerHelper;
import us.codecraft.xsoup.Xsoup;

@Component
public class Flightradar24DataFactory implements WizardDataFactory {

    private static final Logger log = LoggerFactory.getLogger(Flightradar24DataFactory.class);

    private AircraftTypesRepository aircraftTypesRepository = null;
    private AirportsRepository airportsRepository = null;
    private Flightradar24DataConfiguration configuration = null;
    private Clock clock = Clock.systemUTC();

    @Override
    public WizardData createData(String airlineCode, String flightNumber, LocalDate departureDate) {
        if (this.getConfiguration().isEnabled()) {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

                String httpGetUrl = "https://www.flightradar24.com/data/flights/" + airlineCode + flightNumber;
                log.info("Querying flightradar24 for flight {}{} on {} using URL: {}", airlineCode, flightNumber, departureDate, httpGetUrl);

                try (CloseableHttpResponse httpResponse = httpClient.execute(new HttpGet(httpGetUrl))) {
                     String httpResponseContent = EntityUtils.toString(httpResponse.getEntity());
                     Document htmlDocument = Jsoup.parse(httpResponseContent);
                     Elements dataTableElements = Xsoup.select(htmlDocument, "//table[@id='tbl-datatable']").getElements();
                     if (dataTableElements.size() > 0) {
                         return this.createDataFromDataTable(dataTableElements.get(0), airlineCode, flightNumber, departureDate);
                     }
                }

            } catch (Exception e) {
                log.debug("Cannot retreive data from flightradar24 for flight {}{} on {}", airlineCode, flightNumber, departureDate, e);
            }
        }
        return null;
    }

    private WizardData createDataFromDataTable(Element dataTableElement, String airlineCode, String flightNumber, LocalDate departureDate) {
        LocalDate currentDate = LocalDate.now(this.getClock());
        Elements trElements = dataTableElement.getElementsByTag("tr");
        Element trElementForSpecificDate = currentDate.isBefore(departureDate) ? null : this.lookupTableRowForDate(trElements, departureDate);
        if (trElementForSpecificDate != null) {
            return this.createDataFromTableRow(trElementForSpecificDate, airlineCode, flightNumber, departureDate);
        } else {
            Element trElementForCurrentDate = this.lookupTableRowForDate(trElements, LocalDate.now().minusDays(1));
            if (trElementForCurrentDate != null) {
                WizardData currentDateFlightData = this.createDataFromTableRow(trElementForCurrentDate, airlineCode, flightNumber, departureDate);
                if (currentDateFlightData != null) {

                    // As the departure and arrival times as well as the aircraft registry is not actually the
                    // right one for the departure date passed as parameter to the method but instead the values
                    // from yesterday, we'll only use the values which we assume are the same (which currently
                    // are the airport codes plus the originally queried departure date)
                    WizardData resultFlightData = new WizardData();
                    resultFlightData.setArrivalAirportCode(currentDateFlightData.getArrivalAirportCode());
                    resultFlightData.setDepartureAirportCode(currentDateFlightData.getDepartureAirportCode());
                    resultFlightData.setDepartureDateLocal(departureDate);
                    return resultFlightData;

                }
            }
        }
        return null;
    }

    private Element lookupTableRowForDate(Elements trElements, LocalDate targetDate) {
        String targetDateString = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(targetDate);
        for (int i=0; i < trElements.size(); i++) {
            Element trElement = trElements.get(i);
            Elements tdElements = trElement.getElementsByTag("td");
            Element dateElement = tdElements.size() <= 1 ? null : tdElements.get(1);
            String dateValue = dateElement == null ? null : dateElement.text();
            if (dateValue != null && dateValue.equalsIgnoreCase(targetDateString)) {
                return trElement;
            }
        }
        return null;
    }

    private WizardData createDataFromTableRow(Element trElement, String airlineCode, String flightNumber, LocalDate departureDate) {

        WizardData wizardData = new WizardData();
        wizardData.setDepartureDateLocal(departureDate);

        Elements tdElements = trElement.getElementsByTag("td");
        Element aircraftElement = tdElements.size() <= 4 ? null : tdElements.get(4);
        Elements aircraftTypeElement = aircraftElement == null ? null : aircraftElement.getElementsByTag("span");
        if (aircraftTypeElement != null && !aircraftTypeElement.isEmpty()) {
            AircraftTypeEntity aircraftTypeEntity = this.getAircraftTypesRepository().loadAircraftTypeByCode(aircraftTypeElement.text());
            if (aircraftTypeEntity != null && !StringUtils.isEmpty(aircraftTypeEntity.getTitle())) {
                wizardData.setAircraftType(aircraftTypeEntity.getTitle());
            } else {
                wizardData.setAircraftType(aircraftTypeElement.text());
            }
        }
        Elements aircraftRegistryElement = aircraftElement == null ? null : aircraftElement.getElementsByTag("a");
        if (aircraftRegistryElement != null && !aircraftRegistryElement.isEmpty()) {
            wizardData.setAircraftRegistration(aircraftRegistryElement.text().trim());
        }

        Element departureAirportElement = tdElements.size() <= 2 ? null : tdElements.get(2);
        Elements departureAirportCodeElements = departureAirportElement == null ? null : departureAirportElement.getElementsByTag("a");
        Element departureAirportCodeElement = departureAirportCodeElements == null || departureAirportCodeElements.isEmpty() ? null : departureAirportCodeElements.get(0);
        String departureAirportCode = departureAirportCodeElement == null ? null : departureAirportCodeElement.text().trim();
        wizardData.setDepartureAirportCode(departureAirportCode);

        Element arrivalAirportElement = tdElements.size() <= 3 ? null : tdElements.get(3);
        Elements arrivalAirportCodeElements = arrivalAirportElement == null ? null : arrivalAirportElement.getElementsByTag("a");
        Element arrivalAirportCodeElement = arrivalAirportCodeElements == null || arrivalAirportCodeElements.isEmpty() ? null : arrivalAirportCodeElements.get(0);
        String arrivalAirportCode = arrivalAirportCodeElement == null ? null : arrivalAirportCodeElement.text().trim();
        wizardData.setArrivalAirportCode(arrivalAirportCode);

        AirportEntity departureAirportEntity = this.getAirportsRepository().loadAirportByIataCode(departureAirportCode);
        AirportEntity arrivalAirportEntity = this.getAirportsRepository().loadAirportByIataCode(arrivalAirportCode);
        if (departureAirportEntity != null && arrivalAirportEntity != null) {

            Element actualDepartureTimeElement = tdElements.size() <= 7 ? null : tdElements.get(7);
            String actualDepartureTimeValue = actualDepartureTimeElement == null ? null : actualDepartureTimeElement.text().trim();
            if (!StringUtils.isEmpty(actualDepartureTimeValue) && !"-".equalsIgnoreCase(actualDepartureTimeValue)) {
                if (departureAirportEntity.getTimezoneId() != null && arrivalAirportEntity.getTimezoneId() != null) {

                    ZonedDateTime actualDeparturetimeUtc = LocalTime.parse(actualDepartureTimeValue).atDate(departureDate).atZone(ZoneId.of("UTC"));
                    LocalTime actualDepartureTimeLocal = actualDeparturetimeUtc.withZoneSameInstant(departureAirportEntity.getTimezoneId()).toLocalTime();
                    wizardData.setDepartureTimeLocal(actualDepartureTimeLocal);

                    Element flightDurationElement = tdElements.size() <= 5 ? null : tdElements.get(5);
                    String flightDurationValue = flightDurationElement == null ? null : flightDurationElement.text().trim();
                    Duration flightDuration = FlighttrackerHelper.parseDuration(flightDurationValue);

                    ZonedDateTime actualDepartureTimeZoned = actualDepartureTimeLocal.atDate(departureDate).atZone(departureAirportEntity.getTimezoneId());
                    ZonedDateTime actualArrivalTimeZonedAtDeparture = actualDepartureTimeZoned.plus(flightDuration);
                    ZonedDateTime actualArrivalTimeZonedAtArrival = actualArrivalTimeZonedAtDeparture.withZoneSameInstant(arrivalAirportEntity.getTimezoneId());
                    wizardData.setArrivalDateLocal(actualArrivalTimeZonedAtArrival.toLocalDate());
                    wizardData.setArrivalTimeLocal(actualArrivalTimeZonedAtArrival.toLocalTime());

                }
            }

        }

        return wizardData;

    }

    AircraftTypesRepository getAircraftTypesRepository() {
        return this.aircraftTypesRepository;
    }
    @Autowired
    void setAircraftTypesRepository(AircraftTypesRepository aircraftTypesRepository) {
        this.aircraftTypesRepository = aircraftTypesRepository;
    }

    AirportsRepository getAirportsRepository() {
        return this.airportsRepository;
    }
    @Autowired
    void setAirportsRepository(AirportsRepository airportsRepository) {
        this.airportsRepository = airportsRepository;
    }

    Flightradar24DataConfiguration getConfiguration() {
        return this.configuration;
    }
    @Autowired
    void setConfiguration(Flightradar24DataConfiguration configuration) {
        this.configuration = configuration;
    }

    Clock getClock() {
        return this.clock;
    }
    void setClock(Clock clock) {
        this.clock = clock;
    }

}
