package ru.practicum.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.UserDto;
import ru.practicum.exception.NotFoundException;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserControllerServer.class)
class UserControllerServerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .build();

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
    }

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        Mockito.when(userMapper.toUser(Mockito.any(UserDto.class))).thenReturn(user);
        Mockito.when(userService.saveUser(Mockito.any(User.class))).thenReturn(user);
        Mockito.when(userMapper.toUserDto(Mockito.any(User.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UserDto updatedDto = UserDto.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        Mockito.when(userMapper.toUser(Mockito.any(UserDto.class))).thenReturn(user);
        Mockito.when(userService.updateUser(Mockito.any(User.class), Mockito.anyLong())).thenReturn(user);
        Mockito.when(userMapper.toUserDto(Mockito.any(User.class))).thenReturn(updatedDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(updatedDto.getName())))
                .andExpect(jsonPath("$.email", is(updatedDto.getEmail())));
    }


    @Test
    void getUser_shouldReturnUser() throws Exception {
        Mockito.when(userService.getUserById(Mockito.anyLong())).thenReturn(user);
        Mockito.when(userMapper.toUserDto(Mockito.any(User.class))).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void getUser_shouldReturnNotFoundWhenUserNotExist() throws Exception {
        Mockito.when(userService.getUserById(anyLong())).thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_shouldReturnUserList() throws Exception {
        // Remove unused userDtos variable since we're mocking the mapper response directly
        List<User> users = List.of(user);

        Mockito.when(userService.getAllUsers()).thenReturn(users);
        Mockito.when(userMapper.toUserDto(Mockito.any(User.class))).thenReturn(userDto);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).deleteUser(1L);
    }
}