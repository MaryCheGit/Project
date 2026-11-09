package com.example.shop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(length = 2000)
    private String description;

    private String publisher;

    private Integer year;

    @Column(unique = true)
    private String isbn;

    private Integer pages;

    private String genre;

    private String coverUrl;

    private String downloadUrl;

    private String readOnlineUrl;

    private String fileFormat;

    private Long fileSize;

    // Средний рейтинг и количество оценок (вычисляется)
    private Double averageRating = 0.0;

    private Integer ratingCount = 0;

    // Связь с категорией
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    private LocalDateTime createdAt = LocalDateTime.now();

    // ========== КОНСТРУКТОРЫ ==========

    public Book() {
    }

    public Book(String title, String author, String description, String publisher,
                Integer year, Integer pages, String genre, Category category) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.publisher = publisher;
        this.year = year;
        this.pages = pages;
        this.genre = genre;
        this.category = category;
    }

    // ========== ГЕТТЕРЫ И СЕТТЕРЫ ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public Integer getPages() { return pages; }
    public void setPages(Integer pages) { this.pages = pages; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

    public String getReadOnlineUrl() { return readOnlineUrl; }
    public void setReadOnlineUrl(String readOnlineUrl) { this.readOnlineUrl = readOnlineUrl; }

    public String getFileFormat() { return fileFormat; }
    public void setFileFormat(String fileFormat) { this.fileFormat = fileFormat; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Integer getRatingCount() { return ratingCount; }
    public void setRatingCount(Integer ratingCount) { this.ratingCount = ratingCount; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", averageRating=" + averageRating +
                '}';
    }
}

