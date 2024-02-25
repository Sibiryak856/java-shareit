package ru.practicum.shareit.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@Slf4j
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService requestService;

    @Autowired
    public ItemRequestController(ItemRequestService requestService) {
        this.requestService = requestService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @Valid @RequestBody ItemRequestCreateDto request) {
        log.info("Request received: POST /requests: {}", request);
        ItemRequestDto createdRequest = requestService.create(request, userId);
        log.info("Request POST /requests processed: request={} is created", createdRequest);
        return createdRequest;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll(@RequestParam @Min(0) int from,
                                        @RequestParam int size) {
        log.info("Request received: GET /requests/all");
        List<ItemRequestDto> requests = requestService.findAll();
        log.info("Request GET /requests/all processed: {}", requests);
        return requests;
    }

    @GetMapping
    public List<ItemRequestDto> findAllByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Request received: GET /requests");
        List<ItemRequestDto> requests = requestService.findAllByUser(userId);
        log.info("Request GET /requests processed: {}", requests);
        return requests;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@PathVariable Long requestId) {
        log.info("Request received: GET /requests/id={}", requestId);
        ItemRequestDto request = requestService.findById(requestId);
        log.info("Request GET /requests/id processed: {}", request);
        return request;
    }


}
