// In a 'dto' package, e.g., ru.practicum.dto
package ru.practicum.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String error; // A short error code (e.g., "validation_error")
    private String message; // A human-readable error message
}