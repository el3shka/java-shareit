package ru.practicum.shareit.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;
    private final BookingService bookingService;

    @Override
    public Comment addComment(final Comment comment, final long id, final long userId) {
        final List<Booking> bookings = bookingService.findAllCompleteBookingByUserIdAndItemId(userId, id);
        if (bookings.isEmpty()) {
            throw new ValidationException("id no complete bookings of item by user");
        }
        final Booking booking = bookings.getFirst();
        comment.setItem(booking.getItem());
        comment.setAuthor(booking.getBooker());
        final Comment createdComment = repository.save(comment);
        log.info("Created comment with id = {}: {}", createdComment.getId(), createdComment);
        return createdComment;
    }
}