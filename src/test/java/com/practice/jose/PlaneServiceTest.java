package com.practice.jose;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class PlaneServiceTest {
    public static final String FIRST_CLASS = "First Class";
    public static final String ECONOMIC_CLASS = "Economic Class";

    List<Passenger> passengerList;
    @InjectMocks
    PlaneService planeService;

    @Before
    public void setUp() {
        passengerList = Arrays.asList(new Passenger(ECONOMIC_CLASS),
                new Passenger(FIRST_CLASS), new Passenger(ECONOMIC_CLASS), new Passenger(ECONOMIC_CLASS),
                new Passenger(FIRST_CLASS));
    }

    @Test
    public void testBoarding() {
        Plane plane = new Plane(5);

        List<Passenger> actualPassengerList = planeService.boarding(plane, passengerList);
        Assert.assertFalse(actualPassengerList.isEmpty());
        Assert.assertEquals(5, actualPassengerList.size());
        Assert.assertEquals(FIRST_CLASS, actualPassengerList.get(1).getTicketType());
        Assert.assertEquals(ECONOMIC_CLASS, actualPassengerList.get(2).getTicketType());
    }

    @Test
    public void testBoardingReturnsEmptyListWhenPassengerListIsEmpty() {
        Plane plane = new Plane(8);

        List<Passenger> actualPassengerList = planeService.boarding(plane, new ArrayList<>());
        Assert.assertTrue(actualPassengerList.isEmpty());
    }

    @Test(expected = PlaneMaxCapacityExceededException.class)
    public void testBoardingReturnsCustomExceptionWhenPlaneCapacityExceeds() {
        Plane plane = new Plane(3);

        planeService.boarding(plane, passengerList);
    }


}