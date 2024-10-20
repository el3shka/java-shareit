package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingGivenDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingState;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingServiceImpl bookingService;
    private final BookingMapper mapper;

    @PostMapping
    public BookingGivenDto createBooking(@RequestBody BookingDto newBookingDto,
                                         @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        log.info("Received POST at /bookings: {}", newBookingDto);
        Booking booking = bookingService.createBooking(bookerId, newBookingDto);
        BookingGivenDto bookingDto = mapper.toBookingGivenDto(booking);
        log.info("Responded to POST /bookings/: {}", bookingDto);
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingGivenDto approveBooking(@PathVariable Long bookingId, @RequestParam Boolean approved,
                                     @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Received PATCH at /bookings/{}, approved: {}, from owner: {}", bookingId, approved, ownerId);
        BookingGivenDto updatedBooking = mapper.toBookingGivenDto(bookingService.approveBooking(bookingId, ownerId,
                approved));
        log.info("Responded to PATCH /bookings/{}: {}", bookingId, updatedBooking);
        return updatedBooking;
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllUserBookings(@RequestParam(defaultValue = "ALL") BookingState state,
                                                @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Received GET at /bookings");
        return mapper.toBookingDtoList(bookingService.getBookingsByOwner(ownerId, state));
    }

    @GetMapping("/{bookingId}")
    public Optional<BookingGivenDto> getAllBookingByBooker(@RequestParam(defaultValue = "ALL") BookingState state,
                                               @RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                  @PathVariable Long bookingId) {
        log.info("Received GET at /bookings/{}", bookingId);
        Optional<Booking> booking = bookingService.getBookingsByBookingId(bookingId, state);

        if (booking.isPresent()) {
            BookingGivenDto bookingDto = mapper.toBookingGivenDto(booking.get());
            log.info("Responded to GET /bookings/{}: {}", bookingId, bookingDto);
            return Optional.of(bookingDto);
        } else {
            log.warn("Booking not found for id: {}", bookingId);
            return Optional.empty();
        }
    }

    @GetMapping
    public List<BookingGivenDto> getAllBookingByUser(@RequestParam(defaultValue = "ALL") BookingState state,
                                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received GET at /bookings?state={}", state);
        List<Booking> bookings = bookingService.getAllBookingsByUser(userId, state);
        return mapper.toBookingGivenDtoList(bookings);
    }
}
