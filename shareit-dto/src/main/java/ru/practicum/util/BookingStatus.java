// booking/BookingState.java
package ru.practicum.util;

import java.util.Optional;

public enum BookingStatus {
    WAITING, APPROVED, REJECTED, CANCELED;

    public static Optional<BookingStatus> from(String stringStatus) {
        for (BookingStatus status : values()) {
            if (status.name().equalsIgnoreCase(stringStatus)) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }
}