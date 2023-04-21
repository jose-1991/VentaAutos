package com.practice.jose;

import java.util.ArrayList;
import java.util.List;

public class Test3 {
}

class Passenger {
    private String ticketType;

    public Passenger(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getTicketType() {
        return ticketType;
    }

}


class Plane {
    private ArrayList<Passenger> passengerList;
    private int maxCapacity;

    public Plane(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        passengerList = new ArrayList<>();
    }

    public ArrayList<Passenger> getPassengerList() {
        return passengerList;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }
}

class PlaneMaxCapacityExceededException extends RuntimeException {
    public PlaneMaxCapacityExceededException() {
        super("The number of passengers exceeded the plane capacity");
    }
}

class PlaneService {
    public static final String FIRST_CLASS = "First Class";
    public static final String ECONOMIC_CLASS = "Economic Class";

    public List<Passenger> boarding(Plane plane, List<Passenger> passengers) {
        List<Passenger> economicClassList = new ArrayList<>();

        if (passengers != null && plane != null) {
            if (passengers.size() > plane.getMaxCapacity()) {
                throw new PlaneMaxCapacityExceededException();
            }
            for (Passenger passenger : passengers) {
                if (passenger.getTicketType().equals(FIRST_CLASS)) {
                    plane.getPassengerList().add(passenger);
                }
                if (passenger.getTicketType().equals(ECONOMIC_CLASS)) {
                    economicClassList.add(passenger);
                }
            }
            plane.getPassengerList().addAll(economicClassList);
            return plane.getPassengerList();
        }
        return new ArrayList<>();
    }
}

//    Un avion aborda 100 pasajeros por vuelo, los pasajeros abordan de acuerdo al tipo de ticket,
//    si es 1ra clase, abordan primeros, si es  clase economica
//    entonces abordan ultimos
