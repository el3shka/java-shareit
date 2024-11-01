package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.exception.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    private final ItemService itemService;
    private final UserService userService;

    @Override
    public Booking createBooking(Booking booking) {
        Objects.requireNonNull(booking, "Cannot create booking: is null");
        Objects.requireNonNull(booking.getItem(), "Cannot create booking: booking.item is null");
        Objects.requireNonNull(booking.getItem().getId(), "Cannot create booking: booking.item.id is null");
        Objects.requireNonNull(booking.getBooker(), "Cannot create booking: booking.booker is null");
        Objects.requireNonNull(booking.getBooker().getId(), "Cannot create booking: booking.booker.id is null");
        final LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        if (booking.getStart().isBefore(now)) {
            throw new ValidationException("start cannot be in past");
        }
        if (!booking.getEnd().isAfter(booking.getStart())) {
            throw new ValidationException("end should be after start");
        }
        userService.getUser(booking.getBooker().getId());
        final Item item = itemService.getItemToBook(booking.getItem().getId(), booking.getBooker().getId());
        if (!item.getAvailable()) {
            throw new ValidationException("item is unavailable item");
        }
        booking.getItem().setName(item.getName());
        final Booking createdBooking = bookingRepository.save(booking);
        log.info("Created booking with id = {}: {}", createdBooking.getId(), createdBooking);
        return createdBooking;
    }

    @Override
    public Booking getBookingById(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return booking;
        } else {
            throw new AccessErrorException("Only the owner of the item or the booker can get booking");
        }
    }

    @Override
    public List<Booking> getBookingsByBooker(long userId, BookingState state) {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        LocalDateTime now = LocalDateTime.now();
        return switch (state) {
            case ALL -> bookingRepository.findByBookerId(userId, sort);
            case CURRENT -> bookingRepository.findCurrentByBookerId(userId, sort);
            case PAST -> bookingRepository.findPastByBookerId(userId, sort);
            case FUTURE -> bookingRepository.findFutureByBookerId(userId, sort);
            case WAITING -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, sort);
            case REJECTED -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort);
            default -> throw new ValidationException("Unknown state: " + state);
        };
    }

    @Override
    public List<Booking> getBookingsByOwner(long ownerId, BookingState state) {
        if (!itemService.existByOwnerId(ownerId)) {
            throw new AccessErrorException("Only the owner of the item can get bookings");
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        return switch (state) {
            case ALL -> bookingRepository.findAllByItemOwnerId(ownerId, sort);
            case CURRENT -> bookingRepository.findCurrentByItemOwnerId(ownerId, sort);
            case PAST -> bookingRepository.findPastByItemOwnerId(ownerId, sort);
            case FUTURE -> bookingRepository.findFutureByItemOwnerId(ownerId, sort);
            case WAITING -> bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING, sort);
            case REJECTED -> bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, sort);
            default -> throw new ValidationException("Unknown state: " + state);
        };
    }

    @Override
    public Booking changeStatus(long bookingId, long ownerId, Boolean approved) {
        if (approved == null) {
            throw new ValidationException("Approved status must be provided");
        }
        Booking booking = bookingRepository
                .findById(bookingId).orElseThrow(() -> new NotFoundException("Booking with id " + bookingId +
                        " not found"));
        if (!booking.getItem().getOwner().getId()
                .equals(ownerId)) {
            throw new AccessErrorException("Only the owner of the item can change booking status");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> findAllCompleteBookingByUserIdAndItemId(long userId, long itemId) {
        return bookingRepository.findAllCompleteBookingByBookerIdAndItemId(userId, itemId);
    }

    @Override
    public void deleteBooking(long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    @Override
    public Booking updateBooking(Booking booking, long bookingId) {
        Booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
            existingBooking.setStart(booking.getStart());
            existingBooking.setEnd(booking.getEnd());
        if (booking.getStatus() != null) {
            existingBooking.setStatus(booking.getStatus());
        } else {
            existingBooking.setStatus(BookingStatus.WAITING);
        }
        return bookingRepository.save(existingBooking);
    }

    @Override
    public Booking approveBooking(long bookingId, long ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()
                -> new NotFoundException("Booking not found"));

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new IllegalStateException("Booking has already been approved or rejected");
        }

        if (!Objects.equals(booking.getItem().getOwner().getId(), ownerId)) {
            throw new AccessErrorException("Only the owner of the item can approve booking");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return bookingRepository.save(booking);
    }



}
