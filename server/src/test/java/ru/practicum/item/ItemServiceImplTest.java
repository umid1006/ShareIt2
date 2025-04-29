package ru.practicum.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.Booking;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.ItemDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidateException;
import ru.practicum.user.User;
import ru.practicum.util.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(ItemServiceImpl.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private TestEntityManager entityManager;

    private User owner;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        // Create and persist test owner
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        entityManager.persist(owner);

        // Create and persist test items
        item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(owner);
        entityManager.persist(item1);

        item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setOwner(owner);
        entityManager.persist(item2);

        entityManager.flush();
    }

    @Test
    void getItemsByOwner_shouldReturnAllItemsForUser() {
        List<ItemDto> result = itemService.getItemsByOwner(owner.getId());

        assertEquals(2, result.size());
        assertEquals("Item 1", result.get(0).getName());
        assertEquals("Item 2", result.get(1).getName());
    }

    @Test
    void add_shouldCreateNewItem() {
        ItemDto newItemDto = new ItemDto();
        newItemDto.setName("New Item");
        newItemDto.setDescription("New Description");
        newItemDto.setAvailable(true);

        ItemDto result = itemService.add(newItemDto, owner.getId());

        assertNotNull(result.getId());
        assertEquals("New Item", result.getName());
        assertEquals("New Description", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void add_shouldThrowExceptionWhenOwnerNotFound() {
        ItemDto newItemDto = new ItemDto();
        newItemDto.setName("New Item");
        newItemDto.setDescription("New Description");
        newItemDto.setAvailable(true);

        assertThrows(NotFoundException.class, () -> itemService.add(newItemDto, 999L));
    }

    @Test
    void updateInStorage_shouldUpdateExistingItem() {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Name");
        updateDto.setDescription("Updated Description");
        updateDto.setAvailable(false);

        ItemDto result = itemService.updateInStorage(updateDto, owner.getId(), item1.getId());

        assertEquals(item1.getId(), result.getId());
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertFalse(result.getAvailable());
    }

    @Test
    void updateInStorage_shouldThrowExceptionWhenItemNotFound() {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Name");

        assertThrows(NotFoundException.class,
                () -> itemService.updateInStorage(updateDto, owner.getId(), 999L));
    }

    @Test
    void updateInStorage_shouldThrowExceptionWhenOwnerNotMatch() {
        User anotherUser = new User();
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@example.com");
        entityManager.persist(anotherUser);

        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Name");

        assertThrows(NotFoundException.class,
                () -> itemService.updateInStorage(updateDto, anotherUser.getId(), item1.getId()));
    }

    @Test
    void getItemById_shouldReturnItemWithComments() {
        // Add a comment to item1
        User commentAuthor = new User();
        commentAuthor.setName("Comment Author");
        commentAuthor.setEmail("author@example.com");
        entityManager.persist(commentAuthor);

        Comment comment = new Comment();
        comment.setText("Test comment");
        comment.setItem(item1);
        comment.setAuthor(commentAuthor);
        comment.setCreated(LocalDateTime.now());
        entityManager.persist(comment);

        ItemDto result = itemService.getItemById(item1.getId());

        assertEquals(item1.getId(), result.getId());
        assertEquals(1, result.getComments().size());
        assertEquals("Test comment", result.getComments().getFirst().getText());
    }

    @Test
    void removeItemById_shouldDeleteItem() {
        itemService.removeItemById(item1.getId());

        assertFalse(itemService.isExcludeItemById(item1.getId()));
    }

    @Test
    void searchItemsByText_shouldReturnMatchingItems() {
        List<ItemDto> result = itemService.searchItemsByText("item 1");

        assertEquals(1, result.size());
        assertEquals("Item 1", result.getFirst().getName());
    }

    @Test
    void searchItemsByText_shouldReturnEmptyListForBlankText() {
        List<ItemDto> result = itemService.searchItemsByText(" ");

        assertTrue(result.isEmpty());
    }

    @Test
    void addComment_shouldCreateNewComment() {
        // Setup booking to satisfy validation
        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        entityManager.persist(booker);

        Booking booking = new Booking();
        booking.setItem(item1);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        entityManager.persist(booking);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        CommentDto result = itemService.addComment(item1.getId(), booker.getId(), commentDto);

        assertNotNull(result.getId());
        assertEquals("Great item!", result.getText());
        assertEquals("Booker", result.getAuthorName());
    }

    @Test
    void addComment_shouldThrowExceptionWhenUserNeverBookedItem() {
        User nonBooker = new User();
        nonBooker.setName("Non-Booker");
        nonBooker.setEmail("nonbooker@example.com");
        entityManager.persist(nonBooker);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        assertThrows(ValidateException.class,
                () -> itemService.addComment(item1.getId(), nonBooker.getId(), commentDto));
    }
}