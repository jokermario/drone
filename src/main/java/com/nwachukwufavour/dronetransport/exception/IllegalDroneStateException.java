package com.nwachukwufavour.dronetransport.exception;

public class IllegalDroneStateException extends RuntimeException{
    public IllegalDroneStateException(String message){
        super(message);
    }
}
