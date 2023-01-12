package com.store.sale.exceptions;

public class DifferentExtensionException extends RuntimeException {
    public DifferentExtensionException(String message) {
        System.out.println(message);
        ;
    }
}
