package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingRetrieveDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingState;
import ru.practicum.shareit.exception.UnsupportedBookingStateFilterException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingServiceImpl bookingService;

    private final BookingMapper mapper;

    public BookingController(BookingServiceImpl bookingService, BookingMapper mapper) {
        this.bookingService = bookingService;
        this.mapper = mapper;
    }

    @PostMapping
    public BookingRetrieveDto createBooking(@RequestBody @Valid BookingCreateDto newBookingDto,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received POST at /bookings: {}", newBookingDto);
        Booking booking = mapper.mapToBooking(newBookingDto, userId);
        BookingRetrieveDto bookingDto = mapper.mapToDto(bookingService.createBooking(booking));
        log.info("Created booking: {}", bookingDto);
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingRetrieveDto getBooking(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                         @PathVariable long bookingId) {
        log.info("Received GET at /bookings/{}", bookingId);
        BookingRetrieveDto bookingDto = mapper.mapToDto(bookingService.getBookingById(bookingId, bookerId));
        log.info("Got booking: {}", bookingDto);
        return bookingDto;
    }

    @GetMapping
    public List<BookingRetrieveDto> getAllBookingByUser(@RequestParam(defaultValue = "ALL") String state,
                                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Received GET at /bookings?state={}", state);
        BookingState bookingState = convertToFilter(state);
        List<BookingRetrieveDto> bookingDtoList = mapper.mapToDto(bookingService.getBookingsByBooker(userId,
                bookingState));
        log.info("Got bookings: {}", bookingDtoList);
        return bookingDtoList;
    }

    @GetMapping("/owner")
    public List<BookingRetrieveDto> getAllUserBookings(@RequestParam(defaultValue = "ALL") String state,
                                                    @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Received GET at /bookings");
        BookingState bookingState = convertToFilter(state);
        List<BookingRetrieveDto> bookingDtoList = mapper.mapToDto(bookingService.getBookingsByOwner(ownerId,
                bookingState));
        log.info("Got bookings: {}", bookingDtoList);
        return bookingDtoList;
    }

    @PatchMapping("/{bookingId}")
    public BookingRetrieveDto approveBooking(@PathVariable long bookingId, @RequestParam boolean approved,
                                     @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Received PATCH at /bookings/{}, approved: {}, from owner: {}", bookingId, approved, ownerId);
        BookingRetrieveDto bookingDto = mapper.mapToDto(bookingService.approveBooking(bookingId, ownerId, approved));
        log.info("Approved booking: {}", bookingDto);
        return bookingDto;
    }

    private BookingState convertToFilter(final String state) {
        return Arrays.stream(BookingState.values())
                .filter(value -> Objects.equals(value.name(), state.toUpperCase()))
                .findAny()
                .orElseThrow(
                        () -> new UnsupportedBookingStateFilterException(state)
                );
    }
}
