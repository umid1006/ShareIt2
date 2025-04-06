// User.java (Corrected JPA Entity)
package ru.practicum.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter  // Use Lombok for getters
@Setter  // Use Lombok for setters
@ToString
@EqualsAndHashCode
@Builder
@Entity  // Marks this class as a JPA entity
@Table(name = "users")  // Specifies the database table name
@AllArgsConstructor // Add
@NoArgsConstructor // Add
public class User {
    @Id  // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generates the ID
    private Long id;

    @Column(name = "name")  // Maps the field to a database column
    @Size(max = 255)
    private String name;

    @Column(name = "email", nullable = false, unique = true)  // Email should be unique
    @Size(max = 255)
    private String email;
}