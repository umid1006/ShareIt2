// ItemDto.java
package ru.practicum.item;

import lombok.*;

@Value
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ItemDto {
    Long id;
    String name;
    String description;
    Long ownerId; // Keep this for the controller's input
    Boolean available;
    Long requestId; // Keep this
}