package com.example.shop.controller;

import com.example.shop.entity.Book;
import com.example.shop.entity.Movie;
import com.example.shop.entity.User;
import com.example.shop.entity.UserLibrary;
import com.example.shop.enums.ReadingStatus;
import com.example.shop.repository.BookRepository;
import com.example.shop.repository.MovieRepository;
import com.example.shop.repository.UserLibraryRepository;
import com.example.shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/library")
public class UserLibraryController {

    @Autowired
    private UserLibraryRepository libraryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MovieRepository movieRepository;

    // ========== ПОЛУЧИТЬ БИБЛИОТЕКУ ПОЛЬЗОВАТЕЛЯ ==========
    @GetMapping("/user/{userId}")
    public List<UserLibrary> getUserLibrary(@PathVariable Long userId) {
        return libraryRepository.findByUserId(userId);
    }

    // ========== ПОЛУЧИТЬ ПО СТАТУСУ ==========
    @GetMapping("/user/{userId}/status/{status}")
    public List<UserLibrary> getUserLibraryByStatus(@PathVariable Long userId,
                                                     @PathVariable ReadingStatus status) {
        return libraryRepository.findByUserIdAndStatus(userId, status);
    }

    // ========== ПРОВЕРИТЬ, ЕСТЬ ЛИ В БИБЛИОТЕКЕ ==========
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkInLibrary(
            @RequestParam Long userId,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) Long movieId) {

        boolean exists;
        if (bookId != null) {
            exists = libraryRepository.existsByUserIdAndBookId(userId, bookId);
        } else if (movieId != null) {
            exists = libraryRepository.existsByUserIdAndMovieId(userId, movieId);
        } else {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(exists);
    }

    // ========== ДОБАВИТЬ ИЛИ ОБНОВИТЬ ==========
    @PostMapping
    public ResponseEntity<UserLibrary> addToLibrary(@RequestBody UserLibrary entry) {
        // 1. Проверяем пользователя
        if (entry.getUser() == null || entry.getUser().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<User> userOpt = userRepository.findById(entry.getUser().getId());
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        User user = userOpt.get();
        entry.setUser(user);

        // 2. Проверяем: книга или фильм
        Long bookId = null;
        Long movieId = null;

        if (entry.getBook() != null && entry.getBook().getId() != null) {
            bookId = entry.getBook().getId();
            Optional<Book> bookOpt = bookRepository.findById(bookId);
            if (!bookOpt.isPresent()) return ResponseEntity.badRequest().build();
            entry.setBook(bookOpt.get());
            entry.setMovie(null);
        } else if (entry.getMovie() != null && entry.getMovie().getId() != null) {
            movieId = entry.getMovie().getId();
            Optional<Movie> movieOpt = movieRepository.findById(movieId);
            if (!movieOpt.isPresent()) return ResponseEntity.badRequest().build();
            entry.setMovie(movieOpt.get());
            entry.setBook(null);
        } else {
            return ResponseEntity.badRequest().build();
        }

        // 3. Проверяем, есть ли уже такая запись
        UserLibrary existing;
        if (bookId != null) {
            existing = libraryRepository.findByUserIdAndBookId(user.getId(), bookId).orElse(null);
        } else {
            existing = libraryRepository.findByUserIdAndMovieId(user.getId(), movieId).orElse(null);
        }

        if (existing != null) {
            // Обновляем статус
            existing.setStatus(entry.getStatus());
            return ResponseEntity.ok(libraryRepository.save(existing));
        } else {
            // Создаём новую
            return ResponseEntity.ok(libraryRepository.save(entry));
        }
    }

    // ========== ИЗМЕНИТЬ СТАТУС ==========
    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<UserLibrary> changeStatus(@PathVariable Long id,
                                                     @PathVariable ReadingStatus status) {
        Optional<UserLibrary> optional = libraryRepository.findById(id);
        if (optional.isPresent()) {
            UserLibrary entry = optional.get();
            entry.setStatus(status);
            return ResponseEntity.ok(libraryRepository.save(entry));
        }
        return ResponseEntity.notFound().build();
    }

    // ========== УДАЛИТЬ ИЗ БИБЛИОТЕКИ ==========
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        if (libraryRepository.existsById(id)) {
            libraryRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}