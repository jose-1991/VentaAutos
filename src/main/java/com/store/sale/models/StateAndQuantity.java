package com.store.sale.models;

import java.util.Objects;

public class StateAndQuantity {
    private String state;
    private int quantity;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "state= "+ state +
                "quantity= " + quantity;
    }
}
