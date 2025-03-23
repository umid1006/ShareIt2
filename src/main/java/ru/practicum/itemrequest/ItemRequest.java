// ItemRequest.java (Entity - in ru.practicum.request)
package ru.practicum.itemrequest;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.practicum.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "item_requests")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"}) // Use only ID for equals/hashCode
@EntityListeners(AuditingEntityListener.class) // Enable auditing
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @CreatedDate // Automatically sets the creation date
    @Column(name = "created_at", nullable = false, updatable = false) // Don't allow updates
    private LocalDateTime created;
}