package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ITEMS", schema = "PUBLIC")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_ID")
    private Long id;

    @Column(name = "ITEM_NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "AVAILABLE")
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User owner;

    @Transient
    private Booking nextBooking;

    @Transient
    private Booking lastBooking;

    @Transient
    private List<Booking> bookings = new ArrayList<>();

    @Transient
    private List<Comment> comments = new ArrayList<>();

    public void addComment(Comment comment) {
        comments.add(comment);
    }
}
