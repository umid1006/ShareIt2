package ru.practicum.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.booking.BookingMapper;
import ru.practicum.dto.ItemDto;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class ItemMapperTest {

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private ItemMapperImpl itemMapper;

    @Test
    void testItemMapping() {
        ItemDto dto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .ownerId(1L)
                .available(true)
                .requestId(10L)
                .build();

        Item item = itemMapper.mapToModel(dto);

        assertThat(item.getName()).isEqualTo(dto.getName());
        assertThat(item.getDescription()).isEqualTo(dto.getDescription());
        assertThat(item.getOwner().getId()).isEqualTo(dto.getOwnerId());
        assertThat(item.getRequest().getId()).isEqualTo(dto.getRequestId());

        ItemDto mappedBack = itemMapper.mapToDto(item);

        assertThat(mappedBack.getName()).isEqualTo(dto.getName());
        assertThat(mappedBack.getOwnerId()).isEqualTo(dto.getOwnerId());
    }
}