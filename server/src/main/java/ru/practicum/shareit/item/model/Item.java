package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString
@Table(name = "items")
@EqualsAndHashCode(of = "id")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Size(max = 200)
    private String description;

    @NotNull
    private Boolean available;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "last_bookings",
            joinColumns = @JoinColumn(name = "item_id",updatable = false, insertable = false),
            inverseJoinColumns = @JoinColumn(name = "booking_id", updatable = false, insertable = false))
    private Booking lastBooking;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "next_bookings",
            joinColumns = @JoinColumn(name = "item_id", updatable = false, insertable = false),
            inverseJoinColumns = @JoinColumn(name = "booking_id", updatable = false, insertable = false))
    private Booking nextBooking;

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Comment> comments;

    @ManyToOne(fetch = FetchType.LAZY)
    private ItemRequest request;

    @ToString.Include
    public Long request() {
        return Optional.ofNullable(request).map(ItemRequest::getId).orElse(null);
    }
}
