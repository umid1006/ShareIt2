package ru.practicum.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime now, LocalDateTime now1);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, Booking.BookingStatus waiting);

    List<Booking> findByItemIdOrderByStartDesc(Long itemId);

    List<Booking> findByItemIdAndStartBeforeAndEndAfterOrderByStartDesc(Long itemId, LocalDateTime now, LocalDateTime now1);

    List<Booking> findByItemIdAndEndBeforeOrderByStartDesc(Long itemId, LocalDateTime now);

    List<Booking> findByItemIdAndStartAfterOrderByStartDesc(Long itemId, LocalDateTime now);

    List<Booking> findByItemIdAndStatusOrderByStartDesc(Long itemId, Booking.BookingStatus waiting);

    List<Booking> findByItemId(Long itemId);

    List<Booking> findByBookerId(Long bookerId);

    List<Booking> findByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime now);

    List<Booking> findByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);

    boolean existsByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime end);
}