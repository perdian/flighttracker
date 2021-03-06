package de.perdian.flightlog.modules.overview.web;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.perdian.flightlog.support.types.CabinClass;
import de.perdian.flightlog.support.types.FlightDistance;
import de.perdian.flightlog.support.types.FlightReason;
import de.perdian.flightlog.support.types.FlightType;

public class OverviewQueryHelper implements Serializable {

    static final long serialVersionUID = 1L;

    private Set<Integer> availableYears = Collections.emptySet();
    private List<OverviewQueryHelperItem> availableAirlines = null;
    private List<OverviewQueryHelperItem> availableAirports = null;
    private List<OverviewQueryHelperItem> availableAircraftTypes = null;
    private List<CabinClass> availableCabinClasses = null;
    private List<FlightReason> availableFlightReasons = null;
    private List<FlightType> availableFlightTypes = null;
    private List<FlightDistance> availableFlightDistances = null;

    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        toStringBuilder.append("availableYears", this.getAvailableYears());
        return toStringBuilder.toString();
    }

    public Set<Integer> getAvailableYears() {
        return this.availableYears;
    }
    public void setAvailableYears(Set<Integer> availableYears) {
        this.availableYears = availableYears;
    }

    public List<OverviewQueryHelperItem> getAvailableAirlines() {
        return this.availableAirlines;
    }
    public void setAvailableAirlines(List<OverviewQueryHelperItem> availableAirlines) {
        this.availableAirlines = availableAirlines;
    }

    public List<OverviewQueryHelperItem> getAvailableAirports() {
        return this.availableAirports;
    }
    public void setAvailableAirports(List<OverviewQueryHelperItem> availableAirports) {
        this.availableAirports = availableAirports;
    }

    public List<OverviewQueryHelperItem> getAvailableAircraftTypes() {
        return this.availableAircraftTypes;
    }
    public void setAvailableAircraftTypes(List<OverviewQueryHelperItem> availableAircraftTypes) {
        this.availableAircraftTypes = availableAircraftTypes;
    }

    public List<CabinClass> getAvailableCabinClasses() {
        return this.availableCabinClasses;
    }
    public void setAvailableCabinClasses(List<CabinClass> availableCabinClasses) {
        this.availableCabinClasses = availableCabinClasses;
    }

    public List<FlightReason> getAvailableFlightReasons() {
        return this.availableFlightReasons;
    }
    public void setAvailableFlightReasons(List<FlightReason> availableFlightReasons) {
        this.availableFlightReasons = availableFlightReasons;
    }

    public List<FlightType> getAvailableFlightTypes() {
        return this.availableFlightTypes;
    }
    public void setAvailableFlightTypes(List<FlightType> availableFlightTypes) {
        this.availableFlightTypes = availableFlightTypes;
    }

    public List<FlightDistance> getAvailableFlightDistances() {
        return this.availableFlightDistances;
    }
    public void setAvailableFlightDistances(List<FlightDistance> availableFlightDistances) {
        this.availableFlightDistances = availableFlightDistances;
    }

}
