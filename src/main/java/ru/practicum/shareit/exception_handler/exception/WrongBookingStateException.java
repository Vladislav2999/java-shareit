package ru.practicum.shareit.exception_handler.exception;

public class WrongBookingStateException extends RuntimeException {

    public WrongBookingStateException(String message) {
        super(message);
    }
}