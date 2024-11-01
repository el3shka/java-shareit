package ru.practicum.shareit.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import  ru.practicum.shareit.comment.service.CommentServiceImpl;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repo.CommentRepository;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CommentServiceImplTest {

    @Mock
    private CommentRepository repository;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment comment;
    private Booking booking;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        comment = new Comment();
        comment.setText("This is a comment");

        booking = new Booking();
    }

    @Test
    void whenAddComment_withCompleteBooking_thenReturnComment() {
        long itemId = 1L;
        long userId = 1L;

        when(bookingService.findAllCompleteBookingByUserIdAndItemId(userId, itemId))
                .thenReturn(List.of(booking));

        Comment commentToSave = new Comment();
        commentToSave.setText("This is a comment");
        commentToSave.setId(1L);

        when(repository.save(any(Comment.class))).thenReturn(commentToSave);

        Comment createdComment = commentService.addComment(comment, itemId, userId);

        assertThat(createdComment).isNotNull();
        assertThat(createdComment.getText()).isEqualTo("This is a comment");
        verify(repository, times(1)).save(any(Comment.class));  // Проверяем, что метод save был вызван
    }

    @Test
    void whenAddComment_withoutCompleteBooking_thenThrowValidationException() {
        long itemId = 1L;
        long userId = 1L;
        when(bookingService.findAllCompleteBookingByUserIdAndItemId(userId, itemId))
                .thenReturn(Collections.emptyList());  // Возвращаем пустой список

        ValidationException exception = assertThrows(ValidationException.class,
                () -> commentService.addComment(comment, itemId, userId));
        assertThat(exception.getMessage()).isEqualTo("id no complete bookings of item by user");
    }

    @Test
    void whenAddComment_thenSetItemAndAuthor() {
        long itemId = 1L;
        long userId = 1L;

        when(bookingService.findAllCompleteBookingByUserIdAndItemId(userId, itemId))
                .thenReturn(List.of(booking));

        Comment createdComment = new Comment();
        createdComment.setId(1L);
        createdComment.setItem(booking.getItem());
        createdComment.setAuthor(booking.getBooker());

        when(repository.save(any(Comment.class))).thenReturn(createdComment);

        Comment result = commentService.addComment(comment, itemId, userId);

        assertThat(result.getItem()).isEqualTo(booking.getItem());
        assertThat(result.getAuthor()).isEqualTo(booking.getBooker());
    }
}

