package com.reactiveapis.demo.exceptions;

public class InvalidCustomerException extends Exception{
    public InvalidCustomerException(String message){
        super(message);
    }
}
