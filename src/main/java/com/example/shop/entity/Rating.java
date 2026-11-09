package com.example.shop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== СВЯЗЬ С ПОЛЬЗОВАТЕЛЕМ ==========
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ========== СВЯЗЬ С КНИГОЙ (может быть null, если оценивают фильм) ==========
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id")
    private Book book;

    // ========== СВЯЗЬ С ФИЛЬМОМ (может быть null, если оценивают книгу) ==========
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    // ========== КРИТЕРИИ ОЦЕНКИ (1-5) ==========
    @Column(nullable = false)
    private Integer plotScore;         // Оценка за сюжет

    @Column(nullable = false)
    private Integer characterScore;    // Оценка за персонажей

    @Column(nullable = false)
    private Integer overallScore;      // Оценка за общее впечатление

    // ========== СРЕДНЯЯ ОЦЕНКА (вычисляется автоматически) ==========
    @Column(nullable = false)
    private Double averageScore;

    @Column(length = 2000)
    private String comment;

    private LocalDateTime createdAt = LocalDateTime.now();

    // ========== КОНСТРУКТОРЫ ==========

    public Rating() {
    }

    public Rating(User user, Book book, Movie movie,
                  Integer plotScore, Integer characterScore, Integer overallScore) {
        this.user = user;
        this.book = book;
        this.movie = movie;
        this.plotScore = plotScore;
        this.characterScore = characterScore;
        this.overallScore = overallScore;
        this.averageScore = (plotScore + characterScore + overallScore) / 3.0;
    }

    // ========== МЕТОД ДЛЯ ПЕРЕСЧЕТА СРЕДНЕЙ ОЦЕНКИ ==========
    @PrePersist
    @PreUpdate
    public void calculateAverage() {
        if (plotScore != null && characterScore != null && overallScore != null) {
            this.averageScore = (plotScore + characterScore + overallScore) / 3.0;
        }
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

    public Integer getPlotScore() { return plotScore; }
    public void setPlotScore(Integer plotScore) { this.plotScore = plotScore; }

    public Integer getCharacterScore() { return characterScore; }
    public void setCharacterScore(Integer characterScore) { this.characterScore = characterScore; }

    public Integer getOverallScore() { return overallScore; }
    public void setOverallScore(Integer overallScore) { this.overallScore = overallScore; }

    public Double getAverageScore() { return averageScore; }
    public void setAverageScore(Double averageScore) { this.averageScore = averageScore; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Rating{" +
                "id=" + id +
                ", plotScore=" + plotScore +
                ", characterScore=" + characterScore +
                ", overallScore=" + overallScore +
                ", averageScore=" + averageScore +
                '}';
    }
}
