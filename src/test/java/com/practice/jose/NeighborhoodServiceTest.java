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
public class NeighborhoodServiceTest {

    private Neighborhood neighborhood;
    private List<House> houses;
    @InjectMocks
    NeighborhoodService neighborhoodService;

    @Before
    public void setUp(){
        neighborhood = new Neighborhood("Barrio lindo", 2, 50);
        neighborhood.setParks(Arrays.asList(new Park(10), new Park(20), new Park(90)));
        houses = Arrays.asList(new House(20), new House(50),
                new House(80));
    }
    @Test
    public void testFilterHouses() {

        List<House> actualHouses = neighborhoodService.filterHouses(houses, neighborhood);
        Assert.assertEquals(3, actualHouses.size());
    }

    @Test
    public void testFilterHousesReturnsEmptyListWhenHousesEmptyList(){
        List <House> houses = new ArrayList<>();

        List<House> actualHouses = neighborhoodService.filterHouses(houses,neighborhood);
        Assert.assertTrue(actualHouses.isEmpty());
    }

    @Test
    public void testFilterHousesReturnEmptyListWhenHouseListIsNull(){

        List<House> actualHouses = neighborhoodService.filterHouses(null,neighborhood);
        Assert.assertTrue(actualHouses.isEmpty());
    }
    @Test
    public void testFilterHousesReturnEmptyListWhenNeighborhoodIsNull(){

        List<House> actualHouses = neighborhoodService.filterHouses(new ArrayList<>(),null);
        Assert.assertTrue(actualHouses.isEmpty());
    }

    @Test
    public void testFilterHousesReturnEmptyListWhenParksIsEmptyList(){
        neighborhood.setParks(new ArrayList<>());
        List<House> actualHouses = neighborhoodService.filterHouses(houses, neighborhood);
        Assert.assertTrue(actualHouses.isEmpty());
    }



}