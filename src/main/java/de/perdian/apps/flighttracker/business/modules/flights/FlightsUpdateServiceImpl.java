package de.perdian.apps.flighttracker.business.modules.flights;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.perdian.apps.flighttracker.business.modules.flights.model.AircraftBean;
import de.perdian.apps.flighttracker.business.modules.flights.model.AirlineBean;
import de.perdian.apps.flighttracker.business.modules.flights.model.AirportBean;
import de.perdian.apps.flighttracker.business.modules.flights.model.AirportContactBean;
import de.perdian.apps.flighttracker.business.modules.flights.model.FlightBean;
import de.perdian.apps.flighttracker.persistence.entities.AirlineEntity;
import de.perdian.apps.flighttracker.persistence.entities.AirportEntity;
import de.perdian.apps.flighttracker.persistence.entities.FlightEntity;
import de.perdian.apps.flighttracker.persistence.repositories.AirlinesRepository;
import de.perdian.apps.flighttracker.persistence.repositories.AirportsRepository;
import de.perdian.apps.flighttracker.persistence.repositories.FlightsRepository;
import de.perdian.apps.flighttracker.support.FlighttrackerHelper;

@Service
class FlightsUpdateServiceImpl implements FlightsUpdateService {

    private FlightsRepository flightsRepository = null;
    private FlightsQueryService flightsQueryService = null;
    private AirlinesRepository airlinesRepository = null;
    private AirportsRepository airportsRepository = null;

    @Override
    @Transactional
    public FlightBean saveFlight(FlightBean flightBean) {

        FlightEntity flightEntity = flightBean.getEntityId() == null ? new FlightEntity() : this.getFlightsRepository().findOne(flightBean.getEntityId());

        AircraftBean aircraftBean = flightBean.getAircraft();
        if (aircraftBean != null) {
            flightEntity.setAircraftName(aircraftBean.getName());
            flightEntity.setAircraftRegistration(aircraftBean.getRegistration());
            flightEntity.setAircraftType(aircraftBean.getType());
        }

        AirlineBean airlineBean = flightBean.getAirline();
        if (airlineBean != null) {
            flightEntity.setAirlineCode(airlineBean.getCode());
            flightEntity.setAirlineName(airlineBean.getName());
        }
        AirlineEntity airlineEntity = airlineBean == null || StringUtils.isEmpty(airlineBean.getCode()) ? null : this.getAirlinesRepository().loadAirlineByIataCode(airlineBean.getCode());
        if (airlineEntity != null) {
            if (StringUtils.isEmpty(flightEntity.getAirlineName())) {
                flightEntity.setAirlineName(airlineEntity.getName());
            }
        }

        AirportContactBean arrivalContactBean = flightBean.getArrivalContact();
        if (arrivalContactBean != null) {
            flightEntity.setArrivalDateLocal(arrivalContactBean.getDateLocal());
            flightEntity.setArrivalTimeLocal(arrivalContactBean.getTimeLocal());
        }
        AirportBean arrivalAirportBean = arrivalContactBean == null ? null : arrivalContactBean.getAirport();
        if (arrivalAirportBean != null) {
            flightEntity.setArrivalAirportCode(arrivalAirportBean.getCode());
        }
        AirportEntity arrivalAirportEntity = arrivalAirportBean == null ? null : this.getAirportsRepository().loadAirportByIataCode(arrivalAirportBean.getCode());

        AirportContactBean departureContactBean = flightBean.getDepartureContact();
        if (departureContactBean != null) {
            flightEntity.setDepartureDateLocal(departureContactBean.getDateLocal());
            flightEntity.setDepartureTimeLocal(departureContactBean.getTimeLocal());
        }
        AirportBean departureAirportBean = departureContactBean == null ? null : departureContactBean.getAirport();
        if (departureAirportBean != null) {
            flightEntity.setDepartureAirportCode(departureAirportBean.getCode());
        }
        AirportEntity departureAirportEntity = departureAirportBean == null ? null : this.getAirportsRepository().loadAirportByIataCode(departureAirportBean.getCode());

        Integer flightDuration = flightBean.getFlightDuration() == null ? null : (int)flightBean.getFlightDuration().toMinutes();
        flightEntity.setFlightDistance(flightBean.getFlightDistance() != null && flightBean.getFlightDistance().intValue() > 0 ? flightBean.getFlightDistance() : this.computeFlightDistance(departureAirportEntity, arrivalAirportEntity));
        flightEntity.setFlightDuration(flightDuration != null ? flightDuration : this.computeFlightDuration(departureContactBean, departureAirportEntity, arrivalContactBean, arrivalAirportEntity));
        flightEntity.setFlightNumber(flightBean.getFlightNumber());
        flightEntity.setFlightReason(flightBean.getFlightReason());
        flightEntity.setCabinClass(flightBean.getCabinClass());
        flightEntity.setSeatNumber(flightBean.getSeatNumber());
        flightEntity.setSeatType(flightBean.getSeatType());
        flightEntity.setUser(flightBean.getUser());

        this.getFlightsRepository().save(flightEntity);

        return this.getFlightsQueryService().loadFlightById(flightEntity.getId());

    }

    private Integer computeFlightDistance(AirportEntity departureAirportEntity, AirportEntity arrivalAirportEntity) {
        return FlighttrackerHelper.computeDistanceInKilometers(
            departureAirportEntity == null ? null : departureAirportEntity.getLongitude(),
            departureAirportEntity == null ? null : departureAirportEntity.getLatitude(),
            arrivalAirportEntity == null ? null : arrivalAirportEntity.getLongitude(),
            arrivalAirportEntity == null ? null : arrivalAirportEntity.getLatitude()
        );
    }

    private Integer computeFlightDuration(AirportContactBean departureContact, AirportEntity departureAirportEntity, AirportContactBean arrivalContact, AirportEntity arrivalAirportEntity) {
        return Optional.ofNullable(FlighttrackerHelper.computeDuration(
            departureAirportEntity == null ? null : departureAirportEntity.getTimezoneId(),
            departureContact == null ? null : departureContact.getDateLocal(),
            departureContact == null ? null : departureContact.getTimeLocal(),
            arrivalAirportEntity == null ? null : arrivalAirportEntity.getTimezoneId(),
            arrivalContact == null ? null : arrivalContact.getDateLocal(),
            arrivalContact == null ? null : arrivalContact.getTimeLocal()
        )).map(duration -> (int)duration.toMinutes()).orElse(null);
    }

    @Override
    @Transactional
    public void deleteFlight(FlightBean flightBean) {
        this.getFlightsRepository().delete(flightBean.getEntityId());
    }

    FlightsRepository getFlightsRepository() {
        return this.flightsRepository;
    }
    @Autowired
    void setFlightsRepository(FlightsRepository flightsRepository) {
        this.flightsRepository = flightsRepository;
    }

    FlightsQueryService getFlightsQueryService() {
        return this.flightsQueryService;
    }
    @Autowired
    void setFlightsQueryService(FlightsQueryService flightsQueryService) {
        this.flightsQueryService = flightsQueryService;
    }

    AirlinesRepository getAirlinesRepository() {
        return this.airlinesRepository;
    }
    @Autowired
    void setAirlinesRepository(AirlinesRepository airlinesRepository) {
        this.airlinesRepository = airlinesRepository;
    }

    AirportsRepository getAirportsRepository() {
        return this.airportsRepository;
    }
    @Autowired
    void setAirportsRepository(AirportsRepository airportsRepository) {
        this.airportsRepository = airportsRepository;
    }

}