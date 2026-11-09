package com.example.shop.repository;

import com.example.shop.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Найти книги по категории
    List<Book> findByCategoryId(Long categoryId);

    // Найти книги по автору (содержит подстроку)
    List<Book> findByAuthorContainingIgnoreCase(String author);

    // Найти книги по названию (содержит подстроку)
    List<Book> findByTitleContainingIgnoreCase(String title);

    // Найти книги с рейтингом выше указанного
    List<Book> findByAverageRatingGreaterThan(Double rating);
}
