package de.perdian.apps.flighttracker.modules.flights.web;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import de.perdian.apps.flighttracker.modules.flights.model.FlightBean;
import de.perdian.apps.flighttracker.modules.flights.services.FlightsQueryService;
import de.perdian.apps.flighttracker.modules.flights.services.FlightsUpdateService;
import de.perdian.apps.flighttracker.modules.flights.web.FlightsEditController.FlightNotFoundException;
import de.perdian.apps.flighttracker.support.persistence.PaginatedList;
import de.perdian.apps.flighttracker.support.web.MessageSeverity;
import de.perdian.apps.flighttracker.support.web.Messages;

public class FlightsEditControllerTest {

    @Test
    public void doDeleteGetNotFound() {

        FlightsQueryService queryService = Mockito.mock(FlightsQueryService.class);
        Mockito.when(queryService.loadFlights(Mockito.any())).thenReturn(new PaginatedList<>(null, null));

        FlightsEditController controller = new FlightsEditController();
        controller.setFlightsQueryService(queryService);

        Assertions.assertThrows(FlightNotFoundException.class, () -> controller.doDeleteGet(null, Long.valueOf(42), null));

    }

    @Test
    public void doDeleteGet() {

        FlightBean flightBean = new FlightBean();
        FlightsQueryService queryService = Mockito.mock(FlightsQueryService.class);
        Mockito.when(queryService.loadFlights(Mockito.any())).thenReturn(new PaginatedList<>(Arrays.asList(flightBean), null));

        Model model = Mockito.mock(Model.class);

        FlightsEditController controller = new FlightsEditController();
        controller.setFlightsQueryService(queryService);

        Assertions.assertEquals("/flights/delete", controller.doDeleteGet(null, Long.valueOf(42), model));

        Mockito.verify(model).addAttribute(Mockito.eq("flight"), Mockito.eq(flightBean));
        Mockito.verifyNoMoreInteractions(model);

    }

    @Test
    public void doDeletePostNotFound() {

        FlightsQueryService queryService = Mockito.mock(FlightsQueryService.class);
        Mockito.when(queryService.loadFlights(Mockito.any())).thenReturn(new PaginatedList<>(null, null));

        FlightsEditController controller = new FlightsEditController();
        controller.setFlightsQueryService(queryService);

        Assertions.assertThrows(FlightNotFoundException.class, () -> controller.doDeletePost(null, Long.valueOf(42)));

    }

    @Test
    public void doDeletePost() {

        FlightBean flightBean = new FlightBean();
        FlightsQueryService queryService = Mockito.mock(FlightsQueryService.class);
        Mockito.when(queryService.loadFlights(Mockito.any())).thenReturn(new PaginatedList<>(Arrays.asList(flightBean), null));

        FlightsUpdateService updateService = Mockito.mock(FlightsUpdateService.class);

        FlightsEditController controller = new FlightsEditController();
        controller.setFlightsQueryService(queryService);
        controller.setFlightsUpdateService(updateService);

        Assertions.assertEquals("redirect:/flights/list?updated=true", controller.doDeletePost(null, Long.valueOf(42)));

        Mockito.verify(updateService).deleteFlight(Mockito.eq(flightBean));

    }

    @Test
    public void doEditGetNotFound() {

        FlightsQueryService queryService = Mockito.mock(FlightsQueryService.class);
        Mockito.when(queryService.loadFlights(Mockito.any())).thenReturn(new PaginatedList<>(null, null));

        FlightsEditController controller = new FlightsEditController();
        controller.setFlightsQueryService(queryService);

        Assertions.assertThrows(FlightNotFoundException.class, () -> controller.doEditGet(null, Long.valueOf(42), true, null, null, null));

    }

    @Test
    public void doEditGetUpdatedTrue() {

        FlightBean flightBean = new FlightBean();
        FlightsQueryService queryService = Mockito.mock(FlightsQueryService.class);
        Mockito.when(queryService.loadFlights(Mockito.any())).thenReturn(new PaginatedList<>(Arrays.asList(flightBean), null));

        Messages messages = new Messages();

        Model model = Mockito.mock(Model.class);

        FlightsEditController controller = new FlightsEditController();
        controller.setMessageSource(Mockito.mock(MessageSource.class));
        controller.setFlightsQueryService(queryService);

        Assertions.assertEquals("/flights/edit", controller.doEditGet(null, Long.valueOf(42), true, messages, null, model));

        Mockito.verify(model).addAttribute(Mockito.eq("flightEditor"), Mockito.any(FlightEditor.class));
        Mockito.verifyNoMoreInteractions(model);
        Assertions.assertEquals(1, messages.getMessagesBySeverity().size());
        Assertions.assertEquals(1, messages.getMessagesBySeverity().get(MessageSeverity.INFO).size());

    }

