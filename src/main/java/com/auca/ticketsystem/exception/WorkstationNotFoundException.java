package com.auca.ticketsystem.exception;

public class WorkstationNotFoundException extends RuntimeException {
    public WorkstationNotFoundException(String message) {
        super(message);
    }
}
