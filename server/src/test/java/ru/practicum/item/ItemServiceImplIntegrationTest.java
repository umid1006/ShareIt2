package ru.practicum.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import ru.practicum.booking.Booking;
import ru.practicum.booking.BookingMapper;
import ru.practicum.booking.BookingMapperImpl;
import ru.practicum.booking.BookingRepository;
import ru.practicum.user.UserRepository;
import ru.practicum.util.BookingStatus;
import ru.practicum.user.User;
import ru.practicum.validation.ValidationService;
import ru.practicum.dto.ItemDto;
import ru.practicum.dto.CommentDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidateException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({ItemServiceImpl.class, ValidationService.class, ItemMapperImpl.class,
        BookingMapperImpl.class, CommentMapperImpl.class})
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


    @BeforeEach
    void setUp() {
        User owner = userRepository.save(new User(null, "Owner", "owner@email.com"));
        User booker = userRepository.save(new User(null, "Booker", "booker@email.com"));

        Item item1 = itemRepository.save(new Item(null, "Item1", "Desc1", owner, true, null));
        Item item2 = itemRepository.save(new Item(null, "Item2", "Desc2", owner, true, null));

        LocalDateTime now = LocalDateTime.now();
        Booking pastBooking = bookingRepository.save(
                new Booking(null, now.minusDays(2), now.minusDays(1), item1, booker, BookingStatus.APPROVED));
        Booking futureBooking = bookingRepository.save(
                new Booking(null, now.plusDays(1), now.plusDays(2), item1, booker, BookingStatus.APPROVED));
    }


    private User createTestUser(String name, String email) {
        User user = new User(null, name, email);
        em.persist(user);
        return user;
    }

    private Item createTestItem(String name, String description, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);
        return item;
    }

    @Test
    void add_shouldSaveItem() {
        User owner = createTestUser("owner", "owner@email.com");

        ItemDto itemDto = new ItemDto();
        itemDto.setName("item");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);

        ItemDto result = itemService.add(itemDto, owner.getId());

        assertNotNull(result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(owner.getId(), result.getOwnerId());
    }

    @Test
    void updateInStorage_shouldUpdateItem() {
        User owner = createTestUser("owner", "owner@email.com");
        Item item = createTestItem("old name", "old description", owner);

        ItemDto updateDto = new ItemDto();
        updateDto.setName("new name");
        updateDto.setDescription("new description");

        ItemDto result = itemService.updateInStorage(updateDto, owner.getId(), item.getId());

        assertEquals("new name", result.getName());
        assertEquals("new description", result.getDescription());
        assertEquals(owner.getId(), result.getOwnerId());
    }

    @Test
    void updateInStorage_whenNotOwner_shouldThrowException() {
        User owner = createTestUser("owner", "owner@email.com");
        User notOwner = createTestUser("notOwner", "notOwner@email.com");
        Item item = createTestItem("item", "description", owner);

        ItemDto updateDto = new ItemDto();
        updateDto.setName("new name");

        assertThrows(NotFoundException.class, () ->
                itemService.updateInStorage(updateDto, notOwner.getId(), item.getId()));
    }

    @Test
    void getItemById_shouldReturnItemWithComments() {
        User owner = createTestUser("owner", "owner@email.com");
        Item item = createTestItem("item", "description", owner);

        User commentAuthor = createTestUser("author", "author@email.com");
        Comment comment = new Comment();
        comment.setText("comment text");
        comment.setItem(item);
        comment.setAuthor(commentAuthor);
        comment.setCreated(LocalDateTime.now());
        em.persist(comment);
        em.flush();

        ItemDto result = itemService.getItemById(item.getId());

        assertEquals(item.getId(), result.getId());
        assertEquals(1, result.getComments().size());
        assertEquals("comment text", result.getComments().getFirst().getText());
    }

    @Test
    void removeItemById_shouldDeleteItem() {
        User owner = createTestUser("owner", "owner@email.com");
        Item item = createTestItem("item", "description", owner);

        itemService.removeItemById(item.getId());

        assertFalse(itemService.isExcludeItemById(item.getId()));
    }

    @Test
    void searchItemsByText_shouldReturnMatchingItems() {
        User owner = createTestUser("owner", "owner@email.com");
        Item item1 = createTestItem("drill", "powerful tool", owner);
        Item item2 = createTestItem("hammer", "simple tool", owner);

        List<ItemDto> result = itemService.searchItemsByText("tool");

        assertEquals(2, result.size());

        result = itemService.searchItemsByText("drill");
        assertEquals(1, result.size());
        assertEquals("drill", result.getFirst().getName());
    }

    @Test
    void addComment_shouldSaveComment() {
        User owner = createTestUser("owner", "owner@email.com");
        User booker = createTestUser("booker", "booker@email.com");
        Item item = createTestItem("item", "description", owner);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        em.persist(booking);
        em.flush();

        CommentDto commentDto = new CommentDto();
        commentDto.setText("great item!");

        CommentDto result = itemService.addComment(item.getId(), booker.getId(), commentDto);

        assertNotNull(result.getId());
        assertEquals("great item!", result.getText());
        assertEquals(booker.getName(), result.getAuthorName());
    }

    @Test
    void addComment_whenUserDidNotBook_shouldThrowException() {
        User owner = createTestUser("owner", "owner@email.com");
        User notBooker = createTestUser("notBooker", "notBooker@email.com");
        Item item = createTestItem("item", "description", owner);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("great item!");

        assertThrows(ValidateException.class, () ->
                itemService.addComment(item.getId(), notBooker.getId(), commentDto));
    }

    @Test
    void getUserItems_shouldReturnItemsWithBookings() {
        // 1. Create test data
        User owner = new User(null, "owner", "owner@email.com");
        em.persist(owner);

        User booker = new User(null, "booker", "booker@email.com");
        em.persist(booker);

        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);

        // Create past booking
        Booking pastBooking = new Booking();
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(BookingStatus.APPROVED);
        em.persist(pastBooking);

        // Create future booking
        Booking futureBooking = new Booking();
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStatus(BookingStatus.APPROVED);
        em.persist(futureBooking);

        em.flush();

        // 2. Test the service method
        List<ItemDto> result = itemService.getItemsByOwner(owner.getId());

        // 3. Verify results
        assertEquals(1, result.size());
        ItemDto itemDto = result.getFirst();
        assertNotNull(itemDto.getLastBooking());
        assertNotNull(itemDto.getNextBooking());
        assertEquals(pastBooking.getId(), itemDto.getLastBooking().getId());
        assertEquals(futureBooking.getId(), itemDto.getNextBooking().getId());
    }
}