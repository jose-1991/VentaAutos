package com.practice.jose;

import java.util.ArrayList;
import java.util.List;

public class Test1 {
}

class House {
    private int location;

    public House() {
    }

    public House(int location) {
        this.location = location;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }
}

class Park {
    private int location;

    public Park() {
    }

    public Park(int location) {
        this.location = location;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }
}

class Neighborhood {
    private String name;
    private int numberParks;
    private List<House> houses;
    private List<Park> parks;
    private int maxDistance;

    public Neighborhood(String name, int numberParks, int maxDistance) {
        this.name = name;
        this.numberParks = numberParks;
        this.maxDistance = maxDistance;
        this.parks = new ArrayList<>(numberParks);
        this.houses = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberParks() {
        return numberParks;
    }

    public void setNumberParks(int numberParks) {
        this.numberParks = numberParks;
    }

    public List<House> getHouses() {
        return houses;
    }

    public void setHouses(List<House> houses) {
        this.houses = houses;
    }

    public List<Park> getParks() {
        return parks;
    }

    public void setParks(List<Park> parks) {
        this.parks = parks;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }
}

class NeighborhoodService {
    Neighborhood neighborhood = new Neighborhood("Señor de Mayo", 3,700);

    public List<House> filterHouses(List<House> houses, Neighborhood neighborhood) {
        if (houses != null && neighborhood != null) {
            for (House house : houses) {
                for (Park park : neighborhood.getParks()) {
                    if (Math.abs(park.getLocation() - house.getLocation()) <= neighborhood.getMaxDistance()) {
                        if (!neighborhood.getHouses().contains(house)) {
                            neighborhood.getHouses().add(house);
                        }
                    }
                }
            }
            return neighborhood.getHouses();
        }
        return new ArrayList<>();
    }

//    se tiene un barrio que cuenta con N numero de casas y 3 parques, para pertenecer al barrio la casa debe estar a no mas de 700 m de distancia de cualquier parque


}