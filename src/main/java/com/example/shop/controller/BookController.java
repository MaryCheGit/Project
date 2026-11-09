package com.example.shop.controller;

import com.example.shop.entity.Book;
import com.example.shop.entity.Category;
import com.example.shop.repository.BookRepository;
import com.example.shop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookRepository.findById(id);
        return book.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{categoryId}")
    public List<Book> getBooksByCategory(@PathVariable Long categoryId) {
        return bookRepository.findByCategoryId(categoryId);
    }

    @GetMapping("/search/author")
    public List<Book> searchByAuthor(@RequestParam String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    @GetMapping("/search/title")
    public List<Book> searchByTitle(@RequestParam String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    // РЕКОМЕНДАЦИИ: 2 книги той же категории с высоким рейтингом
    @GetMapping("/{id}/recommendations")
    public List<Book> getRecommendations(@PathVariable Long id) {
        Optional<Book> bookOpt = bookRepository.findById(id);
        if (!bookOpt.isPresent() || bookOpt.get().getCategory() == null) {
            return List.of();
        }

        Book book = bookOpt.get();
        Long categoryId = book.getCategory().getId();

        List<Book> sameCategory = bookRepository.findByCategoryId(categoryId);
        sameCategory.removeIf(b -> b.getId().equals(id));

        sameCategory.sort((a, b) -> Double.compare(
                b.getAverageRating() != null ? b.getAverageRating() : 0,
                a.getAverageRating() != null ? a.getAverageRating() : 0
        ));

        return sameCategory.stream().limit(2).toList();
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        if (book.getCategory() != null && book.getCategory().getId() != null) {
            Optional<Category> category = categoryRepository.findById(book.getCategory().getId());
            if (category.isPresent()) {
                book.setCategory(category.get());
                return ResponseEntity.ok(bookRepository.save(book));
            }
        }
        return ResponseEntity.ok(bookRepository.save(book));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            book.setTitle(bookDetails.getTitle());
            book.setAuthor(bookDetails.getAuthor());
            book.setDescription(bookDetails.getDescription());
            book.setPublisher(bookDetails.getPublisher());
            book.setYear(bookDetails.getYear());
            book.setPages(bookDetails.getPages());
            book.setGenre(bookDetails.getGenre());
            book.setCoverUrl(bookDetails.getCoverUrl());
            return ResponseEntity.ok(bookRepository.save(book));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}