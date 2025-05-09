// ItemRequestServiceImplIT.java (Integration Tests)
package ru.practicum.itemrequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.Item;
import ru.practicum.item.ItemRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;
import ru.practicum.validation.ValidationService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({ItemRequestServiceImpl.class, ValidationService.class})
class ItemRequestServiceImplTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user1;
    private User user2;
    private ItemRequest request1;
    private ItemRequest request2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setName("User 1");
        user1.setEmail("user1@example.com");
        userRepository.save(user1);

        user2 = new User();
        user2.setName("User 2");
        user2.setEmail("user2@example.com");
        userRepository.save(user2);

        request1 = new ItemRequest();
        request1.setDescription("Need item 1");
        request1.setRequester(user1);
        request1.setCreated(LocalDateTime.now().minusDays(1));
        itemRequestRepository.save(request1);

        request2 = new ItemRequest();
        request2.setDescription("Need item 2");
        request2.setRequester(user1);
        request2.setCreated(LocalDateTime.now());
        itemRequestRepository.save(request2);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user2);
        item.setRequest(request1);
        itemRepository.save(item);
    }

    @Test
    void create_shouldSaveNewItemRequest() {
        ItemRequest newRequest = new ItemRequest();
        newRequest.setDescription("New request description");

        ItemRequest savedRequest = itemRequestService.create(newRequest, user1.getId());

        assertNotNull(savedRequest.getId());
        assertEquals("New request description", savedRequest.getDescription());
        assertEquals(user1.getId(), savedRequest.getRequester().getId());
        assertNotNull(savedRequest.getCreated());

        ItemRequest fromDb = em.find(ItemRequest.class, savedRequest.getId());
        assertEquals(savedRequest.getDescription(), fromDb.getDescription());
    }

    @Test
    void getById_shouldReturnRequestWhenExists() {
        ItemRequest result = itemRequestService.getById(request1.getId(), user1.getId());

        assertNotNull(result);
        assertEquals(request1.getId(), result.getId());
        assertEquals("Need item 1", result.getDescription());
    }

    @Test
    void getById_shouldThrowWhenRequestNotFound() {
        Long nonExistentId = 999L;

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getById(nonExistentId, user1.getId()));
    }

    @Test
    void getAllByUser_shouldReturnUserRequests() {
        List<ItemRequest> result = itemRequestService.getAllByUser(user1.getId());

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> r.getId().equals(request1.getId())));
        assertTrue(result.stream().anyMatch(r -> r.getId().equals(request2.getId())));
    }

    @Test
    void getAllByUser_shouldReturnEmptyListForUserWithoutRequests() {
        List<ItemRequest> result = itemRequestService.getAllByUser(user2.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void getAll_shouldReturnPaginatedRequestsExcludingUserOwn() {
        List<ItemRequest> result = itemRequestService.getAll(0, 10, user2.getId());

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> r.getId().equals(request1.getId())));
        assertTrue(result.stream().anyMatch(r -> r.getId().equals(request2.getId())));
    }

    @Test
    void getAll_shouldReturnEmptyListWhenNoOtherUsersRequests() {
        // Создаем запрос от user2
        ItemRequest request3 = new ItemRequest();
        request3.setDescription("User2 request");
        request3.setRequester(user2);
        request3.setCreated(LocalDateTime.now());
        itemRequestRepository.save(request3);

        // Запрашиваем для user1 - должен вернуться только request3
        List<ItemRequest> result = itemRequestService.getAll(0, 10, user1.getId());

        assertEquals(1, result.size());
        assertEquals(request3.getId(), result.get(0).getId());
    }
}