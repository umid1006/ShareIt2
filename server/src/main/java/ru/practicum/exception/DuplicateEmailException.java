// In src/main/java/ru/practicum/exception/DuplicateEmailException.java
package ru.practicum.exception;

public class DuplicateEmailException extends RuntimeException { // Or extends Exception if you want checked exception
    public DuplicateEmailException(String message) {
        super(message);
    }
}