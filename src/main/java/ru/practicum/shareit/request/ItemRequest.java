package ru.practicum.shareit.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Valid
public class ItemRequest {
    private Long id;
    @NotBlank
    private String description;
    @NotBlank
    private String requestorName;
    private LocalDateTime created;
}