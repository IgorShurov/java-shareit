package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestListDto;
import ru.practicum.shareit.request.dto.RequestDtoResponseWithMD;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/requests")
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDtoResponse createRequest(@RequestHeader(userIdHeader) @Min(1) Long requesterId,
                                                @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestService.createItemRequest(itemRequestDto, requesterId);
    }

    @GetMapping
    public ItemRequestListDto getPrivateRequests(
            @RequestHeader(userIdHeader) @Min(1) Long requesterId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        return itemRequestService
                .getPrivateRequests(PageRequest.of(from / size, size)
                        .withSort(Sort.by("created").descending()), requesterId);
    }

    @GetMapping("all")
    public ItemRequestListDto getOtherRequests(
            @RequestHeader(userIdHeader) @Min(1) Long requesterId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        return itemRequestService.getOtherRequests(
                PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created")), requesterId);
    }

    @GetMapping("{requestId}")
    public RequestDtoResponseWithMD getItemRequest(
            @RequestHeader(userIdHeader) @Min(1) Long userId,
            @PathVariable @Min(1) Long requestId) {
        return itemRequestService.getItemRequest(userId, requestId);
    }
}