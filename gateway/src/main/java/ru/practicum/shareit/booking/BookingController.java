package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> bookItem(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                           @Valid @RequestBody BookItemRequestDto requestDto) {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        if (requestDto.getStart().isBefore(now) || (!requestDto.getEnd().isAfter(requestDto.getStart()))) {
            throw new ValidationException("Start time booking must be before end time and both times don't be " +
                    "in the past");
        }
        log.info("Request time {}", now);
        log.info("Start time {}, end time {}", requestDto.getStart(), requestDto.getEnd());
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@Positive @RequestHeader("X-Sharer-User-Id") final long userId,
                                             @Positive @PathVariable long bookingId) {
        log.info("Responded to GET /bookings/{}", bookingId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String state) {
        log.info("Responded to GET /bookings?state={}&userId={}", state, userId);
        return bookingClient.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOfOwner(@Positive @RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(name = "state", defaultValue = "all")
                                                     String state) {
        log.info("Responded to GET /bookings/owner?state={}", state);
        return bookingClient.getOwnerBookings(userId, state);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeStatus(@Positive @PathVariable("bookingId") long id,
                                               @RequestParam boolean approved,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Change status booking id: {}, approved: {}, userId: {}", id, approved, userId);
        return bookingClient.changeStatus(id, approved, userId);
    }
}