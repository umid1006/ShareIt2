package ru.practicum.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import ru.practicum.dto.BookingDto;
import ru.practicum.item.Item;
import ru.practicum.item.ItemRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserMapper;
import ru.practicum.user.UserRepository;
import ru.practicum.util.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({BookingServiceImpl.class, BookingMapperImpl.class})
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @MockBean
    private BookingMapper bookingMapper;

    @MockBean
    private UserMapper userMapper;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void createBooking_shouldCreateNewBooking() {
        User owner = createUser("owner@example.com");
        User booker = createUser("booker@example.com");
        Item item = createAvailableItem(owner);

        BookingDto bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingDto result = bookingService.createBooking(bookingDto, booker.getId());

        assertNotNull(result.getId());
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(booker.getId(), result.getBooker().getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
    }

    @Test
    void getBookings_shouldReturnUserBookings() {
        // Create and save owner
        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@email.com");
        owner = userRepository.save(owner);
        assertNotNull(owner.getId());

// Create and save booker
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@email.com");
        booker = userRepository.save(booker);
        assertNotNull(booker.getId());


        // Create and save item - pass the User entity directly
        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);  // primitive boolean
        item.setOwner(owner);    // pass User entity, not just ID
        item.setRequest(null);
        item = itemRepository.save(item);
        assertNotNull(item.getId());

        // Create and save booking
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = new Booking(null, start, end, item, booker, BookingStatus.WAITING);
        booking = bookingRepository.save(booking);
        assertNotNull(booking.getId());

        // Verify booking exists in DB
        List<Booking> savedBookings = bookingRepository.findAll();
        assertFalse(savedBookings.isEmpty());

        // Now test the service
        List<BookingDto> bookings = bookingService.getBookings(booker.getId(), "ALL");
        assertFalse(bookings.isEmpty());  // This is failing
        assertNotNull(bookings.getFirst().getBooker());  // This is where NPE occurs
    }

    // Helper methods
    private User createUser(String email) {
        User user = new User();
        user.setName("Test User");
        user.setEmail(email);
        entityManager.persist(user);
        return user;
    }

    private Item createAvailableItem(User owner) {
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        entityManager.persist(item);
        return item;
    }

    private void createBooking(Item item, User booker) {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        entityManager.persist(booking);
    }
}