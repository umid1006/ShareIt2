package ru.practicum.item;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.itemrequest.ItemRequest;
import ru.practicum.user.User;

@Getter
@Setter
@ToString(exclude = {"owner", "request"})
@EqualsAndHashCode(exclude = {"owner", "request"})
@Builder
@Entity
@Table(name = "items")
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 255)
    @Size(max = 255)
    String name;

    @Column(length = 255)
    @Size(max = 255)
    String description;

    @ManyToOne(fetch = FetchType.LAZY)
    User owner;

    @Column
    Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    ItemRequest request;
}