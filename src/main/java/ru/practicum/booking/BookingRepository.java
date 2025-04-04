package ru.practicum.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // For user bookings
    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    // For owner bookings
    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId")
    List<Booking> findByItemOwnerId(@Param("ownerId") Long ownerId, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.status = :status")
    List<Booking> findByItemOwnerIdAndStatus(
            @Param("ownerId") Long ownerId,
            @Param("status") BookingStatus status,
            Sort sort
    );

    // Additional queries
    List<Booking> findByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime now);
    List<Booking> findByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);
    boolean existsByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime now);
}