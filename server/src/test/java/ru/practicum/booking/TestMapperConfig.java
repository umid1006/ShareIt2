package ru.practicum.booking;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.practicum.user.UserMapper;
import ru.practicum.user.UserMapperImpl;

@TestConfiguration
public class TestMapperConfig {

    @Bean
    public UserMapper userMapper() {
        return new UserMapperImpl();
    }

    @Bean
    public BookingMapper bookingMapper(UserMapper userMapper) {
        return new BookingMapperImpl();
    }
}