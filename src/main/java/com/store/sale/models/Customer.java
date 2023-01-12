package com.store.sale.models;

import java.util.Objects;

public class Customer {

    private String customerId;
    private String customerName;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customer_ID) {
        this.customerId = customer_ID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(customerId, customer.customerId);
    }

    @Override
    public String toString() {
        return customerId + " | " + customerName + "\n";
    }
}
