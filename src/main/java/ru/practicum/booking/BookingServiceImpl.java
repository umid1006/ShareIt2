package ru.practicum.booking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.exception.*;
import ru.practicum.item.Item;
import ru.practicum.item.ItemRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto createBooking(BookingDto bookingDto, Long bookerId) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        // Check item availability - THIS IS THE KEY CHANGE
        if (!item.getAvailable()) {
            throw new ValidateException("Item is not available for booking"); // Changed to ValidateException
        }

        // Rest of your validation checks...
        if (item.getOwner().getId().equals(bookerId)) {
            throw new ConflictException("Owner cannot book their own item");
        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidateException("End date must be after start date");
        }

        Booking booking = bookingMapper.toEntity(bookingDto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto updateBooking(Long bookingId, Long userId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        // Validate update
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidateException("Only item owner can approve/reject booking");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidateException("Only WAITING bookings can be modified");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(updatedBooking);
    }

    @Override
    @Transactional
    public BookingDto cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        // Validate cancellation
        if (!booking.getBooker().getId().equals(userId)) {
            throw new ValidateException("Only booker can cancel booking");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidateException("Only WAITING bookings can be canceled");
        }

        booking.setStatus(BookingStatus.CANCELED);
        Booking canceledBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(canceledBooking);
    }

    @Override
    public BookingDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidateException("Only booker or owner can view booking details");
        }

        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getBookings(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings;

        try {
            BookingStatus status = BookingStatus.valueOf(state.toUpperCase());
            bookings = bookingRepository.findByBookerIdAndStatus(userId, status, sort);
        } catch (IllegalArgumentException e) {
            // If state is not a valid BookingStatus, return all bookings
            bookings = bookingRepository.findByBookerId(userId, sort);
        }

        return bookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, String state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + ownerId));

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings;

        try {
            BookingStatus status = BookingStatus.valueOf(state.toUpperCase());
            bookings = bookingRepository.findByItemOwnerIdAndStatus(ownerId, status, sort);
        } catch (IllegalArgumentException e) {
            // If state is not a valid BookingStatus, return all bookings
            bookings = bookingRepository.findByItemOwnerId(ownerId, sort);
        }

        return bookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }
}