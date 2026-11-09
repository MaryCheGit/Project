package com.example.shop.controller;

import com.example.shop.entity.Book;
import com.example.shop.entity.Movie;
import com.example.shop.entity.Rating;
import com.example.shop.entity.User;
import com.example.shop.repository.BookRepository;
import com.example.shop.repository.MovieRepository;
import com.example.shop.repository.RatingRepository;
import com.example.shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MovieRepository movieRepository;

    // Все оценки книги
    @GetMapping("/book/{bookId}")
    public List<Rating> getRatingsForBook(@PathVariable Long bookId) {
        return ratingRepository.findByBookId(bookId);
    }

    // Все оценки фильма
    @GetMapping("/movie/{movieId}")
    public List<Rating> getRatingsForMovie(@PathVariable Long movieId) {
        return ratingRepository.findByMovieId(movieId);
    }

    // Средняя оценка книги
    @GetMapping("/book/{bookId}/average")
    public ResponseEntity<Double> getAverageForBook(@PathVariable Long bookId) {
        Double avg = ratingRepository.getAverageRatingForBook(bookId);
        return ResponseEntity.ok(avg != null ? avg : 0.0);
    }

    // Средняя оценка фильма
    @GetMapping("/movie/{movieId}/average")
    public ResponseEntity<Double> getAverageForMovie(@PathVariable Long movieId) {
        Double avg = ratingRepository.getAverageRatingForMovie(movieId);
        return ResponseEntity.ok(avg != null ? avg : 0.0);
    }

    // ========== СОЗДАТЬ ИЛИ ОБНОВИТЬ ОЦЕНКУ ==========
    @PostMapping
    public ResponseEntity<Rating> createOrUpdateRating(@RequestBody Rating rating) {
        // 1. Проверяем пользователя
        if (rating.getUser() == null || rating.getUser().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<User> user = userRepository.findById(rating.getUser().getId());
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        rating.setUser(user.get());

        // 2. Проверяем книгу или фильм
        Long bookId = null;
        Long movieId = null;

        if (rating.getBook() != null && rating.getBook().getId() != null) {
            bookId = rating.getBook().getId();
            Optional<Book> book = bookRepository.findById(bookId);
            if (!book.isPresent()) return ResponseEntity.badRequest().build();
            rating.setBook(book.get());
            rating.setMovie(null);
        } else if (rating.getMovie() != null && rating.getMovie().getId() != null) {
            movieId = rating.getMovie().getId();
            Optional<Movie> movie = movieRepository.findById(movieId);
            if (!movie.isPresent()) return ResponseEntity.badRequest().build();
            rating.setMovie(movie.get());
            rating.setBook(null);
        } else {
            return ResponseEntity.badRequest().build();
        }

        // 3. Проверяем, ставил ли пользователь уже оценку
        Rating existingRating = null;
        if (bookId != null) {
            existingRating = ratingRepository.findByUserIdAndBookId(user.get().getId(), bookId).orElse(null);
        } else {
            existingRating = ratingRepository.findByUserIdAndMovieId(user.get().getId(), movieId).orElse(null);
        }

        Rating saved;
        if (existingRating != null) {
            // ОБНОВЛЯЕМ существующую оценку
            existingRating.setPlotScore(rating.getPlotScore());
            existingRating.setCharacterScore(rating.getCharacterScore());
            existingRating.setOverallScore(rating.getOverallScore());
            existingRating.setComment(rating.getComment());
            existingRating.setAverageScore(
                    (rating.getPlotScore() + rating.getCharacterScore() + rating.getOverallScore()) / 3.0
            );
            saved = ratingRepository.save(existingRating);
        } else {
            // СОЗДАЕМ новую оценку
            rating.setAverageScore(
                    (rating.getPlotScore() + rating.getCharacterScore() + rating.getOverallScore()) / 3.0
            );
            saved = ratingRepository.save(rating);
        }

        // 4. Обновляем средний рейтинг книги/фильма
        updateBookOrMovieRating(saved);

        return ResponseEntity.ok(saved);
    }

    private void updateBookOrMovieRating(Rating rating) {
        if (rating.getBook() != null) {
            Long bookId = rating.getBook().getId();
            Double avg = ratingRepository.getAverageRatingForBook(bookId);
            long count = ratingRepository.countByBookId(bookId);

            Optional<Book> bookOpt = bookRepository.findById(bookId);
            if (bookOpt.isPresent() && avg != null) {
                Book book = bookOpt.get();
                book.setAverageRating(Math.round(avg * 10.0) / 10.0);
                book.setRatingCount((int) count);
                bookRepository.save(book);
            }
        } else if (rating.getMovie() != null) {
            Long movieId = rating.getMovie().getId();
            Double avg = ratingRepository.getAverageRatingForMovie(movieId);
            long count = ratingRepository.countByMovieId(movieId);

            Optional<Movie> movieOpt = movieRepository.findById(movieId);
            if (movieOpt.isPresent() && avg != null) {
                Movie movie = movieOpt.get();
                movie.setAverageRating(Math.round(avg * 10.0) / 10.0);
                movie.setRatingCount((int) count);
                movieRepository.save(movie);
            }
        }
    }

    // Удалить оценку
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        if (ratingRepository.existsById(id)) {
            ratingRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}