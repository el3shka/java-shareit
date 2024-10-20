package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.exception.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.service.BookingStatus.APPROVED;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingMapper mapper;

    @Override
    public Booking createBooking(Long bookerId, BookingDto bookingDto) {
        if (bookerId == null || bookingDto == null) {
            throw new ValidationException("User ID or booking cannot be null");
        }

        Long itemId = bookingDto.getItemId();
        Item item = itemService.getItem(itemId);
        if (itemId == null) {
            throw new NotFoundException("Item must be provided.");
        }

        Booking booking = mapper.toBooking(bookingDto, itemService);
        booking.setBooker(userService.getUser(bookerId));
        booking.setItem(item);

        Long ownerId = item.getOwner().getId();
        if (ownerId.equals(bookerId)) {
            throw new ValidationException("Owner cannot book their own item.");
        }
        if (!itemService.isItemAvailable(itemId, booking.getStart(), booking.getEnd())) {
            throw new ValidationException("Item is not available for the selected dates.");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available.");
        }
        booking.setOwner(item.getOwner());

        LocalDateTime now = LocalDateTime.now();
        if (booking.getStart().isBefore(now) || booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("Invalid booking dates: 'start' must be in the future and 'end' " +
                    "must be after 'start'.");
        }

        booking.setStatus(BookingStatus.WAITING);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found"));
    }

    @Override
    public void deleteBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    @Override
    public Booking updateBooking(Booking booking, Long bookingId) {
        Booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (booking.getStart() != null) {
            if (booking.getStart().isBefore(LocalDateTime.now())) {
                throw new ValidationException("Start date must be in the future.");
            }
            existingBooking.setStart(booking.getStart());
        }

        if (booking.getEnd() != null) {
            if (booking.getEnd().isBefore(existingBooking.getStart())) {
                throw new ValidationException("End date must be after the start date.");
            }
            existingBooking.setEnd(booking.getEnd());
        }

        if (booking.getStatus() != null) {
            existingBooking.setStatus(booking.getStatus());
        }

        return bookingRepository.save(existingBooking);
    }

    @Override
    public List<Booking> getBookingsByBooker(Long userId, BookingState state) {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        return switch (state) {
            case CURRENT -> bookingRepository.findByBookerIdAndStatus(userId, APPROVED, sort);
            case PAST -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort);
            case FUTURE -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, sort);
            default -> bookingRepository.findByBookerId(userId, sort);
        };
    }

    @Override
    public List<Booking> getBookingsByOwner(Long ownerId, BookingState state) {
        if (ownerId == null) {
            throw new ValidationException("Owner ID cannot be null");
        }

        User owner = userService.getUser(ownerId);

        List<Booking> bookings = bookingRepository.findByOwnerId(ownerId, Sort.by(Sort.Direction.ASC, "start"));

        return switch (state) {
            case ALL -> bookings;
            case CURRENT -> {
                LocalDateTime now = LocalDateTime.now();
                yield bookings.stream()
                        .filter(booking -> booking.getStart().isBefore(now) && booking.getEnd().isAfter(now))
                        .collect(Collectors.toList());
            }
            case PAST -> bookings.stream()
                    .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                    .collect(Collectors.toList());
            case FUTURE -> bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList());
            case WAITING -> bookings.stream()
                    .filter(booking -> booking.getStatus() == BookingStatus.WAITING)
                    .collect(Collectors.toList());
            case REJECTED -> bookings.stream()
                    .filter(booking -> booking.getStatus() == BookingStatus.REJECTED)
                    .collect(Collectors.toList());
            default -> throw new ValidationException("Unknown state: " + state);
        };
    }

    @Override
    public List<Booking> getBookingsByUser(Long userId, Long bookingId, BookingState state) {
        return null;
    }

    @Override
    public List<Booking> getAllBookingsByUser(Long userId, BookingState state) {
        return switch (state) {
            case WAITING -> bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED);
            case PAST -> bookingRepository.findAllByBookerIdAndEndBefore(userId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllByBookerIdAndStartAfter(userId, LocalDateTime.now());
            default -> bookingRepository.findAllByBookerId(userId);
        };
    }

    @Override
    public Booking approveBooking(Long bookingId, Long ownerId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()
                -> new NotFoundException("Booking not found"));

        if (!booking.getOwner().getId().equals(ownerId)) {
            throw new ValidationException("You are not the owner of this item");
        }

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new IllegalStateException("Booking has already been approved or rejected");
        }

        booking.setStatus(approved ? APPROVED : BookingStatus.REJECTED);

        return bookingRepository.save(booking);
    }

    @Override
    public Optional<Booking> getBookingsByBookingId(Long bookingId, BookingState state) {
        //Sort sort = Sort.by(Sort.Direction.DESC, "start");  (TO DO сортировка по дате на 16 спринт)

        Optional<Booking> booking = bookingRepository.findById(bookingId);

        return switch (state) {
            case CURRENT -> booking.filter(b -> b.getStatus() == APPROVED);
            case PAST -> booking.filter(b -> b.getStatus() == BookingStatus.REJECTED);
            case FUTURE -> booking.filter(b -> b.getStatus() == BookingStatus.WAITING);
            default -> booking;
        };
    }
}
