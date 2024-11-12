package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestWithItemInfoDto {
    private long id;
    private String description;
    private LocalDateTime created;
    private List<ItemShortDto> items;

    record ItemShortDto(long id, String name, long ownerId) {
    }
}
