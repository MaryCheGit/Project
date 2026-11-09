package com.example.shop.repository;

import com.example.shop.entity.UserLibrary;
import com.example.shop.enums.ReadingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLibraryRepository extends JpaRepository<UserLibrary, Long> {

    // Вся библиотека пользователя
    List<UserLibrary> findByUserId(Long userId);

    // Библиотека пользователя по статусу
    List<UserLibrary> findByUserIdAndStatus(Long userId, ReadingStatus status);

    // Проверка наличия записи (для checkInLibrary)
    boolean existsByUserIdAndBookId(Long userId, Long bookId);
    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    // Поиск конкретной записи (для addToLibrary - обновление существующей)
    Optional<UserLibrary> findByUserIdAndBookId(Long userId, Long bookId);
    Optional<UserLibrary> findByUserIdAndMovieId(Long userId, Long movieId);
}