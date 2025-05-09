package ru.practicum.itemrequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.ItemRequestDto;
import ru.practicum.dto.ItemRequestResponseDto;
import ru.practicum.dto.ItemRequestWithItemsDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.util.Constants;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @MockBean
    private ItemRequestMapper itemRequestMapper;

    private ItemRequestDto itemRequestDto;
    private ItemRequestResponseDto responseDto;
    private ItemRequestWithItemsDto withItemsDto;

    @BeforeEach
    void setUp() {
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Нужна дрель");

        responseDto = new ItemRequestResponseDto();
        responseDto.setId(1L);
        responseDto.setDescription("Нужна дрель");
        responseDto.setCreated(LocalDateTime.now());

        withItemsDto = new ItemRequestWithItemsDto();
        withItemsDto.setId(1L);
        withItemsDto.setDescription("Нужна дрель");
        withItemsDto.setCreated(LocalDateTime.now());
        withItemsDto.setItems(List.of());
    }

    @Test
    void create_shouldReturnCreatedRequest() throws Exception {
        Mockito.when(itemRequestMapper.toEntity(any(ItemRequestDto.class)))
                .thenReturn(new ItemRequest());
        Mockito.when(itemRequestService.create(any(ItemRequest.class), anyLong()))
                .thenReturn(new ItemRequest());
        Mockito.when(itemRequestMapper.toResponseDto(any(ItemRequest.class)))
                .thenReturn(responseDto);

        mockMvc.perform(
                        post("/requests")
                                .header(Constants.USER_ID_HEADER, 1L)
                                .content(mapper.writeValueAsString(itemRequestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(responseDto.getDescription())));
    }

    @Test
    void create_shouldReturnNotFoundWhenUserNotExist() throws Exception {
        Mockito.when(itemRequestMapper.toEntity(any(ItemRequestDto.class)))
                .thenReturn(new ItemRequest());
        Mockito.when(itemRequestService.create(any(ItemRequest.class), anyLong()))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(post("/requests")
                        .header(Constants.USER_ID_HEADER, 999L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))  // Close the perform() parameters here
                .andExpect(status().isNotFound());  // Then call andExpect() on the perform() result
    }

    @Test
    void getById_shouldReturnRequestWithItems() throws Exception {
        Mockito.when(itemRequestService.getById(anyLong(), anyLong()))
                .thenReturn(new ItemRequest());
        Mockito.when(itemRequestMapper.toDtoWithItems(any(ItemRequest.class)))
                .thenReturn(withItemsDto);

        mockMvc.perform(get("/requests/1")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(withItemsDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(withItemsDto.getDescription())));
    }

    @Test
    void getById_shouldReturnNotFoundWhenRequestNotExist() throws Exception {
        Mockito.when(itemRequestService.getById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Request not found"));

        mockMvc.perform(get("/requests/999")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllByUser_shouldReturnUserRequests() throws Exception {
        Mockito.when(itemRequestService.getAllByUser(anyLong()))
                .thenReturn(List.of(new ItemRequest()));
        Mockito.when(itemRequestMapper.toDtoWithItemsList(any(List.class)))
                .thenReturn(List.of(withItemsDto));

        mockMvc.perform(get("/requests")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(withItemsDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(withItemsDto.getDescription())));
    }

    @Test
    void getAllByUser_shouldReturnNotFoundWhenUserNotExist() throws Exception {
        Mockito.when(itemRequestService.getAllByUser(anyLong()))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/requests")
                        .header(Constants.USER_ID_HEADER, 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_shouldReturnAllRequests() throws Exception {
        Mockito.when(itemRequestService.getAll(anyInt(), anyInt(), anyLong()))
                .thenReturn(List.of(new ItemRequest()));
        Mockito.when(itemRequestMapper.toDtoWithItemsList(any(List.class)))
                .thenReturn(List.of(withItemsDto));

        mockMvc.perform(get("/requests/all")
                        .header(Constants.USER_ID_HEADER, 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(withItemsDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(withItemsDto.getDescription())));
    }

    @Test
    void getAll_shouldUseDefaultPagination() throws Exception {
        // Correct - using matchers for all parameters
        Mockito.when(itemRequestService.getAll(anyInt(), anyInt(), anyLong()))
                .thenReturn(List.of(new ItemRequest()));
        Mockito.when(itemRequestMapper.toDtoWithItemsList(any(List.class)))
                .thenReturn(List.of(withItemsDto));

        mockMvc.perform(get("/requests/all")
                        .header(Constants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getAll_shouldReturnNotFoundWhenUserNotExist() throws Exception {
        Mockito.when(itemRequestService.getAll(anyInt(), anyInt(), anyLong()))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/requests/all")
                        .header(Constants.USER_ID_HEADER, 999L))
                .andExpect(status().isNotFound());
    }
}