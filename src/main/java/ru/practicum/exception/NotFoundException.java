package ru.practicum.exception;

public class NotFoundException extends RuntimeException { // Extend RuntimeException

    public NotFoundException(String message) {
        super(message);
    }
}