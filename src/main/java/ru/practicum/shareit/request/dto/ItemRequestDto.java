package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@AllArgsConstructor
@Valid
public class ItemRequestDto {
    @NotBlank
    private String description;
    @NotBlank
    private String requestorName;
    private LocalDateTime created;
}
