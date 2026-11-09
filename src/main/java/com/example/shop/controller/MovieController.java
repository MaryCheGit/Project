package com.example.shop.controller;

import com.example.shop.entity.Book;
import com.example.shop.entity.Movie;
import com.example.shop.repository.BookRepository;
import com.example.shop.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Optional<Movie> movie = movieRepository.findById(id);
        return movie.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/book/{bookId}")
    public List<Movie> getMoviesByBook(@PathVariable Long bookId) {
        return movieRepository.findByBookId(bookId);
    }

    @GetMapping("/search/director")
    public List<Movie> searchByDirector(@RequestParam String director) {
        return movieRepository.findByDirectorContainingIgnoreCase(director);
    }

    @GetMapping("/search/title")
    public List<Movie> searchByTitle(@RequestParam String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title);
    }

    // РЕКОМЕНДАЦИИ: 2 фильма той же категории
    @GetMapping("/{id}/recommendations")
    public List<Movie> getRecommendations(@PathVariable Long id) {
        Optional<Movie> movieOpt = movieRepository.findById(id);
        if (!movieOpt.isPresent() || movieOpt.get().getBook() == null) {
            return List.of();
        }

        Movie movie = movieOpt.get();
        Long categoryId = movie.getBook().getCategory() != null
                ? movie.getBook().getCategory().getId()
                : null;

        if (categoryId == null) return List.of();

        List<Movie> allMovies = movieRepository.findAll();
        return allMovies.stream()
                .filter(m -> m.getBook() != null
                        && m.getBook().getCategory() != null
                        && m.getBook().getCategory().getId().equals(categoryId)
                        && !m.getId().equals(id))
                .sorted((a, b) -> Double.compare(
                        b.getAverageRating() != null ? b.getAverageRating() : 0,
                        a.getAverageRating() != null ? a.getAverageRating() : 0
                ))
                .limit(2)
                .toList();
    }

    @PostMapping
    public ResponseEntity<Movie> createMovie(@RequestBody Movie movie) {
        if (movie.getBook() != null && movie.getBook().getId() != null) {
            Optional<Book> book = bookRepository.findById(movie.getBook().getId());
            if (book.isPresent()) {
                movie.setBook(book.get());
                return ResponseEntity.ok(movieRepository.save(movie));
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.ok(movieRepository.save(movie));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody Movie movieDetails) {
        Optional<Movie> optionalMovie = movieRepository.findById(id);
        if (optionalMovie.isPresent()) {
            Movie movie = optionalMovie.get();
            movie.setTitle(movieDetails.getTitle());
            movie.setDirector(movieDetails.getDirector());
            movie.setYear(movieDetails.getYear());
            movie.setDescription(movieDetails.getDescription());
            movie.setPosterUrl(movieDetails.getPosterUrl());
            return ResponseEntity.ok(movieRepository.save(movie));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        if (movieRepository.existsById(id)) {
            movieRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}