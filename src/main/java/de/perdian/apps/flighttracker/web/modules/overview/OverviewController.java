package de.perdian.apps.flighttracker.web.modules.overview;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import de.perdian.apps.flighttracker.business.modules.flights.FlightsQueryService;
import de.perdian.apps.flighttracker.business.modules.overview.OverviewQuery;
import de.perdian.apps.flighttracker.business.modules.overview.OverviewService;
import de.perdian.apps.flighttracker.business.modules.overview.model.OverviewBean;
import de.perdian.apps.flighttracker.web.security.FlighttrackerUser;

@Controller
public class OverviewController {

    private OverviewService overviewService = null;
    private FlightsQueryService flightsQueryService = null;

    @RequestMapping(value = "/")
    public String overview() {
        return "/overview/overview";
    }

    @ModelAttribute(name = "overview")
    public OverviewBean overview(@AuthenticationPrincipal FlighttrackerUser authenticationPrincipal, @ModelAttribute("overviewQuery") OverviewQuery overviewQuery) {
        overviewQuery.setRestrictUsers(authenticationPrincipal == null ? null : authenticationPrincipal.toUserEntities());
        return this.getOverviewService().loadOverview(overviewQuery);
    }

    @ModelAttribute(name = "overviewQuery")
    public OverviewQuery overviewQuery() {
        OverviewQuery overviewQuery = new OverviewQuery();
        return overviewQuery;
    }

    OverviewService getOverviewService() {
        return this.overviewService;
    }
    @Autowired
    void setOverviewService(OverviewService overviewService) {
        this.overviewService = overviewService;
    }

    FlightsQueryService getFlightsQueryService() {
        return this.flightsQueryService;
    }
    @Autowired
    void setFlightsQueryService(FlightsQueryService flightsQueryService) {
        this.flightsQueryService = flightsQueryService;
    }

}