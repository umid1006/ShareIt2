// booking/BookingServiceImpl.java
package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidateException;
import ru.practicum.item.Item;
import ru.practicum.item.ItemRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto createBooking(BookingDto bookingDto, Long userId) {
        // Validate input
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new ValidateException("Start and end dates must be provided.");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidateException("Start date must be before end date.");
        }
        // Check item exists and is available
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item with id " + bookingDto.getItemId() + " not found"));
        if (!item.getAvailable()) {
            throw new ValidateException("Item with id " + bookingDto.getItemId() + " is not available.");
        }

        // Check user exists
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        // Create booking
        Booking booking = bookingMapper.mapToModel(bookingDto);
        booking.setBooker(booker); // Set the *User* object
        booking.setItem(item);  // Set the *Item* object
        booking.setStatus(Booking.BookingStatus.WAITING); // Initial status

        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.mapToDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto updateBooking(Long bookingId, BookingDto bookingDto) {
        Booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found"));

        // Update fields (only if provided in the DTO)
        if (bookingDto.getStart() != null) {
            existingBooking.setStart(bookingDto.getStart());
        }
        if (bookingDto.getEnd() != null) {
            existingBooking.setEnd(bookingDto.getEnd());
        }
        if (bookingDto.getStatus() != null) {
            existingBooking.setStatus(bookingDto.getStatus());
        }
        // You might not allow updating item or booker after creation, depending on requirements

        // Validate the updated booking (e.g., start before end)
        if (existingBooking.getStart().isAfter(existingBooking.getEnd())) {
            throw new ValidateException("Start date must be before end date.");
        }

        Booking updatedBooking = bookingRepository.save(existingBooking);
        return bookingMapper.mapToDto(updatedBooking);
    }

    @Override
    public BookingDto getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found"));
        return bookingMapper.mapToDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingsByBooker(Long bookerId) {
        return bookingRepository.findByBookerId(bookerId).stream()
                .map(bookingMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingsByItemOwner(Long ownerId) {
        // Find all items owned by the owner
        List<Item> items = itemRepository.findByOwnerId(ownerId);

        // Create an empty list to store BookingDto objects
        List<BookingDto> bookingDtos = new java.util.ArrayList<>();

        // Iterate over each item and find its bookings
        for (Item item : items) {
            // Find all bookings for the current item and map them to DTOs
            List<BookingDto> itemBookings = bookingRepository.findByItemId(item.getId()).stream()
                    .map(bookingMapper::mapToDto)
                    .collect(Collectors.toList());

            // Add the DTOs for the current item's bookings to our result list
            bookingDtos.addAll(itemBookings);
        }

        // Return the aggregated list of BookingDto objects
        return bookingDtos;
    }
    @Override
    @Transactional
    public BookingDto approveBooking(Long bookingId, Long ownerId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found"));

        // Check if the user is the owner of the item
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ValidateException("User with id " + ownerId + " is not the owner of the item.");
        }

        // Check if booking is already approved/rejected
        if (booking.getStatus() != Booking.BookingStatus.WAITING) {
            throw new ValidateException("Booking status is not WAITING.");
        }

        // Update booking status based on 'approved' parameter
        booking.setStatus(approved ? Booking.BookingStatus.APPROVED : Booking.BookingStatus.REJECTED);

        Booking updatedBooking = bookingRepository.save(booking); // Save the updated booking
        return bookingMapper.mapToDto(updatedBooking);
    }
    @Override
    public List<BookingDto> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(bookingMapper::mapToDto)
                .collect(Collectors.toList());
    }
}