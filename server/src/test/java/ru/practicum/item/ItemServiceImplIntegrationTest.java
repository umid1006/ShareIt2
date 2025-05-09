package ru.practicum.item;

import jakarta.validation.ValidationException;
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
import java.util.Optional;

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
    void add_shouldThrowExceptionWhenUserNotFound() {
        // Подготовка несуществующего ID пользователя
        Long nonExistentUserId = 999L;

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        // Проверка исключения
        assertThrows(NotFoundException.class, () -> {
            itemService.add(itemDto, nonExistentUserId);
        });
    }

    @Test
    void add_shouldThrowExceptionWhenItemDtoIsNull() {
        User owner = new User();
        owner.setName("Test Owner");
        owner.setEmail("owner@test.com");
        owner = userRepository.save(owner);

        User finalOwner = owner;
        assertThrows(IllegalArgumentException.class, () -> {
            itemService.add(null, finalOwner.getId());
        });
    }

    @Test
    void add_shouldThrowExceptionWhenRequiredFieldsAreMissing() {
        User owner = new User();
        owner.setName("Test Owner");
        owner.setEmail("owner@test.com");
        owner = userRepository.save(owner);

        // Тест 1: Отсутствует имя
        ItemDto noNameDto = new ItemDto();
        noNameDto.setDescription("Test Description");
        noNameDto.setAvailable(true);
        User finalOwner = owner;
        assertThrows(ValidationException.class, () -> {
            itemService.add(noNameDto, finalOwner.getId());
        });

        // Тест 2: Отсутствует описание available
        ItemDto noAvailableDto = new ItemDto();
        noAvailableDto.setName("Test Item");
        noAvailableDto.setDescription("Test Description");
        User finalOwner1 = owner;
        assertThrows(ValidationException.class, () -> {
            itemService.add(noAvailableDto, finalOwner1.getId());
        });
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