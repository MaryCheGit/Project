package com.example.shop.entity;

import com.example.shop.enums.ReadingStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_library")
public class UserLibrary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== СВЯЗЬ С ПОЛЬЗОВАТЕЛЕМ ==========
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ========== СВЯЗЬ С КНИГОЙ (может быть null, если это фильм) ==========
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id")
    private Book book;

    // ========== СВЯЗЬ С ФИЛЬМОМ (может быть null, если это книга) ==========
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    // ========== СТАТУС ==========
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatus status;

    private LocalDateTime addedAt = LocalDateTime.now();

    // ========== КОНСТРУКТОРЫ ==========

    public UserLibrary() {
    }

    public UserLibrary(User user, Book book, Movie movie, ReadingStatus status) {
        this.user = user;
        this.book = book;
        this.movie = movie;
        this.status = status;
    }

    // ========== ГЕТТЕРЫ И СЕТТЕРЫ ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }

    public ReadingStatus getStatus() { return status; }
    public void setStatus(ReadingStatus status) { this.status = status; }

    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }

    @Override
    public String toString() {
        return "UserLibrary{" +
                "id=" + id +
                ", status=" + status +
                ", addedAt=" + addedAt +
                '}';
    }
}
