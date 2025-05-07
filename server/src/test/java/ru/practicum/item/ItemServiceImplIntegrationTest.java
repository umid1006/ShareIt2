package ru.practicum.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import ru.practicum.booking.Booking;
import ru.practicum.booking.BookingMapper;
import ru.practicum.booking.BookingRepository;
import ru.practicum.user.UserRepository;
import ru.practicum.user.User;
import ru.practicum.util.BookingStatus;
import ru.practicum.validation.ValidationService;
import ru.practicum.dto.ItemDto;
import ru.practicum.dto.CommentDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidateException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({ItemServiceImpl.class, ValidationService.class})
class ItemServiceImplIntegrationTest {

    @MockBean
    private BookingMapper bookingMapper;

    @MockBean
    private ItemMapper itemMapper;

    @MockBean
    private CommentMapper commentMapper;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User createTestUser(String name, String email) {
        User user = new User(null, name, email);
        em.persist(user);
        return user;
    }

    private Item createTestItem(User owner) {
        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);
        return item;
    }

    private Booking createTestBooking(Item item, User booker,
                                      LocalDateTime start, LocalDateTime end,
                                      BookingStatus status) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(status);
        return em.persist(booking);
    }

    @Test
    void updateInStorage_whenNotOwner_shouldThrowException() {
        User owner = createTestUser("owner", "owner@email.com");
        User notOwner = createTestUser("notOwner", "notOwner@email.com");
        Item item = createTestItem(owner);

        ItemDto updateDto = new ItemDto();
        updateDto.setName("new name");

        assertThrows(NotFoundException.class, () ->
                itemService.updateInStorage(updateDto, notOwner.getId(), item.getId()));
    }

    @Test
    void removeItemById_shouldDeleteItem() {
        User owner = createTestUser("owner", "owner@email.com");
        Item item = createTestItem(owner);

        itemService.removeItemById(item.getId());

        assertFalse(itemService.isExcludeItemById(item.getId()));
    }

    @Test
    void addComment_whenUserDidNotBook_shouldThrowException() {
        User owner = createTestUser("owner", "owner@email.com");
        User notBooker = createTestUser("notBooker", "notBooker@email.com");
        Item item = createTestItem(owner);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("great item!");

        assertThrows(ValidateException.class, () ->
                itemService.addComment(item.getId(), notBooker.getId(), commentDto));
    }
}