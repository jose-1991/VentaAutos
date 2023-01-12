package com.store.sale.models;

public enum Tables {
    CUSTOMER("INSERT INTO store.customer(customer_ID, cName)" + "VALUES(?,?)"),

    ADDRESS(
            "INSERT INTO store.address(address_ID, country, state, city) "
                    + "VALUES(?,?,?,?)"),

    PRODUCT(
            "INSERT INTO store.product(product_ID, category, sub_category, pName) " + "VALUES(?,?,?,?)"),

    ORDER(
            "INSERT INTO store.order(order_ID, orderDate, customer_ID, address_ID, product_ID, price, quantity, discount, total, profit)"
                    + "VALUES(?,?,?,?,?,?,?,?,?,?)");
    final String code;

    Tables(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}
