package ru.practicum.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserDto {
    Long id;

    @Size(max = 255, message = "Name cannot exceed 255 characters") // You can omit @NotBlank if you *allow* updating the name to null
    String name;  //  Allow name updates, but constrain the size

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email cannot exceed 255 characters")  // Good practice to limit size
    String email;
}