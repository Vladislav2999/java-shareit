package ru.practicum.shareit.exceptionHandler.exception;

public class WrongBookingStateException extends RuntimeException {

    public WrongBookingStateException(String message) {
        super(message);
    }
}