// Item.java
package ru.practicum.item;

import jakarta.persistence.*;
import lombok.*;
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
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "available")
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private ItemRequest request;
}