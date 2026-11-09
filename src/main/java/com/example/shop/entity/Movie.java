package com.example.shop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String director;

    private Integer year;

    @Column(length = 2000)
    private String description;

    private String posterUrl;

    private String downloadUrl;

    private String watchOnlineUrl;

    private String fileFormat;

    private Long fileSize;

    private Double averageRating = 0.0;

    private Integer ratingCount = 0;

    // ========== СВЯЗЬ С КНИГОЙ (ManyToOne) ==========
    // Много фильмов могут быть экранизациями одной книги.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id")
    private Book book;

    private LocalDateTime createdAt = LocalDateTime.now();

    // ========== КОНСТРУКТОРЫ ==========

    public Movie() {
    }

    public Movie(String title, String director, Integer year, String description, Book book) {
        this.title = title;
        this.director = director;
        this.year = year;
        this.description = description;
        this.book = book;
    }

    // ========== ГЕТТЕРЫ И СЕТТЕРЫ ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

    public String getWatchOnlineUrl() { return watchOnlineUrl; }
    public void setWatchOnlineUrl(String watchOnlineUrl) { this.watchOnlineUrl = watchOnlineUrl; }

    public String getFileFormat() { return fileFormat; }
    public void setFileFormat(String fileFormat) { this.fileFormat = fileFormat; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Integer getRatingCount() { return ratingCount; }
    public void setRatingCount(Integer ratingCount) { this.ratingCount = ratingCount; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", director='" + director + '\'' +
                ", year=" + year +
                '}';
    }
}
