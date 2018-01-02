package de.perdian.apps.flighttracker.modules.airlines.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import de.perdian.apps.flighttracker.modules.airlines.model.AirlineBean;
import de.perdian.apps.flighttracker.modules.airlines.services.AirlinesService;
import de.perdian.apps.flighttracker.modules.importexport.data.impl.OpenflightsHelper;

/**
 * Airline data is used from
 * https://openflights.org/data.html#airline
 */

@Repository
class AirlinesRepositoryImpl implements AirlinesService {

    private static final Logger log = LoggerFactory.getLogger(AirlinesRepositoryImpl.class);

    private ResourceLoader resourceLoader = null;
    private List<AirlineBean> airlineBeans = null;
    private Map<String, AirlineBean> airlineBeansByIataCode = null;

    @PostConstruct
    void initialize() throws IOException {

        Resource airlinesResource = this.getResourceLoader().getResource("classpath:de/perdian/apps/flighttracker/data/airlines.dat");
        log.info("Loading airlines from resource: {}", airlinesResource);

        List<AirlineBean> airlineBeans = new ArrayList<>();
        Map<String, AirlineBean> airlineBeansByIataCode = new LinkedHashMap<>();
        try (BufferedReader airlinesReader = new BufferedReader(new InputStreamReader(airlinesResource.getInputStream(), "UTF-8"))) {
            for (String airlineLine = airlinesReader.readLine(); airlineLine != null; airlineLine = airlinesReader.readLine()) {
                try {

                    List<String> lineFields = OpenflightsHelper.tokenizeLine(airlineLine);
                    String iataCode = lineFields.get(3);
                    String icaoCode = lineFields.get(4);

                    AirlineBean airlineBean = new AirlineBean();
                    airlineBean.setName(lineFields.get(1));
                    airlineBean.setAlias(lineFields.get(2));
                    airlineBean.setIataCode(iataCode);
                    airlineBean.setIcaoCode(icaoCode);
                    airlineBean.setCallsign(lineFields.get(5));
                    airlineBean.setCountryCode(lineFields.get(6));

                    airlineBeans.add(airlineBean);
                    airlineBeansByIataCode.putIfAbsent(iataCode, airlineBean);

                } catch (Exception e) {
                    log.warn("Invalid airline line: {}", airlineLine, e);
                }
            }
        }
        this.setAirlineBeans(airlineBeans);
        this.setAirlineBeansByIataCode(airlineBeansByIataCode);
        log.debug("Loaded {} airlines from resource: {}", airlineBeans.size(), airlinesResource);

    }

    @Override
    public AirlineBean loadAirlineByIataCode(String iataAirlineCode) {
        return StringUtils.isEmpty(iataAirlineCode) ? null : this.getAirlineBeansByIataCode().get(iataAirlineCode);
    }

    @Override
    public AirlineBean loadAirlineByName(String airlineName) {
        return this.getAirlineBeans().stream()
            .filter(airline -> airline.getName().equalsIgnoreCase(airlineName))
            .findFirst()
            .orElse(null);
    }

    ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }
    @Autowired
    void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    List<AirlineBean> getAirlineBeans() {
        return this.airlineBeans;
    }
    void setAirlineBeans(List<AirlineBean> airlineBeans) {
        this.airlineBeans = airlineBeans;
    }

    Map<String, AirlineBean> getAirlineBeansByIataCode() {
        return this.airlineBeansByIataCode;
    }
    void setAirlineBeansByIataCode(Map<String, AirlineBean> airlineBeansByIataCode) {
        this.airlineBeansByIataCode = airlineBeansByIataCode;
    }

}
