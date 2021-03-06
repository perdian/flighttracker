package de.perdian.flightlog.modules.flights.web;

import java.io.Serializable;

public class FlightWizardEditor implements Serializable {

    static final long serialVersionUID = 1L;

    private String wizDepartureDateLocal = null;
    private String wizDepartureAirportCode = null;
    private String wizAirlineCode = null;
    private String wizFlightNumber = null;

    public String getWizDepartureDateLocal() {
        return this.wizDepartureDateLocal;
    }
    public void setWizDepartureDateLocal(String wizDepartureDateLocal) {
        this.wizDepartureDateLocal = wizDepartureDateLocal;
    }

    public String getWizDepartureAirportCode() {
        return this.wizDepartureAirportCode;
    }
    public void setWizDepartureAirportCode(String wizDepartureAirportCode) {
        this.wizDepartureAirportCode = wizDepartureAirportCode;
    }

    public String getWizAirlineCode() {
        return this.wizAirlineCode;
    }
    public void setWizAirlineCode(String wizAirlineCode) {
        this.wizAirlineCode = wizAirlineCode;
    }

    public String getWizFlightNumber() {
        return this.wizFlightNumber;
    }
    public void setWizFlightNumber(String wizFlightNumber) {
        this.wizFlightNumber = wizFlightNumber;
    }

}
