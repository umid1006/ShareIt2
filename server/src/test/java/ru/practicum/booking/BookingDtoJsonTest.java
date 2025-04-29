package ru.practicum.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.dto.BookingDto;
import ru.practicum.util.BookingStatus;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws IOException {
        BookingDto dto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 1, 1, 10, 0))
                .end(LocalDateTime.of(2023, 1, 2, 10, 0))
                .status(BookingStatus.APPROVED)
                .build();

        JsonContent<BookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-01-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }

    @Test
    void testDeserialize() throws IOException {
        String content = "{\"id\":1,\"start\":\"2023-01-01T10:00:00\",\"end\":\"2023-01-02T10:00:00\",\"status\":\"APPROVED\"}";

        BookingDto result = json.parseObject(content);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2023, 1, 1, 10, 0));
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }
}