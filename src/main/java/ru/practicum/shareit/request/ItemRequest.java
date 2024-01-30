package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@Builder
public class ItemRequest {
    private Long id;
    @NotBlank
    private String description;
    @NotNull
    private User requester;
    @NotNull
    private LocalDateTime created;

}
