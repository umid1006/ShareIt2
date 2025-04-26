package ru.practicum.booking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.dto.BookingDto;
import ru.practicum.dto.ItemDto;
import ru.practicum.dto.UserDto;
import ru.practicum.util.BookingStatus;
import ru.practicum.exception.*;
import ru.practicum.item.Item;
import ru.practicum.item.ItemRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
        log.info("Creating booking for item {} by user {}", bookingDto.getItemId(), bookerId);

        // 1. Validate and fetch required entities
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + bookerId));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + bookingDto.getItemId()));

        // 2. Business validations
        validateBookingCreation(item, bookerId);

        // 3. Create and save booking
        Booking booking = buildBookingEntity(bookingDto, item, booker);
        Booking savedBooking = bookingRepository.save(booking);

        // 4. Return fully populated DTO
        return buildBookingResponseDto(savedBooking);
    }

    private void validateBookingCreation(Item item, Long bookerId) {
        if (!item.getAvailable()) {
            throw new ValidateException("Item is not available for booking");
        }
        if (item.getOwner().getId().equals(bookerId)) {
            throw new ConflictException("Owner cannot book their own item");
        }
    }

    private Booking buildBookingEntity(BookingDto bookingDto, Item item, User booker) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    private BookingDto buildBookingResponseDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemDto.builder()
                        .id(booking.getItem().getId())
                        .name(booking.getItem().getName())
                        .build())
                .booker(UserDto.builder()
                        .id(booking.getBooker().getId())
                        .name(booking.getBooker().getName())
                        .email(booking.getBooker().getEmail())
                        .build())
                .status(booking.getStatus())
                .build();
    }

    @Override
    @Transactional
    public BookingDto updateBooking(Long bookingId, Long userId, boolean approved) {
        log.info("Updating booking {} with approved={} by user {}", bookingId, approved, userId);

        // 1. Find booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        // 2. Verify user is the item owner
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only item owner can approve/reject booking");
        }

        // 3. Check booking status
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidateException("Booking status cannot be changed");
        }

        // 4. Update status
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