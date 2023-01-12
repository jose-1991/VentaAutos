package com.store.sale.exceptions;

public class ReportPdfNotFoundException extends RuntimeException{
    public ReportPdfNotFoundException(String message){
        System.out.println(message);
    }
}
