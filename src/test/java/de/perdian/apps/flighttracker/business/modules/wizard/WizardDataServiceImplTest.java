package de.perdian.apps.flighttracker.business.modules.wizard;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import de.perdian.apps.flighttracker.business.modules.wizard.WizardData;
import de.perdian.apps.flighttracker.business.modules.wizard.WizardDataFactory;
import de.perdian.apps.flighttracker.business.modules.wizard.WizardDataServiceImpl;

public class WizardDataServiceImplTest {

    @Test
    public void lookupFlightData() {

        WizardData data2 = new WizardData();
        WizardDataFactory factory1 = Mockito.mock(WizardDataFactory.class);
        WizardDataFactory factory2 = Mockito.mock(WizardDataFactory.class);
        Mockito.when(factory2.createData(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(data2);
        WizardDataFactory factory3 = Mockito.mock(WizardDataFactory.class);

        WizardDataServiceImpl serviceImpl = new WizardDataServiceImpl();
        serviceImpl.setFactories(Arrays.asList(factory1, factory2, factory3));

        WizardData flightDataResolved = serviceImpl.createData("LH", "123", LocalDate.of(2017, 10, 9));
        Assert.assertEquals(data2, flightDataResolved);
        Mockito.verify(factory1).createData(Mockito.eq("LH"), Mockito.eq("123"), Mockito.eq(LocalDate.of(2017, 10, 9)));
        Mockito.verify(factory2).createData(Mockito.eq("LH"), Mockito.eq("123"), Mockito.eq(LocalDate.of(2017, 10, 9)));
        Mockito.verifyNoMoreInteractions(factory3);

    }

    @Test
    public void lookupFlightDataNothingFound() {

        WizardDataFactory factory1 = Mockito.mock(WizardDataFactory.class);
        WizardDataFactory factory2 = Mockito.mock(WizardDataFactory.class);

        WizardDataServiceImpl serviceImpl = new WizardDataServiceImpl();
        serviceImpl.setFactories(Arrays.asList(factory1, factory2));

        Assert.assertNull(serviceImpl.createData("LH", "123", LocalDate.of(2017, 10, 9)));
        Mockito.verify(factory1).createData(Mockito.eq("LH"), Mockito.eq("123"), Mockito.eq(LocalDate.of(2017, 10, 9)));
        Mockito.verify(factory2).createData(Mockito.eq("LH"), Mockito.eq("123"), Mockito.eq(LocalDate.of(2017, 10, 9)));

    }

    @Test
    public void lookupFlightDataExceptionFromFlightDataSource() {

        WizardDataFactory factory1 = Mockito.mock(WizardDataFactory.class);
        Mockito.when(factory1.createData(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(new IllegalArgumentException());
        WizardDataFactory factory2 = Mockito.mock(WizardDataFactory.class);

        WizardDataServiceImpl serviceImpl = new WizardDataServiceImpl();
        serviceImpl.setFactories(Arrays.asList(factory1, factory2));

        Assert.assertNull(serviceImpl.createData("LH", "123", LocalDate.of(2017, 10, 9)));
        Mockito.verify(factory1).createData(Mockito.eq("LH"), Mockito.eq("123"), Mockito.eq(LocalDate.of(2017, 10, 9)));
        Mockito.verify(factory2).createData(Mockito.eq("LH"), Mockito.eq("123"), Mockito.eq(LocalDate.of(2017, 10, 9)));

    }

}
