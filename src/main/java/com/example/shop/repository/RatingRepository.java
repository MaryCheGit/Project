package com.example.shop.repository;

import com.example.shop.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByBookId(Long bookId);

    List<Rating> findByMovieId(Long movieId);

    List<Rating> findByUserId(Long userId);

    // Проверка существующей оценки
    boolean existsByUserIdAndBookId(Long userId, Long bookId);
    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    // Найти оценку пользователя для книги/фильма
    Optional<Rating> findByUserIdAndBookId(Long userId, Long bookId);
    Optional<Rating> findByUserIdAndMovieId(Long userId, Long movieId);

    @Query("SELECT AVG(r.averageScore) FROM Rating r WHERE r.book.id = :bookId")
    Double getAverageRatingForBook(@Param("bookId") Long bookId);

    @Query("SELECT AVG(r.averageScore) FROM Rating r WHERE r.movie.id = :movieId")
    Double getAverageRatingForMovie(@Param("movieId") Long movieId);

    long countByBookId(Long bookId);
    long countByMovieId(Long movieId);
}