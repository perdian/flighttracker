package de.perdian.apps.flighttracker.modules.airlines.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.perdian.apps.flighttracker.modules.airlines.model.AirlineBean;
import de.perdian.apps.flighttracker.modules.airlines.services.AirlinesService;

/**
 * AJAX target controller to deliver information about airlines during the edit process
 *
 * @author Christian Robert
 */

@RestController
public class AirlineController {

    private AirlinesService airlinesService = null;

    @RequestMapping(path = "/airline/{airlineCode}", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    public Airline doAirline(@PathVariable("airlineCode") String airlineCode) {
        AirlineBean airlineBean = this.getAirlinesService().loadAirlineByIataCode(airlineCode);
        if (airlineBean == null) {
            throw new AirlineNotFoundException();
        } else {
            Airline airline = new Airline();
            airline.setCode(airlineBean.getIataCode());
            airline.setName(airlineBean.getName());
            return airline;
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    static class AirlineNotFoundException extends RuntimeException {

        static final long serialVersionUID = 1L;

    }

    AirlinesService getAirlinesService() {
        return this.airlinesService;
    }
    @Autowired
    void setAirlinesService(AirlinesService airlinesService) {
        this.airlinesService = airlinesService;
    }

}
