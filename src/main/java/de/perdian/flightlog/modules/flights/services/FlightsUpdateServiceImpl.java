package de.perdian.flightlog.modules.flights.services;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.perdian.flightlog.modules.airlines.persistence.AirlineEntity;
import de.perdian.flightlog.modules.airlines.persistence.AirlinesRepository;
import de.perdian.flightlog.modules.airports.persistence.AirportEntity;
import de.perdian.flightlog.modules.airports.persistence.AirportsRepository;
import de.perdian.flightlog.modules.flights.model.AircraftBean;
import de.perdian.flightlog.modules.flights.model.AirportBean;
import de.perdian.flightlog.modules.flights.model.AirportContactBean;
import de.perdian.flightlog.modules.flights.model.FlightBean;
import de.perdian.flightlog.modules.flights.persistence.FlightEntity;
import de.perdian.flightlog.modules.flights.persistence.FlightsRepository;
import de.perdian.flightlog.modules.users.persistence.UserEntity;
import de.perdian.flightlog.support.FlightlogHelper;

@Service
class FlightsUpdateServiceImpl implements FlightsUpdateService {

    private Supplier<FlightEntity> newFlightEntitySupplier = FlightEntity::new;
    private FlightsRepository flightsRepository = null;
    private FlightsQueryService flightsQueryService = null;
    private AirlinesRepository airlinesRepository = null;
    private AirportsRepository airportsRepository = null;

    @Override
    @Transactional
    public FlightBean saveFlight(FlightBean flightBean, UserEntity user) {

        FlightsQuery flightsQuery = new FlightsQuery();
        flightsQuery.setRestrictIdentifiers(Collections.singleton(flightBean.getEntityId()));
        flightsQuery.setRestrictUser(user);
        FlightEntity existingEntity = flightBean.getEntityId() == null ? null : this.getFlightsRepository().findOne((root, query, cb) -> flightsQuery.toPredicate(root, query, cb)).orElse(null);
        FlightEntity flightEntity = existingEntity == null ? this.getNewFlightEntitySupplier().get() : existingEntity;

        AircraftBean aircraftBean = flightBean.getAircraft();
        if (aircraftBean != null) {
            flightEntity.setAircraftName(aircraftBean.getName());
            flightEntity.setAircraftRegistration(aircraftBean.getRegistration());
            flightEntity.setAircraftType(aircraftBean.getType());
        }

        AirlineEntity airlineBeanFromFlight = flightBean.getAirline();
        if (airlineBeanFromFlight != null) {
            flightEntity.setAirlineCode(airlineBeanFromFlight.getCode());
            flightEntity.setAirlineName(airlineBeanFromFlight.getName());
        }
        AirlineEntity airlineBeanFromService = airlineBeanFromFlight == null || StringUtils.isEmpty(airlineBeanFromFlight.getCode()) ? null : this.getAirlinesRepository().loadAirlineByCode(airlineBeanFromFlight.getCode());
        if (airlineBeanFromService != null) {
            if (StringUtils.isEmpty(flightEntity.getAirlineName())) {
                flightEntity.setAirlineName(airlineBeanFromService.getName());
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
        flightEntity.setComment(flightBean.getComment());
        flightEntity.setSeatNumber(flightBean.getSeatNumber());
        flightEntity.setSeatType(flightBean.getSeatType());
        flightEntity.setUser(user);

        this.getFlightsRepository().save(flightEntity);

        return this.getFlightsQueryService().loadFlightById(flightEntity.getId(), user);

    }

    private Integer computeFlightDistance(AirportEntity departureAirportEntity, AirportEntity arrivalAirportEntity) {
        return FlightlogHelper.computeDistanceInKilometers(
            departureAirportEntity == null ? null : departureAirportEntity.getLongitude(),
            departureAirportEntity == null ? null : departureAirportEntity.getLatitude(),
            arrivalAirportEntity == null ? null : arrivalAirportEntity.getLongitude(),
            arrivalAirportEntity == null ? null : arrivalAirportEntity.getLatitude()
        );
    }

    private Integer computeFlightDuration(AirportContactBean departureContact, AirportEntity departureAirportEntity, AirportContactBean arrivalContact, AirportEntity arrivalAirportEntity) {
        return Optional.ofNullable(FlightlogHelper.computeDuration(
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
        this.getFlightsRepository().deleteById(flightBean.getEntityId());
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

    Supplier<FlightEntity> getNewFlightEntitySupplier() {
        return this.newFlightEntitySupplier;
    }
    void setNewFlightEntitySupplier(Supplier<FlightEntity> newFlightEntitySupplier) {
        this.newFlightEntitySupplier = newFlightEntitySupplier;
    }

}
