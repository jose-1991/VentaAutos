package com.store.sale.exceptions;

public class RecordsNotFoundException extends RuntimeException{
    public RecordsNotFoundException(String message){
        System.out.println(message);
    }
}
