package com.store.sale.exceptions;

public class IdValueNotFoundException extends RuntimeException {
    public IdValueNotFoundException(String message) {
        System.out.println(message);
    }
}
