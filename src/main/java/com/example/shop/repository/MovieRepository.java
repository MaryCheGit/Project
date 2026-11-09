package com.example.shop.repository;

import com.example.shop.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    // Найти все фильмы по книге (экранизации)
    List<Movie> findByBookId(Long bookId);

    // Найти фильмы по режиссеру
    List<Movie> findByDirectorContainingIgnoreCase(String director);

    // Найти фильмы по названию
    List<Movie> findByTitleContainingIgnoreCase(String title);
}
