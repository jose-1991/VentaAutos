package com.store.sale.exceptions;

public class EmptyFileException extends RuntimeException {
    public EmptyFileException(String message) {
        System.out.println(message);
    }
}
