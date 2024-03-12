package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestCreateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestBody @Valid RequestCreateDto requestDto) {
        log.info("Creating request {}, userId={}", requestDto, userId);
        return requestClient.create(userId, requestDto);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) int from,
            @Positive @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        log.info("Get all requests, userId={}, from={}, size={}", userId, from, size);
        return requestClient.findAll(userId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get all by userId={}", userId);
        return requestClient.findAllByUser(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable Long requestId) {
        log.info("Get requestId={}, userId={}", requestId, userId);
        return requestClient.findRequest(userId, requestId);
    }
}