    @Test
    public void doEditGetUpdatedFalse() {

        FlightBean flightBean = new FlightBean();
        FlightsQueryService queryService = Mockito.mock(FlightsQueryService.class);
        Mockito.when(queryService.loadFlights(Mockito.any())).thenReturn(new PaginatedList<>(Arrays.asList(flightBean), null));

        Messages messages = new Messages();

        Model model = Mockito.mock(Model.class);

        FlightsEditController controller = new FlightsEditController();
        controller.setMessageSource(Mockito.mock(MessageSource.class));
        controller.setFlightsQueryService(queryService);

        Assertions.assertEquals("/flights/edit", controller.doEditGet(null, Long.valueOf(42), false, messages, null, model));

        Mockito.verify(model).addAttribute(Mockito.eq("flightEditor"), Mockito.any(FlightEditor.class));
        Mockito.verifyNoMoreInteractions(model);
        Assertions.assertEquals(0, messages.getMessagesBySeverity().size());

    }

    @Test
    public void doEditPostBindingResultHasErrors() {

        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Mockito.when(bindingResult.hasErrors()).thenReturn(true);

        FlightBean flightBean = new FlightBean();
        FlightsQueryService queryService = Mockito.mock(FlightsQueryService.class);
        Mockito.when(queryService.loadFlights(Mockito.any())).thenReturn(new PaginatedList<>(Arrays.asList(flightBean), null));

        Messages messages = new Messages();

        Model model = Mockito.mock(Model.class);

        FlightsEditController controller = new FlightsEditController();
        controller.setFlightsQueryService(queryService);
        controller.setMessageSource(Mockito.mock(MessageSource.class));

        Assertions.assertEquals("/flights/edit", controller.doEditPost(null, Long.valueOf(42), null, bindingResult, messages, null, model));
        Mockito.verify(model).addAttribute(Mockito.eq("flightEditor"), Mockito.any(FlightEditor.class));
        Mockito.verifyNoMoreInteractions(model);

    }

    @Test
    public void doEditPostBindingResultHasNoErrorsFound() {

        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);

        FlightBean flightBean = new FlightBean();
        flightBean.setEntityId(Long.valueOf(43));
        FlightsQueryService queryService = Mockito.mock(FlightsQueryService.class);
        Mockito.when(queryService.loadFlights(Mockito.any())).thenReturn(new PaginatedList<>(Arrays.asList(flightBean), null));

        FlightsUpdateService updateService = Mockito.mock(FlightsUpdateService.class);

        FlightEditor flightEditor = Mockito.mock(FlightEditor.class);
        Mockito.when(flightEditor.getEntityId()).thenReturn(Long.valueOf(42));

        FlightsEditController controller = new FlightsEditController();
        controller.setFlightsQueryService(queryService);
        controller.setFlightsUpdateService(updateService);

        Assertions.assertEquals("redirect:/flights/edit/43?updated=true", controller.doEditPost(null, Long.valueOf(42), flightEditor, bindingResult, null, null, null));

        Mockito.verify(updateService).saveFlight(Mockito.eq(flightBean));

    }

    @Test
    public void doEditPostBindingResultHasNoErrorsNotFound() {

        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);

        FlightsQueryService queryService = Mockito.mock(FlightsQueryService.class);
        Mockito.when(queryService.loadFlights(Mockito.any())).thenReturn(new PaginatedList<>(null, null));

        FlightsUpdateService updateService = Mockito.mock(FlightsUpdateService.class);

        FlightEditor flightEditor = Mockito.mock(FlightEditor.class);
        Mockito.when(flightEditor.getEntityId()).thenReturn(Long.valueOf(42));

        FlightsEditController controller = new FlightsEditController();
        controller.setFlightsQueryService(queryService);
        controller.setFlightsUpdateService(updateService);

        Assertions.assertThrows(FlightNotFoundException.class, () -> controller.doEditPost(null, Long.valueOf(42), flightEditor, bindingResult, null, null, null));

    }

}
